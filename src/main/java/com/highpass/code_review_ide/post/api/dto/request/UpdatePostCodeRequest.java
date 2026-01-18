package com.highpass.code_review_ide.post.api.dto.request;

import jakarta.validation.constraints.NotBlank;

/**
 * 게시글 코드 수정 요청
 * - 작성자만 코드를 수정할 수 있음
 * - OPEN 상태인 게시글만 수정 가능
 */
public record UpdatePostCodeRequest(
        @NotBlank(message = "코드는 비어있을 수 없습니다.")
        String codeText
) {
}
