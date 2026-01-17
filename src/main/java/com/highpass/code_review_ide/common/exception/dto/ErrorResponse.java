package com.highpass.code_review_ide.common.exception.dto;

import com.highpass.code_review_ide.common.exception.BaseExceptionType;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

@Getter
@Builder
public class ErrorResponse {
    private final int status;
    private final String message;

    public static ResponseEntity<ErrorResponse> toResponseEntity(BaseExceptionType exceptionType) {
        return ResponseEntity
                .status(exceptionType.getStatus())
                .body(ErrorResponse.builder()
                        .status(exceptionType.getStatus().value())
                        .message(exceptionType.getMessage())
                        .build()
                );
    }
}