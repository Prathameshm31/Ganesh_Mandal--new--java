package com.ganesh.mandal.controller;

import com.ganesh.mandal.dto.*;
import com.ganesh.mandal.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/stats")
    public ResponseEntity<DashboardStatsDTO> getStats() {
        return ResponseEntity.ok(dashboardService.getDashboardStats());
    }

    @GetMapping("/monthly-collection")
    public ResponseEntity<List<MonthlyCollectionDTO>> getMonthlyCollection() {
        return ResponseEntity.ok(dashboardService.getMonthlyCollection());
    }

    @GetMapping("/colony-wise")
    public ResponseEntity<List<ColonyWiseDTO>> getColonyWise() {
        return ResponseEntity.ok(dashboardService.getColonyWise());
    }

    @GetMapping("/payment-mode-breakdown")
    public ResponseEntity<List<PaymentModeDTO>> getPaymentModeBreakdown() {
        return ResponseEntity.ok(dashboardService.getPaymentModeBreakdown());
    }

    @GetMapping("/yearly-trend")
    public ResponseEntity<List<YearlyTrendDTO>> getYearlyTrend() {
        return ResponseEntity.ok(dashboardService.getYearlyTrend());
    }

    @GetMapping("/top-donors")
    public ResponseEntity<List<TopDonorDTO>> getTopDonors(
            @RequestParam(defaultValue = "5") int limit) {
        return ResponseEntity.ok(dashboardService.getTopDonors(limit));
    }

    @GetMapping("/recent-activity")
    public ResponseEntity<RecentActivityDTO> getRecentActivity() {
        return ResponseEntity.ok(dashboardService.getRecentActivity());
    }
}
