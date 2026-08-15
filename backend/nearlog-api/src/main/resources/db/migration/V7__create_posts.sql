CREATE TABLE posts
(
    id              BIGSERIAL PRIMARY KEY,

    author_id       BIGINT NOT NULL,

    content         VARCHAR(2200) NOT NULL DEFAULT '',

    visibility      VARCHAR(20) NOT NULL DEFAULT 'PUBLIC',

    like_count      BIGINT NOT NULL DEFAULT 0,

    comment_count   BIGINT NOT NULL DEFAULT 0,

    created_at      TIMESTAMPTZ NOT NULL,

    updated_at      TIMESTAMPTZ NOT NULL,

    deleted_at      TIMESTAMPTZ,

    CONSTRAINT fk_posts_author
        FOREIGN KEY (author_id)
            REFERENCES users (id),

    CONSTRAINT chk_post_visibility
        CHECK (
            visibility IN (
                           'PUBLIC',
                           'FOLLOWERS',
                           'PRIVATE'
                )
            )
);

CREATE INDEX idx_posts_author_created
    ON posts (
              author_id,
              created_at DESC,
              id DESC
        );


CREATE TABLE post_media
(
    id              BIGSERIAL PRIMARY KEY,

    post_id         BIGINT NOT NULL,

    object_key      VARCHAR(500) NOT NULL,

    content_type    VARCHAR(100) NOT NULL,

    sort_order      INTEGER NOT NULL,

    created_at      TIMESTAMPTZ NOT NULL,

    CONSTRAINT fk_post_media_post
        FOREIGN KEY (post_id)
            REFERENCES posts (id)
            ON DELETE CASCADE,

    CONSTRAINT uk_post_media_order
        UNIQUE (
                post_id,
                sort_order
            )
);

CREATE INDEX idx_post_media_post
    ON post_media (
                   post_id,
                   sort_order
        );