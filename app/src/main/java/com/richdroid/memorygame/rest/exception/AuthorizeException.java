package com.richdroid.memorygame.rest.exception;

class AuthorizeException extends RuntimeException {

    public AuthorizeException() {
    }

    public AuthorizeException(String message) {
        super(message);
    }

    public AuthorizeException(Throwable cause) {
        super(cause);
    }

    public AuthorizeException(String message, Throwable cause) {
        super(message, cause);
    }
}