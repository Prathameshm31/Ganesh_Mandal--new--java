package com.ganesh.mandal.dto;

import lombok.*;

import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PermissionDashboardDTO {
    private long totalUsers;
    private long totalRoles;
    private long totalPermissions;
    private long activeUsers;
    private long usersWithCustomPermissions;
    private List<String> recentlyUpdated;
}
