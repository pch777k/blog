package com.pch777.blog.exception.resource;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason="Category not found.")
public class CategoryNotFoundException extends RuntimeException {
    public CategoryNotFoundException(UUID categoryId) {
        super("Category not found with id: " + categoryId);
    }

    public CategoryNotFoundException(String name) {
        super("Category not found with title url: " + name);
    }
}
