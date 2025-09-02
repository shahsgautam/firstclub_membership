package com.firstclub.membership.service.impl;

import com.firstclub.membership.dto.request.CreatePlanRequest;
import com.firstclub.membership.dto.request.UpdatePlanRequest;
import com.firstclub.membership.dto.response.PlanResponse;
import com.firstclub.membership.exception.PlanNotFoundException;
import com.firstclub.membership.mapper.PlanMapper;
import com.firstclub.membership.model.entity.MembershipPlan;
import com.firstclub.membership.repository.MembershipPlanRepository;
import com.firstclub.membership.service.MembershipPlanService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementation of MembershipPlanService for managing membership plans.
 * Provides CRUD operations for membership plans with proper validation and error handling.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MembershipPlanServiceImpl implements MembershipPlanService {

    private final MembershipPlanRepository planRepository;
    private final PlanMapper planMapper;

    @Override
    @Transactional(readOnly = true)
    public List<PlanResponse> getAllActivePlans() {
        log.debug("Retrieving all active membership plans");
        List<MembershipPlan> plans = planRepository.findAllByActive(true);
        return planMapper.toPlanResponseList(plans);
    }

    @Override
    @Transactional(readOnly = true)
    public PlanResponse getPlanById(Long planId) {
        log.debug("Retrieving membership plan with ID: {}", planId);
        MembershipPlan plan = planRepository.findById(planId)
            .orElseThrow(() -> new PlanNotFoundException("Plan not found with ID: " + planId));
        return planMapper.toPlanResponse(plan);
    }

    @Override
    public PlanResponse createPlan(CreatePlanRequest request) {
        log.info("Creating new membership plan: {}", request.getName());
        
        // Check if plan with same name already exists
        planRepository.findByName(request.getName())
            .ifPresent(plan -> {
                throw new IllegalArgumentException("Plan with name '" + request.getName() + "' already exists");
            });
        
        MembershipPlan plan = planMapper.toMembershipPlan(request);
        plan = planRepository.save(plan);
        
        log.info("Successfully created membership plan with ID: {}", plan.getId());
        return planMapper.toPlanResponse(plan);
    }

    @Override
    public PlanResponse updatePlan(Long planId, UpdatePlanRequest request) {
        log.info("Updating membership plan with ID: {}", planId);
        
        MembershipPlan plan = planRepository.findById(planId)
            .orElseThrow(() -> new PlanNotFoundException("Plan not found with ID: " + planId));
        
        // Check if name is being changed and if it conflicts with existing plan
        if (request.getName() != null && !request.getName().equals(plan.getName())) {
            planRepository.findByName(request.getName())
                .ifPresent(existingPlan -> {
                    throw new IllegalArgumentException("Plan with name '" + request.getName() + "' already exists");
                });
        }
        
        plan = planMapper.updateMembershipPlan(plan, request);
        plan = planRepository.save(plan);
        
        log.info("Successfully updated membership plan with ID: {}", planId);
        return planMapper.toPlanResponse(plan);
    }

    @Override
    public void deactivatePlan(Long planId) {
        log.info("Deactivating membership plan with ID: {}", planId);
        
        MembershipPlan plan = planRepository.findById(planId)
            .orElseThrow(() -> new PlanNotFoundException("Plan not found with ID: " + planId));
        
        plan.setActive(false);
        planRepository.save(plan);
        
        log.info("Successfully deactivated membership plan with ID: {}", planId);
    }

    @Override
    @Transactional(readOnly = true)
    public MembershipPlan getPlanEntityById(Long planId) {
        log.debug("Retrieving membership plan entity with ID: {}", planId);
        return planRepository.findById(planId)
            .orElseThrow(() -> new PlanNotFoundException("Plan not found with ID: " + planId));
    }
}
