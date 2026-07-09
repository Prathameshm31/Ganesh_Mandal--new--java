package com.ganesh.mandal.controller;

import com.ganesh.mandal.dto.CurrentYearMurtiDTO;
import com.ganesh.mandal.dto.GaneshMurtiDTO;
import com.ganesh.mandal.service.GaneshMurtiService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/murti")
@RequiredArgsConstructor
public class GaneshMurtiController {

    private final GaneshMurtiService murtiService;

    private static final String UPLOAD_DIR = "uploads/murti-photos/";

    @PostMapping
    public ResponseEntity<GaneshMurtiDTO> createMurti(@Valid @RequestBody GaneshMurtiDTO dto) {
        GaneshMurtiDTO created = murtiService.createMurti(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<GaneshMurtiDTO>> getAllMurtis() {
        return ResponseEntity.ok(murtiService.getAllMurtis());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GaneshMurtiDTO> getMurtiById(@PathVariable Long id) {
        return ResponseEntity.ok(murtiService.getMurtiById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GaneshMurtiDTO> updateMurti(@PathVariable Long id, @Valid @RequestBody GaneshMurtiDTO dto) {
        return ResponseEntity.ok(murtiService.updateMurti(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMurti(@PathVariable Long id) {
        murtiService.deleteMurti(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/current-year")
    public ResponseEntity<CurrentYearMurtiDTO> getCurrentYearMurti() {
        CurrentYearMurtiDTO murti = murtiService.getCurrentYearMurti();
        if (murti == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(murti);
    }

    @GetMapping("/history")
    public ResponseEntity<List<GaneshMurtiDTO>> getMurtiHistory(@RequestParam String year) {
        return ResponseEntity.ok(murtiService.getMurtiHistoryByYear(year));
    }

    @GetMapping("/search")
    public ResponseEntity<List<GaneshMurtiDTO>> searchMurti(@RequestParam String donorName) {
        return ResponseEntity.ok(murtiService.searchByDonorName(donorName));
    }

    @GetMapping("/filter")
    public ResponseEntity<List<GaneshMurtiDTO>> filterMurti(@RequestParam String year) {
        return ResponseEntity.ok(murtiService.filterByYear(year));
    }

    @PostMapping("/upload-photo")
    public ResponseEntity<String> uploadPhoto(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is empty");
        }
        try {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path filePath = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), filePath);
            String photoUrl = "/uploads/murti-photos/" + filename;
            return ResponseEntity.ok(photoUrl);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload file");
        }
    }
}
