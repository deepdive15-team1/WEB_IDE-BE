CREATE TABLE users (
                       id BIGINT PRIMARY KEY AUTO_INCREMENT,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       nickname VARCHAR(30) NOT NULL UNIQUE,
                       password_hash VARCHAR(255) NOT NULL,
                       status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
                       created_at DATETIME NOT NULL,
                       updated_at DATETIME NOT NULL
);

CREATE TABLE refresh_tokens (
                                id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                user_id BIGINT NOT NULL,
                                token_hash VARCHAR(255) NOT NULL,
                                expires_at DATETIME NOT NULL,
                                revoked_at DATETIME NULL,
                                created_at DATETIME NOT NULL,
                                CONSTRAINT fk_refresh_tokens_user
                                    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE email_verifications (
                                     id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                     email VARCHAR(255) NOT NULL,
                                     purpose VARCHAR(30) NOT NULL,
                                     code_hash VARCHAR(255) NOT NULL,
                                     expires_at DATETIME NOT NULL,
                                     verified_at DATETIME NULL,
                                     created_at DATETIME NOT NULL
);
