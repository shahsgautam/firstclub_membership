package com.firstclub.membership.repository;

import com.firstclub.membership.model.entity.MembershipPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MembershipPlanRepository extends JpaRepository<MembershipPlan, Long> {
    
    /**
     * Find all active membership plans
     * @param active true for active plans, false for inactive
     * @return List of active/inactive membership plans
     */
    List<MembershipPlan> findAllByActive(Boolean active);
    
    /**
     * Find a membership plan by name
     * @param name the plan name
     * @return Optional containing the membership plan if found
     */
    java.util.Optional<MembershipPlan> findByName(String name);
}
