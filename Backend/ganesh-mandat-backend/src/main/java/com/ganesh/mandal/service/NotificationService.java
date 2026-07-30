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
            case "Activity_Creation" -> "New Activity - Hindavi Swarajya Ganesh Festival";
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
            case "Activity_Creation" -> isEmail ? buildActivityEmail(request) : buildActivityWhatsApp(request);
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
        String email = request.getEmail() != null ? request.getEmail() : "";
        Long memberId = request.getUserId();
        String tempPassword = request.getTempPassword() != null ? request.getTempPassword() : "";
        String logoUrl = request.getLogoUrl() != null ? request.getLogoUrl() : "https://ganesh-mandal-new-react-tan.vercel.app/assets/hindavi-swarajya-logo.80462267.png";
        String websiteUrl = request.getWebsiteUrl() != null ? request.getWebsiteUrl() : "http://localhost:5173";
        String loginUrl = "https://ganesh-mandal-new-react-tan.vercel.app/login";
        String year = String.valueOf(java.time.Year.now().getValue());
        String registerDate = java.time.LocalDate.now().toString();
        String memberIdFormatted = memberId != null ? String.format("HSF-%04d", memberId) : "HSF-0000";

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
                                        <div style="font-size:48px; line-height:1;">🎉</div>
                                        <h2 style="color:#d32f2f; font-size:28px; margin:10px 0 5px;">🙏 Welcome to<br/>Hindavi Swarajya Family 🙏</h2>
                                        <div style="width:80px; height:3px; background:linear-gradient(90deg,#ff9933,#d32f2f,#ffd700); margin:12px auto; border-radius:2px;"></div>
                                        <p style="color:#555; font-size:15px; line-height:1.7; margin:10px 20px 0; max-width:480px;">
                                            Dear <strong style="color:#1a1a2e;">%s</strong>,<br/><br/>
                                            🙏 <strong>Jai Ganesh!</strong><br/><br/>
                                            Thank you for registering with <strong>Hindavi Swarajya</strong>.
                                            We are delighted to welcome you to our family.<br/><br/>
                                            Your account has been created successfully, and you can now access the portal
                                            to stay connected with all festival activities, events, donations, volunteers,
                                            announcements, and much more.
                                        </p>
                                    </td>
                                </tr>

                                <!-- Member Info Card -->
                                <tr>
                                    <td align="center" style="padding:15px 20px;">
                                        <table width="100%%" cellpadding="0" cellspacing="0" style="background:linear-gradient(135deg,#fef9f0,#fff3e0); border-radius:12px; border:1px solid #e8d5a3; max-width:520px;">
                                            <tr>
                                                <td style="padding:20px;">
                                                    <h3 style="color:#b8860b; font-size:16px; margin:0 0 15px; text-align:center; letter-spacing:1px;">✦ MEMBER DETAILS ✦</h3>
                                                    <table width="100%%" cellpadding="0" cellspacing="0">
                                                        <tr>
                                                            <td style="padding:6px 0; color:#888; font-size:13px; width:40px;">🆔</td>
                                                            <td style="padding:6px 0; color:#888; font-size:13px; width:120px;">Member ID</td>
                                                            <td style="padding:6px 0; color:#1a1a2e; font-size:14px; font-weight:600;">%s</td>
                                                        </tr>
                                                        <tr><td colspan="3" style="border-bottom:1px dashed #e8d5a3; height:1px;"></td></tr>
                                                        <tr>
                                                            <td style="padding:6px 0; color:#888; font-size:13px;">👤</td>
                                                            <td style="padding:6px 0; color:#888; font-size:13px;">Name</td>
                                                            <td style="padding:6px 0; color:#1a1a2e; font-size:14px;">%s</td>
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
                                                            <td style="padding:6px 0; color:#888; font-size:13px;">Mobile Number</td>
                                                            <td style="padding:6px 0; color:#1a1a2e; font-size:14px;">%s</td>
                                                        </tr>
                                                        <tr><td colspan="3" style="border-bottom:1px dashed #e8d5a3; height:1px;"></td></tr>
                                                        <tr>
                                                            <td style="padding:6px 0; color:#888; font-size:13px;">🎭</td>
                                                            <td style="padding:6px 0; color:#888; font-size:13px;">Role</td>
                                                            <td style="padding:6px 0; color:#1a1a2e; font-size:14px;">User</td>
                                                        </tr>
                                                        <tr><td colspan="3" style="border-bottom:1px dashed #e8d5a3; height:1px;"></td></tr>
                                                        <tr>
                                                            <td style="padding:6px 0; color:#888; font-size:13px;">📅</td>
                                                            <td style="padding:6px 0; color:#888; font-size:13px;">Registration Date</td>
                                                            <td style="padding:6px 0; color:#1a1a2e; font-size:14px;">%s</td>
                                                        </tr>
                                                    </table>
                                                </td>
                                            </tr>
                                        </table>
                                    </td>
                                </tr>

                                <!-- Login Credentials Card -->
                                <tr>
                                    <td align="center" style="padding:15px 20px;">
                                        <table width="100%%" cellpadding="0" cellspacing="0" style="background:#fff8e7; border-radius:12px; border:1px solid #e8d5a3; max-width:520px;">
                                            <tr>
                                                <td style="padding:20px;">
                                                    <h3 style="color:#d32f2f; font-size:16px; margin:0 0 15px; text-align:center; letter-spacing:1px;">🔐 LOGIN CREDENTIALS</h3>
                                                    <table width="100%%" cellpadding="0" cellspacing="0">
                                                        <tr>
                                                            <td style="padding:6px 0; color:#888; font-size:13px; width:40px;">👤</td>
                                                            <td style="padding:6px 0; color:#888; font-size:13px; width:130px;">Username (Email)</td>
                                                            <td style="padding:6px 0; color:#1a1a2e; font-size:14px; font-weight:600;">%s</td>
                                                        </tr>
                                                        <tr><td colspan="3" style="border-bottom:1px dashed #e8d5a3; height:1px;"></td></tr>
                                                        <tr>
                                                            <td style="padding:6px 0; color:#888; font-size:13px;">🔑</td>
                                                            <td style="padding:6px 0; color:#888; font-size:13px;">Temporary Password</td>
                                                            <td style="padding:6px 0; color:#d32f2f; font-size:14px; font-weight:700;">%s</td>
                                                        </tr>
                                                    </table>
                                                    <p style="color:#d32f2f; font-size:12px; margin:12px 0 0; text-align:center; background:#fef0f0; padding:8px; border-radius:6px;">
                                                        ⚠️ For your security, please log in and change your password immediately after first login.
                                                    </p>
                                                </td>
                                            </tr>
                                        </table>
                                    </td>
                                </tr>

                                <!-- Login Button -->
                                <tr>
                                    <td align="center" style="padding:10px 20px 20px;">
                                        <table cellpadding="0" cellspacing="0" style="border-radius:50px; background:linear-gradient(135deg,#ff9933,#d32f2f); box-shadow:0 4px 15px rgba(211,47,47,0.3);">
                                            <tr>
                                                <td align="center" style="padding:14px 40px;">
                                                    <a href="%s" style="color:#ffffff; font-size:16px; font-weight:700; text-decoration:none; letter-spacing:1px; display:inline-block;">🚀 Login to Your Account</a>
                                                </td>
                                            </tr>
                                        </table>
                                    </td>
                                </tr>

                                <!-- Divider -->
                                <tr>
                                    <td align="center" style="padding:0 20px;">
                                        <div style="width:100%%; height:1px; background:linear-gradient(90deg,transparent,#e8d5a3,#d32f2f,#e8d5a3,transparent);"></div>
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
                                                        <tr><td align="center" style="color:#1a1a2e; font-size:12px; font-weight:600; padding-top:0;">View Upcoming Events</td></tr>
                                                    </table>
                                                </td>
                                                <td width="50%%" style="padding:5px;">
                                                    <table width="100%%" cellpadding="8" cellspacing="0" style="background:#fef9f0; border-radius:8px; border:1px solid #f0e0c0;">
                                                        <tr><td align="center" style="font-size:24px; padding-bottom:0;">🛕</td></tr>
                                                        <tr><td align="center" style="color:#1a1a2e; font-size:12px; font-weight:600; padding-top:0;">Ganesh Murti Details</td></tr>
                                                    </table>
                                                </td>
                                            </tr>
                                            <tr>
                                                <td width="50%%" style="padding:5px;">
                                                    <table width="100%%" cellpadding="8" cellspacing="0" style="background:#fef9f0; border-radius:8px; border:1px solid #f0e0c0;">
                                                        <tr><td align="center" style="font-size:24px; padding-bottom:0;">🍛</td></tr>
                                                        <tr><td align="center" style="color:#1a1a2e; font-size:12px; font-weight:600; padding-top:0;">Prasad Schedule</td></tr>
                                                    </table>
                                                </td>
                                                <td width="50%%" style="padding:5px;">
                                                    <table width="100%%" cellpadding="8" cellspacing="0" style="background:#fef9f0; border-radius:8px; border:1px solid #f0e0c0;">
                                                        <tr><td align="center" style="font-size:24px; padding-bottom:0;">💰</td></tr>
                                                        <tr><td align="center" style="color:#1a1a2e; font-size:12px; font-weight:600; padding-top:0;">Make Online Donations</td></tr>
                                                    </table>
                                                </td>
                                            </tr>
                                            <tr>
                                                <td width="50%%" style="padding:5px;">
                                                    <table width="100%%" cellpadding="8" cellspacing="0" style="background:#fef9f0; border-radius:8px; border:1px solid #f0e0c0;">
                                                        <tr><td align="center" style="font-size:24px; padding-bottom:0;">🤝</td></tr>
                                                        <tr><td align="center" style="color:#1a1a2e; font-size:12px; font-weight:600; padding-top:0;">Register as Volunteer</td></tr>
                                                    </table>
                                                </td>
                                                <td width="50%%" style="padding:5px;">
                                                    <table width="100%%" cellpadding="8" cellspacing="0" style="background:#fef9f0; border-radius:8px; border:1px solid #f0e0c0;">
                                                        <tr><td align="center" style="font-size:24px; padding-bottom:0;">📢</td></tr>
                                                        <tr><td align="center" style="color:#1a1a2e; font-size:12px; font-weight:600; padding-top:0;">WhatsApp &amp; Email Updates</td></tr>
                                                    </table>
                                                </td>
                                            </tr>
                                            <tr>
                                                <td width="50%%" style="padding:5px;">
                                                    <table width="100%%" cellpadding="8" cellspacing="0" style="background:#fef9f0; border-radius:8px; border:1px solid #f0e0c0;">
                                                        <tr><td align="center" style="font-size:24px; padding-bottom:0;">🖼️</td></tr>
                                                        <tr><td align="center" style="color:#1a1a2e; font-size:12px; font-weight:600; padding-top:0;">View Gallery &amp; Announcements</td></tr>
                                                    </table>
                                                </td>
                                                <td width="50%%" style="padding:5px;">
                                                    <table width="100%%" cellpadding="8" cellspacing="0" style="background:#fef9f0; border-radius:8px; border:1px solid #f0e0c0;">
                                                        <tr><td align="center" style="font-size:24px; padding-bottom:0;">👤</td></tr>
                                                        <tr><td align="center" style="color:#1a1a2e; font-size:12px; font-weight:600; padding-top:0;">Update Profile &amp; Password</td></tr>
                                                    </table>
                                                </td>
                                            </tr>
                                        </table>
                                    </td>
                                </tr>

                                <!-- Thank You Section -->
                                <tr>
                                    <td align="center" style="padding:15px 20px 10px;">
                                        <p style="color:#555; font-size:14px; line-height:1.7; margin:0 10px; max-width:480px;">
                                            Thank you for becoming a part of the <strong>Hindavi Swarajya</strong> family.<br/>
                                            Together, let's celebrate <strong>Ganesh Utsav</strong> with devotion, unity, and enthusiasm.<br/><br/>
                                            If you have any questions or need assistance, please feel free to contact us.
                                        </p>
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
                                        <p style="color:#1a1a2e; font-size:14px; margin:0 0 5px; font-weight:600;">Hindavi Swarajya Team</p>
                                        <p style="color:#888; font-size:12px; margin:3px 0;">📞 Contact: +91 9876543210</p>
                                        <p style="color:#888; font-size:12px; margin:3px 0;">✉️ <a href="mailto:info@hindaviswarajya.com" style="color:#888; text-decoration:none;">info@hindaviswarajya.com</a></p>
                                        <p style="color:#888; font-size:12px; margin:3px 0 15px;">🌐 <a href="%s" style="color:#888; text-decoration:none;">%s</a></p>

                                        <!-- Social Icons -->
                                        <table cellpadding="0" cellspacing="0" align="center">
                                            <tr>
                                                <td style="padding:0 5px;"><a href="#" style="text-decoration:none;"><span style="display:inline-block; width:32px; height:32px; line-height:32px; text-align:center; background:#1877f2; color:#fff; border-radius:50%%; font-size:14px;">f</span></a></td>
                                                <td style="padding:0 5px;"><a href="https://www.instagram.com/hindavi._.swarajya" style="text-decoration:none;"><span style="display:inline-block; width:32px; height:32px; line-height:32px; text-align:center; background:#e4405f; color:#fff; border-radius:50%%; font-size:14px;">ig</span></a></td>
                                                <td style="padding:0 5px;"><a href="#" style="text-decoration:none;"><span style="display:inline-block; width:32px; height:32px; line-height:32px; text-align:center; background:#ff0000; color:#fff; border-radius:50%%; font-size:14px;">▶</span></a></td>
                                                <td style="padding:0 5px;"><a href="#" style="text-decoration:none;"><span style="display:inline-block; width:32px; height:32px; line-height:32px; text-align:center; background:#25d366; color:#fff; border-radius:50%%; font-size:14px;">WA</span></a></td>
                                            </tr>
                                        </table>

                                        <p style="color:#bbb; font-size:11px; margin:15px 0 0;">&copy; %s Hindavi Swarajya. All Rights Reserved.</p>
                                        <p style="color:#ccc; font-size:10px; margin-top:10px;">This email was sent to %s. You are receiving this because you registered with Hindavi Swarajya.</p>
                                    </td>
                                </tr>

                                <!-- Bottom Decorative Border -->
                                <tr>
                                    <td style="background: linear-gradient(90deg, #ff9933, #d32f2f, #ffd700, #d32f2f, #ff9933); height:6px; border-radius:0 0 16px 16px;"></td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                </table>
            </body>
            </html>
            """.formatted(
                logoUrl, logoUrl, name,
                memberIdFormatted, name,
                email, mobile,
                registerDate,
                email, tempPassword,
                loginUrl,
                websiteUrl, websiteUrl,
                year,
                email
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

    private String buildActivityWhatsApp(NotificationRequest request) {
        return """
                🎉 New Activity Added: %s
                
                Date: %s
                Time: %s
                Venue: %s
                
                Join us!
                - Hindavi Swarajya Team
                """.formatted(
                    request.getActivityName() != null ? request.getActivityName() : "TBA",
                    request.getDate() != null ? request.getDate() : "TBA",
                    request.getActivityTime() != null ? request.getActivityTime() : "TBA",
                    request.getActivityVenue() != null ? request.getActivityVenue() : "TBA"
                );
    }

    private String buildActivityEmail(NotificationRequest request) {
        String logoUrl = request.getLogoUrl() != null ? request.getLogoUrl() : "https://ganesh-mandal-new-react-tan.vercel.app/assets/hindavi-swarajya-logo.80462267.png";
        String bannerUrl = request.getBannerUrl() != null ? request.getBannerUrl() : "https://placehold.co/600x250/ff9933/ffffff?text=Ganesh+Festival";
        String websiteUrl = request.getWebsiteUrl() != null ? request.getWebsiteUrl() : "http://localhost:5173";
        String year = String.valueOf(java.time.Year.now().getValue());
        String mandalName = "Hindavi Swarajya";
        String activityName = request.getActivityName() != null ? request.getActivityName() : "TBA";
        String date = request.getDate() != null ? request.getDate() : "TBA";
        String time = request.getActivityTime() != null ? request.getActivityTime() : "TBA";
        String venue = request.getActivityVenue() != null ? request.getActivityVenue() : "TBA";
        
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>New Activity Announcement</title>
            </head>
            <body style="margin:0; padding:0; background-color:#fef9f0; font-family:'Segoe UI',Tahoma,Geneva,Verdana,sans-serif;">
                <table width="100%%" cellpadding="0" cellspacing="0" style="background-color:#fef9f0;">
                    <tr>
                        <td align="center" style="padding:20px 10px;">
                            <table width="600" cellpadding="0" cellspacing="0" style="max-width:600px; width:100%%; background-color:#ffffff; border-radius:16px; box-shadow:0 4px 24px rgba(0,0,0,0.08); border:1px solid #f0e0c0;">
                                <tr>
                                    <td style="background: linear-gradient(90deg, #ff9933, #d32f2f, #ffd700, #d32f2f, #ff9933); height:6px; border-radius:16px 16px 0 0;"></td>
                                </tr>
                                <tr>
                                    <td align="center" style="padding:30px 20px 10px;">
                                        <img src="%s" alt="Hindavi Swarajya" style="width:160px; height:auto; display:block;" />
                                        <h1 style="color:#1a1a2e; font-size:22px; margin:10px 0 2px; letter-spacing:1px;">Hindavi Swarajya</h1>
                                    </td>
                                </tr>
                                <tr>
                                    <td align="center" style="padding:20px 20px 10px;">
                                        <img src="%s" alt="New Activity" style="width:100%%; max-width:560px; height:auto; border-radius:12px; display:block; box-shadow:0 4px 12px rgba(0,0,0,0.1);" />
                                    </td>
                                </tr>
                                <tr>
                                    <td align="center" style="padding:25px 20px 10px;">
                                        <div style="font-size:48px; line-height:1;">🎉</div>
                                        <h2 style="color:#d32f2f; font-size:26px; margin:10px 0 5px;">New Activity Announcement!</h2>
                                        <div style="width:80px; height:3px; background:linear-gradient(90deg,#ff9933,#d32f2f,#ffd700); margin:12px auto; border-radius:2px;"></div>
                                        <p style="color:#555; font-size:15px; line-height:1.7; margin:10px 20px 0; max-width:480px; text-align:left;">
                                            Dear Team,<br/><br/>
                                            Greetings!<br/><br/>
                                            As part of our <strong>Ganesh Festival %s</strong> celebrations, we are delighted to introduce a new activity for all members and well-wishers.<br/><br/>
                                            We warmly invite you to participate and make this activity a grand success. Your enthusiasm and involvement will make the celebration even more memorable.
                                        </p>
                                    </td>
                                </tr>
                                <tr>
                                    <td align="center" style="padding:15px 20px;">
                                        <table width="100%%" cellpadding="0" cellspacing="0" style="background:linear-gradient(135deg,#fef9f0,#fff3e0); border-radius:12px; border:1px solid #e8d5a3; max-width:520px;">
                                            <tr>
                                                <td style="padding:20px;">
                                                    <h3 style="color:#b8860b; font-size:16px; margin:0 0 15px; text-align:center; letter-spacing:1px;">✦ ACTIVITY DETAILS ✦</h3>
                                                    <table width="100%%" cellpadding="0" cellspacing="0">
                                                        <tr>
                                                            <td style="padding:6px 0; color:#888; font-size:13px; width:40px;">📌</td>
                                                            <td style="padding:6px 0; color:#888; font-size:13px; width:130px;">Activity Name</td>
                                                            <td style="padding:6px 0; color:#d32f2f; font-size:16px; font-weight:700;">%s</td>
                                                        </tr>
                                                        <tr><td colspan="3" style="border-bottom:1px dashed #e8d5a3; height:1px;"></td></tr>
                                                        <tr>
                                                            <td style="padding:6px 0; color:#888; font-size:13px;">📅</td>
                                                            <td style="padding:6px 0; color:#888; font-size:13px;">Date</td>
                                                            <td style="padding:6px 0; color:#1a1a2e; font-size:14px;">%s</td>
                                                        </tr>
                                                        <tr><td colspan="3" style="border-bottom:1px dashed #e8d5a3; height:1px;"></td></tr>
                                                        <tr>
                                                            <td style="padding:6px 0; color:#888; font-size:13px;">⏰</td>
                                                            <td style="padding:6px 0; color:#888; font-size:13px;">Time</td>
                                                            <td style="padding:6px 0; color:#1a1a2e; font-size:14px;">%s</td>
                                                        </tr>
                                                        <tr><td colspan="3" style="border-bottom:1px dashed #e8d5a3; height:1px;"></td></tr>
                                                        <tr>
                                                            <td style="padding:6px 0; color:#888; font-size:13px;">📍</td>
                                                            <td style="padding:6px 0; color:#888; font-size:13px;">Venue</td>
                                                            <td style="padding:6px 0; color:#1a1a2e; font-size:14px;">%s</td>
                                                        </tr>
                                                    </table>
                                                </td>
                                            </tr>
                                        </table>
                                    </td>
                                </tr>
                                <tr>
                                    <td align="center" style="padding:25px 20px 10px;">
                                        <p style="color:#555; font-size:15px; line-height:1.7; margin:0 20px 10px; max-width:480px; text-align:left;">
                                            We request you all to join us and encourage your friends and family to participate as well. Let us come together to celebrate the spirit of Ganesh Chaturthi with devotion, joy, and unity.<br/><br/>
                                            We look forward to your active participation.
                                        </p>
                                    </td>
                                </tr>
                                <tr>
                                    <td align="center" style="padding:0 20px;">
                                        <div style="width:100%%; height:1px; background:linear-gradient(90deg,transparent,#e8d5a3,#d32f2f,#e8d5a3,transparent);"></div>
                                    </td>
                                </tr>
                                <tr>
                                    <td align="center" style="padding:20px 20px 10px;">
                                        <p style="color:#555; font-size:14px; margin:0 0 5px;">Warm Regards,</p>
                                        <p style="color:#1a1a2e; font-size:14px; margin:0 0 10px; font-weight:600;">Admin Team<br/>%s</p>
                                        <p style="color:#888; font-size:12px; margin:3px 0;">📍 Pune, Maharashtra</p>
                                        <p style="color:#888; font-size:12px; margin:3px 0;">📞 +91 9876543210</p>
                                        <p style="color:#888; font-size:12px; margin:3px 0;">📧 info@hindaviswarajya.com</p>
                                        <p style="color:#888; font-size:12px; margin:3px 0 15px;">🌐 %s</p>
                                        <h3 style="color:#d32f2f; font-size:16px; margin:15px 0;">Ganpati Bappa Morya! 🙏</h3>
                                        <p style="color:#bbb; font-size:11px; margin:15px 0 0;">&copy; %s %s. All rights reserved.</p>
                                    </td>
                                </tr>
                                <tr>
                                    <td style="background: linear-gradient(90deg, #ff9933, #d32f2f, #ffd700, #d32f2f, #ff9933); height:6px; border-radius:0 0 16px 16px;"></td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                </table>
            </body>
            </html>
            """.formatted(
                logoUrl, bannerUrl, year,
                activityName, date, time, venue,
                mandalName, websiteUrl, year, mandalName
            );
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
        String name = request.getDonorName() != null ? request.getDonorName() : "Valued Donor";
        String amount = request.getAmount() != null ? request.getAmount() : "0";
        String transactionId = request.getTransactionId() != null ? request.getTransactionId() : "N/A";
        String date = request.getDate() != null ? request.getDate() : "";
        String logoUrl = request.getLogoUrl() != null ? request.getLogoUrl() : "https://ganesh-mandal-new-react-tan.vercel.app/assets/hindavi-swarajya-logo.80462267.png";
        String bannerUrl = request.getBannerUrl() != null ? request.getBannerUrl() : "https://placehold.co/600x250/ff9933/ffffff?text=Ganesh+Festival";
        String websiteUrl = request.getWebsiteUrl() != null ? request.getWebsiteUrl() : "http://localhost:5173";
        String dashboardUrl = "https://ganesh-mandal-new-react-tan.vercel.app/login";
        String year = String.valueOf(java.time.Year.now().getValue());
        String mandalName = "Hindavi Swarajya";
        
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Thank You For Your Donation</title>
            </head>
            <body style="margin:0; padding:0; background-color:#fef9f0; font-family:'Segoe UI',Tahoma,Geneva,Verdana,sans-serif;">
                <table width="100%%" cellpadding="0" cellspacing="0" style="background-color:#fef9f0;">
                    <tr>
                        <td align="center" style="padding:20px 10px;">
                            <table width="600" cellpadding="0" cellspacing="0" style="max-width:600px; width:100%%; background-color:#ffffff; border-radius:16px; box-shadow:0 4px 24px rgba(0,0,0,0.08); border:1px solid #f0e0c0;">
                                <tr>
                                    <td style="background: linear-gradient(90deg, #ff9933, #d32f2f, #ffd700, #d32f2f, #ff9933); height:6px; border-radius:16px 16px 0 0;"></td>
                                </tr>
                                <tr>
                                    <td align="center" style="padding:30px 20px 10px;">
                                        <img src="%s" alt="Hindavi Swarajya" style="width:160px; height:auto; display:block;" />
                                        <h1 style="color:#1a1a2e; font-size:22px; margin:10px 0 2px; letter-spacing:1px;">Hindavi Swarajya</h1>

                                    </td>
                                </tr>
                                <tr>
                                    <td align="center" style="padding:20px 20px 10px;">
                                        <img src="%s" alt="Donation Thank You" style="width:100%%; max-width:560px; height:auto; border-radius:12px; display:block; box-shadow:0 4px 12px rgba(0,0,0,0.1);" />
                                    </td>
                                </tr>
                                <tr>
                                    <td align="center" style="padding:25px 20px 10px;">
                                        <div style="font-size:48px; line-height:1;">🌺</div>
                                        <h2 style="color:#d32f2f; font-size:26px; margin:10px 0 5px;">🙏 Thank You For Your Generosity 🙏</h2>
                                        <div style="width:80px; height:3px; background:linear-gradient(90deg,#ff9933,#d32f2f,#ffd700); margin:12px auto; border-radius:2px;"></div>
                                        <p style="color:#555; font-size:15px; line-height:1.7; margin:10px 20px 0; max-width:480px; text-align:left;">
                                            Dear <strong style="color:#1a1a2e;">%s</strong>,<br/><br/>
                                            🙏 <strong>Ganpati Bappa Morya!</strong><br/><br/>
                                            On behalf of <strong>%s</strong>, we sincerely thank you for your generous donation towards our <strong>Ganesh Chaturthi %s</strong> celebrations.<br/><br/>
                                            Your support and devotion play a vital role in making this festival a grand success. Because of your contribution, we can continue our traditions, organize cultural programs, serve devotees, and celebrate Lord Ganesha's arrival with great enthusiasm and devotion.
                                        </p>
                                    </td>
                                </tr>
                                <tr>
                                    <td align="center" style="padding:15px 20px;">
                                        <table width="100%%" cellpadding="0" cellspacing="0" style="background:linear-gradient(135deg,#fef9f0,#fff3e0); border-radius:12px; border:1px solid #e8d5a3; max-width:520px;">
                                            <tr>
                                                <td style="padding:20px;">
                                                    <h3 style="color:#b8860b; font-size:16px; margin:0 0 15px; text-align:center; letter-spacing:1px;">✦ DONATION DETAILS ✦</h3>
                                                    <table width="100%%" cellpadding="0" cellspacing="0">
                                                        <tr>
                                                            <td style="padding:6px 0; color:#888; font-size:13px; width:40px;">💰</td>
                                                            <td style="padding:6px 0; color:#888; font-size:13px; width:130px;">Donation Amount</td>
                                                            <td style="padding:6px 0; color:#d32f2f; font-size:16px; font-weight:700;">₹%s</td>
                                                        </tr>
                                                        <tr><td colspan="3" style="border-bottom:1px dashed #e8d5a3; height:1px;"></td></tr>
                                                        <tr>
                                                            <td style="padding:6px 0; color:#888; font-size:13px;">🧾</td>
                                                            <td style="padding:6px 0; color:#888; font-size:13px;">Transaction ID</td>
                                                            <td style="padding:6px 0; color:#1a1a2e; font-size:14px;">%s</td>
                                                        </tr>
                                                        <tr><td colspan="3" style="border-bottom:1px dashed #e8d5a3; height:1px;"></td></tr>
                                                        <tr>
                                                            <td style="padding:6px 0; color:#888; font-size:13px;">📅</td>
                                                            <td style="padding:6px 0; color:#888; font-size:13px;">Date</td>
                                                            <td style="padding:6px 0; color:#1a1a2e; font-size:14px;">%s</td>
                                                        </tr>
                                                    </table>
                                                </td>
                                            </tr>
                                        </table>
                                    </td>
                                </tr>
                                <tr>
                                    <td align="center" style="padding:25px 20px 10px;">
                                        <p style="color:#555; font-size:15px; line-height:1.7; margin:0 20px 10px; max-width:480px; text-align:left;">
                                            Your generosity is deeply appreciated, and we are grateful to have supporters like you in our community.<br/><br/>
                                            May <strong>Lord Ganesha</strong> bless you and your family with happiness, prosperity, good health, and success in every endeavor.<br/><br/>
                                            Thank you once again for being a part of this divine celebration.
                                        </p>
                                    </td>
                                </tr>
                                <tr>
                                    <td align="center" style="padding:25px 20px;">
                                        <table cellpadding="0" cellspacing="0" style="border-radius:50px; background:linear-gradient(135deg,#ff9933,#d32f2f); box-shadow:0 4px 15px rgba(211,47,47,0.3);">
                                            <tr>
                                                <td align="center" style="padding:14px 40px;">
                                                    <a href="%s" style="color:#ffffff; font-size:16px; font-weight:700; text-decoration:none; letter-spacing:1px; display:inline-block;">🚀 Visit Dashboard</a>
                                                </td>
                                            </tr>
                                        </table>
                                    </td>
                                </tr>
                                <tr>
                                    <td align="center" style="padding:0 20px;">
                                        <div style="width:100%%; height:1px; background:linear-gradient(90deg,transparent,#e8d5a3,#d32f2f,#e8d5a3,transparent);"></div>
                                    </td>
                                </tr>
                                <tr>
                                    <td align="center" style="padding:20px 20px 10px;">
                                        <p style="color:#555; font-size:14px; margin:0 0 5px;">With gratitude,</p>
                                        <p style="color:#1a1a2e; font-size:14px; margin:0 0 10px; font-weight:600;">%s</p>
                                        <p style="color:#888; font-size:12px; margin:3px 0;">📍 Pune, Maharashtra</p>
                                        <p style="color:#888; font-size:12px; margin:3px 0;">📞 +91 9876543210</p>
                                        <p style="color:#888; font-size:12px; margin:3px 0;">📧 info@hindaviswarajya.com</p>
                                        <p style="color:#888; font-size:12px; margin:3px 0 15px;">🌐 %s</p>
                                        <h3 style="color:#d32f2f; font-size:16px; margin:15px 0;">Ganpati Bappa Morya! Mangal Murti Morya! 🙏</h3>
                                        <table cellpadding="0" cellspacing="0" align="center">
                                            <tr>
                                                <td style="padding:0 5px;"><a href="#" style="text-decoration:none;"><span style="display:inline-block; width:32px; height:32px; line-height:32px; text-align:center; background:#1877f2; color:#fff; border-radius:50%%; font-size:14px;">f</span></a></td>
                                                <td style="padding:0 5px;"><a href="https://www.instagram.com/hindavi._.swarajya?igsh=MWhnZW9mNmFwYjI2Zg==" style="text-decoration:none;"><span style="display:inline-block; width:32px; height:32px; line-height:32px; text-align:center; background:#e4405f; color:#fff; border-radius:50%%; font-size:14px;">ig</span></a></td>
                                                <td style="padding:0 5px;"><a href="#" style="text-decoration:none;"><span style="display:inline-block; width:32px; height:32px; line-height:32px; text-align:center; background:#ff0000; color:#fff; border-radius:50%%; font-size:14px;">▶</span></a></td>
                                                <td style="padding:0 5px;"><a href="#" style="text-decoration:none;"><span style="display:inline-block; width:32px; height:32px; line-height:32px; text-align:center; background:#25d366; color:#fff; border-radius:50%%; font-size:14px;">WA</span></a></td>
                                            </tr>
                                        </table>
                                        <p style="color:#bbb; font-size:11px; margin:15px 0 0;">&copy; %s %s. All rights reserved.</p>
                                    </td>
                                </tr>
                                <tr>
                                    <td style="background: linear-gradient(90deg, #ff9933, #d32f2f, #ffd700, #d32f2f, #ff9933); height:6px; border-radius:0 0 16px 16px;"></td>
                                </tr>
                            </table>
                            <p style="color:#ccc; font-size:10px; margin-top:10px;">This email was sent to %s. You are receiving this because of your contribution to %s.</p>
                        </td>
                    </tr>
                </table>
            </body>
            </html>
            """.formatted(
                logoUrl, bannerUrl, name, mandalName, year, 
                amount, transactionId, date, 
                dashboardUrl, 
                mandalName, websiteUrl, 
                year, mandalName,
                request.getEmail() != null ? request.getEmail() : "", mandalName
            );
    }

    private String buildDonationAdminWhatsApp(NotificationRequest request) {
        return buildDonationWhatsApp(request);
    }

    private String buildDonationAdminEmail(NotificationRequest request) {
        String name = request.getDonorName() != null ? request.getDonorName() : "Donor";
        String amount = request.getAmount() != null ? request.getAmount() : "0";
        String mode = request.getPaymentMode() != null ? request.getPaymentMode() : "Unknown";
        String date = request.getDate() != null ? request.getDate() : java.time.LocalDate.now().toString();
        String logoUrl = request.getLogoUrl() != null ? request.getLogoUrl() : "https://ganesh-mandal-new-react-tan.vercel.app/assets/hindavi-swarajya-logo.80462267.png";
        String bannerUrl = request.getBannerUrl() != null ? request.getBannerUrl() : "https://placehold.co/600x250/ff9933/ffffff?text=Ganesh+Festival";
        String websiteUrl = request.getWebsiteUrl() != null ? request.getWebsiteUrl() : "http://localhost:5173";
        String dashboardUrl = "https://ganesh-mandal-new-react-tan.vercel.app/login";
        String year = String.valueOf(java.time.Year.now().getValue());
        
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>New Donation Received</title>
            </head>
            <body style="margin:0; padding:0; background-color:#fef9f0; font-family:'Segoe UI',Tahoma,Geneva,Verdana,sans-serif;">
                <table width="100%%" cellpadding="0" cellspacing="0" style="background-color:#fef9f0;">
                    <tr>
                        <td align="center" style="padding:20px 10px;">
                            <table width="600" cellpadding="0" cellspacing="0" style="max-width:600px; width:100%%; background-color:#ffffff; border-radius:16px; box-shadow:0 4px 24px rgba(0,0,0,0.08); border:1px solid #f0e0c0;">
                                <tr>
                                    <td style="background: linear-gradient(90deg, #ff9933, #d32f2f, #ffd700, #d32f2f, #ff9933); height:6px; border-radius:16px 16px 0 0;"></td>
                                </tr>
                                <tr>
                                    <td align="center" style="padding:30px 20px 10px;">
                                        <img src="%s" alt="Hindavi Swarajya" style="width:160px; height:auto; display:block;" />
                                        <h1 style="color:#1a1a2e; font-size:22px; margin:10px 0 2px; letter-spacing:1px;">Hindavi Swarajya</h1>

                                    </td>
                                </tr>
                                <tr>
                                    <td align="center" style="padding:20px 20px 10px;">
                                        <img src="%s" alt="New Donation Received" style="width:100%%; max-width:560px; height:auto; border-radius:12px; display:block; box-shadow:0 4px 12px rgba(0,0,0,0.1);" />
                                    </td>
                                </tr>
                                <tr>
                                    <td align="center" style="padding:25px 20px 10px;">
                                        <div style="font-size:48px; line-height:1;">🔔</div>
                                        <h2 style="color:#d32f2f; font-size:26px; margin:10px 0 5px;">New Donation Received!</h2>
                                        <div style="width:80px; height:3px; background:linear-gradient(90deg,#ff9933,#d32f2f,#ffd700); margin:12px auto; border-radius:2px;"></div>
                                        <p style="color:#555; font-size:15px; line-height:1.7; margin:10px 20px 0; max-width:480px;">
                                            Dear Admin,<br/><br/>
                                            A new donation has been successfully added to the system by <strong style="color:#1a1a2e;">%s</strong>.
                                        </p>
                                    </td>
                                </tr>
                                <tr>
                                    <td align="center" style="padding:15px 20px;">
                                        <table width="100%%" cellpadding="0" cellspacing="0" style="background:linear-gradient(135deg,#fef9f0,#fff3e0); border-radius:12px; border:1px solid #e8d5a3; max-width:520px;">
                                            <tr>
                                                <td style="padding:20px;">
                                                    <h3 style="color:#b8860b; font-size:16px; margin:0 0 15px; text-align:center; letter-spacing:1px;">✦ DONATION DETAILS ✦</h3>
                                                    <table width="100%%" cellpadding="0" cellspacing="0">
                                                        <tr>
                                                            <td style="padding:6px 0; color:#888; font-size:13px; width:40px;">👤</td>
                                                            <td style="padding:6px 0; color:#888; font-size:13px; width:100px;">Donor Name</td>
                                                            <td style="padding:6px 0; color:#1a1a2e; font-size:14px; font-weight:600;">%s</td>
                                                        </tr>
                                                        <tr><td colspan="3" style="border-bottom:1px dashed #e8d5a3; height:1px;"></td></tr>
                                                        <tr>
                                                            <td style="padding:6px 0; color:#888; font-size:13px;">💰</td>
                                                            <td style="padding:6px 0; color:#888; font-size:13px;">Amount</td>
                                                            <td style="padding:6px 0; color:#d32f2f; font-size:16px; font-weight:700;">₹%s</td>
                                                        </tr>
                                                        <tr><td colspan="3" style="border-bottom:1px dashed #e8d5a3; height:1px;"></td></tr>
                                                        <tr>
                                                            <td style="padding:6px 0; color:#888; font-size:13px;">💳</td>
                                                            <td style="padding:6px 0; color:#888; font-size:13px;">Mode</td>
                                                            <td style="padding:6px 0; color:#1a1a2e; font-size:14px;">%s</td>
                                                        </tr>
                                                        <tr><td colspan="3" style="border-bottom:1px dashed #e8d5a3; height:1px;"></td></tr>
                                                        <tr>
                                                            <td style="padding:6px 0; color:#888; font-size:13px;">📅</td>
                                                            <td style="padding:6px 0; color:#888; font-size:13px;">Date</td>
                                                            <td style="padding:6px 0; color:#1a1a2e; font-size:14px;">%s</td>
                                                        </tr>
                                                    </table>
                                                </td>
                                            </tr>
                                        </table>
                                    </td>
                                </tr>
                                <tr>
                                    <td align="center" style="padding:25px 20px;">
                                        <table cellpadding="0" cellspacing="0" style="border-radius:50px; background:linear-gradient(135deg,#ff9933,#d32f2f); box-shadow:0 4px 15px rgba(211,47,47,0.3);">
                                            <tr>
                                                <td align="center" style="padding:14px 40px;">
                                                    <a href="%s" style="color:#ffffff; font-size:16px; font-weight:700; text-decoration:none; letter-spacing:1px; display:inline-block;">🚀 View in Dashboard</a>
                                                </td>
                                            </tr>
                                        </table>
                                    </td>
                                </tr>
                                <tr>
                                    <td align="center" style="padding:0 20px;">
                                        <div style="width:100%%; height:1px; background:linear-gradient(90deg,transparent,#e8d5a3,#d32f2f,#e8d5a3,transparent);"></div>
                                    </td>
                                </tr>
                                <tr>
                                    <td align="center" style="padding:20px 20px 10px;">
                                        <p style="color:#555; font-size:14px; margin:0 0 5px;">With gratitude,</p>
                                        <p style="color:#1a1a2e; font-size:14px; margin:0 0 10px; font-weight:600;">Hindavi Swarajya</p>
                                        <p style="color:#888; font-size:12px; margin:3px 0;">📍 Pune, Maharashtra</p>
                                        <p style="color:#888; font-size:12px; margin:3px 0;">📞 +91 9876543210</p>
                                        <p style="color:#888; font-size:12px; margin:3px 0;">📧 info@hindaviswarajya.com</p>
                                        <p style="color:#888; font-size:12px; margin:3px 0 15px;">🌐 %s</p>
                                        <h3 style="color:#d32f2f; font-size:16px; margin:15px 0;">Ganpati Bappa Morya! Mangal Murti Morya! 🙏</h3>
                                        <table cellpadding="0" cellspacing="0" align="center">
                                            <tr>
                                                <td style="padding:0 5px;"><a href="#" style="text-decoration:none;"><span style="display:inline-block; width:32px; height:32px; line-height:32px; text-align:center; background:#1877f2; color:#fff; border-radius:50%%; font-size:14px;">f</span></a></td>
                                                <td style="padding:0 5px;"><a href="https://www.instagram.com/hindavi._.swarajya?igsh=MWhnZW9mNmFwYjI2Zg==" style="text-decoration:none;"><span style="display:inline-block; width:32px; height:32px; line-height:32px; text-align:center; background:#e4405f; color:#fff; border-radius:50%%; font-size:14px;">ig</span></a></td>
                                                <td style="padding:0 5px;"><a href="#" style="text-decoration:none;"><span style="display:inline-block; width:32px; height:32px; line-height:32px; text-align:center; background:#ff0000; color:#fff; border-radius:50%%; font-size:14px;">▶</span></a></td>
                                                <td style="padding:0 5px;"><a href="#" style="text-decoration:none;"><span style="display:inline-block; width:32px; height:32px; line-height:32px; text-align:center; background:#25d366; color:#fff; border-radius:50%%; font-size:14px;">WA</span></a></td>
                                            </tr>
                                        </table>
                                        <p style="color:#bbb; font-size:11px; margin:15px 0 0;">&copy; %s Hindavi Swarajya. All rights reserved.</p>
                                    </td>
                                </tr>
                                <tr>
                                    <td style="background: linear-gradient(90deg, #ff9933, #d32f2f, #ffd700, #d32f2f, #ff9933); height:6px; border-radius:0 0 16px 16px;"></td>
                                </tr>
                            </table>
                            <p style="color:#ccc; font-size:10px; margin-top:10px;">This email is an automated admin notification.</p>
                        </td>
                    </tr>
                </table>
            </body>
            </html>
            """.formatted(
                logoUrl, bannerUrl, name, name, amount, mode, date,
                dashboardUrl, websiteUrl, year
            );
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
