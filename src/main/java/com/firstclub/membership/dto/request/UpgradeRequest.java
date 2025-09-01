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
public class UpgradeRequest {
    
    @NotNull(message = "New plan ID is required")
    private Long newPlanId;
    
    @NotNull(message = "New tier ID is required")
    private Long newTierId;
}
