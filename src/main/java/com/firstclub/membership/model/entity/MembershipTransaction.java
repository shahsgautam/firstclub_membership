package com.firstclub.membership.model.entity;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import jakarta.persistence.*;
import com.firstclub.membership.model.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "membership_transactions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MembershipTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "membership_id", nullable = false)
    private UserMembership membership;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;
    
    @Column(nullable = false)
    private BigDecimal amount;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "old_plan_id")
    private MembershipPlan oldPlan;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "new_plan_id")
    private MembershipPlan newPlan;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "old_tier_id")
    private MembershipTier oldTier;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "new_tier_id")
    private MembershipTier newTier;
    
    private String notes;
    
    @CreationTimestamp
    @Column(name = "transaction_date", nullable = false)
    private LocalDateTime transactionDate;
}