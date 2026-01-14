package com.highpass.code_review_ide.api.post.dto;

import com.highpass.code_review_ide.domain.post.PostStatus;

import java.time.LocalDateTime;

public record CompletePostResponse(
        Long postId,
        PostStatus status,
        LocalDateTime completedAt
) {
}
