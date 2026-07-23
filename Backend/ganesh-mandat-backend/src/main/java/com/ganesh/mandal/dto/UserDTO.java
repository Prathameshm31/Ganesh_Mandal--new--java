package com.ganesh.mandal.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserDTO {
    private Long id;
    private String username;
    private String password;
    private String name;
    private String email;
    private String mobile;
    private String status;
    private String role;
    private java.time.LocalDateTime createdAt;
}
