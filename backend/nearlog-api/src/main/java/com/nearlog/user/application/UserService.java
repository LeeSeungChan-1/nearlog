package com.nearlog.user.application;

import com.nearlog.common.exception.BusinessException;
import com.nearlog.common.exception.ErrorCode;
import com.nearlog.storage.application.MediaUploadService;
import com.nearlog.storage.application.S3StorageService;
import com.nearlog.storage.domain.UploadPurpose;
import com.nearlog.user.domain.User;
import com.nearlog.user.domain.UserRepository;
import com.nearlog.user.dto.ProfileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final MediaUploadService mediaUploadService;
    private final S3StorageService s3StorageService;

    @Transactional
    public void updateProfileImage(
            Long userId,
            UUID uploadId
    ) {

        User user =
                userRepository.findById(userId)
                        .orElseThrow(() ->
                                new BusinessException(
                                        ErrorCode.USER_NOT_FOUND
                                )
                        );

        String objectKey =
                mediaUploadService.consume(
                        userId,
                        uploadId,
                        UploadPurpose.PROFILE
                );

        user.updateProfileImage(
                objectKey
        );
    }

    private ProfileResponse toResponse(
            User user
    ) {

        String imageUrl = null;

        if (user.getProfileImageKey()
                != null) {

            imageUrl =
                    s3StorageService.createDownloadUrl(
                            user.getProfileImageKey()
                    );
        }

        return new ProfileResponse(
                user.getId(),
                user.getUsername(),
                user.getNickname(),
                user.getBio(),
                imageUrl
        );
    }
}
