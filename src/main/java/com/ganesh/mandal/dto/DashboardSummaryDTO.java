package com.ganesh.mandal.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DashboardSummaryDTO {
    private long totalVolunteers;
    private long activeVolunteers;
    private long coreCommittee;
    private long eventOrganizers;
    private long socialMediaTeam;
    private long financeTeam;
    private long decorationTeam;
    private long prasadTeam;
    private long securityTeam;
    private long logisticsTeam;
    private long todayAssigned;
    private long upcomingDuties;
    private long birthdayThisMonth;
}
