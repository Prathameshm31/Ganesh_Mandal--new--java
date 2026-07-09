package com.ganesh.mandal.service;

import com.ganesh.mandal.dto.CurrentYearMurtiDTO;
import com.ganesh.mandal.dto.GaneshMurtiDTO;
import com.ganesh.mandal.entity.GaneshMurti;
import com.ganesh.mandal.exception.ResourceNotFoundException;
import com.ganesh.mandal.repository.GaneshMurtiRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GaneshMurtiService {

    private final GaneshMurtiRepository repository;

    @Transactional(readOnly = true)
    public List<GaneshMurtiDTO> getAllMurtis() {
        return repository.findAllByOrderByFestivalYearDescIdDesc()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public GaneshMurtiDTO getMurtiById(Long id) {
        GaneshMurti murti = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("GaneshMurti", id));
        return toDTO(murti);
    }

    @Transactional(readOnly = true)
    public CurrentYearMurtiDTO getCurrentYearMurti() {
        String currentYear = String.valueOf(LocalDate.now().getYear());
        return repository.findFirstByFestivalYearOrderByIdDesc(currentYear)
                .map(this::toCurrentYearDTO)
                .orElse(null);
    }

    @Transactional(readOnly = true)
    public List<GaneshMurtiDTO> getMurtiHistoryByYear(String year) {
        return repository.findByFestivalYearOrderByIdDesc(year)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<GaneshMurtiDTO> searchByDonorName(String donorName) {
        return repository.findByDonatedByContainingIgnoreCase(donorName)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<GaneshMurtiDTO> filterByYear(String year) {
        return repository.findByFestivalYearContaining(year)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public GaneshMurtiDTO createMurti(GaneshMurtiDTO dto) {
        GaneshMurti murti = toEntity(dto);
        GaneshMurti saved = repository.save(murti);
        return toDTO(saved);
    }

    @Transactional
    public GaneshMurtiDTO updateMurti(Long id, GaneshMurtiDTO dto) {
        GaneshMurti murti = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("GaneshMurti", id));
        murti.setFestivalYear(dto.getFestivalYear());
        murti.setMurtiName(dto.getMurtiName());
        murti.setDonatedBy(dto.getDonatedBy());
        murti.setMobileNumber(dto.getMobileNumber());
        murti.setAddress(dto.getAddress());
        murti.setMurtiHeight(dto.getMurtiHeight());
        murti.setMurtiType(dto.getMurtiType());
        murti.setArtistName(dto.getArtistName());
        murti.setWorkshopName(dto.getWorkshopName());
        murti.setInstallationDate(dto.getInstallationDate());
        murti.setVisarjanDate(dto.getVisarjanDate());
        murti.setEstimatedCost(dto.getEstimatedCost());
        murti.setIsSponsored(dto.getIsSponsored());
        murti.setDonationAmount(dto.getDonationAmount());
        if (dto.getPhotoUrl() != null) {
            murti.setPhotoUrl(dto.getPhotoUrl());
        }
        murti.setRemarks(dto.getRemarks());
        GaneshMurti saved = repository.save(murti);
        return toDTO(saved);
    }

    @Transactional
    public void deleteMurti(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("GaneshMurti", id);
        }
        repository.deleteById(id);
    }

    private GaneshMurtiDTO toDTO(GaneshMurti murti) {
        return GaneshMurtiDTO.builder()
                .id(murti.getId())
                .festivalYear(murti.getFestivalYear())
                .murtiName(murti.getMurtiName())
                .donatedBy(murti.getDonatedBy())
                .mobileNumber(murti.getMobileNumber())
                .address(murti.getAddress())
                .murtiHeight(murti.getMurtiHeight())
                .murtiType(murti.getMurtiType())
                .artistName(murti.getArtistName())
                .workshopName(murti.getWorkshopName())
                .installationDate(murti.getInstallationDate())
                .visarjanDate(murti.getVisarjanDate())
                .estimatedCost(murti.getEstimatedCost())
                .isSponsored(murti.getIsSponsored())
                .donationAmount(murti.getDonationAmount())
                .photoUrl(murti.getPhotoUrl())
                .remarks(murti.getRemarks())
                .createdAt(murti.getCreatedAt())
                .updatedAt(murti.getUpdatedAt())
                .build();
    }

    private CurrentYearMurtiDTO toCurrentYearDTO(GaneshMurti murti) {
        return CurrentYearMurtiDTO.builder()
                .id(murti.getId())
                .festivalYear(murti.getFestivalYear())
                .murtiName(murti.getMurtiName())
                .donatedBy(murti.getDonatedBy())
                .artistName(murti.getArtistName())
                .workshopName(murti.getWorkshopName())
                .murtiHeight(murti.getMurtiHeight())
                .murtiType(murti.getMurtiType())
                .installationDate(murti.getInstallationDate())
                .estimatedCost(murti.getEstimatedCost())
                .photoUrl(murti.getPhotoUrl())
                .build();
    }

    private GaneshMurti toEntity(GaneshMurtiDTO dto) {
        return GaneshMurti.builder()
                .festivalYear(dto.getFestivalYear())
                .murtiName(dto.getMurtiName())
                .donatedBy(dto.getDonatedBy())
                .mobileNumber(dto.getMobileNumber())
                .address(dto.getAddress())
                .murtiHeight(dto.getMurtiHeight())
                .murtiType(dto.getMurtiType())
                .artistName(dto.getArtistName())
                .workshopName(dto.getWorkshopName())
                .installationDate(dto.getInstallationDate())
                .visarjanDate(dto.getVisarjanDate())
                .estimatedCost(dto.getEstimatedCost())
                .isSponsored(dto.getIsSponsored())
                .donationAmount(dto.getDonationAmount())
                .photoUrl(dto.getPhotoUrl())
                .remarks(dto.getRemarks())
                .build();
    }
}
