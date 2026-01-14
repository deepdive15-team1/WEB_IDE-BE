package com.highpass.code_review_ide.domain.post;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReviewPostRepository extends JpaRepository<ReviewPost, Long> {

    Page<ReviewPost> findByStatus(PostStatus status, Pageable pageable);

    Page<ReviewPost> findByAuthor_Id(Long authorId, Pageable pageable);

    @EntityGraph(attributePaths = {"author"})
    Optional<ReviewPost> findWithAuthorById(Long id);
}
