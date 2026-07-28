package com.ganesh.mandal.service;

import com.ganesh.mandal.dto.ActivityDTO;
import com.ganesh.mandal.entity.Activity;
import com.ganesh.mandal.exception.ResourceNotFoundException;
import com.ganesh.mandal.repository.ActivityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ganesh.mandal.event.NotificationEvent;
import com.ganesh.mandal.dto.NotificationRequest;
import com.ganesh.mandal.entity.Member;
import com.ganesh.mandal.repository.MemberRepository;
import org.springframework.context.ApplicationEventPublisher;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ActivityService {

    private final ActivityRepository activityRepository;
    private final MemberRepository memberRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional(readOnly = true)
    public List<ActivityDTO> getAllActivities() {
        return activityRepository.findAllByOrderByDateDesc()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ActivityDTO getActivityById(Long id) {
        Activity activity = activityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Activity", id));
        return toDTO(activity);
    }

    @Transactional
    public ActivityDTO createActivity(ActivityDTO dto) {
        Activity activity = toEntity(dto);
        Activity saved = activityRepository.save(activity);
        ActivityDTO savedDto = toDTO(saved);
        publishActivityNotification(savedDto);
        return savedDto;
    }

    @Transactional
    public ActivityDTO updateActivity(Long id, ActivityDTO dto) {
        Activity activity = activityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Activity", id));
        activity.setTitle(dto.getTitle());
        activity.setDescription(dto.getDescription());
        activity.setDate(dto.getDate());
        activity.setTime(dto.getTime());
        activity.setVenue(dto.getVenue());
        activity.setOrganizer(dto.getOrganizer());
        activity.setBudget(dto.getBudget());
        activity.setStatus(dto.getStatus());
        activity.setBannerImage(dto.getBannerImage());
        activity.setCategory(dto.getCategory());
        Activity saved = activityRepository.save(activity);
        return toDTO(saved);
    }

    @Transactional
    public void deleteActivity(Long id) {
        if (!activityRepository.existsById(id)) {
            throw new ResourceNotFoundException("Activity", id);
        }
        activityRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<ActivityDTO> getActivitiesByStatus(String status) {
        return activityRepository.findByStatusOrderByDateDesc(status)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ActivityDTO> getActivitiesByCategory(String category) {
        return activityRepository.findByCategory(category)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private ActivityDTO toDTO(Activity activity) {
        return ActivityDTO.builder()
                .id(activity.getId())
                .title(activity.getTitle())
                .description(activity.getDescription())
                .date(activity.getDate())
                .time(activity.getTime())
                .venue(activity.getVenue())
                .organizer(activity.getOrganizer())
                .budget(activity.getBudget())
                .status(activity.getStatus())
                .bannerImage(activity.getBannerImage())
                .category(activity.getCategory())
                .createdAt(activity.getCreatedAt())
                .build();
    }

    private Activity toEntity(ActivityDTO dto) {
        return Activity.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .date(dto.getDate())
                .time(dto.getTime())
                .venue(dto.getVenue())
                .organizer(dto.getOrganizer())
                .budget(dto.getBudget())
                .status(dto.getStatus())
                .bannerImage(dto.getBannerImage())
                .category(dto.getCategory())
                .build();
    }

    private void publishActivityNotification(ActivityDTO activity) {
        List<Member> members = memberRepository.findAll();
        List<String> receivers = members.stream()
                .map(Member::getEmail)
                .filter(e -> e != null && !e.isBlank())
                .collect(Collectors.toList());
        if (receivers.isEmpty()) return;

        NotificationRequest req = NotificationRequest.builder()
                .notificationType("Activity_Creation")
                .receivers(receivers)
                .channels(List.of("Email"))
                .eventId(activity.getId())
                .activityName(activity.getTitle())
                .date(activity.getDate() != null ? activity.getDate().toString() : "TBA")
                .activityTime(activity.getTime() != null ? activity.getTime() : "TBA")
                .activityVenue(activity.getVenue() != null ? activity.getVenue() : "TBA")
                .build();
        eventPublisher.publishEvent(new NotificationEvent(this, req));
    }
}
