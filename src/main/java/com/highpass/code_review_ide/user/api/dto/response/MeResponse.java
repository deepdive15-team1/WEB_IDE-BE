package com.highpass.code_review_ide.user.api.dto.response;

public record MeResponse(
        Long id,
        String email,
        String nickname
) {}
