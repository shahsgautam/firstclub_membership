package com.firstclub.membership.controller;

import com.firstclub.membership.dto.*;
import com.firstclub.membership.service.MembershipTierService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/tiers")
@RequiredArgsConstructor
@Tag(name = "Membership Tiers", description = "APIs for managing membership tiers")
public class MembershipTierController {
    
    private final MembershipTierService tierService;
    
    @GetMapping
    @Operation(summary = "Get all active membership tiers")
    public ResponseEntity<ApiResponse<List<TierResponse>>> getAllTiers() {
        List<TierResponse> tiers = tierService.getAllActiveTiers();
        return ResponseEntity.ok(ApiResponse.success(tiers));
    }
    
    @GetMapping("/{tierId}")
    @Operation(summary = "Get tier details by ID")
    public ResponseEntity<ApiResponse<TierResponse>> getTierById(@PathVariable Long tierId) {
        TierResponse tier = tierService.getTierById(tierId);
        return ResponseEntity.ok(ApiResponse.success(tier));
    }
    
    @GetMapping("/{tierId}/benefits")
    @Operation(summary = "Get benefits for a specific tier")
    public ResponseEntity<ApiResponse<List<BenefitResponse>>> getTierBenefits(@PathVariable Long tierId) {
        List<BenefitResponse> benefits = tierService.getTierBenefits(tierId);
        return ResponseEntity.ok(ApiResponse.success(benefits));
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new membership tier")
    public ResponseEntity<ApiResponse<TierResponse>> createTier(
            @Valid @RequestBody CreateTierRequest request) {
        
        TierResponse tier = tierService.createTier(request);
        return ResponseEntity.ok(ApiResponse.success("Tier created successfully", tier));
    }
    
    @PutMapping("/{tierId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update membership tier")
    public ResponseEntity<ApiResponse<TierResponse>> updateTier(
            @PathVariable Long tierId,
            @Valid @RequestBody UpdateTierRequest request) {
        
        TierResponse tier = tierService.updateTier(tierId, request);
        return ResponseEntity.ok(ApiResponse.success("Tier updated successfully", tier));
    }
    
    @PostMapping("/{tierId}/benefits")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Add benefit to tier")
    public ResponseEntity<ApiResponse<BenefitResponse>> addBenefit(
            @PathVariable Long tierId,
            @Valid @RequestBody CreateBenefitRequest request) {
        
        BenefitResponse benefit = tierService.addBenefitToTier(tierId, request);
        return ResponseEntity.ok(ApiResponse.success("Benefit added successfully", benefit));
    }
    
    @PostMapping("/{tierId}/criteria")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Add criteria to tier")
    public ResponseEntity<ApiResponse<CriteriaResponse>> addCriteria(
            @PathVariable Long tierId,
            @Valid @RequestBody CreateCriteriaRequest request) {
        
        CriteriaResponse criteria = tierService.addCriteriaToTier(tierId, request);
        return ResponseEntity.ok(ApiResponse.success("Criteria added successfully", criteria));
    }
    
    @DeleteMapping("/{tierId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Deactivate membership tier")
    public ResponseEntity<ApiResponse<Void>> deactivateTier(@PathVariable Long tierId) {
        tierService.deactivateTier(tierId);
        return ResponseEntity.ok(ApiResponse.success("Tier deactivated successfully"));
    }
}upgrade")
    @Operation(summary = "Upgrade membership plan or tier")
    public ResponseEntity<ApiResponse<MembershipResponse>> upgrade(
            @PathVariable @NotNull Long userId,
            @Valid @RequestBody UpgradeRequest request) {
        
        MembershipResponse response = membershipService.upgradeMembership(
            userId, request.getNewPlanId(), request.getNewTierId()
        );
        return ResponseEntity.ok(ApiResponse.success("Upgrade successful", response));
    }
    
    @PutMapping("/users/{userId}/
