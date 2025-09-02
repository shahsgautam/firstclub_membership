package com.firstclub.membership.repository;

import com.firstclub.membership.model.entity.MembershipTier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MembershipTierRepository extends JpaRepository<MembershipTier, Long> {
    
    /**
     * Find all active tiers ordered by level in descending order
     * @param active true for active tiers, false for inactive
     * @return List of active/inactive tiers ordered by level desc
     */
    List<MembershipTier> findAllByActiveOrderByLevelDesc(Boolean active);
    
    /**
     * Find a tier by name
     * @param name the tier name
     * @return Optional containing the tier if found
     */
    Optional<MembershipTier> findByName(String name);
}
