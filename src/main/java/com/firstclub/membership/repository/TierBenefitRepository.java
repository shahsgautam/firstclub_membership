package com.firstclub.membership.repository;

import com.firstclub.membership.model.entity.TierBenefit;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TierBenefitRepository extends JpaRepository<TierBenefit, Long> {
    
    /**
     * Find all active benefits for a specific tier
     * @param tierId the tier ID
     * @param active true for active benefits, false for inactive
     * @return List of active/inactive benefits for the tier
     */
    List<TierBenefit> findByTierIdAndActive(Long tierId, Boolean active);
}
