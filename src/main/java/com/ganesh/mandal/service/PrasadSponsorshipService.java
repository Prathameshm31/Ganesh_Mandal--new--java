package com.ganesh.mandal.service;

import com.ganesh.mandal.dto.PrasadSponsorshipDTO;
import com.ganesh.mandal.entity.PrasadSponsorship;
import com.ganesh.mandal.exception.ResourceNotFoundException;
import com.ganesh.mandal.repository.PrasadSponsorshipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PrasadSponsorshipService {

    private final PrasadSponsorshipRepository repository;

    @Transactional(readOnly = true)
    public List<PrasadSponsorshipDTO> getByFestivalYear(String festivalYear) {
        return repository.findByFestivalYearOrderByPrasadDateAscIdAsc(festivalYear)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PrasadSponsorshipDTO> getByFestivalYearAndDay(String festivalYear, String festivalDay) {
        return repository.findByFestivalYearAndFestivalDayOrderByPrasadDateAsc(festivalYear, festivalDay)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PrasadSponsorshipDTO getById(Long id) {
        PrasadSponsorship entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PrasadSponsorship", id));
        return toDTO(entity);
    }

    @Transactional(readOnly = true)
    public List<PrasadSponsorshipDTO> search(String keyword) {
        return repository.findBySponsoredByContainingIgnoreCaseOrPrasadNameContainingIgnoreCase(keyword, keyword)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public PrasadSponsorshipDTO create(PrasadSponsorshipDTO dto) {
        PrasadSponsorship entity = toEntity(dto);
        PrasadSponsorship saved = repository.save(entity);
        return toDTO(saved);
    }

    @Transactional
    public PrasadSponsorshipDTO update(Long id, PrasadSponsorshipDTO dto) {
        PrasadSponsorship entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PrasadSponsorship", id));
        entity.setFestivalYear(dto.getFestivalYear());
        entity.setFestivalDay(dto.getFestivalDay());
        entity.setPrasadDate(dto.getPrasadDate());
        entity.setPrasadName(dto.getPrasadName());
        entity.setSponsoredBy(dto.getSponsoredBy());
        entity.setMobileNumber(dto.getMobileNumber());
        entity.setAddress(dto.getAddress());
        entity.setQuantity(dto.getQuantity());
        entity.setEstimatedCost(dto.getEstimatedCost());
        entity.setDonationAmount(dto.getDonationAmount());
        entity.setPreparedBy(dto.getPreparedBy());
        entity.setDistributionTime(dto.getDistributionTime());
        entity.setStatus(dto.getStatus());
        entity.setNotes(dto.getNotes());
        PrasadSponsorship saved = repository.save(entity);
        return toDTO(saved);
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("PrasadSponsorship", id);
        }
        repository.deleteById(id);
    }

    private PrasadSponsorshipDTO toDTO(PrasadSponsorship entity) {
        return PrasadSponsorshipDTO.builder()
                .id(entity.getId())
                .festivalYear(entity.getFestivalYear())
                .festivalDay(entity.getFestivalDay())
                .prasadDate(entity.getPrasadDate())
                .prasadName(entity.getPrasadName())
                .sponsoredBy(entity.getSponsoredBy())
                .mobileNumber(entity.getMobileNumber())
                .address(entity.getAddress())
                .quantity(entity.getQuantity())
                .estimatedCost(entity.getEstimatedCost())
                .donationAmount(entity.getDonationAmount())
                .preparedBy(entity.getPreparedBy())
                .distributionTime(entity.getDistributionTime())
                .status(entity.getStatus())
                .notes(entity.getNotes())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    private PrasadSponsorship toEntity(PrasadSponsorshipDTO dto) {
        return PrasadSponsorship.builder()
                .festivalYear(dto.getFestivalYear())
                .festivalDay(dto.getFestivalDay())
                .prasadDate(dto.getPrasadDate())
                .prasadName(dto.getPrasadName())
                .sponsoredBy(dto.getSponsoredBy())
                .mobileNumber(dto.getMobileNumber())
                .address(dto.getAddress())
                .quantity(dto.getQuantity())
                .estimatedCost(dto.getEstimatedCost())
                .donationAmount(dto.getDonationAmount())
                .preparedBy(dto.getPreparedBy())
                .distributionTime(dto.getDistributionTime())
                .status(dto.getStatus())
                .notes(dto.getNotes())
                .build();
    }
}
