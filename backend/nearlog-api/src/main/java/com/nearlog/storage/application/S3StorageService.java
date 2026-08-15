package com.nearlog.storage.application;

import com.nearlog.storage.config.S3Properties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.*;

@Service
@RequiredArgsConstructor
public class S3StorageService {

    private final S3Client s3Client;

    private final S3Presigner s3Presigner;

    private final S3Properties properties;

    public PresignedPutObjectRequest createUploadUrl(
            String objectKey,
            String contentType
    ) {

        PutObjectRequest putRequest =
                PutObjectRequest.builder()
                        .bucket(properties.bucket())
                        .key(objectKey)
                        .contentType(contentType)
                        .build();

        PutObjectPresignRequest request =
                PutObjectPresignRequest.builder()
                        .signatureDuration(
                                properties.uploadExpiration()
                        )
                        .putObjectRequest(
                                putRequest
                        )
                        .build();

        return s3Presigner
                .presignPutObject(request);
    }

    public HeadObjectResponse head(
            String objectKey
    ) {

        return s3Client.headObject(
                HeadObjectRequest.builder()
                        .bucket(properties.bucket())
                        .key(objectKey)
                        .build()
        );
    }

    public void copy(
            String sourceKey,
            String destinationKey
    ) {

        s3Client.copyObject(
                CopyObjectRequest.builder()
                        .bucket(properties.bucket())
                        .copySource(
                                properties.bucket()
                                        + "/"
                                        + sourceKey
                        )
                        .key(destinationKey)
                        .build()
        );
    }

    public void delete(
            String objectKey
    ) {

        s3Client.deleteObject(
                DeleteObjectRequest.builder()
                        .bucket(properties.bucket())
                        .key(objectKey)
                        .build()
        );
    }

    public String createDownloadUrl(
            String objectKey
    ) {

        GetObjectRequest getRequest =
                GetObjectRequest.builder()
                        .bucket(properties.bucket())
                        .key(objectKey)
                        .build();

        GetObjectPresignRequest request =
                GetObjectPresignRequest.builder()
                        .signatureDuration(
                                properties.downloadExpiration()
                        )
                        .getObjectRequest(
                                getRequest
                        )
                        .build();

        return s3Presigner
                .presignGetObject(request)
                .url()
                .toString();
    }
}