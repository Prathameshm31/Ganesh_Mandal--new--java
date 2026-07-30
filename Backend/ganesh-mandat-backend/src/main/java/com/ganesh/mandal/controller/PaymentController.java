package com.ganesh.mandal.controller;

import com.ganesh.mandal.dto.*;
import com.ganesh.mandal.entity.PaymentAuditLog;
import com.ganesh.mandal.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/create-order")
    public ResponseEntity<PaymentOrderResponse> createOrder(
            @Valid @RequestBody PaymentOrderRequest request,
            HttpServletRequest httpRequest) {
        String performedBy = httpRequest.getRemoteAddr();
        PaymentOrderResponse response = paymentService.createPaymentOrder(request, performedBy);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify")
    public ResponseEntity<PaymentDTO> verifyPayment(
            @Valid @RequestBody PaymentVerificationRequest request,
            HttpServletRequest httpRequest) {
        String performedBy = extractUser(httpRequest);
        PaymentDTO response = paymentService.verifyPayment(request, performedBy);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/verify-with-gateway")
    public ResponseEntity<PaymentDTO> verifyWithGateway(
            @PathVariable Long id,
            HttpServletRequest httpRequest) {
        String performedBy = extractUser(httpRequest);
        PaymentDTO response = paymentService.verifyWithGateway(id, performedBy);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/dashboard")
    public ResponseEntity<PaymentDashboardDTO> getDashboard() {
        return ResponseEntity.ok(paymentService.getDashboard());
    }

    @GetMapping
    public ResponseEntity<Page<PaymentDTO>> searchPayments(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String donationType,
            @RequestParam(required = false) String paymentGateway,
            @RequestParam(required = false) String donorName,
            @RequestParam(required = false) String mobile,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortOrder) {
        return ResponseEntity.ok(paymentService.searchPayments(
                status, startDate, endDate, donationType, paymentGateway,
                donorName, mobile, email, search,
                page, size, sortBy, sortOrder));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentDTO> getPayment(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.getPaymentById(id));
    }

    @GetMapping("/{id}/audit-logs")
    public ResponseEntity<List<PaymentAuditLog>> getAuditLogs(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.getAuditLogs(id));
    }

    @GetMapping("/gateway-config")
    public ResponseEntity<GatewayConfigDTO> getGatewayConfig() {
        return ResponseEntity.ok(paymentService.getGatewayConfig());
    }

    @GetMapping("/reports/by-gateway")
    public ResponseEntity<List<PaymentReportDTO>> getReportByGateway() {
        return ResponseEntity.ok(paymentService.getReportByGateway());
    }

    private String extractUser(HttpServletRequest request) {
        String auth = request.getHeader("Authorization");
        if (auth != null && auth.startsWith("Bearer ")) {
            return auth.substring(7);
        }
        return request.getRemoteAddr();
    }
}
