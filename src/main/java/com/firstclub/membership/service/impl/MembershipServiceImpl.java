package com.firstclub.membership.service.impl;

import com.firstclub.membership.dto.request.SubscriptionRequest;
import com.firstclub.membership.dto.response.BenefitResponse;
import com.firstclub.membership.dto.response.MembershipResponse;
import com.firstclub.membership.dto.response.PaymentResult;
import com.firstclub.membership.dto.response.TransactionResponse;
import com.firstclub.membership.exception.*;
import com.firstclub.membership.mapper.MembershipMapper;
import com.firstclub.membership.model.entity.*;
import com.firstclub.membership.model.enums.MembershipStatus;
import com.firstclub.membership.model.enums.PlanDuration;
import com.firstclub.membership.model.enums.TransactionType;
import com.firstclub.membership.repository.UserMembershipRepository;
import com.firstclub.membership.repository.MembershipTransactionRepository;
import com.firstclub.membership.service.MembershipService;
import com.firstclub.membership.service.MembershipPlanService;
import com.firstclub.membership.service.MembershipTierService;
import com.firstclub.membership.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
// import org.springframework.context.ApplicationEventPublisher;
// import com.firstclub.membership.service.TierEvaluationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.annotation.Backoff;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Duration;
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
    private final MembershipPlanService planService;
    private final MembershipTierService tierService;
    private final MembershipTransactionRepository transactionRepository;
    // private final ApplicationEventPublisher eventPublisher;
    // private final TierEvaluationService tierEvaluationService;
    private final PaymentService paymentService;
    private final MembershipMapper membershipMapper;

    // User-level locks to prevent concurrent membership operations
    private final ConcurrentHashMap<Long, ReentrantLock> userLocks = new ConcurrentHashMap<>();

    @Transactional
    @Retryable(value = {
            ObjectOptimisticLockingFailureException.class }, maxAttempts = 3, backoff = @Backoff(delay = 100))
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

            MembershipPlan plan = planService.getPlanEntityById(request.getPlanId());

            MembershipTier tier = tierService.getTierEntityById(request.getTierId());

            // Process payment asynchronously
            CompletableFuture<PaymentResult> paymentFuture = CompletableFuture
                    .supplyAsync(() -> paymentService.processPayment(userId, plan.getPrice()));

            // Create membership
            UserMembership membership = UserMembership.builder()
                    .userId(userId)
                    .plan(plan)
                    .tier(tier)
                    .status(MembershipStatus.PENDING_PAYMENT)
                    .startDate(LocalDateTime.now())
                    .endDate(calculateEndDate(LocalDateTime.now(), plan.getDuration()))
                    .autoRenew(request.getAutoRenew())
                    .build();

            membership = membershipRepository.save(membership);

            // MOCK: Wait for payment result
            PaymentResult paymentResult = paymentFuture.join();

            if (paymentResult.isSuccess()) {
                membership.setStatus(MembershipStatus.ACTIVE);

                // Record transaction
                MembershipTransaction transaction = MembershipTransaction.builder()
                        .membership(membership)
                        .type(TransactionType.SUBSCRIPTION)
                        .amount(plan.getPrice())
                        .newPlan(plan)
                        .newTier(tier)
                        .build();

                transactionRepository.save(transaction);

                // Publish event
                // eventPublisher.publishEvent(new MembershipCreatedEvent(membership));

                log.info("Membership created successfully for user: {}", userId);
            } else {
                membership.setStatus(MembershipStatus.CANCELLED);
                throw new PaymentFailedException("Payment processing failed");
            }

            return membershipMapper.toMembershipResponse(membershipRepository.save(membership));

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

            MembershipPlan newPlan = planService.getPlanEntityById(newPlanId);

            MembershipTier newTier = tierService.getTierEntityById(newTierId);

            // Validate upgrade (tier level should be higher)
            if (newTier.getLevel() <= membership.getTier().getLevel()) {
                throw new InvalidOperationException("Can only upgrade to a higher tier");
            }

            // Calculate prorated amount
            BigDecimal proratedAmount = calculateProratedAmount(
                    membership, newPlan, newTier);

            // Process payment
            PaymentResult paymentResult = paymentService.processPayment(userId, proratedAmount);

            if (paymentResult.isSuccess()) {
                // Record transaction
                MembershipTransaction transaction = MembershipTransaction.builder()
                        .membership(membership)
                        .type(TransactionType.UPGRADE)
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
                // eventPublisher.publishEvent(new MembershipUpgradedEvent(membership));

                log.info("Membership upgraded for user: {}", userId);

                return membershipMapper.toMembershipResponse(membership);
            } else {
                throw new PaymentFailedException("Upgrade payment failed");
            }

        } finally {
            lock.unlock();
        }
    }

    @Override
    public MembershipResponse downgradeMembership(Long userId, Long newPlanId, Long newTierId) {
        log.info("Downgrading membership for user: {}", userId);
        Lock lock = getUserLock(userId);
        lock.lock();

        try {
            UserMembership membership = membershipRepository.findActiveByUserId(userId)
                    .orElseThrow(() -> new MembershipNotFoundException("No active membership found"));

            MembershipPlan newPlan = planService.getPlanEntityById(newPlanId);

            MembershipTier newTier = tierService.getTierEntityById(newTierId);

            // Validate downgrade (tier level should be lower)
            if (newTier.getLevel() >= membership.getTier().getLevel()) {
                throw new InvalidOperationException("Can only downgrade to a lower tier");
            }

            // Calculate new end date based on current plan's remaining time
            LocalDateTime currentEndDate = membership.getEndDate();
            LocalDateTime newEndDate = calculateEndDate(currentEndDate, newPlan.getDuration());

            // Record transaction for plan change
            MembershipTransaction transaction = MembershipTransaction.builder()
                    .membership(membership)
                    .type(TransactionType.DOWNGRADE)
                    .amount(BigDecimal.ZERO) // No immediate charge
                    .oldPlan(membership.getPlan())
                    .newPlan(newPlan)
                    .oldTier(membership.getTier())
                    .newTier(newTier)
                    .notes("Plan downgrade - effective after current plan ends")
                    .build();

            transactionRepository.save(transaction);

            // Update membership with new plan/tier and extended end date
            membership.setPlan(newPlan);
            membership.setTier(newTier);
            membership.setEndDate(newEndDate);

            membership = membershipRepository.save(membership);

            log.info("Membership downgraded for user: {} - new plan effective from {}", userId, currentEndDate);

            return membershipMapper.toMembershipResponse(membership);

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

            membership.setStatus(MembershipStatus.CANCELLED);
            membership.setAutoRenew(false);

            // Record transaction
            MembershipTransaction transaction = MembershipTransaction.builder()
                    .membership(membership)
                    .type(TransactionType.CANCELLATION)
                    .amount(BigDecimal.ZERO)
                    .oldPlan(membership.getPlan())
                    .oldTier(membership.getTier())
                    .notes("User initiated cancellation")
                    .build();

            transactionRepository.save(transaction);
            membership = membershipRepository.save(membership);

            // Publish event
            // eventPublisher.publishEvent(new MembershipCancelledEvent(membership));

            log.info("Membership cancelled for user: {}", userId);

            return membershipMapper.toMembershipResponse(membership);

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
        // MembershipTier newTier = tierEvaluationService.evaluateTier(userId,
        // membership);

        // if (!currentTier.getId().equals(newTier.getId())) {
        // // Record tier change
        // MembershipTransaction transaction = MembershipTransaction.builder()
        // .membership(membership)
        // .type(TransactionType.TIER_CHANGE)
        // .amount(BigDecimal.ZERO)
        // .oldTier(currentTier)
        // .newTier(newTier)
        // .notes("Automatic tier evaluation")
        // .build();

        // transactionRepository.save(transaction);

        // membership.setTier(newTier);
        // membershipRepository.save(membership);

        // // Publish event
        // eventPublisher.publishEvent(new TierChangedEvent(membership, currentTier,
        // newTier));

        // log.info("Tier updated for user {} from {} to {}",
        // userId, currentTier.getName(), newTier.getName());
        // }

        log.info("Tier will be automatically updated for userID: {} from {} to suitable tier after some time",
                userId, currentTier.getName());
    }

    @Override
    public List<BenefitResponse> getUserBenefits(Long userId) {
        log.debug("Retrieving benefits for user: {}", userId);
        UserMembership membership = membershipRepository.findActiveByUserId(userId)
                .orElseThrow(() -> new MembershipNotFoundException("No active membership found for user: " + userId));

        return membershipMapper.mapBenefits(membership.getTier().getBenefits());
    }

    @Override
    public List<TransactionResponse> getTransactionHistory(Long userId, int page, int size) {
        log.debug("Retrieving transaction history for user: {}", userId);
        UserMembership membership = membershipRepository.findActiveByUserId(userId)
                .orElseThrow(() -> new MembershipNotFoundException("No active membership found for user: " + userId));

        PageRequest pageRequest = PageRequest.of(page, size);
        Page<MembershipTransaction> transactionPage = transactionRepository
                .findByMembershipIdOrderByTransactionDateDesc(membership.getId(), pageRequest);
        return membershipMapper.toTransactionResponseList(transactionPage.getContent());
    }

    private Lock getUserLock(Long userId) {
        return userLocks.computeIfAbsent(userId, k -> new ReentrantLock());
    }

    private LocalDateTime calculateEndDate(LocalDateTime startDate, PlanDuration duration) {
        return startDate.plusMonths(duration.getMonths());
    }

    private BigDecimal calculateProratedAmount(UserMembership membership,
            MembershipPlan newPlan,
            MembershipTier newTier) {
        // Implementation for prorated calculation
        LocalDateTime now = LocalDateTime.now();
        long remainingDays = Duration.between(now, membership.getEndDate()).toDays();
        long totalDays = Duration.between(membership.getStartDate(), membership.getEndDate()).toDays();

        BigDecimal remainingValue = membership.getPlan().getPrice()
                .multiply(BigDecimal.valueOf(remainingDays))
                .divide(BigDecimal.valueOf(totalDays), 2, BigDecimal.ROUND_HALF_UP);

        return newPlan.getPrice().subtract(remainingValue);
    }

    private MembershipResponse toMembershipResponse(UserMembership membership) {
        return membershipMapper.toMembershipResponse(membership);
    }

}
