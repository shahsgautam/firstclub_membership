package com.firstclub.membership.dto.response;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponse {
    private Long id;
    private String type;
    private BigDecimal amount;
    private String oldPlan;
    private String newPlan;
    private String oldTier;
    private String newTier;
    private String notes;
    private LocalDateTime transactionDate;
}
