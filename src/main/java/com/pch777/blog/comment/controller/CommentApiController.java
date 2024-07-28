package com.pch777.blog.comment.controller;

import com.pch777.blog.comment.domain.model.Comment;
import com.pch777.blog.comment.dto.CommentDto;
import com.pch777.blog.comment.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.security.Principal;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/articles/{article-id}/comments")
public class CommentApiController {

    private final CommentService commentService;

    @GetMapping
    ResponseEntity<Page<Comment>> getComments(@PathVariable("article-id") UUID articleId, Pageable pageable) {
        Page<Comment> pageComments = commentService.getCommentsByArticleId(articleId, pageable);
        return ResponseEntity.ok(pageComments);
    }

    @GetMapping("{comment-id}")
    ResponseEntity<Comment> getComment(@PathVariable("article-id") UUID articleId,
                       @PathVariable("comment-id") UUID commentId) {
        Comment comment = commentService.getCommentById(commentId);
        return ResponseEntity.ok(comment);
    }

    @PostMapping
    public ResponseEntity<Comment> createComment(@PathVariable("article-id") UUID articleId,
                                                 @Valid @RequestBody CommentDto commentDto,
                                                 Principal principal) {
        Comment createdComment = commentService.createComment(articleId, commentDto, principal.getName());

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdComment.getId())
                .toUri();

        return ResponseEntity.created(location).body(createdComment);
    }

    @PutMapping("{comment-id}")
    public ResponseEntity<Comment> updateComment(@PathVariable("article-id") UUID articleId,
                                                 @PathVariable("comment-id") UUID commentId,
                                                 @Valid @RequestBody CommentDto commentDto,
                                                 Principal principal) {
        Comment updatedComment = commentService.updateComment(commentId, commentDto, principal.getName());
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(updatedComment);
    }

    @DeleteMapping("{comment-id}")
    public ResponseEntity<Void> deleteComment(@PathVariable("article-id") UUID articleId,
                                              @PathVariable("comment-id") UUID commentId,
                                              Principal principal) {
        commentService.deleteComment(commentId, principal.getName());
        return ResponseEntity.noContent().build();
    }
}