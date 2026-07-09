package com.ganesh.mandal.service;

import com.ganesh.mandal.dto.ColonyDTO;
import com.ganesh.mandal.entity.Colony;
import com.ganesh.mandal.exception.ResourceNotFoundException;
import com.ganesh.mandal.repository.CollectionRepository;
import com.ganesh.mandal.repository.ColonyRepository;
import com.ganesh.mandal.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ColonyService {

    private final ColonyRepository colonyRepository;
    private final MemberRepository memberRepository;
    private final CollectionRepository collectionRepository;

    @Transactional(readOnly = true)
    public List<ColonyDTO> getAllColonies() {
        return colonyRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ColonyDTO getColonyById(Long id) {
        Colony colony = colonyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Colony", id));
        return toDTO(colony);
    }

    @Transactional
    public ColonyDTO createColony(ColonyDTO dto) {
        Colony colony = toEntity(dto);
        Colony saved = colonyRepository.save(colony);
        return toDTO(saved);
    }

    @Transactional
    public ColonyDTO updateColony(Long id, ColonyDTO dto) {
        Colony colony = colonyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Colony", id));
        colony.setName(dto.getName());
        colony.setArea(dto.getArea());
        colony.setPincode(dto.getPincode());
        Colony saved = colonyRepository.save(colony);
        return toDTO(saved);
    }

    @Transactional
    public void deleteColony(Long id) {
        if (!colonyRepository.existsById(id)) {
            throw new ResourceNotFoundException("Colony", id);
        }
        colonyRepository.deleteById(id);
    }

    private ColonyDTO toDTO(Colony colony) {
        long totalMembers = memberRepository.countByColony(colony.getName());
        BigDecimal totalCollection = collectionRepository.sumByColony(colony.getName());

        return ColonyDTO.builder()
                .id(colony.getId())
                .name(colony.getName())
                .area(colony.getArea())
                .pincode(colony.getPincode())
                .totalMembers(totalMembers)
                .totalCollection(totalCollection)
                .pendingCollection(BigDecimal.ZERO)
                .build();
    }

    private Colony toEntity(ColonyDTO dto) {
        return Colony.builder()
                .name(dto.getName())
                .area(dto.getArea())
                .pincode(dto.getPincode())
                .build();
    }
}
