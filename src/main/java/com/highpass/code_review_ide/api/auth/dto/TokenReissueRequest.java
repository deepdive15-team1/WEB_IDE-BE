package com.highpass.code_review_ide.api.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record TokenReissueRequest(
        @NotBlank String refreshToken
) {}
