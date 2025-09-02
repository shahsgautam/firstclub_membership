package com.firstclub.membership.repository;

import com.firstclub.membership.model.entity.MembershipTransaction;
import com.firstclub.membership.model.enums.TransactionType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Repository
public interface MembershipTransactionRepository extends JpaRepository<MembershipTransaction, Long> {
    
    /**
     * Find all transactions by transaction type
     * @param type the transaction type
     * @return List of transactions with the specified type
     */
    List<MembershipTransaction> findByType(TransactionType type);
    
    /**
     * Find transactions by membership ID with pagination and ordering
     * @param membershipId the membership ID
     * @param pageable the pagination and sorting parameters
     * @return Page of transactions for the membership
     */
    Page<MembershipTransaction> findByMembershipIdOrderByTransactionDateDesc(Long membershipId, Pageable pageable);
}
