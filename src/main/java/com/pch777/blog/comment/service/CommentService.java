package com.pch777.blog.comment.service;

import com.pch777.blog.article.domain.model.Article;
import com.pch777.blog.article.domain.repository.ArticleRepository;
import com.pch777.blog.comment.domain.model.Comment;
import com.pch777.blog.comment.domain.repository.CommentRepository;
import com.pch777.blog.comment.dto.CommentDto;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final ArticleRepository articleRepository;

    @Transactional(readOnly = true)
    public List<Comment> getComments(UUID articleId) {
        return commentRepository.findByArticleId(articleId);
    }

    @Transactional(readOnly = true)
    public Comment getComment(UUID id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found with id: " + id));
    }

    @Transactional
    public Comment createComment(UUID articleId, CommentDto commentDto) {
        Comment comment = new Comment();
        comment.setContent(commentDto.getContent());

        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new EntityNotFoundException("Article not found with id: " + articleId));
        article.addComment(comment);

        commentRepository.save(comment);
        articleRepository.save(article);

        return comment;
    }

    @Transactional
    public Comment updateComment(UUID commentId, CommentDto commentDto) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found with id: " + commentId));
        comment.setContent(commentDto.getContent());

        return commentRepository.save(comment);
    }

    @Transactional
    public void deleteComment(UUID commentId) {
        commentRepository.deleteById(commentId);
    }

    public Page<Comment> getCommentsByArticleId(UUID articleId, Pageable pageable) {
        return commentRepository.findByArticleId(articleId, pageable);
    }
}
