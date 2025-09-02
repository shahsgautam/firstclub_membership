package com.firstclub.membership.controller;

import com.firstclub.membership.dto.request.SubscriptionRequest;
import com.firstclub.membership.dto.request.ModifyRequest;
import com.firstclub.membership.dto.response.ApiResponse;
import com.firstclub.membership.dto.response.BenefitResponse;
import com.firstclub.membership.dto.response.MembershipResponse;
import com.firstclub.membership.dto.response.TransactionResponse;
import com.firstclub.membership.service.MembershipService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/api/v1/memberships")
@RequiredArgsConstructor
@Validated
@Tag(name = "Membership Management", description = "APIs for managing user memberships")
public class MembershipController {
    
    private final MembershipService membershipService;
    
    @PostMapping("/subscribe")
    @Operation(summary = "Subscribe to a membership plan")
    public ResponseEntity<ApiResponse<MembershipResponse>> subscribe(
            @Valid @RequestBody SubscriptionRequest request) {
        
        MembershipResponse response = membershipService.subscribeToPlan(request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Subscription successful", response));
    }
    
    @PutMapping("/users/{userId}/upgrade")
    @Operation(summary = "Upgrade membership plan or tier")
    public ResponseEntity<ApiResponse<MembershipResponse>> upgrade(
            @PathVariable @NotNull Long userId,
            @Valid @RequestBody ModifyRequest request) {
        
        MembershipResponse response = membershipService.upgradeMembership(
            userId, request.getNewPlanId(), request.getNewTierId()
        );
        return ResponseEntity.ok(ApiResponse.success("Upgrade successful", response));
    }
    
    @PutMapping("/users/{userId}/downgrade")
    @Operation(summary = "Downgrade membership tier")
    public ResponseEntity<ApiResponse<MembershipResponse>> downgrade(
            @PathVariable @NotNull Long userId,
            @Valid @RequestBody ModifyRequest request) {
        
        MembershipResponse response = membershipService.downgradeMembership(
            userId, request.getNewPlanId(), request.getNewTierId()
        );
        return ResponseEntity.ok(ApiResponse.success("Downgrade successful", response));
    }
    
    @DeleteMapping("/users/{userId}/cancel")
    @Operation(summary = "Cancel membership")
    public ResponseEntity<ApiResponse<MembershipResponse>> cancel(
            @PathVariable @NotNull Long userId) {
        
        MembershipResponse response = membershipService.cancelMembership(userId);
        return ResponseEntity.ok(ApiResponse.success("Membership cancelled", response));
    }
    
    @GetMapping("/users/{userId}/current")
    @Operation(summary = "Get current membership details")
    public ResponseEntity<ApiResponse<MembershipResponse>> getCurrentMembership(
            @PathVariable @NotNull Long userId) {
        
        MembershipResponse response = membershipService.getCurrentMembership(userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @PostMapping("/users/{userId}/evaluate-tier")
    @Operation(summary = "Manually trigger tier evaluation")
    public ResponseEntity<ApiResponse<String>> evaluateTier(
            @PathVariable @NotNull Long userId) {
        
        membershipService.evaluateAndUpdateTier(userId);
        return ResponseEntity.ok(ApiResponse.success("Tier evaluation triggered"));
    }
    
    @GetMapping("/users/{userId}/benefits")
    @Operation(summary = "Get current membership benefits")
    public ResponseEntity<ApiResponse<List<BenefitResponse>>> getMembershipBenefits(
            @PathVariable @NotNull Long userId) {
        
        List<BenefitResponse> benefits = membershipService.getUserBenefits(userId);
        return ResponseEntity.ok(ApiResponse.success(benefits));
    }
    
    @GetMapping("/users/{userId}/history")
    @Operation(summary = "Get membership transaction history")
    public ResponseEntity<ApiResponse<List<TransactionResponse>>> getTransactionHistory(
            @PathVariable @NotNull Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        List<TransactionResponse> history = membershipService.getTransactionHistory(userId, page, size);
        return ResponseEntity.ok(ApiResponse.success(history));
    }
}
