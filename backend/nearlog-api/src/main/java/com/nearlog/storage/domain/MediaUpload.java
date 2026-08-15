package com.nearlog.storage.domain;

import com.nearlog.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Getter
@Entity
@Table(name = "media_uploads")
@NoArgsConstructor(
        access = AccessLevel.PROTECTED
)
public class MediaUpload {

    @Id
    private UUID id;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "user_id",
            nullable = false
    )
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UploadPurpose purpose;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UploadStatus status;

    @Column(
            name = "original_filename",
            nullable = false
    )
    private String originalFilename;

    @Column(
            name = "content_type",
            nullable = false
    )
    private String contentType;

    @Column(
            name = "declared_size",
            nullable = false
    )
    private long declaredSize;

    @Column(
            name = "temp_object_key",
            nullable = false
    )
    private String tempObjectKey;

    @Column(name = "final_object_key")
    private String finalObjectKey;

    @Column(
            name = "expires_at",
            nullable = false
    )
    private Instant expiresAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    @Column(name = "consumed_at")
    private Instant consumedAt;

    @Column(
            name = "created_at",
            nullable = false
    )
    private Instant createdAt;

    public static MediaUpload create(
            UUID id,
            User user,
            UploadPurpose purpose,
            String originalFilename,
            String contentType,
            long declaredSize,
            String tempObjectKey,
            Instant expiresAt
    ) {

        MediaUpload upload =
                new MediaUpload();

        upload.id = id;
        upload.user = user;
        upload.purpose = purpose;

        upload.status =
                UploadStatus.PENDING;

        upload.originalFilename =
                originalFilename;

        upload.contentType =
                contentType;

        upload.declaredSize =
                declaredSize;

        upload.tempObjectKey =
                tempObjectKey;

        upload.expiresAt =
                expiresAt;

        upload.createdAt =
                Instant.now();

        return upload;
    }

    public void complete(
            String finalObjectKey
    ) {

        this.finalObjectKey =
                finalObjectKey;

        this.status =
                UploadStatus.COMPLETED;

        this.completedAt =
                Instant.now();
    }

    public void consume() {

        this.status =
                UploadStatus.CONSUMED;

        this.consumedAt =
                Instant.now();
    }

    public boolean isExpired() {

        return Instant.now()
                .isAfter(expiresAt);
    }
}