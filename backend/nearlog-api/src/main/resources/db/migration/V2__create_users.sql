CREATE TABLE users
(
    id                BIGSERIAL PRIMARY KEY,

    email             VARCHAR(255) NOT NULL,
    password          VARCHAR(255) NOT NULL,

    username          VARCHAR(30)  NOT NULL,
    nickname          VARCHAR(30)  NOT NULL,

    bio               VARCHAR(150),
    profile_image_url TEXT,

    role              VARCHAR(20)  NOT NULL DEFAULT 'USER',
    status            VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',

    created_at        TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uk_users_email
        UNIQUE (email),

    CONSTRAINT uk_users_username
        UNIQUE (username),

    CONSTRAINT chk_users_role
        CHECK (role IN ('USER', 'ADMIN')),

    CONSTRAINT chk_users_status
        CHECK (status IN ('ACTIVE', 'SUSPENDED', 'WITHDRAWN'))
);