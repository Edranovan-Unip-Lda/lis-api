-- liquibase formatted sql

-- changeset geovanniovinhas:1745668013923-1
CREATE TABLE lis_dm_role
(
    id         BIGSERIAL PRIMARY KEY,
    is_deleted BOOLEAN,
    created_at TIMESTAMP NULL,
    updated_at TIMESTAMP NULL,
    created_by VARCHAR(255) NULL,
    updated_by VARCHAR(255) NULL,
    name       VARCHAR(255) NULL
);

-- changeset geovanniovinhas:1745668013923-2
CREATE TABLE lis_email_config
(
    id         BIGSERIAL PRIMARY KEY,
    is_deleted BOOLEAN,
    created_at TIMESTAMP NULL,
    updated_at TIMESTAMP NULL,
    created_by VARCHAR(255) NULL,
    updated_by VARCHAR(255) NULL,
    smtp_host  VARCHAR(255) NULL,
    smtp_port  INTEGER NOT NULL,
    username   VARCHAR(255) NULL,
    password   VARCHAR(255) NULL,
    from_email VARCHAR(255) NULL,
    is_active  BOOLEAN NOT NULL
);

-- changeset geovanniovinhas:1745668013923-3
CREATE TABLE lis_user
(
    id          BIGSERIAL PRIMARY KEY,
    is_deleted  BOOLEAN,
    created_at  TIMESTAMP NULL,
    updated_at  TIMESTAMP NULL,
    created_by  VARCHAR(255) NULL,
    updated_by  VARCHAR(255) NULL,
    first_name  VARCHAR(255) NULL,
    last_name   VARCHAR(255) NULL,
    username    VARCHAR(255) NULL,
    email       VARCHAR(255) NULL,
    password    VARCHAR(255) NULL,
    role_id     BIGINT NOT NULL,
    jwt_session VARCHAR(255) NULL,
    status      VARCHAR(255) NULL
);

-- changeset geovanniovinhas:1745668013923-4
ALTER TABLE lis_user
    ADD CONSTRAINT uc_lis_user_email UNIQUE (email);

-- changeset geovanniovinhas:1745668013923-5
ALTER TABLE lis_user
    ADD CONSTRAINT uc_lis_user_username UNIQUE (username);

-- changeset geovanniovinhas:1745668013923-
