package com.ganesh.mandal.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 100)
    private String email;

    @Column(length = 15)
    private String mobile;

    @Column(length = 20)
    @Builder.Default
    private String status = "ACTIVE";

    @Builder.Default
    @Column(name = "first_login")
    private Boolean firstLogin = true;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    @Builder.Default
    @Column(name = "failed_login_attempts")
    private Integer failedLoginAttempts = 0;

    @Builder.Default
    @Column(name = "account_locked")
    private Boolean accountLocked = false;

    @Column(name = "password_updated_at")
    private LocalDateTime passwordUpdatedAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
