package com.firstclub.membership.dto.response;

import com.firstclub.membership.model.entity.TierBenefit;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MembershipResponse {
    private Long membershipId;
    private Long userId;
    private String planName;
    private String tierName;
    private String status;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Boolean autoRenew;
    private List<TierBenefit> benefits;
    private Integer daysRemaining;
    
    public Integer getDaysRemaining() {
        if (endDate != null) {
            return (int) java.time.Duration.between(LocalDateTime.now(), endDate).toDays();
        }
        return null;
    }
}
