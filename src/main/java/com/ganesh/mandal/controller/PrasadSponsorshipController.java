package com.ganesh.mandal.controller;

import com.ganesh.mandal.dto.PrasadSponsorshipDTO;
import com.ganesh.mandal.service.PrasadSponsorshipService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/prasad")
@RequiredArgsConstructor
public class PrasadSponsorshipController {

    private final PrasadSponsorshipService service;

    @PostMapping
    public ResponseEntity<PrasadSponsorshipDTO> create(@Valid @RequestBody PrasadSponsorshipDTO dto) {
        return new ResponseEntity<>(service.create(dto), HttpStatus.CREATED);
    }

    @GetMapping("/year/{festivalYear}")
    public ResponseEntity<List<PrasadSponsorshipDTO>> getByYear(@PathVariable String festivalYear) {
        return ResponseEntity.ok(service.getByFestivalYear(festivalYear));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PrasadSponsorshipDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PrasadSponsorshipDTO> update(@PathVariable Long id, @Valid @RequestBody PrasadSponsorshipDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/year/{festivalYear}/day/{festivalDay}")
    public ResponseEntity<List<PrasadSponsorshipDTO>> getByYearAndDay(
            @PathVariable String festivalYear, @PathVariable String festivalDay) {
        return ResponseEntity.ok(service.getByFestivalYearAndDay(festivalYear, festivalDay));
    }

    @GetMapping("/search")
    public ResponseEntity<List<PrasadSponsorshipDTO>> search(@RequestParam String keyword) {
        return ResponseEntity.ok(service.search(keyword));
    }
}
