package com.firstclub.membership.repository;

import com.firstclub.membership.model.entity.UserMembership;
import com.firstclub.membership.model.enums.MembershipStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserMembershipRepository extends JpaRepository<UserMembership, Long> {
    
    /**
     * Find active membership for a specific user
     * @param userId the user ID
     * @return Optional containing the active membership if found
     */
    Optional<UserMembership> findActiveByUserId(Long userId);
    
    /**
     * Find all memberships for a specific user
     * @param userId the user ID
     * @return List of all memberships for the user
     */
    List<UserMembership> findByUserId(Long userId);
    
    /**
     * Find all memberships by status
     * @param status the membership status
     * @return List of memberships with the specified status
     */
    List<UserMembership> findAllByStatus(MembershipStatus status);
    
    /**
     * Find memberships by plan ID
     * @param planId the plan ID
     * @return List of memberships with the specified plan
     */
    List<UserMembership> findByPlanId(Long planId);
    
    /**
     * Find memberships by tier ID
     * @param tierId the tier ID
     * @return List of memberships with the specified tier
     */
    List<UserMembership> findByTierId(Long tierId);
}
