package com.ganesh.mandal.dto;

import lombok.*;

import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class VolunteerDashboardDTO {
    private long totalVolunteers;
    private long activeVolunteers;
    private long coreCommittee;
    private long eventOrganizers;
    private long socialMediaTeam;
    private long financeTeam;
    private long todayAssigned;
    private long upcomingDuties;
    private long birthdayThisMonth;
    private List<VolunteerAssignmentDTO> todayAssignments;
    private List<VolunteerAssignmentDTO> upcomingAssignments;
    private List<VolunteerDTO> birthdayVolunteers;
}
