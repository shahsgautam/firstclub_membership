package com.firstclub.membership.controller;

import com.firstclub.membership.dto.*;
import com.firstclub.membership.service.MembershipService;
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
    
    private final MembershipService  membershipService;
    
    @GetMapping
    @Operation(summary = "Get all active membership plans")
    public ResponseEntity<ApiResponse<List<PlanResponse>>> getAllPlans() {
        List<PlanResponse> plans =  membershipService.getAllActivePlans();
        return ResponseEntity.ok(ApiResponse.success(plans));
    }
    
    @GetMapping("/{planId}")
    @Operation(summary = "Get plan details by ID")
    public ResponseEntity<ApiResponse<PlanResponse>> getPlanById(@PathVariable Long planId) {
        PlanResponse plan =  membershipService.getPlanById(planId);
        return ResponseEntity.ok(ApiResponse.success(plan));
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new membership plan")
    public ResponseEntity<ApiResponse<PlanResponse>> createPlan(
            @Valid @RequestBody CreatePlanRequest request) {
        
        PlanResponse plan =  membershipService.createPlan(request);
        return ResponseEntity.ok(ApiResponse.success("Plan created successfully", plan));
    }
    
    @PutMapping("/{planId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update membership plan")
    public ResponseEntity<ApiResponse<PlanResponse>> updatePlan(
            @PathVariable Long planId,
            @Valid @RequestBody UpdatePlanRequest request) {
        
        PlanResponse plan =  membershipService.updatePlan(planId, request);
        return ResponseEntity.ok(ApiResponse.success("Plan updated successfully", plan));
    }
    
    @DeleteMapping("/{planId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Deactivate membership plan")
    public ResponseEntity<ApiResponse<Void>> deactivatePlan(@PathVariable Long planId) {
         membershipService.deactivatePlan(planId);
        return ResponseEntity.ok(ApiResponse.success("Plan deactivated successfully"));
    }
}

