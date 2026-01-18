package com.highpass.code_review_ide.gist.dto;

import jakarta.validation.constraints.NotBlank;

public record GistRequest(
        @NotBlank String title,
        @NotBlank String content,
        String description
) {
}
