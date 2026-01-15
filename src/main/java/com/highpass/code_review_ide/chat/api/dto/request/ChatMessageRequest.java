package com.highpass.code_review_ide.chat.api.dto.request;

public record ChatMessageRequest(
        Long roomId,
        String message,
        String senderEmail
) {
}
