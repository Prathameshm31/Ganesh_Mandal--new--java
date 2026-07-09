package com.ganesh.mandal.controller;

import com.ganesh.mandal.dto.ColonyDTO;
import com.ganesh.mandal.service.ColonyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/colonies")
@RequiredArgsConstructor
public class ColonyController {

    private final ColonyService colonyService;

    @PostMapping
    public ResponseEntity<ColonyDTO> createColony(@Valid @RequestBody ColonyDTO dto) {
        ColonyDTO created = colonyService.createColony(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ColonyDTO>> getAllColonies() {
        return ResponseEntity.ok(colonyService.getAllColonies());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ColonyDTO> getColonyById(@PathVariable Long id) {
        return ResponseEntity.ok(colonyService.getColonyById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ColonyDTO> updateColony(@PathVariable Long id, @Valid @RequestBody ColonyDTO dto) {
        return ResponseEntity.ok(colonyService.updateColony(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteColony(@PathVariable Long id) {
        colonyService.deleteColony(id);
        return ResponseEntity.noContent().build();
    }
}
