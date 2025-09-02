package com.firstclub.membership.mapper;

import com.firstclub.membership.dto.request.CreateBenefitRequest;
import com.firstclub.membership.dto.request.CreateCriteriaRequest;
import com.firstclub.membership.dto.request.CreateTierRequest;
import com.firstclub.membership.dto.request.UpdateTierRequest;
import com.firstclub.membership.dto.response.BenefitResponse;
import com.firstclub.membership.dto.response.CriteriaResponse;
import com.firstclub.membership.dto.response.TierResponse;
import com.firstclub.membership.model.entity.MembershipTier;
import com.firstclub.membership.model.entity.TierBenefit;
import com.firstclub.membership.model.entity.TierCriteria;
import com.firstclub.membership.model.enums.BenefitType;
import com.firstclub.membership.model.enums.CriteriaType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper class for converting between tier entities and DTOs.
 * Provides clean separation between domain objects and API responses.
 */
@Component
public class TierMapper {

    /**
     * Maps a MembershipTier entity to TierResponse DTO.
     * 
     * @param tier The tier entity
     * @return TierResponse containing the tier details
     */
    public TierResponse toTierResponse(MembershipTier tier) {
        if (tier == null) {
            return null;
        }

        return TierResponse.builder()
            .id(tier.getId())
            .name(tier.getName())
            .level(tier.getLevel())
            .description(tier.getDescription())
            .active(tier.getActive())
            .build();
    }

    /**
     * Maps a list of MembershipTier entities to TierResponse DTOs.
     * 
     * @param tiers The list of tiers
     * @return List of TierResponse DTOs
     */
    public List<TierResponse> toTierResponseList(List<MembershipTier> tiers) {
        if (tiers == null) {
            return List.of();
        }

        return tiers.stream()
            .map(this::toTierResponse)
            .collect(Collectors.toList());
    }

    /**
     * Maps a CreateTierRequest DTO to MembershipTier entity.
     * 
     * @param request The create tier request
     * @return MembershipTier entity
     */
    public MembershipTier toMembershipTier(CreateTierRequest request) {
        if (request == null) {
            return null;
        }

        return MembershipTier.builder()
            .name(request.getName())
            .level(request.getLevel())
            .description(request.getDescription())
            .active(true)
            .build();
    }

    /**
     * Updates a MembershipTier entity with data from UpdateTierRequest.
     * 
     * @param tier The existing tier entity
     * @param request The update tier request
     * @return Updated MembershipTier entity
     */
    public MembershipTier updateMembershipTier(MembershipTier tier, UpdateTierRequest request) {
        if (tier == null || request == null) {
            return tier;
        }

        if (request.getName() != null) {
            tier.setName(request.getName());
        }
        if (request.getLevel() != null) {
            tier.setLevel(request.getLevel());
        }
        if (request.getDescription() != null) {
            tier.setDescription(request.getDescription());
        }
        if (request.getActive() != null) {
            tier.setActive(request.getActive());
        }

        return tier;
    }

    /**
     * Maps a TierBenefit entity to BenefitResponse DTO.
     * 
     * @param benefit The tier benefit entity
     * @return BenefitResponse containing the benefit details
     */
    public BenefitResponse toBenefitResponse(TierBenefit benefit) {
        if (benefit == null) {
            return null;
        }

        return BenefitResponse.builder()
            .id(benefit.getId())
            .type(benefit.getType().toString())
            .value(benefit.getValue())
            .description(benefit.getDescription())
            .build();
    }

    /**
     * Maps a list of TierBenefit entities to BenefitResponse DTOs.
     * 
     * @param benefits The list of benefits
     * @return List of BenefitResponse DTOs
     */
    public List<BenefitResponse> toBenefitResponseList(List<TierBenefit> benefits) {
        if (benefits == null) {
            return List.of();
        }

        return benefits.stream()
            .filter(TierBenefit::getActive)
            .map(this::toBenefitResponse)
            .collect(Collectors.toList());
    }

    /**
     * Maps a CreateBenefitRequest DTO to TierBenefit entity.
     * 
     * @param request The create benefit request
     * @param tier The tier to associate with
     * @return TierBenefit entity
     */
    public TierBenefit toTierBenefit(CreateBenefitRequest request, MembershipTier tier) {
        if (request == null) {
            return null;
        }

        return TierBenefit.builder()
            .tier(tier)
            .type(BenefitType.valueOf(request.getType().toUpperCase()))
            .value(request.getValue())
            .description(request.getDescription())
            .active(true)
            .build();
    }

    /**
     * Maps a TierCriteria entity to CriteriaResponse DTO.
     * 
     * @param criteria The tier criteria entity
     * @return CriteriaResponse containing the criteria details
     */
    public CriteriaResponse toCriteriaResponse(TierCriteria criteria) {
        if (criteria == null) {
            return null;
        }

        return CriteriaResponse.builder()
            .id(criteria.getId())
            .type(criteria.getType().toString())
            .threshold(criteria.getThreshold())
            .cohortName(criteria.getCohortName())
            .evaluationPeriodDays(criteria.getEvaluationPeriodDays())
            .build();
    }

    /**
     * Maps a CreateCriteriaRequest DTO to TierCriteria entity.
     * 
     * @param request The create criteria request
     * @param tier The tier to associate with
     * @return TierCriteria entity
     */
    public TierCriteria toTierCriteria(CreateCriteriaRequest request, MembershipTier tier) {
        if (request == null) {
            return null;
        }

        return TierCriteria.builder()
            .tier(tier)
            .type(CriteriaType.valueOf(request.getType().toUpperCase()))
            .threshold(request.getThreshold())
            .cohortName(request.getCohortName())
            .evaluationPeriodDays(request.getEvaluationPeriodDays())
            .active(true)
            .build();
    }
}
