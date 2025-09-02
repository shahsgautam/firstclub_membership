// package com.firstclub.membership.service;

// import com.firstclub.membership.model.entity.UserMembership;
// import com.firstclub.membership.model.entity.MembershipTier;

// import java.util.concurrent.CompletableFuture;

// /**
//  * Service interface for evaluating and managing membership tier eligibility
//  * based on various criteria such as order count, spending, user cohorts,
//  * and membership duration.
//  * 
//  * This service provides comprehensive tier evaluation functionality including:
//  * - Individual user tier evaluation
//  * - Scheduled batch evaluation for all active memberships
//  * - Support for multiple evaluation criteria types
//  * - Integration with external services for order and cohort data
//  */
// public interface TierEvaluationService {

//     /**
//      * Evaluates the appropriate tier for a user based on their current
//      * membership and activity criteria.
//      * 
//      * This method analyzes the user's eligibility for different membership
//      * tiers by evaluating various criteria including:
//      * - Minimum order count within evaluation period
//      * - Minimum order value within evaluation period
//      * - User cohort membership
//      * - Cumulative spending over time
//      * - Membership duration
//      * 
//      * The evaluation process iterates through available tiers in descending
//      * order by level, returning the highest tier for which the user meets
//      * all criteria. If no criteria are met, returns the lowest available tier.
//      * 
//      * @param userId The ID of the user whose tier is being evaluated
//      * @param membership The user's current membership information
//      * @return MembershipTier the appropriate tier for the user based on evaluation
//      */
//     MembershipTier evaluateTier(Long userId, UserMembership membership);

//     /**
//      * Performs scheduled evaluation of all active memberships to update
//      * tiers based on current criteria and user activity.
//      * 
//      * This method is designed to run as a scheduled task (typically daily)
//      * and performs the following operations:
//      * - Retrieves all active memberships from the system
//      * - Evaluates each user's tier eligibility in parallel
//      * - Updates membership records when tier changes are detected
//      * - Logs tier change events for audit purposes
//      * 
//      * The method uses parallel processing to efficiently handle large
//      * numbers of memberships and includes error handling to prevent
//      * individual user evaluation failures from affecting the entire batch.
//      * 
//      * @return CompletableFuture<Void> a future that completes when the evaluation is finished
//      */
//     CompletableFuture<Void> evaluateAllMemberships();
// }
