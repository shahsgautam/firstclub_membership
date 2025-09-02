package com.firstclub.membership.exception;

/**
 * Exception thrown when a user membership is not found.
 */
public class MembershipNotFoundException extends RuntimeException {
    
    public MembershipNotFoundException(String message) {
        super(message);
    }
    
    public MembershipNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
