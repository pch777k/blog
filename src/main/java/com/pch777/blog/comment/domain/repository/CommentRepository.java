package com.pch777.blog.comment.domain.repository;

import com.pch777.blog.comment.domain.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface CommentRepository extends JpaRepository<Comment, UUID> {
    List<Comment> findByArticleId(UUID articleId);
    Page<Comment> findByArticleId(UUID articleId, Pageable pageable);
    Page<Comment> findAllByContentContainingIgnoreCase(String content, Pageable pageable);

    boolean existsByUser_IdAndId(UUID userId, UUID commentId);

    Page<Comment> findByUserId(UUID userId, Pageable pageable);

    Page<Comment> findByUserIdAndContentContainingIgnoreCase(UUID userId, String search, Pageable pageable);

    Page<Comment> findByUserIdAndCreatedAfter(UUID userId, LocalDateTime startDateTime, Pageable pageable);

    Page<Comment> findByUserIdAndContentContainingIgnoreCaseAndCreatedBefore(UUID userId, String content, LocalDateTime endDate, Pageable pageable);
    Page<Comment> findByUserIdAndContentContainingIgnoreCaseAndCreatedAfter(UUID userId, String content, LocalDateTime startDate, Pageable pageable);
    Page<Comment> findByUserIdAndContentContainingIgnoreCaseAndCreatedBetween(UUID userId, String content, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    Page<Comment> findByUserIdAndCreatedBefore(UUID userId, LocalDateTime endDate, Pageable pageable);

    Page<Comment> findByUserIdAndCreatedBetween(UUID userId, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    List<Comment> findByArticleTitleUrl(String titleUrl);
}