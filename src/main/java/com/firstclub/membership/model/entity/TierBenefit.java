package com.firstclub.membership.model.entity;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.persistence.*;
import com.firstclub.membership.model.enums.BenefitType;

import java.math.BigDecimal;

@Entity
@Table(name = "tier_benefits")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TierBenefit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tier_id", nullable = false)
    private MembershipTier tier;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BenefitType type;
    
    private BigDecimal value;
    
    private String description;
    
    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;
}
