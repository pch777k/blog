package com.pch777.blog.exception.handlers;

import com.pch777.blog.exception.authentication.ForbiddenException;
import com.pch777.blog.exception.authentication.IncorrectOldPasswordException;
import com.pch777.blog.exception.authentication.UnauthorizedException;
import com.pch777.blog.exception.resource.*;
import com.pch777.blog.exception.validation.UserLikedException;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice
public class CustomGlobalExceptionHandler {

    @ExceptionHandler({
            ArticleNotFoundException.class,
            CategoryNotFoundException.class,
            EntityNotFoundException.class,
            CommentNotFoundException.class,
            UserLikeArticleNotFoundException.class,
            UserNotFoundException.class,
            TagNotFoundException.class})
    public Object handleNotFoundExceptions(Exception ex, HttpServletRequest request, Model model) {
        if (isApiRequest(request)) {
            return buildResponseEntity(ex, HttpStatus.NOT_FOUND);
        } else {
            return "error/404";
        }
    }

    @ExceptionHandler(ForbiddenException.class)
    public Object handleForbiddenException(ForbiddenException ex, HttpServletRequest request, Model model) {
        if (isApiRequest(request)) {
            return buildResponseEntity(ex, HttpStatus.FORBIDDEN);
        } else {
            return "error/403";
        }
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<Map<String, Object>> handleUnauthorizedException(UnauthorizedException ex) {
        return buildResponseEntity(ex, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler({
            IncorrectOldPasswordException.class,
            HttpMessageNotReadableException.class})
    public ResponseEntity<Map<String, Object>> handleBadRequestExceptions(Exception ex) {
        return buildResponseEntity(ex, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({
            EntityExistsException.class,
            UserLikedException.class})
    public ResponseEntity<Map<String, Object>> handleConflictExceptions(Exception ex) {
        return buildResponseEntity(ex, HttpStatus.CONFLICT);
    }

    private ResponseEntity<Map<String, Object>> buildResponseEntity(Exception ex, HttpStatus status) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", new Date());
        body.put("status", status.value());
        body.put("error", ex.getMessage());
        return new ResponseEntity<>(body, status);
    }

    private boolean isApiRequest(HttpServletRequest request) {
        return request.getRequestURI().startsWith("/api");
    }

}
