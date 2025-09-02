package com.firstclub.membership.exception;

/**
 * Exception thrown when payment processing fails.
 */
public class PaymentFailedException extends RuntimeException {
    
    public PaymentFailedException(String message) {
        super(message);
    }
    
    public PaymentFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
