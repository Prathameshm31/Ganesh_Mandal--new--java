package com.ganesh.mandal.controller;

import com.ganesh.mandal.dto.CollectionDTO;
import com.ganesh.mandal.dto.CollectionSummaryDTO;
import com.ganesh.mandal.service.CollectionService;
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
@RequestMapping("/api/collections")
@RequiredArgsConstructor
public class CollectionController {

    private final CollectionService collectionService;

    @PostMapping
    public ResponseEntity<CollectionDTO> createCollection(@Valid @RequestBody CollectionDTO dto) {
        CollectionDTO created = collectionService.createCollection(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<CollectionDTO>> getAllCollections() {
        return ResponseEntity.ok(collectionService.getAllCollections());
    }

    @GetMapping("/member/{memberId}")
    public ResponseEntity<List<CollectionDTO>> getCollectionsByMember(@PathVariable Long memberId) {
        return ResponseEntity.ok(collectionService.getCollectionsByMember(memberId));
    }

    @GetMapping("/summary")
    public ResponseEntity<CollectionSummaryDTO> getSummary() {
        return ResponseEntity.ok(collectionService.getSummary());
    }

    @GetMapping("/search")
    public ResponseEntity<Page<CollectionDTO>> searchCollections(
            @RequestParam(required = false) String memberName,
            @RequestParam(required = false) String paymentMode,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "collectionDate") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortOrder) {
        Page<CollectionDTO> result = collectionService.searchCollections(
                memberName, paymentMode, startDate, endDate, page, size, sortBy, sortOrder);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CollectionDTO> getCollectionById(@PathVariable Long id) {
        return ResponseEntity.ok(collectionService.getCollectionById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CollectionDTO> updateCollection(@PathVariable Long id,
                                                           @Valid @RequestBody CollectionDTO dto) {
        CollectionDTO updated = collectionService.updateCollection(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCollection(@PathVariable Long id) {
        collectionService.deleteCollection(id);
        return ResponseEntity.noContent().build();
    }
}
