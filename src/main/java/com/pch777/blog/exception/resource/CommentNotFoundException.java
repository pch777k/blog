package com.pch777.blog.exception.resource;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason="Comment not found.")
public class CommentNotFoundException extends RuntimeException {
    public CommentNotFoundException(UUID commentId) {
        super("Comment not found with id: " + commentId);
    }
}
