package com.pch777.blog.exception;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


@ControllerAdvice
public class CustomGlobalExceptionHandler {

    private static final String ERROR = "error";
    private static final String STATUS = "status";
    private static final String TIMESTAMP = "timestamp";

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        HttpStatus status = HttpStatus.BAD_REQUEST;
        body.put(TIMESTAMP, new Date());
        body.put(STATUS, status.value());
        List<String> errors = ex
                .getConstraintViolations()
                .stream()
                .map(x -> x.getPropertyPath().toString() + " - " + x.getMessage())
                .toList();
        body.put("errors", errors);
        return new ResponseEntity<>(body, status);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Object> handleEntityNotFoundException(EntityNotFoundException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        HttpStatus status = HttpStatus.NOT_FOUND;
        body.put(TIMESTAMP, new Date());
        body.put(STATUS, status.value());
        body.put(ERROR, ex.getMessage());
        return new ResponseEntity<>(body, status);
    }
}
