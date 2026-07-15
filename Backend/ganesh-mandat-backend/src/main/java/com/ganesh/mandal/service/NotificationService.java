package com.ganesh.mandal.service;

import com.ganesh.mandal.dto.NotificationDashboardDTO;
import com.ganesh.mandal.dto.NotificationHistoryDTO;
import com.ganesh.mandal.dto.NotificationRequest;
import com.ganesh.mandal.entity.NotificationHistory;
import com.ganesh.mandal.event.NotificationEvent;
import com.ganesh.mandal.repository.NotificationHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationHistoryRepository historyRepository;
    private final EmailService emailService;
    private final WhatsAppService whatsAppService;
    private final ApplicationEventPublisher eventPublisher;

    public void sendAsync(NotificationRequest request) {
        eventPublisher.publishEvent(new NotificationEvent(this, request));
    }

    @Transactional
    public void processNotification(NotificationRequest request) {
        List<String> channels = request.getChannels();
        if (channels == null || channels.isEmpty()) {
            channels = List.of("WhatsApp", "Email");
        }
        List<String> receivers = request.getReceivers();
        if (receivers == null || receivers.isEmpty()) {
            log.warn("No receivers specified for notification");
            return;
        }
        for (String receiver : receivers) {
            for (String channel : channels) {
                String channelMessage = request.getCustomMessage() != null ? request.getCustomMessage() : buildMessageForChannel(request, channel);
                boolean success = sendViaChannel(receiver, channel, channelMessage, request);
                NotificationHistory history = NotificationHistory.builder()
                        .userId(request.getUserId())
                        .eventId(request.getEventId())
                        .notificationType(request.getNotificationType())
                        .channel(channel)
                        .receiver(receiver)
                        .message(channelMessage)
                        .status(success ? "Sent" : "Failed")
                        .sentTime(success ? LocalDateTime.now() : null)
                        .errorMessage(success ? null : "Failed to send via " + channel)
                        .build();
                historyRepository.save(history);
            }
        }
    }

    private boolean sendViaChannel(String receiver, String channel, String message, NotificationRequest request) {
        try {
            if ("Email".equalsIgnoreCase(channel)) {
                if (receiver.contains("@")) {
                    String subject = getEmailSubject(request);
                    return emailService.sendEmail(receiver, subject, message);
                }
                log.warn("Invalid email address: {}", receiver);
                return false;
            } else if ("WhatsApp".equalsIgnoreCase(channel)) {
                if (receiver.contains("@")) {
                    log.warn("Invalid WhatsApp number (looks like email): {}", receiver);
                    return false;
                }
                return whatsAppService.sendWhatsApp(receiver, message);
            }
            return false;
        } catch (Exception e) {
            log.error("Error sending via {} to {}: {}", channel, receiver, e.getMessage());
            return false;
        }
    }

    private String getEmailSubject(NotificationRequest request) {
        if (request.getNotificationType() == null) return "Notification";
        return switch (request.getNotificationType()) {
            case "Event_Creation" -> "New Event - Hindavi Swarajya Ganesh Festival";
            case "Reminder" -> "Reminder - Hindavi Swarajya Ganesh Festival";
            case "Registration" -> "🙏 Welcome to Hindavi Swarajya Family 🙏";
            case "Donation" -> "Donation Received - Hindavi Swarajya";
            default -> "Hindavi Swarajya Notification";
        };
    }

    private String buildMessageForChannel(NotificationRequest request, String channel) {
        String type = request.getNotificationType();
        if (type == null) return request.getCustomMessage() != null ? request.getCustomMessage() : "";
        boolean isEmail = "Email".equalsIgnoreCase(channel);
        return switch (type) {
            case "Registration" -> isEmail ? buildRegistrationEmail(request) : buildRegistrationWhatsApp(request);
            case "Event_Creation" -> isEmail ? buildEventEmail(request) : buildEventWhatsApp(request);
            case "Donation" -> isEmail ? buildDonationEmail(request) : buildDonationWhatsApp(request);
            case "Donation_Admin" -> isEmail ? buildDonationAdminEmail(request) : buildDonationAdminWhatsApp(request);
            default -> request.getCustomMessage() != null ? request.getCustomMessage() : "";
        };
    }

    private String buildRegistrationWhatsApp(NotificationRequest request) {
        String name = request.getDonorName() != null ? request.getDonorName() : "Valued Member";
        return """
                🙏 Welcome to Hindavi Swarajya 🙏
                
                Your registration is completed successfully.
                Name: %s
                
                Thank you for joining us!
                - Hindavi Swarajya Team
                """.formatted(name);
    }

    private String buildRegistrationEmail(NotificationRequest request) {
        String name = request.getDonorName() != null ? request.getDonorName() : "Valued Member";
        String mobile = request.getMobile() != null ? request.getMobile() : "";
        Long memberId = request.getUserId();
        String logoUrl = request.getLogoUrl() != null ? request.getLogoUrl() : "https://placehold.co/180x60/1a1a2e/ff9933?text=Hindavi+Swarajya";
        String bannerUrl = request.getBannerUrl() != null ? request.getBannerUrl() : "https://placehold.co/600x250/ff9933/ffffff?text=Ganesh+Festival";
        String websiteUrl = request.getWebsiteUrl() != null ? request.getWebsiteUrl() : "http://localhost:5173";
        String year = String.valueOf(java.time.Year.now().getValue());

        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Welcome to Hindavi Swarajya</title>
            </head>
            <body style="margin:0; padding:0; background-color:#fef9f0; font-family:'Segoe UI',Tahoma,Geneva,Verdana,sans-serif;">
                <table width="100%%" cellpadding="0" cellspacing="0" style="background-color:#fef9f0;">
                    <tr>
                        <td align="center" style="padding:20px 10px;">
                            <table width="600" cellpadding="0" cellspacing="0" style="max-width:600px; width:100%%; background-color:#ffffff; border-radius:16px; box-shadow:0 4px 24px rgba(0,0,0,0.08); border:1px solid #f0e0c0;">
                                
                                <!-- Top Decorative Border -->
                                <tr>
                                    <td style="background: linear-gradient(90deg, #ff9933, #d32f2f, #ffd700, #d32f2f, #ff9933); height:6px; border-radius:16px 16px 0 0;"></td>
                                </tr>
                                
                                <!-- Header with Logo -->
                                <tr>
                                    <td align="center" style="padding:30px 20px 10px;">
                                        <img src="%s" alt="Hindavi Swarajya" style="width:160px; height:auto; display:block;" />
                                        <h1 style="color:#1a1a2e; font-size:22px; margin:10px 0 2px; letter-spacing:1px;">Hindavi Swarajya</h1>
                                        <p style="color:#b8860b; font-size:13px; margin:0; letter-spacing:2px; text-transform:uppercase;">Ganesh Festival Management System</p>
                                    </td>
                                </tr>
                                
                                <!-- Ganesha Banner -->
                                <tr>
                                    <td align="center" style="padding:20px 20px 10px;">
                                        <img src="%s" alt="Lord Ganesha" style="width:100%%; max-width:560px; height:auto; border-radius:12px; display:block; box-shadow:0 4px 12px rgba(0,0,0,0.1);" />
                                    </td>
                                </tr>
                                
                                <!-- Welcome Message -->
                                <tr>
                                    <td align="center" style="padding:25px 20px 10px;">
                                        <div style="font-size:48px; line-height:1;">🪷</div>
                                        <h2 style="color:#d32f2f; font-size:28px; margin:10px 0 5px;">🙏 Welcome to<br/>Hindavi Swarajya Family 🙏</h2>
                                        <div style="width:80px; height:3px; background:linear-gradient(90deg,#ff9933,#d32f2f,#ffd700); margin:12px auto; border-radius:2px;"></div>
                                        <p style="color:#555; font-size:15px; line-height:1.7; margin:10px 20px 0; max-width:480px;">
                                            Dear <strong style="color:#1a1a2e;">%s</strong>,<br/><br/>
                                            Thank you for registering with <strong>Hindavi Swarajya Ganesh Festival</strong>. 
                                            We are delighted to welcome you to our community. Together, we will make this 
                                            festival a grand success with your support and participation.
                                        </p>
                                    </td>
                                </tr>
                                
                                <!-- Member Info Card -->
                                <tr>
                                    <td align="center" style="padding:15px 20px;">
                                        <table width="100%%" cellpadding="0" cellspacing="0" style="background:linear-gradient(135deg,#fef9f0,#fff3e0); border-radius:12px; border:1px solid #e8d5a3; max-width:520px;">
                                            <tr>
                                                <td style="padding:20px;">
                                                    <h3 style="color:#b8860b; font-size:16px; margin:0 0 15px; text-align:center; letter-spacing:1px;">✦ MEMBER INFORMATION ✦</h3>
                                                    <table width="100%%" cellpadding="0" cellspacing="0">
                                                        <tr>
                                                            <td style="padding:6px 0; color:#888; font-size:13px; width:40px;">👤</td>
                                                            <td style="padding:6px 0; color:#888; font-size:13px; width:100px;">Name</td>
                                                            <td style="padding:6px 0; color:#1a1a2e; font-size:14px; font-weight:600;">%s</td>
                                                        </tr>
                                                        <tr><td colspan="3" style="border-bottom:1px dashed #e8d5a3; height:1px;"></td></tr>
                                                        <tr>
                                                            <td style="padding:6px 0; color:#888; font-size:13px;">📧</td>
                                                            <td style="padding:6px 0; color:#888; font-size:13px;">Email</td>
                                                            <td style="padding:6px 0; color:#1a1a2e; font-size:14px;">%s</td>
                                                        </tr>
                                                        <tr><td colspan="3" style="border-bottom:1px dashed #e8d5a3; height:1px;"></td></tr>
                                                        <tr>
                                                            <td style="padding:6px 0; color:#888; font-size:13px;">📱</td>
                                                            <td style="padding:6px 0; color:#888; font-size:13px;">Mobile</td>
                                                            <td style="padding:6px 0; color:#1a1a2e; font-size:14px;">%s</td>
                                                        </tr>
                                                        <tr><td colspan="3" style="border-bottom:1px dashed #e8d5a3; height:1px;"></td></tr>
                                                        <tr>
                                                            <td style="padding:6px 0; color:#888; font-size:13px;">🆔</td>
                                                            <td style="padding:6px 0; color:#888; font-size:13px;">Member ID</td>
                                                            <td style="padding:6px 0; color:#1a1a2e; font-size:14px; font-weight:600;">HSF-%s</td>
                                                        </tr>
                                                        <tr><td colspan="3" style="border-bottom:1px dashed #e8d5a3; height:1px;"></td></tr>
                                                        <tr>
                                                            <td style="padding:6px 0; color:#888; font-size:13px;">📅</td>
                                                            <td style="padding:6px 0; color:#888; font-size:13px;">Registered</td>
                                                            <td style="padding:6px 0; color:#1a1a2e; font-size:14px;">%s</td>
                                                        </tr>
                                                    </table>
                                                </td>
                                            </tr>
                                        </table>
                                    </td>
                                </tr>
                                
                                <!-- Features Section -->
                                <tr>
                                    <td align="center" style="padding:20px 20px 10px;">
                                        <h3 style="color:#1a1a2e; font-size:18px; margin:0 0 15px;">🌟 What You Can Do 🌟</h3>
                                        <table width="100%%" cellpadding="0" cellspacing="0" style="max-width:520px;">
                                            <tr>
                                                <td width="50%%" style="padding:5px;">
                                                    <table width="100%%" cellpadding="8" cellspacing="0" style="background:#fef9f0; border-radius:8px; border:1px solid #f0e0c0;">
                                                        <tr><td align="center" style="font-size:24px; padding-bottom:0;">📅</td></tr>
                                                        <tr><td align="center" style="color:#1a1a2e; font-size:12px; font-weight:600; padding-top:0;">View Festival Events</td></tr>
                                                    </table>
                                                </td>
                                                <td width="50%%" style="padding:5px;">
                                                    <table width="100%%" cellpadding="8" cellspacing="0" style="background:#fef9f0; border-radius:8px; border:1px solid #f0e0c0;">
                                                        <tr><td align="center" style="font-size:24px; padding-bottom:0;">🎉</td></tr>
                                                        <tr><td align="center" style="color:#1a1a2e; font-size:12px; font-weight:600; padding-top:0;">Cultural Programs</td></tr>
                                                    </table>
                                                </td>
                                            </tr>
                                            <tr>
                                                <td width="50%%" style="padding:5px;">
                                                    <table width="100%%" cellpadding="8" cellspacing="0" style="background:#fef9f0; border-radius:8px; border:1px solid #f0e0c0;">
                                                        <tr><td align="center" style="font-size:24px; padding-bottom:0;">💰</td></tr>
                                                        <tr><td align="center" style="color:#1a1a2e; font-size:12px; font-weight:600; padding-top:0;">Donate Online</td></tr>
                                                    </table>
                                                </td>
                                                <td width="50%%" style="padding:5px;">
                                                    <table width="100%%" cellpadding="8" cellspacing="0" style="background:#fef9f0; border-radius:8px; border:1px solid #f0e0c0;">
                                                        <tr><td align="center" style="font-size:24px; padding-bottom:0;">🤝</td></tr>
                                                        <tr><td align="center" style="color:#1a1a2e; font-size:12px; font-weight:600; padding-top:0;">Become Volunteer</td></tr>
                                                    </table>
                                                </td>
                                            </tr>
                                            <tr>
                                                <td width="50%%" style="padding:5px;">
                                                    <table width="100%%" cellpadding="8" cellspacing="0" style="background:#fef9f0; border-radius:8px; border:1px solid #f0e0c0;">
                                                        <tr><td align="center" style="font-size:24px; padding-bottom:0;">📢</td></tr>
                                                        <tr><td align="center" style="color:#1a1a2e; font-size:12px; font-weight:600; padding-top:0;">WhatsApp &amp; Email Updates</td></tr>
                                                    </table>
                                                </td>
                                                <td width="50%%" style="padding:5px;">
                                                    <table width="100%%" cellpadding="8" cellspacing="0" style="background:#fef9f0; border-radius:8px; border:1px solid #f0e0c0;">
                                                        <tr><td align="center" style="font-size:24px; padding-bottom:0;">🍛</td></tr>
                                                        <tr><td align="center" style="color:#1a1a2e; font-size:12px; font-weight:600; padding-top:0;">View Prasad Schedule</td></tr>
                                                    </table>
                                                </td>
                                            </tr>
                                            <tr>
                                                <td width="50%%" style="padding:5px;">
                                                    <table width="100%%" cellpadding="8" cellspacing="0" style="background:#fef9f0; border-radius:8px; border:1px solid #f0e0c0;">
                                                        <tr><td align="center" style="font-size:24px; padding-bottom:0;">🛕</td></tr>
                                                        <tr><td align="center" style="color:#1a1a2e; font-size:12px; font-weight:600; padding-top:0;">Ganesh Murti Details</td></tr>
                                                    </table>
                                                </td>
                                                <td width="50%%" style="padding:5px;">
                                                    <table width="100%%" cellpadding="8" cellspacing="0" style="background:#fef9f0; border-radius:8px; border:1px solid #f0e0c0;">
                                                        <tr><td align="center" style="font-size:24px; padding-bottom:0;">❤️</td></tr>
                                                        <tr><td align="center" style="color:#1a1a2e; font-size:12px; font-weight:600; padding-top:0;">Community Activities</td></tr>
                                                    </table>
                                                </td>
                                            </tr>
                                        </table>
                                    </td>
                                </tr>
                                
                                <!-- CTA Button -->
                                <tr>
                                    <td align="center" style="padding:25px 20px;">
                                        <table cellpadding="0" cellspacing="0" style="border-radius:50px; background:linear-gradient(135deg,#ff9933,#d32f2f); box-shadow:0 4px 15px rgba(211,47,47,0.3);">
                                            <tr>
                                                <td align="center" style="padding:14px 40px;">
                                                    <a href="%s" style="color:#ffffff; font-size:16px; font-weight:700; text-decoration:none; letter-spacing:1px; display:inline-block;">🚀 Visit Dashboard</a>
                                                </td>
                                            </tr>
                                        </table>
                                        <p style="color:#999; font-size:12px; margin:10px 0 0;">Click the button above to access your dashboard</p>
                                    </td>
                                </tr>
                                
                                <!-- Divider -->
                                <tr>
                                    <td align="center" style="padding:0 20px;">
                                        <div style="width:100%%; height:1px; background:linear-gradient(90deg,transparent,#e8d5a3,#d32f2f,#e8d5a3,transparent);"></div>
                                    </td>
                                </tr>
                                
                                <!-- Footer -->
                                <tr>
                                    <td align="center" style="padding:20px 20px 10px;">
                                        <p style="color:#1a1a2e; font-size:14px; margin:0 0 5px; font-weight:600;">Hindavi Swarajya</p>
                                        <p style="color:#b8860b; font-size:12px; margin:0 0 10px;">Ganesh Festival Management System</p>
                                        <p style="color:#888; font-size:12px; margin:3px 0;">📞 Contact: +91 9876543210</p>
                                        <p style="color:#888; font-size:12px; margin:3px 0;">✉️ Email: info@hindaviswarajya.com</p>
                                        <p style="color:#888; font-size:12px; margin:3px 0 15px;">🌐 %s</p>
                                        
                                        <!-- Social Icons -->
                                        <table cellpadding="0" cellspacing="0" align="center">
                                            <tr>
                                                <td style="padding:0 5px;"><a href="#" style="text-decoration:none;"><span style="display:inline-block; width:32px; height:32px; line-height:32px; text-align:center; background:#1877f2; color:#fff; border-radius:50%%; font-size:14px;">f</span></a></td>
                                                <td style="padding:0 5px;"><a href="#" style="text-decoration:none;"><span style="display:inline-block; width:32px; height:32px; line-height:32px; text-align:center; background:#e4405f; color:#fff; border-radius:50%%; font-size:14px;">ig</span></a></td>
                                                <td style="padding:0 5px;"><a href="#" style="text-decoration:none;"><span style="display:inline-block; width:32px; height:32px; line-height:32px; text-align:center; background:#ff0000; color:#fff; border-radius:50%%; font-size:14px;">▶</span></a></td>
                                                <td style="padding:0 5px;"><a href="#" style="text-decoration:none;"><span style="display:inline-block; width:32px; height:32px; line-height:32px; text-align:center; background:#25d366; color:#fff; border-radius:50%%; font-size:14px;">WA</span></a></td>
                                            </tr>
                                        </table>
                                        
                                        <!-- Bottom Links -->
                                        <table cellpadding="0" cellspacing="0" align="center" style="margin-top:15px;">
                                            <tr>
                                                <td style="padding:0 8px; border-right:1px solid #ddd;"><a href="#" style="color:#888; font-size:11px; text-decoration:none;">Privacy Policy</a></td>
                                                <td style="padding:0 8px;"><a href="#" style="color:#888; font-size:11px; text-decoration:none;">Terms of Service</a></td>
                                            </tr>
                                        </table>
                                        
                                        <p style="color:#bbb; font-size:11px; margin:15px 0 0;">&copy; %s Hindavi Swarajya. All rights reserved.</p>
                                    </td>
                                </tr>
                                
                                <!-- Bottom Decorative Border -->
                                <tr>
                                    <td style="background: linear-gradient(90deg, #ff9933, #d32f2f, #ffd700, #d32f2f, #ff9933); height:6px; border-radius:0 0 16px 16px;"></td>
                                </tr>
                            </table>
                            
                            <!-- Invisible tracking / simple note -->
                            <p style="color:#ccc; font-size:10px; margin-top:10px;">This email was sent to %s. You are receiving this because you registered with Hindavi Swarajya.</p>
                        </td>
                    </tr>
                </table>
            </body>
            </html>
            """.formatted(
                logoUrl, bannerUrl, name, name,
                request.getEmail() != null ? request.getEmail() : "",
                mobile,
                memberId != null ? String.format("%04d", memberId) : "0000",
                java.time.LocalDate.now().toString(),
                websiteUrl, websiteUrl, year,
                request.getEmail() != null ? request.getEmail() : ""
            );
    }

    private String buildEventWhatsApp(NotificationRequest request) {
        return """
                🎉 New Event Added!
                
                %s
                
                Stay tuned for more details.
                - Hindavi Swarajya Team
                """.formatted(request.getCustomMessage() != null ? request.getCustomMessage() : "A new event has been scheduled.");
    }

    private String buildEventEmail(NotificationRequest request) {
        return """
            <h2 style="color:#d32f2f;">🎉 New Event Added</h2>
            <p>A new event has been scheduled for the Ganesh Festival.</p>
            <p>%s</p>
            <p>Stay tuned for more details.</p>
            """.formatted(request.getCustomMessage() != null ? request.getCustomMessage() : "");
    }

    private String buildDonationWhatsApp(NotificationRequest request) {
        String name = request.getDonorName() != null ? request.getDonorName() : "Donor";
        String amount = request.getAmount() != null ? request.getAmount() : "0";
        return """
                🙏 Thank You For Your Donation 🙏
                
                Donation Received Successfully!
                Name: %s
                Amount: ₹%s
                
                Your contribution helps us organize the festival.
                - Hindavi Swarajya Team
                """.formatted(name, amount);
    }

    private String buildDonationEmail(NotificationRequest request) {
        String name = request.getDonorName() != null ? request.getDonorName() : "Donor";
        String amount = request.getAmount() != null ? request.getAmount() : "0";
        String mode = request.getPaymentMode() != null ? request.getPaymentMode() : "";
        String date = request.getDate() != null ? request.getDate() : "";
        return """
            <h2 style="color:#d32f2f;">🙏 Thank You For Your Donation</h2>
            <p>Donation Received Successfully.</p>
            <table style="border:1px solid #ddd; border-radius:8px; padding:15px; background:#fef9f0;">
                <tr><td><strong>Name:</strong></td><td>%s</td></tr>
                <tr><td><strong>Amount:</strong></td><td>₹%s</td></tr>
                <tr><td><strong>Mode:</strong></td><td>%s</td></tr>
                <tr><td><strong>Date:</strong></td><td>%s</td></tr>
            </table>
            <p>Your contribution helps us organize the festival successfully.</p>
            <p>- Hindavi Swarajya Team</p>
            """.formatted(name, amount, mode, date);
    }

    private String buildDonationAdminWhatsApp(NotificationRequest request) {
        return buildDonationWhatsApp(request);
    }

    private String buildDonationAdminEmail(NotificationRequest request) {
        String name = request.getDonorName() != null ? request.getDonorName() : "Donor";
        String amount = request.getAmount() != null ? request.getAmount() : "0";
        return """
            <h2>New Donation Received</h2>
            <p>Donor Name: %s</p>
            <p>Amount: ₹%s</p>
            """.formatted(name, amount);
    }

    @Transactional
    public NotificationHistoryDTO resend(Long notificationId) {
        NotificationHistory h = historyRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found with id: " + notificationId));
        boolean success = sendViaChannel(h.getReceiver(), h.getChannel(), h.getMessage(), new NotificationRequest());
        h.setStatus(success ? "Sent" : "Failed");
        h.setSentTime(success ? LocalDateTime.now() : null);
        h.setErrorMessage(success ? null : "Retry failed");
        return toDTO(historyRepository.save(h));
    }

    public NotificationDashboardDTO getDashboard() {
        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.atTime(LocalTime.MAX);
        return NotificationDashboardDTO.builder()
                .totalSent(historyRepository.count())
                .whatsappSent(historyRepository.countByChannel("WhatsApp"))
                .emailSent(historyRepository.countByChannel("Email"))
                .failed(historyRepository.countByStatus("Failed"))
                .pending(historyRepository.countByStatus("Pending"))
                .todayCount(historyRepository.countByCreatedAtBetween(start, end))
                .build();
    }

    public List<NotificationHistoryDTO> getHistory(String status, String channel, Long eventId, Long userId,
                                                    LocalDate dateFrom, LocalDate dateTo) {
        List<NotificationHistory> all = historyRepository.findAll();
        var stream = all.stream();
        if (status != null) stream = stream.filter(h -> status.equals(h.getStatus()));
        if (channel != null) stream = stream.filter(h -> channel.equals(h.getChannel()));
        if (eventId != null) stream = stream.filter(h -> eventId.equals(h.getEventId()));
        if (userId != null) stream = stream.filter(h -> userId.equals(h.getUserId()));
        if (dateFrom != null) stream = stream.filter(h -> h.getCreatedAt() != null && !h.getCreatedAt().toLocalDate().isBefore(dateFrom));
        if (dateTo != null) stream = stream.filter(h -> h.getCreatedAt() != null && !h.getCreatedAt().toLocalDate().isAfter(dateTo));
        return stream.map(this::toDTO).collect(Collectors.toList());
    }

    public NotificationHistoryDTO getById(Long id) {
        return toDTO(historyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found with id: " + id)));
    }

    private NotificationHistoryDTO toDTO(NotificationHistory h) {
        return NotificationHistoryDTO.builder()
                .id(h.getId()).userId(h.getUserId()).eventId(h.getEventId())
                .notificationType(h.getNotificationType()).channel(h.getChannel())
                .receiver(h.getReceiver()).message(h.getMessage())
                .status(h.getStatus()).errorMessage(h.getErrorMessage())
                .sentTime(h.getSentTime()).createdAt(h.getCreatedAt())
                .build();
    }
}
