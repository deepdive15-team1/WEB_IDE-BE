package com.highpass.code_review_ide.post.api.dto.response;

import java.util.List;

import org.springframework.data.domain.Page;

import com.highpass.code_review_ide.post.api.dto.PostSummary;
import com.highpass.code_review_ide.post.domain.ReviewPost;

public record PostListResponse(
        List<PostSummary> items,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
    public static PostListResponse from(Page<ReviewPost> p) {
        List<PostSummary> items = p.getContent().stream()
                .map(post -> new PostSummary(
                        post.getId(),
                        post.getTitle(),
                        post.getDescription(),
                        post.getStatus().name(),
                        post.getLanguage(),
                        post.getAuthor().getId(),
                        post.getAuthor().getNickname(),
                        post.getCreatedAt()
                ))
                .toList();

        return new PostListResponse(
                items,
                p.getNumber(),
                p.getSize(),
                p.getTotalElements(),
                p.getTotalPages()
        );
    }
}
