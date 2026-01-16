package com.highpass.code_review_ide.post.api.dto;

import java.time.LocalDateTime;

public record PostSummary(
        Long id,
        String title,
        String status,
        String language,
        Long authorId,
        String authorNickname,
        LocalDateTime createdAt
) {}
