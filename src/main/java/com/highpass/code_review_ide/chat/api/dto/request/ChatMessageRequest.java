package com.highpass.code_review_ide.chat.api.dto.request;

import java.time.LocalDateTime;

public record ChatMessageRequest(
        Long roomId,
        String message,
        String senderName,
        LocalDateTime sendTime
) {
}
