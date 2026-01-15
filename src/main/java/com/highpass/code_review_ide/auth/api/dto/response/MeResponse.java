package com.highpass.code_review_ide.auth.api.dto.response;

public record MeResponse(
        Long id,
        String email,
        String nickname
) {}
