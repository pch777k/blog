package com.pch777.blog.exception.resource;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason="Tag not found.")
public class TagNotFoundException extends RuntimeException {
    public TagNotFoundException(UUID tagId) {
        super("Tag not found with id: " + tagId);
    }

    public TagNotFoundException(String name) {
        super("Tag not found with name: " + name);
    }
}
