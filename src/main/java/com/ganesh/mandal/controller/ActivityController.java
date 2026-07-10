package com.ganesh.mandal.controller;

import com.ganesh.mandal.dto.ActivityDTO;
import com.ganesh.mandal.service.ActivityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/activities")
@RequiredArgsConstructor
public class ActivityController {

    private final ActivityService activityService;

    @PostMapping
    public ResponseEntity<ActivityDTO> createActivity(@Valid @RequestBody ActivityDTO dto) {
        ActivityDTO created = activityService.createActivity(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ActivityDTO>> getAllActivities() {
        return ResponseEntity.ok(activityService.getAllActivities());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ActivityDTO> getActivityById(@PathVariable Long id) {
        return ResponseEntity.ok(activityService.getActivityById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ActivityDTO> updateActivity(@PathVariable Long id, @Valid @RequestBody ActivityDTO dto) {
        return ResponseEntity.ok(activityService.updateActivity(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteActivity(@PathVariable Long id) {
        activityService.deleteActivity(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<ActivityDTO>> getActivitiesByStatus(@PathVariable String status) {
        return ResponseEntity.ok(activityService.getActivitiesByStatus(status));
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<ActivityDTO>> getActivitiesByCategory(@PathVariable String category) {
        return ResponseEntity.ok(activityService.getActivitiesByCategory(category));
    }
}
