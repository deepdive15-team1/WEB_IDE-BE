package com.highpass.code_review_ide.post.application;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.highpass.code_review_ide.post.api.dto.request.CompletePostRequest;
import com.highpass.code_review_ide.post.api.dto.request.CreatePostRequest;
import com.highpass.code_review_ide.post.api.dto.response.CompletePostResponse;
import com.highpass.code_review_ide.post.api.dto.response.CreatePostResponse;
import com.highpass.code_review_ide.post.api.dto.response.PostDetailResponse;
import com.highpass.code_review_ide.post.api.dto.response.PostListResponse;
import com.highpass.code_review_ide.post.domain.PostStatus;
import com.highpass.code_review_ide.post.domain.ReviewPost;
import com.highpass.code_review_ide.post.domain.dao.ReviewPostRepository;
import com.highpass.code_review_ide.user.domain.User;
import com.highpass.code_review_ide.user.domain.dao.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final UserRepository userRepository;
    private final ReviewPostRepository reviewPostRepository;
    //private final ChatRoomRepository chatRoomRepository;
    //private final ChatParticipantRepository chatParticipantRepository;

    @Transactional
    public CreatePostResponse createPost(Long userId, CreatePostRequest req) {
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        ReviewPost post = new ReviewPost(
                author,
                req.title(),
                req.description(),
                req.language(),
                req.codeText()
        );
        ReviewPost saved = reviewPostRepository.save(post);

        // 게시글당 채팅방 1개 생성
        //ChatRoom room = chatRoomRepository.save(new ChatRoom(saved));

        // 작성자 참여자 등록
        //chatParticipantRepository.save(new ChatParticipant(room, author, ChatRole.AUTHOR));

        return new CreatePostResponse(saved.getId(), null, post.getStatus(), post.getCreatedAt());
    }

    public PostListResponse listOpenPosts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<ReviewPost> p = reviewPostRepository.findByStatus(PostStatus.OPEN, pageable);
        return PostListResponse.from(p);
    }

    public PostListResponse listMyPosts(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<ReviewPost> p = reviewPostRepository.findByAuthor_Id(userId, pageable);
        return PostListResponse.from(p);
    }

    public PostDetailResponse getPost(Long requesterId, Long postId) {
        ReviewPost post = reviewPostRepository.findWithAuthorById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        if (post.getStatus() == PostStatus.COMPLETED && !post.getAuthor().getId().equals(requesterId)) {
            throw new IllegalArgumentException("종료된 게시글은 작성자만 열람할 수 있습니다.");
        }

        Long roomId = null; // chatRoomRepository.findByPost_Id(postId)
                // .map(ChatRoom::getId)
                // .orElse(null);

        return PostDetailResponse.from(post, roomId);
    }

    /**
     * 작성자만 DB에 최신 코드 스냅샷을 저장한다.
     */
    

    /**
     * 게시글 완료 처리(작성자만)
     * - 필요 시 요청으로 전달된 최종 codeText를 저장 후 완료한다.
     */
    @Transactional
    public CompletePostResponse completePost(Long requesterId, Long postId, CompletePostRequest req) {
        ReviewPost post = reviewPostRepository.findWithAuthorById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        if (!post.getAuthor().getId().equals(requesterId)) {
            throw new IllegalArgumentException("작성자만 게시글을 완료할 수 있습니다.");
        }

        if (post.getStatus() == PostStatus.COMPLETED) {
            // 멱등 처리: 이미 완료된 경우 현재 상태 그대로 반환
            return new CompletePostResponse(post.getId(), post.getStatus(), post.getCompletedAt());
        }

        if (post.getStatus() != PostStatus.OPEN) {
            throw new IllegalArgumentException("현재 상태에서는 완료 처리를 할 수 없습니다.");
        }

        if (req != null && req.codeText() != null && !req.codeText().isBlank()) {
            post.updateCodeSnapshot(req.codeText());
        }

        post.complete();

        return new CompletePostResponse(post.getId(), post.getStatus(), post.getCompletedAt());
    }
}
