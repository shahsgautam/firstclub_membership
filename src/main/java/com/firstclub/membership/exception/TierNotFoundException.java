package com.firstclub.membership.exception;

/**
 * Exception thrown when a membership tier is not found.
 */
public class TierNotFoundException extends RuntimeException {
    
    public TierNotFoundException(String message) {
        super(message);
    }
    
    public TierNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
