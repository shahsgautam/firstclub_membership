package com.firstclub.membership.mapper;

import com.firstclub.membership.dto.response.BenefitResponse;
import com.firstclub.membership.dto.response.MembershipResponse;
import com.firstclub.membership.dto.response.TransactionResponse;
import com.firstclub.membership.model.entity.MembershipTransaction;
import com.firstclub.membership.model.entity.TierBenefit;
import com.firstclub.membership.model.entity.UserMembership;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper class for converting between membership entities and DTOs.
 * Provides clean separation between domain objects and API responses.
 */
@Component
public class MembershipMapper {

    /**
     * Maps a UserMembership entity to MembershipResponse DTO.
     * 
     * @param membership The membership entity
     * @return MembershipResponse containing the membership details
     */
    public MembershipResponse toMembershipResponse(UserMembership membership) {
        if (membership == null) {
            return null;
        }

        return MembershipResponse.builder()
            .membershipId(membership.getId())
            .userId(membership.getUserId())
            .planName(membership.getPlan().getName())
            .tierName(membership.getTier().getName())
            .status(membership.getStatus().toString())
            .startDate(membership.getStartDate())
            .endDate(membership.getEndDate())
            .autoRenew(membership.getAutoRenew())
            .benefits(membership.getTier().getBenefits())
            .build();
    }

    /**
     * Maps a list of TierBenefit entities to BenefitResponse DTOs.
     * 
     * @param benefits The list of tier benefits
     * @return List of BenefitResponse DTOs
     */
    public List<BenefitResponse> mapBenefits(List<TierBenefit> benefits) {
        if (benefits == null) {
            return List.of();
        }

        return benefits.stream()
            .filter(TierBenefit::getActive)
            .map(this::toBenefitResponse)
            .collect(Collectors.toList());
    }

    /**
     * Maps a TierBenefit entity to BenefitResponse DTO.
     * 
     * @param benefit The tier benefit entity
     * @return BenefitResponse containing the benefit details
     */
    public BenefitResponse toBenefitResponse(TierBenefit benefit) {
        if (benefit == null) {
            return null;
        }

        return BenefitResponse.builder()
            .id(benefit.getId())
            .type(benefit.getType().toString())
            .value(benefit.getValue())
            .description(benefit.getDescription())
            .build();
    }

    /**
     * Maps a MembershipTransaction entity to TransactionResponse DTO.
     * 
     * @param transaction The transaction entity
     * @return TransactionResponse containing the transaction details
     */
    public TransactionResponse toTransactionResponse(MembershipTransaction transaction) {
        if (transaction == null) {
            return null;
        }

        return TransactionResponse.builder()
            .id(transaction.getId())
            .type(transaction.getType().toString())
            .amount(transaction.getAmount())
            .oldPlan(transaction.getOldPlan() != null ? transaction.getOldPlan().getName() : null)
            .newPlan(transaction.getNewPlan() != null ? transaction.getNewPlan().getName() : null)
            .oldTier(transaction.getOldTier() != null ? transaction.getOldTier().getName() : null)
            .newTier(transaction.getNewTier() != null ? transaction.getNewTier().getName() : null)
            .notes(transaction.getNotes())
            .transactionDate(transaction.getTransactionDate())
            .build();
    }

    /**
     * Maps a list of MembershipTransaction entities to TransactionResponse DTOs.
     * 
     * @param transactions The list of transactions
     * @return List of TransactionResponse DTOs
     */
    public List<TransactionResponse> toTransactionResponseList(List<MembershipTransaction> transactions) {
        if (transactions == null) {
            return List.of();
        }

        return transactions.stream()
            .map(this::toTransactionResponse)
            .collect(Collectors.toList());
    }
}
