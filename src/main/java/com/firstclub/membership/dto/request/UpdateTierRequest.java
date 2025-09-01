package com.firstclub.membership.dto.request;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.Positive;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTierRequest {
    private String name;
    
    @Positive(message = "Level must be positive")
    private Integer level;
    
    private String description;
    private Boolean active;
}
