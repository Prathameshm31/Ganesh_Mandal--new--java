package com.ganesh.mandal.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardStatsDTO {
    private Long totalMembers;
    private Long activeMembers;
    private BigDecimal totalCollection;
    private Long totalDonationCount;
    private BigDecimal avgDonation;
    private Long totalColonies;
    private Long totalActivities;
    private Long upcomingActivities;
    private BigDecimal todayCollection;
    private BigDecimal onlineCollection;
    private BigDecimal cashCollection;
    private BigDecimal thisYearCollection;
    private Long pendingMembers;
    private BigDecimal collectionGoal;
    private CurrentYearMurtiDTO currentYearMurti;
}
