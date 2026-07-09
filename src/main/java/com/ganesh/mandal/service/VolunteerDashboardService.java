package com.ganesh.mandal.service;

import com.ganesh.mandal.dto.DashboardSummaryDTO;
import com.ganesh.mandal.dto.VolunteerAssignmentDTO;
import com.ganesh.mandal.dto.VolunteerDashboardDTO;
import com.ganesh.mandal.dto.VolunteerDTO;
import com.ganesh.mandal.repository.VolunteerAssignmentRepository;
import com.ganesh.mandal.repository.VolunteerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VolunteerDashboardService {

    private final VolunteerRepository volunteerRepository;
    private final VolunteerAssignmentRepository assignmentRepository;
    private final VolunteerService volunteerService;
    private final VolunteerAssignmentService assignmentService;

    private static final Set<String> CORE_ROLES = Set.of("President","Vice President","Secretary","Joint Secretary","Treasurer","Joint Treasurer");
    private static final Set<String> SOCIAL_ROLES = Set.of("Social Media Manager","Instagram Handler","Facebook Handler","YouTube Handler","WhatsApp Community Admin","Content Creator","Photographer","Videographer","Graphic Designer","Live Streaming Coordinator");
    private static final Set<String> FINANCE_ROLES = Set.of("Cash Collection Volunteer","Online Payment Coordinator","Receipt Management","Donation Collection","Sponsor Coordinator","Expense Management");
    private static final Set<String> DECORATION_ROLES = Set.of("Decoration Head","Decoration Team","Lighting Team","Flower Decoration Team","Ganesh Murti Management","Visarjan Coordinator");
    private static final Set<String> PRASAD_ROLES = Set.of("Prasad Coordinator","Prasad Distribution Team","Food Arrangement Team","Drinking Water Management");
    private static final Set<String> SECURITY_ROLES = Set.of("Security Head","Crowd Management","Parking Coordinator","First Aid Volunteer","Emergency Response Team");
    private static final Set<String> LOGISTICS_ROLES = Set.of("Sound System Coordinator","Electrical Team","Generator Management","Seating Arrangement","Cleaning Team","Material Management");

    public DashboardSummaryDTO getSummary(String festivalYear) {
        String year = festivalYear != null ? festivalYear : String.valueOf(LocalDate.now().getYear());
        List<VolunteerDTO> all = volunteerService.getByFestivalYear(year);
        LocalDate today = LocalDate.now();

        return DashboardSummaryDTO.builder()
                .totalVolunteers(all.size())
                .activeVolunteers(all.stream().filter(v -> "Active".equalsIgnoreCase(v.getStatus())).count())
                .coreCommittee(all.stream().filter(v -> CORE_ROLES.contains(v.getRole())).count())
                .eventOrganizers(all.stream().filter(v -> "Event Organizer".equals(v.getRole()) || "Event Coordinator".equals(v.getRole())).count())
                .socialMediaTeam(all.stream().filter(v -> SOCIAL_ROLES.contains(v.getRole())).count())
                .financeTeam(all.stream().filter(v -> FINANCE_ROLES.contains(v.getRole())).count())
                .decorationTeam(all.stream().filter(v -> DECORATION_ROLES.contains(v.getRole())).count())
                .prasadTeam(all.stream().filter(v -> PRASAD_ROLES.contains(v.getRole())).count())
                .securityTeam(all.stream().filter(v -> SECURITY_ROLES.contains(v.getRole())).count())
                .logisticsTeam(all.stream().filter(v -> LOGISTICS_ROLES.contains(v.getRole())).count())
                .todayAssigned(assignmentRepository.countByDutyDate(today))
                .upcomingDuties(assignmentRepository.countByDutyDateAfter(today))
                .birthdayThisMonth(volunteerService.getBirthdaysThisMonth(year).size())
                .build();
    }

    public VolunteerDashboardDTO getDashboard(String festivalYear) {
        String year = festivalYear != null ? festivalYear : String.valueOf(LocalDate.now().getYear());
        DashboardSummaryDTO summary = getSummary(year);
        LocalDate today = LocalDate.now();

        List<VolunteerAssignmentDTO> todayList = assignmentRepository.findByDutyDate(today).stream()
                .limit(10).map(a -> assignmentService.getById(a.getId())).toList();
        List<VolunteerAssignmentDTO> upcomingList = assignmentRepository.findByDutyDateAfter(today).stream()
                .limit(10).map(a -> assignmentService.getById(a.getId())).toList();
        List<VolunteerDTO> birthdayList = volunteerService.getBirthdaysThisMonth(year);

        return VolunteerDashboardDTO.builder()
                .totalVolunteers(summary.getTotalVolunteers())
                .activeVolunteers(summary.getActiveVolunteers())
                .coreCommittee(summary.getCoreCommittee())
                .eventOrganizers(summary.getEventOrganizers())
                .socialMediaTeam(summary.getSocialMediaTeam())
                .financeTeam(summary.getFinanceTeam())
                .todayAssigned(summary.getTodayAssigned())
                .upcomingDuties(summary.getUpcomingDuties())
                .birthdayThisMonth(summary.getBirthdayThisMonth())
                .todayAssignments(todayList)
                .upcomingAssignments(upcomingList)
                .birthdayVolunteers(birthdayList)
                .build();
    }
}
