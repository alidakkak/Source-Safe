package com.network_applications.source_safe.exception;

public class EmailAlreadyExistsException extends RuntimeException{
    public EmailAlreadyExistsException(String message) {
        super(message);  // Pass the message to the parent class (RuntimeException)
    }
}
