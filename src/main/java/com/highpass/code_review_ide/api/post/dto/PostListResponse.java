package com.highpass.code_review_ide.api.post.dto;

import com.highpass.code_review_ide.domain.post.ReviewPost;
import org.springframework.data.domain.Page;

import java.util.List;

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
