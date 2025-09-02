package com.firstclub.membership.controller;

import com.firstclub.membership.dto.request.CreatePlanRequest;
import com.firstclub.membership.dto.request.UpdatePlanRequest;
import com.firstclub.membership.dto.response.ApiResponse;
import com.firstclub.membership.dto.response.PlanResponse;
import com.firstclub.membership.service.MembershipPlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/plans")
@RequiredArgsConstructor
@Tag(name = "Membership Plans", description = "APIs for managing membership plans")
public class MembershipPlanController {
    
    private final MembershipPlanService membershipPlanService;
    
    @GetMapping
    @Operation(summary = "Get all active membership plans")
    public ResponseEntity<ApiResponse<List<PlanResponse>>> getAllPlans() {
        List<PlanResponse> plans = membershipPlanService.getAllActivePlans();
        return ResponseEntity.ok(ApiResponse.success(plans));
    }
    
    @GetMapping("/{planId}")
    @Operation(summary = "Get plan details by ID")
    public ResponseEntity<ApiResponse<PlanResponse>> getPlanById(@PathVariable Long planId) {
        PlanResponse plan = membershipPlanService.getPlanById(planId);
        return ResponseEntity.ok(ApiResponse.success(plan));
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new membership plan")
    public ResponseEntity<ApiResponse<PlanResponse>> createPlan(
            @Valid @RequestBody CreatePlanRequest request) {
        
        PlanResponse plan = membershipPlanService.createPlan(request);
        return ResponseEntity.ok(ApiResponse.success("Plan created successfully", plan));
    }
    
    @PutMapping("/{planId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update membership plan")
    public ResponseEntity<ApiResponse<PlanResponse>> updatePlan(
            @PathVariable Long planId,
            @Valid @RequestBody UpdatePlanRequest request) {
        
        PlanResponse plan = membershipPlanService.updatePlan(planId, request);
        return ResponseEntity.ok(ApiResponse.success("Plan updated successfully", plan));
    }
    
    @DeleteMapping("/{planId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Deactivate membership plan")
    public ResponseEntity<ApiResponse<Void>> deactivatePlan(@PathVariable Long planId) {
        membershipPlanService.deactivatePlan(planId);
        return ResponseEntity.ok(ApiResponse.success("Plan deactivated successfully"));
    }
}

