package com.firstclub.membership.dto.response;

public class PaymentResult {
    private final boolean success;
    private final String transactionId;
    private final String message;
    
    public PaymentResult(boolean success, String transactionId, String message) {
        this.success = success;
        this.transactionId = transactionId;
        this.message = message;
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public String getTransactionId() {
        return transactionId;
    }
    
    public String getMessage() {
        return message;
    }
    
    public static PaymentResult success(String transactionId) {
        return new PaymentResult(true, transactionId, "Payment successful");
    }
    
    public static PaymentResult failure(String message) {
        return new PaymentResult(false, null, message);
    }
}
