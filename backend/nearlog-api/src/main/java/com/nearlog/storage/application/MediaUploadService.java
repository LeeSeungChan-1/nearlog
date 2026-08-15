package com.nearlog.storage.application;

import com.nearlog.common.exception.BusinessException;
import com.nearlog.common.exception.ErrorCode;
import com.nearlog.storage.config.S3Properties;
import com.nearlog.storage.domain.MediaUpload;
import com.nearlog.storage.domain.MediaUploadRepository;
import com.nearlog.storage.domain.UploadPurpose;
import com.nearlog.storage.domain.UploadStatus;
import com.nearlog.storage.dto.PresignUploadRequest;
import com.nearlog.storage.dto.PresignUploadResponse;
import com.nearlog.user.domain.User;
import com.nearlog.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;

import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MediaUploadService {

    private static final Set<String>
            ALLOWED_CONTENT_TYPES =
            Set.of(
                    "image/jpeg",
                    "image/png",
                    "image/webp"
            );

    private final MediaUploadRepository uploadRepository;

    private final UserRepository userRepository;

    private final S3StorageService storageService;

    private final S3Properties properties;

    @Transactional
    public PresignUploadResponse presign(
            Long userId,
            PresignUploadRequest request
    ) {

        validateFile(request);

        User user =
                userRepository.findById(userId)
                        .orElseThrow(() ->
                                new BusinessException(
                                        ErrorCode.USER_NOT_FOUND
                                )
                        );

        UUID uploadId =
                UUID.randomUUID();

        String extension =
                extension(
                        request.contentType()
                );

        String tempKey =
                "tmp/"
                        + userId
                        + "/"
                        + uploadId
                        + extension;

        Instant expiresAt =
                Instant.now().plus(
                        properties.uploadExpiration()
                );

        MediaUpload upload =
                MediaUpload.create(
                        uploadId,
                        user,
                        request.purpose(),
                        request.fileName(),
                        request.contentType(),
                        request.fileSize(),
                        tempKey,
                        expiresAt
                );

        uploadRepository.save(upload);

        PresignedPutObjectRequest presigned =
                storageService.createUploadUrl(
                        tempKey,
                        request.contentType()
                );

        return new PresignUploadResponse(
                uploadId,
                presigned.url().toString(),
                "PUT",
                Map.of(
                        "Content-Type",
                        request.contentType()
                ),
                expiresAt
        );
    }

    private void validateFile(
            PresignUploadRequest request
    ) {

        if (!ALLOWED_CONTENT_TYPES.contains(
                request.contentType()
        )) {

            throw new BusinessException(
                    ErrorCode.INVALID_FILE_TYPE
            );
        }

        long limit =
                request.purpose()
                        == UploadPurpose.PROFILE
                        ? properties.profileMaxSize()
                        : properties.postMaxSize();

        if (
                request.fileSize() <= 0
                        || request.fileSize() > limit
        ) {

            throw new BusinessException(
                    ErrorCode.FILE_TOO_LARGE
            );
        }
    }

    private String extension(
            String contentType
    ) {

        return switch (contentType) {

            case "image/jpeg" -> ".jpg";

            case "image/png" -> ".png";

            case "image/webp" -> ".webp";

            default -> throw new BusinessException(
                    ErrorCode.INVALID_FILE_TYPE
            );
        };
    }

    @Transactional
    public void complete(
            Long userId,
            UUID uploadId
    ) {

        MediaUpload upload =
                uploadRepository
                        .findOwnedForUpdate(
                                uploadId,
                                userId
                        )
                        .orElseThrow(() ->
                                new BusinessException(
                                        ErrorCode.UPLOAD_NOT_FOUND
                                )
                        );

        if (upload.getStatus()
                != UploadStatus.PENDING) {

            return;
        }

        if (upload.isExpired()) {

            throw new BusinessException(
                    ErrorCode.UPLOAD_EXPIRED
            );
        }

        HeadObjectResponse head;

        try {

            head =
                    storageService.head(
                            upload.getTempObjectKey()
                    );

        } catch (S3Exception exception) {

            throw new BusinessException(
                    ErrorCode.UPLOADED_OBJECT_NOT_FOUND
            );
        }

        if (
                head.contentLength()
                        != upload.getDeclaredSize()
        ) {

            throw new BusinessException(
                    ErrorCode.FILE_TOO_LARGE
            );
        }

        if (!upload.getContentType()
                .equalsIgnoreCase(
                        head.contentType()
                )) {

            throw new BusinessException(
                    ErrorCode.INVALID_FILE_TYPE
            );
        }

        String finalKey =
                createFinalKey(upload);

        storageService.copy(
                upload.getTempObjectKey(),
                finalKey
        );

        storageService.delete(
                upload.getTempObjectKey()
        );

        upload.complete(finalKey);
    }

    private String createFinalKey(
            MediaUpload upload
    ) {

        String extension =
                extension(
                        upload.getContentType()
                );

        String directory =
                upload.getPurpose()
                        == UploadPurpose.PROFILE
                        ? "profile"
                        : "posts";

        return directory
                + "/"
                + upload.getUser().getId()
                + "/"
                + upload.getId()
                + extension;
    }

    @Transactional
    public String consume(
            Long userId,
            UUID uploadId,
            UploadPurpose purpose
    ) {

        MediaUpload upload =
                uploadRepository
                        .findOwnedForUpdate(
                                uploadId,
                                userId
                        )
                        .orElseThrow(() ->
                                new BusinessException(
                                        ErrorCode.UPLOAD_NOT_FOUND
                                )
                        );

        if (upload.getPurpose()
                != purpose) {

            throw new BusinessException(
                    ErrorCode.INVALID_UPLOAD_PURPOSE
            );
        }

        if (upload.getStatus()
                == UploadStatus.CONSUMED) {

            throw new BusinessException(
                    ErrorCode.UPLOAD_ALREADY_USED
            );
        }

        if (upload.getStatus()
                != UploadStatus.COMPLETED) {

            throw new BusinessException(
                    ErrorCode.UPLOAD_NOT_COMPLETED
            );
        }

        upload.consume();

        return upload.getFinalObjectKey();
    }

    @Transactional
    public ConsumedUpload consumePostUpload(
            Long userId,
            UUID uploadId
    ) {

        MediaUpload upload =
                uploadRepository
                        .findOwnedForUpdate(
                                uploadId,
                                userId
                        )
                        .orElseThrow(() ->
                                new BusinessException(
                                        ErrorCode.UPLOAD_NOT_FOUND
                                )
                        );

        if (
                upload.getPurpose()
                        != UploadPurpose.POST
        ) {

            throw new BusinessException(
                    ErrorCode.INVALID_UPLOAD_PURPOSE
            );
        }

        if (
                upload.getStatus()
                        != UploadStatus.COMPLETED
        ) {

            throw new BusinessException(
                    ErrorCode.UPLOAD_NOT_COMPLETED
            );
        }

        upload.consume();

        return new ConsumedUpload(
                upload.getFinalObjectKey(),
                upload.getContentType()
        );
    }

    public record ConsumedUpload(

            String objectKey,

            String contentType

    ) {
    }
}
