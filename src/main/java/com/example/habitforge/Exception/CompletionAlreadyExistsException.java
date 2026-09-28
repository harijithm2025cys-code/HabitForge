package com.example.habitforge.Exception;

public class CompletionAlreadyExistsException extends RuntimeException {

    public CompletionAlreadyExistsException(String message) {
        super(message);
    }
}
