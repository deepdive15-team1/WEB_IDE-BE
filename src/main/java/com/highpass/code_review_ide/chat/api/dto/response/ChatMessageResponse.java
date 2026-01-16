package com.highpass.code_review_ide.chat.api.dto.response;

public record ChatMessageResponse(
        Long roomId,
        String message,
        String senderEmail
) {
}
