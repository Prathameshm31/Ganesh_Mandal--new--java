package com.ganesh.mandal.dto;

import lombok.*;

import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class LoginResponse {
    private Long id;
    private String username;
    private String name;
    private String role;
    private List<String> permissions;
}
