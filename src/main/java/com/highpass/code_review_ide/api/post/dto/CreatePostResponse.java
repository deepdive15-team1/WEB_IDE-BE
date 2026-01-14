package com.highpass.code_review_ide.api.post.dto;

/**
 * 게시글 생성 결과.
 * roomId는 실시간 채팅/IDE(WebSocket) 연결을 위해 함께 반환.
 */
public record CreatePostResponse(
        Long postId,
        Long roomId
) {}
