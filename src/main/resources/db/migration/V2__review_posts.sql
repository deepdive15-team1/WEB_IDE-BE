CREATE TABLE review_posts (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    author_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    description TEXT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'OPEN',
    language VARCHAR(30) NOT NULL,
    code_text MEDIUMTEXT NOT NULL,
    code_updated_at DATETIME NULL,
    completed_at DATETIME NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    CONSTRAINT fk_review_posts_author
        FOREIGN KEY (author_id) REFERENCES users(id)
);

CREATE INDEX idx_posts_author_created ON review_posts(author_id, created_at);
CREATE INDEX idx_posts_status_created ON review_posts(status, created_at);
