package com.pch777.blog.exception.resource;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason="User not found.")
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String userType, UUID userId) {
        super(userType + " not found with id: " + userId);
    }

    public UserNotFoundException(String userType, String username) {
        super(userType + " not found with username: " + username);
    }
}
