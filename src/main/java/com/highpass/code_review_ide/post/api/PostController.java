package com.highpass.code_review_ide.post.api;

import java.net.URI;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.highpass.code_review_ide.common.security.JwtProvider;
import com.highpass.code_review_ide.post.api.dto.request.CompletePostRequest;
import com.highpass.code_review_ide.post.api.dto.request.CreatePostRequest;
import com.highpass.code_review_ide.post.api.dto.request.UpdatePostCodeRequest;
import com.highpass.code_review_ide.post.api.dto.response.CompletePostResponse;
import com.highpass.code_review_ide.post.api.dto.response.CreatePostResponse;
import com.highpass.code_review_ide.post.api.dto.response.PostDetailResponse;
import com.highpass.code_review_ide.post.api.dto.response.PostListResponse;
import com.highpass.code_review_ide.post.api.dto.response.UpdatePostCodeResponse;
import com.highpass.code_review_ide.post.application.PostService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;
    private final JwtProvider jwtProvider;

    private Long requireUserId(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Authorization 헤더가 필요합니다. (Bearer 토큰)");
        }
        String token = authorization.substring("Bearer ".length()).trim();
        return jwtProvider.getUserId(token);
    }

    /**
     * IDE에서 게시글 생성(단일 파일 스니펫 + 최신 코드 스냅샷 포함)
     */
    @PostMapping
    public ResponseEntity<CreatePostResponse> create(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
            @Valid @RequestBody CreatePostRequest req
    ) {
        Long userId = requireUserId(authorization);
        CreatePostResponse res = postService.createPost(userId, req);
        return ResponseEntity.created(URI.create("/posts/" + res.postId())).body(res);
    }

    /**
     * 전체 게시글 목록(기본: OPEN만 노출)
     */
    @GetMapping
    public ResponseEntity<PostListResponse> listOpen(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(postService.listOpenPosts(page, size));
    }

    /**
     * 마이페이지 - 내가 생성한 게시글 목록(OPEN + COMPLETED)
     */
    @GetMapping("/me")
    public ResponseEntity<PostListResponse> listMine(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size
    ) {
        Long userId = requireUserId(authorization);
        return ResponseEntity.ok(postService.listMyPosts(userId, page, size));
    }

    /**
     * 게시글 상세(OPEN: 로그인 사용자 열람 가능 / COMPLETED: 작성자만)
     */
    @GetMapping("/{postId}")
    public ResponseEntity<PostDetailResponse> get(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
            @PathVariable("postId") Long postId
    ) {
        Long userId = requireUserId(authorization);
        return ResponseEntity.ok(postService.getPost(userId, postId));
    }

    /**
     * 게시글 코드 수정(작성자만, OPEN 상태만 가능)
     * - IDE에서 수정된 코드를 저장
     */
    @PutMapping("/{postId}/code")
    public ResponseEntity<UpdatePostCodeResponse> updateCode(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
            @PathVariable("postId") Long postId,
            @Valid @RequestBody UpdatePostCodeRequest req
    ) {
        Long userId = requireUserId(authorization);
        return ResponseEntity.ok(postService.updatePostCode(userId, postId, req));
    }

    /**
     * 작성자만 최신 코드 스냅샷을 DB에 저장한다.
     * - 실시간 편집 이벤트는 WebSocket으로만 공유
     * - 저장 버튼/디바운스 자동저장/완료 직전 저장 시점에만 호출
     */
    

    /**
     * 게시글 완료 처리(작성자만)
     * - status=COMPLETED
     * - 완료 후 채팅/코드 수정은 금지(서비스/WS 레벨에서 차단)
     *
     * 필요하면 요청 본문으로 최종 codeText를 함께 보내 마지막 스냅샷 저장 후 완료할 수 있다.
     */
    @PostMapping("/{postId}/complete")
    public ResponseEntity<CompletePostResponse> complete(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
            @PathVariable("postId") Long postId,
            @RequestBody(required = false) CompletePostRequest req
    ) {
        Long userId = requireUserId(authorization);
        return ResponseEntity.ok(postService.completePost(userId, postId, req));
    }
}
