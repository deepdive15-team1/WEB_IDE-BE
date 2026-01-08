package com.highpass.code_review_ide.api.auth.dto;

public record MeResponse(
        Long id,
        String email,
        String nickname
) {}
