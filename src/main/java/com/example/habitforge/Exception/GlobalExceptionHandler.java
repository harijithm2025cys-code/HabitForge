package com.example.habitforge.Exception;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private Map<String, Object> createErrorBody(int status, String message) {
        Map<String, Object> error = new LinkedHashMap<>();
        error.put("status", status);
        error.put("message", message);
        return error;
    }

    @ExceptionHandler(HabitNotFoundException.class)
    public ResponseEntity<String> handleHabitNotFound(HabitNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body("Data not found");
    }

    @ExceptionHandler(CompletionAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> handleCompletionAlreadyExists(CompletionAlreadyExistsException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(createErrorBody(HttpStatus.CONFLICT.value(), ex.getMessage()));
    }

    @ExceptionHandler(InvalidCompletionException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidCompletion(InvalidCompletionException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(createErrorBody(HttpStatus.BAD_REQUEST.value(), ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException ex) {
        String message = "Validation failed";
        if (ex.getBindingResult().getFieldError() != null) {
            message = ex.getBindingResult().getFieldError().getDefaultMessage();
        }
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(createErrorBody(HttpStatus.BAD_REQUEST.value(), message));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleNotReadable(HttpMessageNotReadableException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(createErrorBody(HttpStatus.BAD_REQUEST.value(), "Invalid JSON or request body"));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(createErrorBody(HttpStatus.BAD_REQUEST.value(), ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneralException(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorBody(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An unexpected error occurred"));
    }
}
