package com.pch777.blog.comment.service;

import com.pch777.blog.article.domain.model.Article;
import com.pch777.blog.article.domain.repository.ArticleRepository;
import com.pch777.blog.comment.domain.model.Comment;
import com.pch777.blog.comment.domain.repository.CommentRepository;
import com.pch777.blog.comment.dto.CommentDto;
import com.pch777.blog.user.domain.model.User;
import com.pch777.blog.user.domain.repository.UserRepository;
import com.pch777.blog.user.service.UserActivityService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static com.pch777.blog.user.domain.model.Operation.*;

@RequiredArgsConstructor
@Service
public class CommentService {

    public static final String USER_NOT_FOUND_WITH_USERNAME = "User not found with username: ";
    private final CommentRepository commentRepository;
    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;
    private final UserActivityService userActivityService;

    @Transactional(readOnly = true)
    public List<Comment> getComments(UUID articleId) {
        return commentRepository.findByArticleId(articleId);
    }

    @Transactional(readOnly = true)
    public Comment getCommentById(UUID id) {
        return findCommentById(id);
    }

    @Transactional
    public Comment createComment(UUID articleId, CommentDto commentDto, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND_WITH_USERNAME + username));
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new EntityNotFoundException("Article not found with id: " + articleId));
        Comment comment = new Comment(commentDto.getContent(), user);
        article.addComment(comment);
        userActivityService.addCommentOperation(user, CREATE_COMMENT, comment);
        return commentRepository.save(comment);
    }

    @Transactional
    public Comment updateComment(UUID commentId, CommentDto commentDto, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND_WITH_USERNAME + username));
        Comment comment = findCommentById(commentId);
        comment.setContent(commentDto.getContent());
        userActivityService.addCommentOperation(user, EDIT_COMMENT, comment);
        return commentRepository.save(comment);
    }

    @Transactional
    public void deleteComment(UUID commentId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND_WITH_USERNAME + username));
        userActivityService.addCommentOperation(user, DELETE_COMMENT, findCommentById(commentId));
        commentRepository.deleteById(commentId);
    }

    public Comment findCommentById(UUID id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found with id: " + id));
    }

    public Page<Comment> getCommentsByArticleId(UUID articleId, Pageable pageable) {
        return commentRepository.findByArticleId(articleId, pageable);
    }
}
