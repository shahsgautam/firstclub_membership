package com.firstclub.membership.dto.request;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionRequest {
    
    @NotNull(message = "User ID is required")
    private Long userId;
    
    @NotNull(message = "Plan ID is required")
    private Long planId;
    
    @NotNull(message = "Tier ID is required")
    private Long tierId;
    
    private Boolean autoRenew = true;
    
    private String paymentMethodId;
}
