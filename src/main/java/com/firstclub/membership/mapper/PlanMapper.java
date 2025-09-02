package com.firstclub.membership.mapper;

import com.firstclub.membership.dto.request.CreatePlanRequest;
import com.firstclub.membership.dto.request.UpdatePlanRequest;
import com.firstclub.membership.dto.response.PlanResponse;
import com.firstclub.membership.model.entity.MembershipPlan;
import com.firstclub.membership.model.enums.PlanDuration;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper class for converting between plan entities and DTOs.
 * Provides clean separation between domain objects and API responses.
 */
@Component
public class PlanMapper {

    /**
     * Maps a MembershipPlan entity to PlanResponse DTO.
     * 
     * @param plan The plan entity
     * @return PlanResponse containing the plan details
     */
    public PlanResponse toPlanResponse(MembershipPlan plan) {
        if (plan == null) {
            return null;
        }

        return PlanResponse.builder()
            .id(plan.getId())
            .name(plan.getName())
            .duration(plan.getDuration().toString())
            .price(plan.getPrice())
            .description(plan.getDescription())
            .active(plan.getActive())
            .build();
    }

    /**
     * Maps a list of MembershipPlan entities to PlanResponse DTOs.
     * 
     * @param plans The list of plans
     * @return List of PlanResponse DTOs
     */
    public List<PlanResponse> toPlanResponseList(List<MembershipPlan> plans) {
        if (plans == null) {
            return List.of();
        }

        return plans.stream()
            .map(this::toPlanResponse)
            .collect(Collectors.toList());
    }

    /**
     * Maps a CreatePlanRequest DTO to MembershipPlan entity.
     * 
     * @param request The create plan request
     * @return MembershipPlan entity
     */
    public MembershipPlan toMembershipPlan(CreatePlanRequest request) {
        if (request == null) {
            return null;
        }

        return MembershipPlan.builder()
            .name(request.getName())
            .duration(PlanDuration.valueOf(request.getDuration().toUpperCase()))
            .price(request.getPrice())
            .description(request.getDescription())
            .active(true)
            .build();
    }

    /**
     * Updates a MembershipPlan entity with data from UpdatePlanRequest.
     * 
     * @param plan The existing plan entity
     * @param request The update plan request
     * @return Updated MembershipPlan entity
     */
    public MembershipPlan updateMembershipPlan(MembershipPlan plan, UpdatePlanRequest request) {
        if (plan == null || request == null) {
            return plan;
        }

        if (request.getName() != null) {
            plan.setName(request.getName());
        }
        if (request.getDuration() != null) {
            plan.setDuration(PlanDuration.valueOf(request.getDuration().toUpperCase()));
        }
        if (request.getPrice() != null) {
            plan.setPrice(request.getPrice());
        }
        if (request.getDescription() != null) {
            plan.setDescription(request.getDescription());
        }
        if (request.getActive() != null) {
            plan.setActive(request.getActive());
        }

        return plan;
    }
}
