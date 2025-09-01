package com.firstclub.membership.dto.response;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CriteriaResponse {
    private Long id;
    private String type;
    private BigDecimal threshold;
    private String cohortName;
    private Integer evaluationPeriodDays;
    private Boolean active;
}
