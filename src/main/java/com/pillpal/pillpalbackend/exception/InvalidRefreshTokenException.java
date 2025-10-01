package com.pillpal.pillpalbackend.exception;

public class InvalidRefreshTokenException extends RuntimeException {
    public InvalidRefreshTokenException() {
        super("Invalid or expired refresh token");
    }

    public InvalidRefreshTokenException(String message) {
        super(message);
    }
}
