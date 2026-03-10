package com.amalitech.quickpoll.exceptionHandler;

public class DuplicateResourceException extends RuntimeException{
    public DuplicateResourceException(String message) {
        super(message);
    }
}
