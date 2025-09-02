package com.firstclub.membership.service;

import com.firstclub.membership.dto.request.CreateBenefitRequest;
import com.firstclub.membership.dto.request.CreateCriteriaRequest;
import com.firstclub.membership.dto.request.CreateTierRequest;
import com.firstclub.membership.dto.request.UpdateTierRequest;
import com.firstclub.membership.dto.response.BenefitResponse;
import com.firstclub.membership.dto.response.CriteriaResponse;
import com.firstclub.membership.dto.response.TierResponse;
import com.firstclub.membership.model.entity.MembershipTier;

import java.util.List;

/**
 * Service interface for managing membership tiers including creation,
 * updates, retrieval, and management of tier benefits and criteria.
 * 
 * This service provides comprehensive tier management functionality with
 * support for tier lifecycle management, benefits, and evaluation criteria.
 */
public interface MembershipTierService {

    /**
     * Retrieves all active membership tiers.
     * 
     * @return List of active membership tiers
     */
    List<TierResponse> getAllActiveTiers();

    /**
     * Retrieves a specific membership tier by its ID.
     * 
     * @param tierId The ID of the tier to retrieve
     * @return TierResponse containing the tier details
     * @throws TierNotFoundException if the tier doesn't exist
     */
    TierResponse getTierById(Long tierId);

    /**
     * Creates a new membership tier.
     * 
     * @param request The tier creation request containing tier details
     * @return TierResponse containing the created tier details
     * @throws InvalidTierException if the tier data is invalid
     */
    TierResponse createTier(CreateTierRequest request);

    /**
     * Updates an existing membership tier.
     * 
     * @param tierId The ID of the tier to update
     * @param request The tier update request containing new details
     * @return TierResponse containing the updated tier details
     * @throws TierNotFoundException if the tier doesn't exist
     * @throws InvalidTierException if the update data is invalid
     */
    TierResponse updateTier(Long tierId, UpdateTierRequest request);

    /**
     * Retrieves all benefits for a specific tier.
     * 
     * @param tierId The ID of the tier
     * @return List of benefits for the tier
     * @throws TierNotFoundException if the tier doesn't exist
     */
    List<BenefitResponse> getTierBenefits(Long tierId);

    /**
     * Adds a new benefit to a tier.
     * 
     * @param tierId The ID of the tier
     * @param request The benefit creation request
     * @return BenefitResponse containing the created benefit details
     * @throws TierNotFoundException if the tier doesn't exist
     * @throws InvalidBenefitException if the benefit data is invalid
     */
    BenefitResponse addBenefitToTier(Long tierId, CreateBenefitRequest request);

    /**
     * Adds new criteria to a tier.
     * 
     * @param tierId The ID of the tier
     * @param request The criteria creation request
     * @return CriteriaResponse containing the created criteria details
     * @throws TierNotFoundException if the tier doesn't exist
     * @throws InvalidCriteriaException if the criteria data is invalid
     */
    CriteriaResponse addCriteriaToTier(Long tierId, CreateCriteriaRequest request);

    /**
     * Deactivates a membership tier.
     * 
     * @param tierId The ID of the tier to deactivate
     * @throws TierNotFoundException if the tier doesn't exist
     */
    void deactivateTier(Long tierId);

    /**
     * Retrieves a specific membership tier entity by its ID.
     * This method is intended for internal service use.
     * 
     * @param tierId The ID of the tier to retrieve
     * @return MembershipTier entity
     * @throws TierNotFoundException if the tier doesn't exist
     */
    MembershipTier getTierEntityById(Long tierId);
}
