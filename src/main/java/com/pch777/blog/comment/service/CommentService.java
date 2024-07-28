package com.pch777.blog.comment.service;

import com.pch777.blog.article.domain.model.Article;
import com.pch777.blog.article.domain.repository.ArticleRepository;
import com.pch777.blog.comment.domain.model.Comment;
import com.pch777.blog.comment.domain.repository.CommentRepository;
import com.pch777.blog.comment.dto.CommentDto;
import com.pch777.blog.exception.ArticleNotFoundException;
import com.pch777.blog.exception.CommentNotFoundException;
import com.pch777.blog.exception.ForbiddenException;
import com.pch777.blog.exception.UnauthorizedException;
import com.pch777.blog.identity.user.domain.model.Role;
import com.pch777.blog.notification.domain.model.NotificationType;
import com.pch777.blog.notification.service.NotificationService;
import com.pch777.blog.identity.user.domain.model.User;
import com.pch777.blog.identity.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserService userService;
    private final NotificationService notificationService;
    private final ArticleRepository articleRepository;

    @Transactional(readOnly = true)
    public Page<Comment> getComments(String search, Pageable pageable) {
        return commentRepository.findAllByContentContainingIgnoreCase(search, pageable);
    }

    @Transactional(readOnly = true)
    public List<Comment> getComments(UUID articleId) {
        return commentRepository.findByArticleId(articleId);
    }

    @Transactional(readOnly = true)
    public Page<Comment> getCommentsByArticleId(UUID articleId, Pageable pageable) {
        return commentRepository.findByArticleId(articleId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Comment> getCommentsByUserId(UUID userId, String search, Pageable pageable) {
        if(search == null) {
            return commentRepository.findByUserId(userId, pageable);
        } else {
            return commentRepository.findByUserIdAndContentContainingIgnoreCase(userId, search, pageable);
        }
    }

    @Transactional(readOnly = true)
    public Comment getCommentById(UUID id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new CommentNotFoundException(id));
    }

    @Transactional
    public Comment createComment(UUID articleId, CommentDto commentDto, String username) {
        User user = userService.getUserByUsername(username);
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ArticleNotFoundException(articleId));
        Comment comment = new Comment(commentDto.getContent(), user);
        article.addComment(comment);
        notificationService.createNotification(user, "You commented article " + article.getTitle(), NotificationType.COMMENT);
        notificationService.createNotification(article.getAuthor(), user.getFullName() + " commented article " + article.getTitle(), NotificationType.COMMENT);
        return commentRepository.save(comment);
    }

    @Transactional
    public Comment updateComment(UUID commentId, CommentDto commentDto, String username) {
        Comment comment = validateAndProcessComment(commentId, username);
        comment.setContent(commentDto.getContent());
        return commentRepository.save(comment);
    }

    @Transactional
    public void deleteComment(UUID commentId, String username) {
        validateAndProcessComment(commentId, username);
        commentRepository.deleteById(commentId);
    }

    private Comment validateAndProcessComment(UUID commentId, String username) {
        if(username == null) {
            throw new UnauthorizedException("Unauthorized access");
        }
        User user = userService.getUserByUsername(username);
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException(commentId));
        if (!user.getId().equals(comment.getUser().getId()) && !user.getRole().equals(Role.ADMIN)) {
            throw new ForbiddenException("Access denied");
        }
        return comment;
    }

    public boolean canEditOrDeleteComment(UUID userId, UUID commentId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null &&
                authentication.getPrincipal() instanceof UserDetails &&
                commentRepository.existsByUser_IdAndId(userId, commentId);
    }

    public List<Comment> getCommentsByArticleTitleUrl(String titleUrl) {
        return commentRepository.findByArticleTitleUrl(titleUrl);
    }
}
