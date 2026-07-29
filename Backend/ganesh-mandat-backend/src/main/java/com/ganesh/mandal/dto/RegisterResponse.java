package com.ganesh.mandal.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RegisterResponse {
    private Long id;
    private String name;
    private String email;
    private String mobile;
    private String message;
}
