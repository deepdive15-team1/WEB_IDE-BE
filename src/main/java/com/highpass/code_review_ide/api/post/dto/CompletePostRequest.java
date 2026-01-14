package com.highpass.code_review_ide.api.post.dto;

/**
 * 완료 직전에 마지막 코드 스냅샷을 함께 보낼 수 있다(선택).
 * 프론트에서 저장 API를 따로 호출하지 않아도 최종 코드가 DB에 남도록 하기 위함.
 */
public record CompletePostRequest(
        String codeText
) {
}
