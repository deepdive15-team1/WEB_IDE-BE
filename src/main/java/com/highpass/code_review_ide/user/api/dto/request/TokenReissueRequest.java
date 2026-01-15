package com.highpass.code_review_ide.user.api.dto.request;

import jakarta.validation.constraints.NotBlank;

public record TokenReissueRequest(
        @NotBlank String refreshToken
) {}
