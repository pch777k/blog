package com.pch777.blog.exception;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.sql.SQLException;
import java.util.*;

@ControllerAdvice
public class CustomGlobalExceptionHandler {

    private static final String ERROR = "error";
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

    @ExceptionHandler({EntityNotFoundException.class, EntityExistsException.class})
    public ResponseEntity<Object> handleEntityException(Exception ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        if (ex instanceof EntityNotFoundException) {
            status = HttpStatus.NOT_FOUND;
        } else if (ex instanceof EntityExistsException) {
            status = HttpStatus.CONFLICT;
        }
        body.put(TIMESTAMP, new Date());
        body.put(STATUS, status.value());
        body.put(ERROR, ex.getMessage());
        return new ResponseEntity<>(body, status);
    }

    @ExceptionHandler(SQLException.class)
    public ResponseEntity<Object> handleSQLException(SQLException ex) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String errorMessage = "Internal Server Error.";

        if (ex.getMessage().contains("value too long for type character varying")) {
            status = HttpStatus.BAD_REQUEST;
            errorMessage = "Value is too long for the specified column.";
        } else if (ex.getMessage().contains("null value in column")) {
            status = HttpStatus.BAD_REQUEST;
            errorMessage = "Null value violates not-null constraint.";
        } else if (ex.getMessage().contains("duplicate key value violates unique constraint")) {
            status = HttpStatus.CONFLICT;
            errorMessage = "Duplicate key value violates unique constraint.";
        }

        Map<String, Object> body = new LinkedHashMap<>();
        body.put(TIMESTAMP, new Date());
        body.put(STATUS, status.value());
        body.put(ERROR, errorMessage);

        return new ResponseEntity<>(body, status);
    }

}
