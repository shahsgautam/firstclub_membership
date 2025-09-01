package com.firstclub.membership.dto.request;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePlanRequest {
    private String name;
    private String duration;
    
    @Positive(message = "Price must be positive")
    private BigDecimal price;
    
    private String description;
    private Boolean active;
}
