package com.pch777.blog.exception.handlers;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.*;

@ControllerAdvice
public class ValidationExceptionHandler {

    private static final String STATUS = "status";
    private static final String TIMESTAMP = "timestamp";

    @ExceptionHandler({MethodArgumentNotValidException.class, ConstraintViolationException.class})
    public ResponseEntity<Object> handleValidationExceptions(Exception ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        HttpStatus status = HttpStatus.BAD_REQUEST;
        body.put(TIMESTAMP, new Date());
        body.put(STATUS, status.value());

        List<String> errors = new ArrayList<>();

        if (ex instanceof MethodArgumentNotValidException validationException) {
            errors = validationException.getBindingResult()
                    .getFieldErrors()
                    .stream()
                    .map(e -> e.getField() + " - " + e.getDefaultMessage())
                    .toList();
        } else if (ex instanceof ConstraintViolationException constraintViolationException) {
            errors = constraintViolationException.getConstraintViolations()
                    .stream()
                    .map(v -> v.getPropertyPath() + " - " + v.getMessage())
                    .toList();
        }

        body.put("errors", errors);
        return new ResponseEntity<>(body, status);
    }

}
