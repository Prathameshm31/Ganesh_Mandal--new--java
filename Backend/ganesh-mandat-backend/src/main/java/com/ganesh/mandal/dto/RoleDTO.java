package com.ganesh.mandal.dto;

import lombok.*;

import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RoleDTO {
    private Long id;
    private String roleName;
    private String description;
    private String status;
    private List<Long> permissionIds;
    private List<String> permissionCodes;
    private long userCount;
}
