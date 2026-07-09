package com.ganesh.mandal.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDashboardDTO {
    private long totalSent;
    private long whatsappSent;
    private long emailSent;
    private long failed;
    private long pending;
    private long todayCount;
}
