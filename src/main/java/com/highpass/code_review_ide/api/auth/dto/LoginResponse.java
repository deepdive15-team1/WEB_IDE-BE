package com.highpass.code_review_ide.api.auth.dto;

public record LoginResponse(
        String accessToken,
        String refreshToken
) {}
