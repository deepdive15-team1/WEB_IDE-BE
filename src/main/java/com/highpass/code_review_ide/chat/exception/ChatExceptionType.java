package com.highpass.code_review_ide.chat.exception;

import com.highpass.code_review_ide.common.exception.BaseExceptionType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ChatExceptionType implements BaseExceptionType {
    CHAT_ROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "채팅방을 찾을 수 없습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    NOT_PARTICIPANT(HttpStatus.FORBIDDEN, "본인이 속하지 않은 채팅방입니다.");

    private final HttpStatus status;
    private final String message;
}