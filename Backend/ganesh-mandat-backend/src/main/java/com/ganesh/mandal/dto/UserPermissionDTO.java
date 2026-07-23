package com.ganesh.mandal.dto;

import lombok.*;

import java.util.List;
import java.util.Map;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserPermissionDTO {
    private Long userId;
    private String userName;
    private String userRole;
    private List<ModulePermissions> modulePermissions;

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class ModulePermissions {
        private String moduleName;
        private List<PermissionEntry> permissions;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class PermissionEntry {
        private Long permissionId;
        private String permissionName;
        private String permissionCode;
        private boolean allowed;
        private boolean fromRole;
    }
}
