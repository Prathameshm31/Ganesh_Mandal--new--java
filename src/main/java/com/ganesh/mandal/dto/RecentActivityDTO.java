package com.ganesh.mandal.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecentActivityDTO {
    private List<CollectionDTO> recentDonations;
    private List<MemberDTO> recentMembers;
    private List<ActivityDTO> upcomingActivities;
}
