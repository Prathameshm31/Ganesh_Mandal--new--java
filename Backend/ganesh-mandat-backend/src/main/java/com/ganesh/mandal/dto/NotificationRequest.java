package com.ganesh.mandal.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequest {
    private Long eventId;
    private String notificationType;
    private List<String> receivers;
    private String receiverGroup;
    private List<String> channels;
    private String customMessage;
    private Long userId;
    private Long donationId;
    private String donorName;
    private String amount;
    private String paymentMode;
    private String date;
    private String email;
    private String logoUrl;
    private String bannerUrl;
    private String websiteUrl;
}
