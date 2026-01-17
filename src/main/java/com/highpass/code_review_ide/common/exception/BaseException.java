package com.highpass.code_review_ide.common.exception;

import lombok.Getter;

@Getter
public class BaseException extends RuntimeException {
    private final BaseExceptionType exceptionType;

    public BaseException(BaseExceptionType exceptionType) {
        super(exceptionType.getMessage());
        this.exceptionType = exceptionType;
    }
}
