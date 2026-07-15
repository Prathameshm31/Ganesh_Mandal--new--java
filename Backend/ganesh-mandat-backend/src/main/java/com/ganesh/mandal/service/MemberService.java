package com.ganesh.mandal.service;

import com.ganesh.mandal.dto.MemberDTO;
import com.ganesh.mandal.dto.NotificationRequest;
import com.ganesh.mandal.entity.Member;
import com.ganesh.mandal.event.NotificationEvent;
import com.ganesh.mandal.exception.ResourceNotFoundException;
import com.ganesh.mandal.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional(readOnly = true)
    public List<MemberDTO> getAllMembers() {
        return memberRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public MemberDTO getMemberById(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member", id));
        return toDTO(member);
    }

    @Transactional
    public MemberDTO createMember(MemberDTO dto) {
        Member member = toEntity(dto);
        Member saved = memberRepository.save(member);
        MemberDTO result = toDTO(saved);
        publishRegistrationNotification(result);
        return result;
    }

    @Transactional
    public MemberDTO updateMember(Long id, MemberDTO dto) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member", id));
        member.setName(dto.getName());
        member.setMobile(dto.getMobile());
        member.setWhatsappNumber(dto.getWhatsappNumber());
        member.setEmail(dto.getEmail());
        member.setAddress(dto.getAddress());
        member.setColony(dto.getColony());
        member.setArea(dto.getArea());
        member.setHouseNumber(dto.getHouseNumber());
        member.setFamilyMembers(dto.getFamilyMembers());
        member.setOccupation(dto.getOccupation());
        member.setProfilePhoto(dto.getProfilePhoto());
        member.setStatus(dto.getStatus());
        member.setNotes(dto.getNotes());
        member.setJoinDate(dto.getJoinDate());
        member.setLastYearAmount(dto.getLastYearAmount());
        Member saved = memberRepository.save(member);
        return toDTO(saved);
    }

    @Transactional
    public void deleteMember(Long id) {
        if (!memberRepository.existsById(id)) {
            throw new ResourceNotFoundException("Member", id);
        }
        memberRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<MemberDTO> searchMembers(String keyword) {
        return memberRepository.findByNameContainingIgnoreCaseOrMobileContaining(keyword, keyword)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MemberDTO> filterMembers(String keyword, String status, String colony, String occupation) {
        List<Member> members;
        if (keyword != null && !keyword.isBlank()) {
            members = memberRepository.findByNameContainingIgnoreCaseOrMobileContaining(keyword, keyword);
        } else {
            members = memberRepository.findAll();
        }
        java.util.stream.Stream<Member> stream = members.stream();
        if (status != null && !status.isBlank()) {
            stream = stream.filter(m -> status.equals(m.getStatus()));
        }
        if (colony != null && !colony.isBlank()) {
            stream = stream.filter(m -> colony.equals(m.getColony()));
        }
        if (occupation != null && !occupation.isBlank()) {
            stream = stream.filter(m -> occupation.equals(m.getOccupation()));
        }
        return stream.map(this::toDTO).collect(Collectors.toList());
    }

    private MemberDTO toDTO(Member member) {
        return MemberDTO.builder()
                .id(member.getId())
                .name(member.getName())
                .mobile(member.getMobile())
                .whatsappNumber(member.getWhatsappNumber())
                .email(member.getEmail())
                .address(member.getAddress())
                .colony(member.getColony())
                .area(member.getArea())
                .houseNumber(member.getHouseNumber())
                .familyMembers(member.getFamilyMembers())
                .occupation(member.getOccupation())
                .profilePhoto(member.getProfilePhoto())
                .status(member.getStatus())
                .notes(member.getNotes())
                .joinDate(member.getJoinDate())
                .lastYearAmount(member.getLastYearAmount())
                .createdAt(member.getCreatedAt())
                .build();
    }

    private Member toEntity(MemberDTO dto) {
        return Member.builder()
                .name(dto.getName())
                .mobile(dto.getMobile())
                .whatsappNumber(dto.getWhatsappNumber())
                .email(dto.getEmail())
                .address(dto.getAddress())
                .colony(dto.getColony())
                .area(dto.getArea())
                .houseNumber(dto.getHouseNumber())
                .familyMembers(dto.getFamilyMembers())
                .occupation(dto.getOccupation())
                .profilePhoto(dto.getProfilePhoto())
                .status(dto.getStatus())
                .notes(dto.getNotes())
                .joinDate(dto.getJoinDate() != null ? dto.getJoinDate() : LocalDate.now())
                .lastYearAmount(dto.getLastYearAmount())
                .build();
    }

    private void publishRegistrationNotification(MemberDTO member) {
        List<String> receivers = new ArrayList<>();
        List<String> channels = new ArrayList<>();

        if (member.getMobile() != null && !member.getMobile().isBlank()) {
            String mobile = member.getMobile();
            if (!mobile.contains("@")) {
                receivers.add(mobile);
                channels.add("WhatsApp");
            }
        }
        if (member.getEmail() != null && !member.getEmail().isBlank()) {
            receivers.add(member.getEmail());
            channels.add("Email");
        }

        if (receivers.isEmpty()) return;

        NotificationRequest req = NotificationRequest.builder()
                .notificationType("Registration")
                .receivers(receivers)
                .channels(channels)
                .donorName(member.getName())
                .amount(member.getMobile())
                .userId(member.getId())
                .email(member.getEmail())
                .logoUrl(null)
                .bannerUrl(null)
                .websiteUrl("http://localhost:5173")
                .build();
        eventPublisher.publishEvent(new NotificationEvent(this, req));
    }
}
