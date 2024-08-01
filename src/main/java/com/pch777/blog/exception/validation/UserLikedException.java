package com.pch777.blog.exception.validation;

import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@NoArgsConstructor
@ResponseStatus(value = HttpStatus.CONFLICT, reason="User already liked the article")
public class UserLikedException extends RuntimeException {

    public UserLikedException(String message) {
        super(message);
    }
}
