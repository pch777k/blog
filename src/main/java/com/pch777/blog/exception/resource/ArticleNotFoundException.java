package com.pch777.blog.exception.resource;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason="Article not found.")
public class ArticleNotFoundException extends RuntimeException {
    public ArticleNotFoundException(UUID articleId) {
        super("Article not found with id: " + articleId);
    }

    public ArticleNotFoundException(String articleTitleUrl) {
        super("Article not found with title url: " + articleTitleUrl);
    }
}
