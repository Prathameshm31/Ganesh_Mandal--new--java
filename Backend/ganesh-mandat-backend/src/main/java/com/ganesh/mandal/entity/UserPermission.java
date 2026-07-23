package com.ganesh.mandal.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_permissions", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "permission_id"})
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserPermission {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "permission_id", nullable = false)
    private Permission permission;

    @Column(name = "is_allowed")
    @Builder.Default
    private Boolean isAllowed = true;
}
