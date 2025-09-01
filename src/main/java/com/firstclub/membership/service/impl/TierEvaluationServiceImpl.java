package com.firstclub.membership.service.impl;

import com.firstclub.membership.entity.*;
import com.firstclub.membership.repository.*;
import com.firstclub.membership.integration.OrderServiceClient;
import com.firstclub.membership.service.TierEvaluationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TierEvaluationServiceImpl implements TierEvaluationService {
    
    private final MembershipTierRepository tierRepository;
    private final UserMembershipRepository membershipRepository;
    private final OrderServiceClient orderServiceClient;
    private final CohortService cohortService;
    
    @Override
    public MembershipTier evaluateTier(Long userId, UserMembership membership) {
        List<MembershipTier> availableTiers = tierRepository.findAllByActiveOrderByLevelDesc(true);
        
        for (MembershipTier tier : availableTiers) {
            if (meetsCriteria(userId, tier, membership)) {
                return tier;
            }
        }
        
        // Return lowest tier if no criteria met
        return availableTiers.get(availableTiers.size() - 1);
    }
    
    private boolean meetsCriteria(Long userId, MembershipTier tier, UserMembership membership) {
        List<TierCriteria> activeCriteria = tier.getCriteria().stream()
            .filter(TierCriteria::getActive)
            .collect(Collectors.toList());
        
        for (TierCriteria criteria : activeCriteria) {
            if (!evaluateCriteria(userId, criteria, membership)) {
                return false;
            }
        }
        
        return true;
    }
    
    private boolean evaluateCriteria(Long userId, TierCriteria criteria, UserMembership membership) {
        switch (criteria.getType()) {
            case MIN_ORDER_COUNT:
                return evaluateOrderCount(userId, criteria);
            
            case MIN_ORDER_VALUE:
                return evaluateOrderValue(userId, criteria);
            
            case USER_COHORT:
                return evaluateCohort(userId, criteria);
            
            case CUMULATIVE_SPENDING:
                return evaluateCumulativeSpending(userId, criteria);
            
            case MEMBERSHIP_DURATION:
                return evaluateMembershipDuration(membership, criteria);
            
            default:
                return false;
        }
    }
    
    private boolean evaluateOrderCount(Long userId, TierCriteria criteria) {
        LocalDateTime startDate = LocalDateTime.now().minusDays(criteria.getEvaluationPeriodDays());
        Integer orderCount = orderServiceClient.getOrderCount(userId, startDate);
        return orderCount >= criteria.getThreshold().intValue();
    }
    
    private boolean evaluateOrderValue(Long userId, TierCriteria criteria) {
        LocalDateTime startDate = LocalDateTime.now().minusDays(criteria.getEvaluationPeriodDays());
        BigDecimal totalValue = orderServiceClient.getTotalOrderValue(userId, startDate);
        return totalValue.compareTo(criteria.getThreshold()) >= 0;
    }
    
    private boolean evaluateCohort(Long userId, TierCriteria criteria) {
        return cohortService.isUserInCohort(userId, criteria.getCohortName());
    }
    
    private boolean evaluateCumulativeSpending(Long userId, TierCriteria criteria) {
        BigDecimal totalSpending = orderServiceClient.getCumulativeSpending(userId);
        return totalSpending.compareTo(criteria.getThreshold()) >= 0;
    }
    
    private boolean evaluateMembershipDuration(UserMembership membership, TierCriteria criteria) {
        long membershipDays = java.time.Duration.between(
            membership.getStartDate(), 
            LocalDateTime.now()
        ).toDays();
        return membershipDays >= criteria.getThreshold().intValue();
    }
    
    @Override
    @Scheduled(cron = "0 0 2 * * ?") // Run daily at 2 AM
    @Async
    public CompletableFuture<Void> evaluateAllMemberships() {
        log.info("Starting daily tier evaluation for all active memberships");
        
        List<UserMembership> activeMemberships = membershipRepository.findAllByStatus(
            UserMembership.MembershipStatus.ACTIVE
        );
        
        activeMemberships.parallelStream().forEach(membership -> {
            try {
                MembershipTier currentTier = membership.getTier();
                MembershipTier newTier = evaluateTier(membership.getUserId(), membership);
                
                if (!currentTier.getId().equals(newTier.getId())) {
                    membership.setTier(newTier);
                    membershipRepository.save(membership);
                    log.info("Updated tier for user {} from {} to {}", 
                        membership.getUserId(), currentTier.getName(), newTier.getName());
                }
            } catch (Exception e) {
                log.error("Error evaluating tier for user {}", membership.getUserId(), e);
            }
        });
        
        log.info("Completed daily tier evaluation");
        return CompletableFuture.completedFuture(null);
    }
}
