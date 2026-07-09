package com.ganesh.mandal.controller;

import com.ganesh.mandal.dto.AttendanceDTO;
import com.ganesh.mandal.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    @PostMapping
    public ResponseEntity<AttendanceDTO> mark(@RequestBody AttendanceDTO dto) {
        return new ResponseEntity<>(attendanceService.markAttendance(dto), HttpStatus.OK);
    }

    @PostMapping("/bulk")
    public ResponseEntity<List<AttendanceDTO>> markBulk(@RequestBody List<AttendanceDTO> dtos) {
        List<AttendanceDTO> results = dtos.stream().map(attendanceService::markAttendance).toList();
        return ResponseEntity.ok(results);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AttendanceDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(attendanceService.getById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        attendanceService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/by-volunteer/{volunteerId}")
    public ResponseEntity<List<AttendanceDTO>> getByVolunteer(@PathVariable Long volunteerId) {
        return ResponseEntity.ok(attendanceService.getByVolunteer(volunteerId));
    }

    @GetMapping("/by-event/{eventId}")
    public ResponseEntity<List<AttendanceDTO>> getByEvent(@PathVariable Long eventId) {
        return ResponseEntity.ok(attendanceService.getByEvent(eventId));
    }

    @GetMapping("/by-date")
    public ResponseEntity<List<AttendanceDTO>> getByDate(@RequestParam String date) {
        return ResponseEntity.ok(attendanceService.getByDate(LocalDate.parse(date)));
    }

    @GetMapping("/by-event-date")
    public ResponseEntity<List<AttendanceDTO>> getByEventAndDate(@RequestParam Long eventId, @RequestParam String date) {
        return ResponseEntity.ok(attendanceService.getByEventAndDate(eventId, LocalDate.parse(date)));
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Long>> getStats(@RequestParam(required = false) String festivalYear) {
        return ResponseEntity.ok(attendanceService.getStats(festivalYear));
    }
}
