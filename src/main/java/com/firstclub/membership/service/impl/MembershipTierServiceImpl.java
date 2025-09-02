package com.firstclub.membership.service.impl;

import com.firstclub.membership.dto.request.CreateBenefitRequest;
import com.firstclub.membership.dto.request.CreateCriteriaRequest;
import com.firstclub.membership.dto.request.CreateTierRequest;
import com.firstclub.membership.dto.request.UpdateTierRequest;
import com.firstclub.membership.dto.response.BenefitResponse;
import com.firstclub.membership.dto.response.CriteriaResponse;
import com.firstclub.membership.dto.response.TierResponse;
import com.firstclub.membership.exception.TierNotFoundException;
import com.firstclub.membership.mapper.TierMapper;
import com.firstclub.membership.model.entity.MembershipTier;
import com.firstclub.membership.model.entity.TierBenefit;
import com.firstclub.membership.model.entity.TierCriteria;
import com.firstclub.membership.repository.MembershipTierRepository;
import com.firstclub.membership.repository.TierBenefitRepository;
import com.firstclub.membership.repository.TierCriteriaRepository;
import com.firstclub.membership.service.MembershipTierService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementation of MembershipTierService for managing membership tiers.
 * Provides CRUD operations for membership tiers with proper validation and error handling.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MembershipTierServiceImpl implements MembershipTierService {

    private final MembershipTierRepository tierRepository;
    private final TierBenefitRepository benefitRepository;
    private final TierCriteriaRepository criteriaRepository;
    private final TierMapper tierMapper;

    @Override
    @Transactional(readOnly = true)
    public List<TierResponse> getAllActiveTiers() {
        log.debug("Retrieving all active membership tiers");
        List<MembershipTier> tiers = tierRepository.findAllByActiveOrderByLevelDesc(true);
        return tierMapper.toTierResponseList(tiers);
    }

    @Override
    @Transactional(readOnly = true)
    public TierResponse getTierById(Long tierId) {
        log.debug("Retrieving membership tier with ID: {}", tierId);
        MembershipTier tier = tierRepository.findById(tierId)
            .orElseThrow(() -> new TierNotFoundException("Tier not found with ID: " + tierId));
        return tierMapper.toTierResponse(tier);
    }

    @Override
    public TierResponse createTier(CreateTierRequest request) {
        log.info("Creating new membership tier: {}", request.getName());
        
        // Check if tier with same name already exists
        tierRepository.findByName(request.getName())
            .ifPresent(tier -> {
                throw new IllegalArgumentException("Tier with name '" + request.getName() + "' already exists");
            });
        
        MembershipTier tier = tierMapper.toMembershipTier(request);
        tier = tierRepository.save(tier);
        
        log.info("Successfully created membership tier with ID: {}", tier.getId());
        return tierMapper.toTierResponse(tier);
    }

    @Override
    public TierResponse updateTier(Long tierId, UpdateTierRequest request) {
        log.info("Updating membership tier with ID: {}", tierId);
        
        MembershipTier tier = tierRepository.findById(tierId)
            .orElseThrow(() -> new TierNotFoundException("Tier not found with ID: " + tierId));
        
        // Check if name is being changed and if it conflicts with existing tier
        if (request.getName() != null && !request.getName().equals(tier.getName())) {
            tierRepository.findByName(request.getName())
                .ifPresent(existingTier -> {
                    throw new IllegalArgumentException("Tier with name '" + request.getName() + "' already exists");
                });
        }
        
        tier = tierMapper.updateMembershipTier(tier, request);
        tier = tierRepository.save(tier);
        
        log.info("Successfully updated membership tier with ID: {}", tierId);
        return tierMapper.toTierResponse(tier);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BenefitResponse> getTierBenefits(Long tierId) {
        log.debug("Retrieving benefits for tier with ID: {}", tierId);
        
        // Verify tier exists
        tierRepository.findById(tierId)
            .orElseThrow(() -> new TierNotFoundException("Tier not found with ID: " + tierId));
        
        List<TierBenefit> benefits = benefitRepository.findByTierIdAndActive(tierId, true);
        return tierMapper.toBenefitResponseList(benefits);
    }

    @Override
    public BenefitResponse addBenefitToTier(Long tierId, CreateBenefitRequest request) {
        log.info("Adding benefit to tier with ID: {}", tierId);
        
        MembershipTier tier = tierRepository.findById(tierId)
            .orElseThrow(() -> new TierNotFoundException("Tier not found with ID: " + tierId));
        
        TierBenefit benefit = tierMapper.toTierBenefit(request, tier);
        benefit = benefitRepository.save(benefit);
        
        log.info("Successfully added benefit with ID: {} to tier: {}", benefit.getId(), tierId);
        return tierMapper.toBenefitResponse(benefit);
    }

    @Override
    public CriteriaResponse addCriteriaToTier(Long tierId, CreateCriteriaRequest request) {
        log.info("Adding criteria to tier with ID: {}", tierId);
        
        MembershipTier tier = tierRepository.findById(tierId)
            .orElseThrow(() -> new TierNotFoundException("Tier not found with ID: " + tierId));
        
        TierCriteria criteria = tierMapper.toTierCriteria(request, tier);
        criteria = criteriaRepository.save(criteria);
        
        log.info("Successfully added criteria with ID: {} to tier: {}", criteria.getId(), tierId);
        return tierMapper.toCriteriaResponse(criteria);
    }

    @Override
    public void deactivateTier(Long tierId) {
        log.info("Deactivating membership tier with ID: {}", tierId);
        
        MembershipTier tier = tierRepository.findById(tierId)
            .orElseThrow(() -> new TierNotFoundException("Tier not found with ID: " + tierId));
        
        tier.setActive(false);
        tierRepository.save(tier);
        
        log.info("Successfully deactivated membership tier with ID: {}", tierId);
    }

    @Override
    @Transactional(readOnly = true)
    public MembershipTier getTierEntityById(Long tierId) {
        log.debug("Retrieving membership tier entity with ID: {}", tierId);
        return tierRepository.findById(tierId)
            .orElseThrow(() -> new TierNotFoundException("Tier not found with ID: " + tierId));
    }
}
