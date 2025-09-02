package com.firstclub.membership.exception;

/**
 * Exception thrown when a user already has an active membership.
 */
public class MembershipAlreadyExistsException extends RuntimeException {
    
    public MembershipAlreadyExistsException(String message) {
        super(message);
    }
    
    public MembershipAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
