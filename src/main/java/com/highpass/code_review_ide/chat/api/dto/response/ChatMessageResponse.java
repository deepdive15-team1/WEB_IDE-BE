package com.highpass.code_review_ide.chat.api.dto.response;

import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record ChatMessageResponse(
        Long roomId,
        String message,
        String senderName,
        LocalDateTime sendTime,
        Long codeLineNumbers
) {
}
