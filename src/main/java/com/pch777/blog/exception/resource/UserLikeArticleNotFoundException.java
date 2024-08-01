package com.pch777.blog.exception.resource;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason="Like not found.")
public class UserLikeArticleNotFoundException extends RuntimeException {
    public UserLikeArticleNotFoundException(UUID articleId, String username) {
        super("Like by user: " + username + " not found for article: " + articleId);
    }

}
