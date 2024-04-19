package com.pch777.blog.comment.controller;

import com.pch777.blog.comment.domain.model.Comment;
import com.pch777.blog.comment.dto.CommentDto;
import com.pch777.blog.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/articles/{article-id}/comments")
public class CommentApiController {

    private final CommentService commentService;

    @GetMapping
    Page<Comment> getComments(@PathVariable("article-id") UUID articleId, Pageable pageable){
        return commentService.getCommentsByArticleId(articleId, pageable);
    }

    @GetMapping("{comment-id}")
    Comment getComment(@PathVariable("article-id") UUID articleId, @PathVariable("comment-id") UUID commentId){
        return commentService.getComment(commentId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    Comment createComment(@PathVariable("article-id") UUID articleId, @RequestBody CommentDto commentDto){
        return commentService.createComment(articleId, commentDto);
    }

    @PutMapping("{comment-id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    Comment updateComment(@PathVariable("article-id") UUID articleId, @PathVariable("comment-id") UUID commentId,
                        @RequestBody CommentDto commentDto){
        return commentService.updateComment(commentId, commentDto);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("{comment-id}")
    void deleteComment(@PathVariable("comment-id") UUID commentId){
        commentService.deleteComment(commentId);
    }
}