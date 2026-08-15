CREATE TABLE media_uploads
(
    id                  UUID PRIMARY KEY,

    user_id             BIGINT NOT NULL,

    purpose             VARCHAR(20) NOT NULL,

    status              VARCHAR(20) NOT NULL,

    original_filename   VARCHAR(255) NOT NULL,

    content_type        VARCHAR(100) NOT NULL,

    declared_size       BIGINT NOT NULL,

    temp_object_key     VARCHAR(500) NOT NULL,

    final_object_key    VARCHAR(500),

    expires_at          TIMESTAMPTZ NOT NULL,

    completed_at        TIMESTAMPTZ,

    consumed_at         TIMESTAMPTZ,

    created_at          TIMESTAMPTZ NOT NULL,

    CONSTRAINT fk_media_upload_user
        FOREIGN KEY (user_id)
            REFERENCES users (id)
            ON DELETE CASCADE,

    CONSTRAINT uk_media_upload_temp_key
        UNIQUE (temp_object_key),

    CONSTRAINT chk_media_upload_purpose
        CHECK (
            purpose IN ('PROFILE', 'POST')
            ),

    CONSTRAINT chk_media_upload_status
        CHECK (
            status IN (
                       'PENDING',
                       'COMPLETED',
                       'CONSUMED'
                )
            )
);

CREATE INDEX idx_media_upload_user
    ON media_uploads (
                      user_id,
                      created_at DESC
        );