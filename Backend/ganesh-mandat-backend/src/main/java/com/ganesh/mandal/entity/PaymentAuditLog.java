package com.ganesh.mandal.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment_audit_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentAuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "payment_id", nullable = false)
    private Long paymentId;

    @Column(name = "action", nullable = false, length = 50)
    private String action;

    @Column(name = "previous_status", length = 20)
    private String previousStatus;

    @Column(name = "new_status", length = 20)
    private String newStatus;

    @Column(name = "performed_by", length = 100)
    private String performedBy;

    @Column(columnDefinition = "TEXT")
    private String details;

    @Column(name = "gateway_response", columnDefinition = "TEXT")
    private String gatewayResponse;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
