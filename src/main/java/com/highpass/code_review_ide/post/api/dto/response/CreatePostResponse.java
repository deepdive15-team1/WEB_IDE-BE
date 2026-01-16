package com.highpass.code_review_ide.post.api.dto.response;

import java.time.LocalDateTime;

import com.highpass.code_review_ide.post.domain.PostStatus;

/**
 * 게시글 생성 결과.
 * roomId는 실시간 채팅/IDE(WebSocket) 연결을 위해 함께 반환.
 */
public record CreatePostResponse(
        Long postId,
        Long roomId,
        PostStatus status,
        LocalDateTime completedAt
) {}
