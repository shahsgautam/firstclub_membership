package com.firstclub.membership.service;

import com.firstclub.membership.dto.request.CreatePlanRequest;
import com.firstclub.membership.dto.request.UpdatePlanRequest;
import com.firstclub.membership.dto.response.PlanResponse;
import com.firstclub.membership.model.entity.MembershipPlan;

import java.util.List;

/**
 * Service interface for managing membership plans including creation,
 * updates, retrieval, and deactivation of plans.
 * 
 * This service provides comprehensive plan management functionality with
 * support for plan lifecycle management and validation.
 */
public interface MembershipPlanService {

    /**
     * Retrieves all active membership plans.
     * 
     * @return List of active membership plans
     */
    List<PlanResponse> getAllActivePlans();

    /**
     * Retrieves a specific membership plan by its ID.
     * 
     * @param planId The ID of the plan to retrieve
     * @return PlanResponse containing the plan details
     * @throws PlanNotFoundException if the plan doesn't exist
     */
    PlanResponse getPlanById(Long planId);

    /**
     * Creates a new membership plan.
     * 
     * @param request The plan creation request containing plan details
     * @return PlanResponse containing the created plan details
     * @throws InvalidPlanException if the plan data is invalid
     */
    PlanResponse createPlan(CreatePlanRequest request);

    /**
     * Updates an existing membership plan.
     * 
     * @param planId The ID of the plan to update
     * @param request The plan update request containing new details
     * @return PlanResponse containing the updated plan details
     * @throws PlanNotFoundException if the plan doesn't exist
     * @throws InvalidPlanException if the update data is invalid
     */
    PlanResponse updatePlan(Long planId, UpdatePlanRequest request);

    /**
     * Deactivates a membership plan.
     * 
     * @param planId The ID of the plan to deactivate
     * @throws PlanNotFoundException if the plan doesn't exist
     */
    void deactivatePlan(Long planId);

    /**
     * Retrieves a specific membership plan entity by its ID.
     * This method is intended for internal service use.
     * 
     * @param planId The ID of the plan to retrieve
     * @return MembershipPlan entity
     * @throws PlanNotFoundException if the plan doesn't exist
     */
    MembershipPlan getPlanEntityById(Long planId);
}
