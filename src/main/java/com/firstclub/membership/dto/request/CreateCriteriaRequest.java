package com.firstclub.membership.dto.request;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCriteriaRequest {
    
    @NotNull(message = "Criteria type is required")
    private String type;
    
    private BigDecimal threshold;
    
    private String cohortName;
    
    @Positive(message = "Evaluation period must be positive")
    private Integer evaluationPeriodDays = 30;
}
