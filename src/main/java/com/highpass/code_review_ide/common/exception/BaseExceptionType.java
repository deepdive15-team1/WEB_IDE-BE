package com.highpass.code_review_ide.common.exception;

import org.springframework.http.HttpStatus;

public interface BaseExceptionType {
    HttpStatus getStatus();
    String getMessage();
}