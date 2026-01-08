package com.highpass.code_review_ide.api.auth.dto;

public record SignupResponse(
        Long id,
        String email,
        String nickname
) {}
