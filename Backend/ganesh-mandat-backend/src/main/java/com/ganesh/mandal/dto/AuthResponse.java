package com.ganesh.mandal.dto;

import lombok.*;

import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AuthResponse {
    private Long id;
    private String username;
    private String name;
    private String email;
    private String mobile;
    private String token;
    private List<String> roles;
    private List<String> permissions;
    private Boolean firstLogin;
    private String status;
}
