package com.highpass.code_review_ide.chat.exception;

import com.highpass.code_review_ide.common.exception.BaseException;
import com.highpass.code_review_ide.common.exception.BaseExceptionType;

public class ChatException extends BaseException {
    public ChatException(BaseExceptionType exceptionType) {
        super(exceptionType);
    }
}
