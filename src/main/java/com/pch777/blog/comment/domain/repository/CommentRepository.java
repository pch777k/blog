package com.pch777.blog.comment.domain.repository;

import com.pch777.blog.comment.domain.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CommentRepository extends JpaRepository<Comment, UUID> {
    List<Comment> findByArticleId(UUID articleId);
    Page<Comment> findByArticleId(UUID articleId, Pageable pageable);
}
