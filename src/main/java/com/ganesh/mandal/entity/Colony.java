package com.ganesh.mandal.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "colonies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Colony {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required")
    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 100)
    private String area;

    @Column(length = 10)
    private String pincode;
}
