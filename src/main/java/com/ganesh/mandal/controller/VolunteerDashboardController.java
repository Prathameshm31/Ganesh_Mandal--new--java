package com.ganesh.mandal.controller;

import com.ganesh.mandal.dto.DashboardSummaryDTO;
import com.ganesh.mandal.dto.VolunteerDashboardDTO;
import com.ganesh.mandal.service.VolunteerDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/volunteer-dashboard")
@RequiredArgsConstructor
public class VolunteerDashboardController {

    private final VolunteerDashboardService dashboardService;

    @GetMapping
    public ResponseEntity<VolunteerDashboardDTO> getDashboard(@RequestParam(required = false) String festivalYear) {
        return ResponseEntity.ok(dashboardService.getDashboard(festivalYear));
    }

    @GetMapping("/summary")
    public ResponseEntity<DashboardSummaryDTO> getSummary(@RequestParam(required = false) String festivalYear) {
        return ResponseEntity.ok(dashboardService.getSummary(festivalYear));
    }
}
