package com.pch777.blog.exception.handlers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice
public class SQLExceptionHandler {

    private static final String ERROR = "error";
    private static final String STATUS = "status";
    private static final String TIMESTAMP = "timestamp";

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
