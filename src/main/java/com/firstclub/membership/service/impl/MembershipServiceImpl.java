package com.firstclub.membership.service.impl;

import com.firstclub.membership.dto.*;
import com.firstclub.membership.entity.*;
import com.firstclub.membership.repository.*;
import com.firstclub.membership.exception.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.annotation.Backoff;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class MembershipServiceImpl implements MembershipService {
        
    private final UserMembershipRepository membershipRepository;
    private final MembershipPlanRepository planRepository;
    private final MembershipTierRepository tierRepository;
    private final MembershipTransactionRepository transactionRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final TierEvaluationService tierEvaluationService;
    private final PaymentService paymentService;
    
    // User-level locks to prevent concurrent membership operations
    private final ConcurrentHashMap<Long, ReentrantLock> userLocks = new ConcurrentHashMap<>();
    
    @Transactional
    @Retryable(value = {OptimisticLockingException.class}, 
               maxAttempts = 3, 
               backoff = @Backoff(delay = 100))
    public MembershipResponse subscribeToPlan(SubscriptionRequest request) {
        Long userId = request.getUserId();
        Lock lock = getUserLock(userId);
        lock.lock();
        
        try {
            // Check for existing active membership
            membershipRepository.findActiveByUserId(userId)
                .ifPresent(m -> {
                    throw new MembershipAlreadyExistsException("User already has an active membership");
                });
            
            MembershipPlan plan = planRepository.findById(request.getPlanId())
                .orElseThrow(() -> new PlanNotFoundException("Plan not found"));
            
            MembershipTier tier = tierRepository.findById(request.getTierId())
                .orElseThrow(() -> new TierNotFoundException("Tier not found"));
            
            // Process payment asynchronously
            CompletableFuture<PaymentResult> paymentFuture = 
                CompletableFuture.supplyAsync(() -> 
                    paymentService.processPayment(userId, plan.getPrice())
                );
            
            // Create membership
            UserMembership membership = UserMembership.builder()
                .userId(userId)
                .plan(plan)
                .tier(tier)
                .status(UserMembership.MembershipStatus.PENDING_PAYMENT)
                .startDate(LocalDateTime.now())
                .endDate(calculateEndDate(LocalDateTime.now(), plan.getDuration()))
                .autoRenew(request.getAutoRenew())
                .build();
            
            membership = membershipRepository.save(membership);
            
            // Wait for payment result
            PaymentResult paymentResult = paymentFuture.join();
            
            if (paymentResult.isSuccess()) {
                membership.setStatus(UserMembership.MembershipStatus.ACTIVE);
                
                // Record transaction
                MembershipTransaction transaction = MembershipTransaction.builder()
                    .membership(membership)
                    .type(MembershipTransaction.TransactionType.SUBSCRIPTION)
                    .amount(plan.getPrice())
                    .newPlan(plan)
                    .newTier(tier)
                    .build();
                
                transactionRepository.save(transaction);
                
                // Publish event
                eventPublisher.publishEvent(new MembershipCreatedEvent(membership));
                
                log.info("Membership created successfully for user: {}", userId);
            } else {
                membership.setStatus(UserMembership.MembershipStatus.CANCELLED);
                throw new PaymentFailedException("Payment processing failed");
            }
            
            return toMembershipResponse(membershipRepository.save(membership));
            
        } finally {
            lock.unlock();
        }
    }
    
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public MembershipResponse upgradeMembership(Long userId, Long newPlanId, Long newTierId) {
        Lock lock = getUserLock(userId);
        lock.lock();
        
        try {
            UserMembership membership = membershipRepository.findActiveByUserId(userId)
                .orElseThrow(() -> new MembershipNotFoundException("No active membership found"));
            
            MembershipPlan newPlan = planRepository.findById(newPlanId)
                .orElseThrow(() -> new PlanNotFoundException("Plan not found"));
            
            MembershipTier newTier = tierRepository.findById(newTierId)
                .orElseThrow(() -> new TierNotFoundException("Tier not found"));
            
            // Validate upgrade (tier level should be higher)
            if (newTier.getLevel() <= membership.getTier().getLevel()) {
                throw new InvalidOperationException("Can only upgrade to a higher tier");
            }
            
            // Calculate prorated amount
            BigDecimal proratedAmount = calculateProratedAmount(
                membership, newPlan, newTier
            );
            
            // Process payment
            PaymentResult paymentResult = paymentService.processPayment(userId, proratedAmount);
            
            if (paymentResult.isSuccess()) {
                // Record transaction
                MembershipTransaction transaction = MembershipTransaction.builder()
                    .membership(membership)
                    .type(MembershipTransaction.TransactionType.UPGRADE)
                    .amount(proratedAmount)
                    .oldPlan(membership.getPlan())
                    .newPlan(newPlan)
                    .oldTier(membership.getTier())
                    .newTier(newTier)
                    .build();
                
                transactionRepository.save(transaction);
                
                // Update membership
                membership.setPlan(newPlan);
                membership.setTier(newTier);
                membership.setEndDate(calculateEndDate(membership.getStartDate(), newPlan.getDuration()));
                
                membership = membershipRepository.save(membership);
                
                // Publish event
                eventPublisher.publishEvent(new MembershipUpgradedEvent(membership));
                
                log.info("Membership upgraded for user: {}", userId);
                
                return toMembershipResponse(membership);
            } else {
                throw new PaymentFailedException("Upgrade payment failed");
            }
            
        } finally {
            lock.unlock();
        }
    }
    
    @Transactional
    public MembershipResponse cancelMembership(Long userId) {
        Lock lock = getUserLock(userId);
        lock.lock();
        
        try {
            UserMembership membership = membershipRepository.findActiveByUserId(userId)
                .orElseThrow(() -> new MembershipNotFoundException("No active membership found"));
            
            membership.setStatus(UserMembership.MembershipStatus.CANCELLED);
            membership.setAutoRenew(false);
            
            // Record transaction
            MembershipTransaction transaction = MembershipTransaction.builder()
                .membership(membership)
                .type(MembershipTransaction.TransactionType.CANCELLATION)
                .amount(BigDecimal.ZERO)
                .oldPlan(membership.getPlan())
                .oldTier(membership.getTier())
                .notes("User initiated cancellation")
                .build();
            
            transactionRepository.save(transaction);
            membership = membershipRepository.save(membership);
            
            // Publish event
            eventPublisher.publishEvent(new MembershipCancelledEvent(membership));
            
            log.info("Membership cancelled for user: {}", userId);
            
            return toMembershipResponse(membership);
            
        } finally {
            lock.unlock();
        }
    }
    
    @Cacheable(value = "membership", key = "#userId")
    public MembershipResponse getCurrentMembership(Long userId) {
        return membershipRepository.findActiveByUserId(userId)
            .map(this::toMembershipResponse)
            .orElseThrow(() -> new MembershipNotFoundException("No active membership found"));
    }
    
    @CacheEvict(value = "membership", key = "#userId")
    @Transactional
    public void evaluateAndUpdateTier(Long userId) {
        UserMembership membership = membershipRepository.findActiveByUserId(userId)
            .orElse(null);
        
        if (membership == null) {
            return;
        }
        
        MembershipTier currentTier = membership.getTier();
        MembershipTier newTier = tierEvaluationService.evaluateTier(userId, membership);
        
        if (!currentTier.getId().equals(newTier.getId())) {
            // Record tier change
            MembershipTransaction transaction = MembershipTransaction.builder()
                .membership(membership)
                .type(MembershipTransaction.TransactionType.TIER_CHANGE)
                .amount(BigDecimal.ZERO)
                .oldTier(currentTier)
                .newTier(newTier)
                .notes("Automatic tier evaluation")
                .build();
            
            transactionRepository.save(transaction);
            
            membership.setTier(newTier);
            membershipRepository.save(membership);
            
            // Publish event
            eventPublisher.publishEvent(new TierChangedEvent(membership, currentTier, newTier));
            
            log.info("Tier updated for user {} from {} to {}", 
                userId, currentTier.getName(), newTier.getName());
        }
    }
    
    private Lock getUserLock(Long userId) {
        return userLocks.computeIfAbsent(userId, k -> new ReentrantLock());
    }
    
    private LocalDateTime calculateEndDate(LocalDateTime startDate, MembershipPlan.PlanDuration duration) {
        return startDate.plusMonths(duration.getMonths());
    }
    
    private BigDecimal calculateProratedAmount(UserMembership membership, 
                                               MembershipPlan newPlan, 
                                               MembershipTier newTier) {
        // Implementation for prorated calculation
        LocalDateTime now = LocalDateTime.now();
        long remainingDays = java.time.Duration.between(now, membership.getEndDate()).toDays();
        long totalDays = java.time.Duration.between(membership.getStartDate(), membership.getEndDate()).toDays();
        
        BigDecimal remainingValue = membership.getPlan().getPrice()
            .multiply(BigDecimal.valueOf(remainingDays))
            .divide(BigDecimal.valueOf(totalDays), 2, BigDecimal.ROUND_HALF_UP);
        
        return newPlan.getPrice().subtract(remainingValue);
    }
    
    private MembershipResponse toMembershipResponse(UserMembership membership) {
        return MembershipResponse.builder()
            .membershipId(membership.getId())
            .userId(membership.getUserId())
            .planName(membership.getPlan().getName())
            .tierName(membership.getTier().getName())
            .status(membership.getStatus().toString())
            .startDate(membership.getStartDate())
            .endDate(membership.getEndDate())
            .autoRenew(membership.getAutoRenew())
            .benefits(membership.getTier().getBenefits())
            .build();
    }

}
