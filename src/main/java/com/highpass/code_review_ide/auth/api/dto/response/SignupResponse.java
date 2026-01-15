package com.highpass.code_review_ide.auth.api.dto.response;

public record SignupResponse(
        Long id,
        String email,
        String nickname
) {}
