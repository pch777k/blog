package com.pch777.blog.exception.authentication;

import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@NoArgsConstructor
@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Incorrect old password")
public class IncorrectOldPasswordException extends RuntimeException {

    public IncorrectOldPasswordException(String message) {
        super(message);
    }
}

