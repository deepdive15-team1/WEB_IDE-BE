package com.highpass.code_review_ide.post.api.dto.response;

import java.time.LocalDateTime;

import com.highpass.code_review_ide.post.domain.PostStatus;

public record CompletePostResponse(
        Long postId,
        PostStatus status,
        LocalDateTime completedAt
) {
}
