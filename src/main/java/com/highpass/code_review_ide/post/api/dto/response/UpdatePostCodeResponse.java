package com.highpass.code_review_ide.post.api.dto.response;

import java.time.LocalDateTime;

/**
 * 게시글 코드 수정 응답
 */
public record UpdatePostCodeResponse(
        Long postId,
        String codeText,
        LocalDateTime codeUpdatedAt
) {
}
