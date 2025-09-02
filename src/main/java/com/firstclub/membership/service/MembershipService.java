package com.firstclub.membership.service;

import com.firstclub.membership.dto.request.SubscriptionRequest;
import com.firstclub.membership.dto.response.BenefitResponse;
import com.firstclub.membership.dto.response.MembershipResponse;
import com.firstclub.membership.dto.response.TransactionResponse;

import java.util.List;

/**
 * Service interface for managing membership operations including subscriptions,
 * upgrades, cancellations, and tier evaluations.
 * 
 * This service provides comprehensive membership management functionality with
 * support for concurrent operations, payment processing, and automatic tier
 * evaluation based on user activity and criteria.
 */
public interface MembershipService {

    /**
     * Subscribes a user to a membership plan with the specified tier.
     * 
     * This method handles the complete subscription process including:
     * - Validation of existing memberships
     * - Plan and tier validation
     * - Asynchronous payment processing
     * - Membership creation with proper status management
     * - Transaction recording
     * - Event publishing for membership creation
     * 
     * The method uses optimistic locking with retry mechanism to handle
     * concurrent subscription attempts and user-level locks to prevent
     * race conditions for the same user.
     * 
     * @param request The subscription request containing user ID, plan ID, tier ID, and auto-renew preference
     * @return MembershipResponse containing the created membership details
     * @throws MembershipAlreadyExistsException if user already has an active membership
     * @throws PlanNotFoundException if the specified plan doesn't exist
     * @throws TierNotFoundException if the specified tier doesn't exist
     * @throws PaymentFailedException if payment processing fails
     */
    MembershipResponse subscribeToPlan(SubscriptionRequest request);

    /**
     * Upgrades a user's existing membership to a higher tier and/or plan.
     * 
     * This method handles membership upgrades with the following features:
     * - Validation that the new tier is higher than the current tier
     * - Prorated amount calculation for the upgrade
     * - Payment processing for the upgrade cost
     * - Transaction recording with old and new plan/tier information
     * - Membership update with new plan, tier, and extended end date
     * - Event publishing for membership upgrade
     * 
     * Uses repeatable read isolation to ensure data consistency during
     * the upgrade process and user-level locks for concurrency control.
     * 
     * @param userId The ID of the user whose membership is being upgraded
     * @param newPlanId The ID of the new plan to upgrade to
     * @param newTierId The ID of the new tier to upgrade to
     * @return MembershipResponse containing the updated membership details
     * @throws MembershipNotFoundException if no active membership is found for the user
     * @throws PlanNotFoundException if the new plan doesn't exist
     * @throws TierNotFoundException if the new tier doesn't exist
     * @throws InvalidOperationException if attempting to upgrade to a lower or same tier
     * @throws PaymentFailedException if upgrade payment processing fails
     */
    MembershipResponse upgradeMembership(Long userId, Long newPlanId, Long newTierId);

    /**
     * Downgrades a user's existing membership to a lower tier and/or plan.
     * 
     * This method handles membership downgrades with the following features:
     * - Validation that the new tier is lower than the current tier
     * - Prorated refund calculation for the downgrade
     * - Transaction recording with old and new plan/tier information
     * - Membership update with new plan, tier, and adjusted end date
     * - Event publishing for membership downgrade
     * 
     * @param userId The ID of the user whose membership is being downgraded
     * @param newPlanId The ID of the new plan to downgrade to
     * @param newTierId The ID of the new tier to downgrade to
     * @return MembershipResponse containing the updated membership details
     * @throws MembershipNotFoundException if no active membership is found for the user
     * @throws PlanNotFoundException if the new plan doesn't exist
     * @throws TierNotFoundException if the new tier doesn't exist
     * @throws InvalidOperationException if attempting to downgrade to a higher or same tier
     */
    MembershipResponse downgradeMembership(Long userId, Long newPlanId, Long newTierId);

    /**
     * Cancels a user's active membership.
     * 
     * This method handles membership cancellation by:
     * - Setting the membership status to CANCELLED
     * - Disabling auto-renewal
     * - Recording a cancellation transaction
     * - Publishing membership cancellation event
     * 
     * The cancellation is immediate and does not require payment processing.
     * User-level locks ensure thread safety during cancellation.
     * 
     * @param userId The ID of the user whose membership is being cancelled
     * @return MembershipResponse containing the cancelled membership details
     * @throws MembershipNotFoundException if no active membership is found for the user
     */
    MembershipResponse cancelMembership(Long userId);

    /**
     * Retrieves the current active membership for a user.
     * 
     * This method provides cached access to the user's current membership
     * information, including plan details, tier information, benefits,
     * and membership status.
     * 
     * The result is cached using the user ID as the cache key to improve
     * performance for frequently accessed membership data.
     * 
     * @param userId The ID of the user whose membership is being retrieved
     * @return MembershipResponse containing the current membership details
     * @throws MembershipNotFoundException if no active membership is found for the user
     */
    MembershipResponse getCurrentMembership(Long userId);

    /**
     * Evaluates and updates a user's tier based on current criteria and activity.
     * 
     * This method performs automatic tier evaluation by:
     * - Retrieving the user's current membership and tier
     * - Evaluating eligibility for tier changes based on configured criteria
     * - Recording tier change transactions when applicable
     * - Updating the membership with the new tier
     * - Publishing tier change events
     * 
     * The method uses cache eviction to ensure that subsequent calls to
     * getCurrentMembership return updated tier information.
     * 
     * @param userId The ID of the user whose tier is being evaluated
     */
    void evaluateAndUpdateTier(Long userId);

    /**
     * Retrieves the benefits available to a user based on their current membership.
     * 
     * @param userId The ID of the user
     * @return List of benefits available to the user
     * @throws MembershipNotFoundException if no active membership is found for the user
     */
    List<BenefitResponse> getUserBenefits(Long userId);

    /**
     * Retrieves the transaction history for a user's membership.
     * 
     * @param userId The ID of the user
     * @param page The page number (0-based)
     * @param size The page size
     * @return List of transactions for the user
     * @throws MembershipNotFoundException if no membership is found for the user
     */
    List<TransactionResponse> getTransactionHistory(Long userId, int page, int size);
}