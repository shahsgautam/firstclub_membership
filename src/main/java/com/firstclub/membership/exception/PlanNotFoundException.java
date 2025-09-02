package com.firstclub.membership.exception;

/**
 * Exception thrown when a membership plan is not found.
 */
public class PlanNotFoundException extends RuntimeException {
    
    public PlanNotFoundException(String message) {
        super(message);
    }
    
    public PlanNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
