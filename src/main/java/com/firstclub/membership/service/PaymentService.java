package com.firstclub.membership.service;

import java.math.BigDecimal;

import com.firstclub.membership.dto.response.PaymentResult;

/**
 * Service interface for payment processing operations.
 * This is a simplified interface for demonstration purposes.
 */
public interface PaymentService {
    
    /**
     * Processes a payment for a user.
     * 
     * @param userId The user ID
     * @param amount The payment amount
     * @return PaymentResult containing the payment status
     */
    PaymentResult processPayment(Long userId, BigDecimal amount);
}
