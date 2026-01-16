package com.highpass.code_review_ide.api.post.dto;

import com.highpass.code_review_ide.domain.post.ReviewPost;

import java.time.LocalDateTime;

public record PostDetailResponse(
        Long id,
        Long authorId,
        String authorNickname,
        String title,
        String description,
        String status,
        String language,
        String codeText,
        LocalDateTime codeUpdatedAt,
        LocalDateTime completedAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Long roomId
) {
    public static PostDetailResponse from(ReviewPost post, Long roomId) {
        return new PostDetailResponse(
                post.getId(),
                post.getAuthor().getId(),
                post.getAuthor().getNickname(),
                post.getTitle(),
                post.getDescription(),
                post.getStatus().name(),
                post.getLanguage(),
                post.getCodeText(),
                post.getCodeUpdatedAt(),
                post.getCompletedAt(),
                post.getCreatedAt(),
                post.getUpdatedAt(),
                roomId
        );
    }
}
