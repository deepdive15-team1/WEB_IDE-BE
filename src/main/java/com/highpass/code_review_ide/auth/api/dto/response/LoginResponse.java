package com.highpass.code_review_ide.auth.api.dto.response;

public record LoginResponse(
        String accessToken,
        String refreshToken
) {}
