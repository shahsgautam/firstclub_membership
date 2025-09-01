package com.firstclub.membership.model.entity;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.persistence.*;
import com.firstclub.membership.model.enums.CriteriaType;

import java.math.BigDecimal;

@Entity
@Table(name = "tier_criteria")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TierCriteria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tier_id", nullable = false)
    private MembershipTier tier;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CriteriaType type;
    
    private BigDecimal threshold;
    
    private String cohortName; // For cohort-based criteria
    
    @Column(name = "evaluation_period_days")
    @Builder.Default
    private Integer evaluationPeriodDays = 30; // Default to monthly evaluation
    
    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;
}
