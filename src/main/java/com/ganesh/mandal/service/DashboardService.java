package com.ganesh.mandal.service;

import com.ganesh.mandal.dto.*;
import com.ganesh.mandal.entity.PaymentMode;
import com.ganesh.mandal.repository.ActivityRepository;
import com.ganesh.mandal.repository.CollectionRepository;
import com.ganesh.mandal.repository.ColonyRepository;
import com.ganesh.mandal.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

    private final MemberRepository memberRepository;
    private final CollectionRepository collectionRepository;
    private final ActivityRepository activityRepository;
    private final ColonyRepository colonyRepository;
    private final SettingService settingService;
    private final GaneshMurtiService ganeshMurtiService;

    public DashboardStatsDTO getDashboardStats() {
        long totalMembers = memberRepository.count();
        long activeMembers = memberRepository.countByStatus("Active");
        BigDecimal totalCollection = collectionRepository.sumAllAmount();
        long totalDonationCount = collectionRepository.count();

        BigDecimal avgDonation = totalDonationCount > 0
                ? totalCollection.divide(BigDecimal.valueOf(totalDonationCount), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        long totalColonies = colonyRepository.count();
        long totalActivities = activityRepository.count();
        long upcomingActivities = activityRepository.findByStatusOrderByDateAsc("Upcoming").size();

        BigDecimal todayCollection = collectionRepository.sumByDate(LocalDate.now());

        BigDecimal onlineCollection = collectionRepository.sumByPaymentModeNotIn(List.of(PaymentMode.CASH));
        BigDecimal cashCollection = collectionRepository.sumByPaymentMode(PaymentMode.CASH);

        LocalDate startOfYear = LocalDate.of(LocalDate.now().getYear(), 1, 1);
        LocalDate startOfNextYear = startOfYear.plusYears(1);
        BigDecimal thisYearCollection = collectionRepository.sumByDateBetween(startOfYear, startOfNextYear);

        List<Long> memberIdsWithCollection = collectionRepository.findMemberIdsWithCollectionInYear(LocalDate.now().getYear());
        long pendingMembers = totalMembers - memberIdsWithCollection.size();

        return DashboardStatsDTO.builder()
                .totalMembers(totalMembers)
                .activeMembers(activeMembers)
                .totalCollection(totalCollection)
                .totalDonationCount(totalDonationCount)
                .avgDonation(avgDonation)
                .totalColonies(totalColonies)
                .totalActivities(totalActivities)
                .upcomingActivities(upcomingActivities)
                .todayCollection(todayCollection)
                .onlineCollection(onlineCollection)
                .cashCollection(cashCollection)
                .thisYearCollection(thisYearCollection)
                .pendingMembers(pendingMembers)
                .collectionGoal(settingService.getCollectionGoal())
                .currentYearMurti(ganeshMurtiService.getCurrentYearMurti())
                .build();
    }

    public List<MonthlyCollectionDTO> getMonthlyCollection() {
        List<Object[]> results = collectionRepository.findMonthlyCollection();
        return results.stream()
                .map(row -> MonthlyCollectionDTO.builder()
                        .month((String) row[0])
                        .amount((BigDecimal) row[1])
                        .build())
                .collect(Collectors.toList());
    }

    public List<ColonyWiseDTO> getColonyWise() {
        List<Object[]> results = collectionRepository.findColonyWiseCollection();
        return results.stream()
                .map(row -> ColonyWiseDTO.builder()
                        .colonyName((String) row[0])
                        .amount((BigDecimal) row[1])
                        .build())
                .collect(Collectors.toList());
    }

    public List<PaymentModeDTO> getPaymentModeBreakdown() {
        List<Object[]> results = collectionRepository.findPaymentModeBreakdown();
        Map<String, BigDecimal> modeMap = new HashMap<>();
        for (Object[] row : results) {
            modeMap.put((String) row[0], (BigDecimal) row[1]);
        }

        BigDecimal totalAmount = modeMap.values().stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<PaymentModeDTO> breakdown = new ArrayList<>();
        for (PaymentMode mode : PaymentMode.values()) {
            String modeName = mode.name();
            BigDecimal amount = modeMap.getOrDefault(modeName, BigDecimal.ZERO);
            int percentage = totalAmount.compareTo(BigDecimal.ZERO) > 0
                    ? amount.multiply(BigDecimal.valueOf(100)).divide(totalAmount, 0, RoundingMode.HALF_UP).intValue()
                    : 0;
            breakdown.add(PaymentModeDTO.builder()
                    .mode(modeName)
                    .amount(amount)
                    .percentage(percentage)
                    .build());
        }
        return breakdown;
    }

    public List<YearlyTrendDTO> getYearlyTrend() {
        List<Object[]> results = collectionRepository.findYearlyTrend();
        return results.stream()
                .map(row -> YearlyTrendDTO.builder()
                        .year(Integer.valueOf((String) row[0]))
                        .amount((BigDecimal) row[1])
                        .build())
                .collect(Collectors.toList());
    }

    public List<TopDonorDTO> getTopDonors(int limit) {
        List<Object[]> results = collectionRepository.findTopDonors(limit);
        return results.stream()
                .map(row -> TopDonorDTO.builder()
                        .memberId(((Number) row[0]).longValue())
                        .memberName((String) row[1])
                        .totalAmount((BigDecimal) row[2])
                        .build())
                .collect(Collectors.toList());
    }

    public RecentActivityDTO getRecentActivity() {
        List<CollectionDTO> recentDonations = collectionRepository.findFirst5ByOrderByCollectionDateDesc()
                .stream()
                .map(c -> CollectionDTO.builder()
                        .id(c.getId())
                        .memberId(c.getMember().getId())
                        .memberName(c.getMember().getName())
                        .memberMobile(c.getMember().getMobile())
                        .amount(c.getAmount())
                        .paymentMode(c.getPaymentMode().name())
                        .transactionId(c.getTransactionId())
                        .receiptNumber(c.getReceiptNumber())
                        .collectorName(c.getCollectorName())
                        .colony(c.getColony())
                        .collectionDate(c.getCollectionDate())
                        .remarks(c.getRemarks())
                        .createdAt(c.getCreatedAt())
                        .build())
                .collect(Collectors.toList());

        List<MemberDTO> recentMembers = memberRepository.findFirst5ByOrderByCreatedAtDesc()
                .stream()
                .map(m -> MemberDTO.builder()
                        .id(m.getId())
                        .name(m.getName())
                        .mobile(m.getMobile())
                        .whatsappNumber(m.getWhatsappNumber())
                        .email(m.getEmail())
                        .address(m.getAddress())
                        .colony(m.getColony())
                        .area(m.getArea())
                        .houseNumber(m.getHouseNumber())
                        .familyMembers(m.getFamilyMembers())
                        .occupation(m.getOccupation())
                        .profilePhoto(m.getProfilePhoto())
                        .status(m.getStatus())
                        .notes(m.getNotes())
                        .joinDate(m.getJoinDate())
                        .lastYearAmount(m.getLastYearAmount())
                        .createdAt(m.getCreatedAt())
                        .build())
                .collect(Collectors.toList());

        List<ActivityDTO> upcomingActivities = activityRepository.findByStatusOrderByDateAsc("Upcoming")
                .stream()
                .map(a -> ActivityDTO.builder()
                        .id(a.getId())
                        .title(a.getTitle())
                        .description(a.getDescription())
                        .date(a.getDate())
                        .time(a.getTime())
                        .venue(a.getVenue())
                        .organizer(a.getOrganizer())
                        .budget(a.getBudget())
                        .status(a.getStatus())
                        .bannerImage(a.getBannerImage())
                        .category(a.getCategory())
                        .createdAt(a.getCreatedAt())
                        .build())
                .collect(Collectors.toList());

        return RecentActivityDTO.builder()
                .recentDonations(recentDonations)
                .recentMembers(recentMembers)
                .upcomingActivities(upcomingActivities)
                .build();
    }
}
