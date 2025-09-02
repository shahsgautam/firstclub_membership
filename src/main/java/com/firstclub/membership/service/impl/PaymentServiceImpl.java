package com.firstclub.membership.service.impl;

import com.firstclub.membership.dto.response.PaymentResult;
import com.firstclub.membership.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * MOCK of PaymentService for demonstration purposes.
 * In a real application, this would integrate with actual payment gateways.
 */
@Service
@Slf4j
public class PaymentServiceImpl implements PaymentService {
    
    @Override
    public PaymentResult processPayment(Long userId, BigDecimal amount) {
        log.info("Processing payment for user {}: amount {}", userId, amount);
        
        // Simulate payment processing
        try {
            // Simulate processing delay
            Thread.sleep(100);
            
            // For demo purposes, assume payment is successful
            String transactionId = UUID.randomUUID().toString();
            log.info("Payment successful for user {}: transaction ID {}", userId, transactionId);
            
            return PaymentResult.success(transactionId);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Payment processing interrupted for user {}", userId, e);
            return PaymentResult.failure("Payment processing interrupted");
        } catch (Exception e) {
            log.error("Payment processing failed for user {}", userId, e);
            return PaymentResult.failure("Payment processing failed: " + e.getMessage());
        }
    }
}
