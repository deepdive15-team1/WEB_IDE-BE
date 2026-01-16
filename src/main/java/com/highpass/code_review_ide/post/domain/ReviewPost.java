package com.highpass.code_review_ide.post.domain;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.highpass.code_review_ide.user.domain.User;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
        name = "review_posts",
        indexes = {
                @Index(name = "idx_posts_author_created", columnList = "author_id, created_at"),
                @Index(name = "idx_posts_status_created", columnList = "status, created_at")
        }
)
@EntityListeners(AuditingEntityListener.class)
public class ReviewPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @Column(nullable = false, length = 200)
    private String title;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PostStatus status = PostStatus.OPEN;

    @Column(nullable = false, length = 30)
    private String language;

    /**
     * DB에 저장되는 '최신 스냅샷' 코드.
     * (미구현)실시간 편집 이벤트는 WebSocket으로만 공유하고,
     * 저장/디바운스/완료 시점에만 이 필드를 업데이트한다.
     */
    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "code_text", nullable = false, columnDefinition = "MEDIUMTEXT")
    private String codeText;

    @Column(name = "code_updated_at")
    private LocalDateTime codeUpdatedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public ReviewPost(User author, String title, String description, String language, String codeText) {
        this.author = author;
        this.title = title;
        this.description = description;
        this.language = language;
        this.codeText = codeText;
        this.status = PostStatus.OPEN;
        this.codeUpdatedAt = null;
        this.completedAt = null;
    }

    public void updateCodeSnapshot(String newCodeText) {
        this.codeText = newCodeText;
        this.codeUpdatedAt = LocalDateTime.now();
    }

    public void complete() {
        this.status = PostStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
    }
}
