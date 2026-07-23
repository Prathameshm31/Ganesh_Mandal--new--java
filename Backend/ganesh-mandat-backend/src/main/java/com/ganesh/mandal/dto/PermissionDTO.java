package com.ganesh.mandal.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PermissionDTO {
    private Long id;
    private String moduleName;
    private String permissionName;
    private String permissionCode;
    private String description;
}
