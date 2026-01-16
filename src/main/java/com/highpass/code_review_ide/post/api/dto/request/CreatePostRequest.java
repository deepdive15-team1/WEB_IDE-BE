package com.highpass.code_review_ide.post.api.dto.request;

import jakarta.validation.constraints.NotBlank;

/**
 * IDE에서 게시글을 등록할 때 전달되는 값.
 * (단일 파일 스니펫 / 최신 코드 스냅샷)
 */
public record CreatePostRequest(
        @NotBlank String title,
        String description,
        @NotBlank String language,
        @NotBlank String codeText
) {}
