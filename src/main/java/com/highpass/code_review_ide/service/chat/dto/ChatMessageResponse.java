package com.highpass.code_review_ide.service.chat.dto;

public record ChatMessageResponse(
        Long roomId,
        String message,
        String senderEmail
) {
}
