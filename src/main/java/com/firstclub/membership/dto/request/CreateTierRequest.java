package com.firstclub.membership.dto.request;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateTierRequest {
    
    @NotBlank(message = "Tier name is required")
    private String name;
    
    @NotNull(message = "Level is required")
    @Positive(message = "Level must be positive")
    private Integer level;
    
    private String description;
}
