package com.firstclub.membership.dto.response;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TierResponse {
    private Long id;
    private String name;
    private Integer level;
    private String description;
    private List<BenefitResponse> benefits;
    private List<CriteriaResponse> criteria;
    private Boolean active;
}

