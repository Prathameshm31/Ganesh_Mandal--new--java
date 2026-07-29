package com.ganesh.mandal.service;

import com.ganesh.mandal.dto.*;
import com.ganesh.mandal.entity.*;
import com.ganesh.mandal.exception.*;
import com.ganesh.mandal.repository.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final UserSessionRepository userSessionRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final AuthorizationService authorizationService;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    private static final int MAX_FAILED_ATTEMPTS = 5;

    @Transactional
    public AuthResponse login(LoginRequest request, String ipAddress, String userAgent) {
        User user = userRepository.findByEmail(request.getUsername()).orElse(null);
        if (user == null) {
            user = userRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));
        }

        if (Boolean.TRUE.equals(user.getAccountLocked())) {
            throw new AccountLockedException("Account is locked due to multiple failed login attempts. Please reset your password or contact support.");
        }

        if (!"ACTIVE".equals(user.getStatus())) {
            throw new InactiveAccountException("User account is inactive. Please contact support.");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);
            if (user.getFailedLoginAttempts() >= MAX_FAILED_ATTEMPTS) {
                user.setAccountLocked(true);
            }
            userRepository.save(user);
            throw new InvalidCredentialsException("Invalid email or password");
        }

        List<UserRole> userRoles = userRoleRepository.findByUserId(user.getId());
        if (userRoles.isEmpty()) {
            throw new AccessDeniedException("No role assigned. Please contact support.");
        }

        user.setFailedLoginAttempts(0);
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        String token = UUID.randomUUID().toString();
        UserSession session = UserSession.builder()
                .user(user)
                .token(token)
                .active(true)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .loggedInAt(LocalDateTime.now())
                .lastActivityAt(LocalDateTime.now())
                .build();
        userSessionRepository.save(session);

        List<String> roles = userRoles.stream()
                .map(ur -> ur.getRole().getRoleName())
                .collect(Collectors.toList());

        List<String> permissions = authorizationService.getUserPermissions(user.getId());

        String email = user.getEmail() != null ? user.getEmail() : "";
        String mobile = user.getMobile() != null ? user.getMobile() : "";

        return AuthResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .name(user.getName())
                .email(email)
                .mobile(mobile)
                .token(token)
                .roles(roles)
                .permissions(permissions)
                .firstLogin(user.getFirstLogin())
                .status(user.getStatus())
                .build();
    }

    @Transactional
    public void logout(String token) {
        userSessionRepository.findByTokenAndActiveTrue(token).ifPresent(session -> {
            session.setActive(false);
            session.setLoggedOutAt(LocalDateTime.now());
            userSessionRepository.save(session);
        });
    }

    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        String email = request.getEmail();
        User user = userRepository.findByUsername(email).orElse(null);
        if (user == null) {
            user = userRepository.findByEmail(email).orElse(null);
        }
        if (user == null) {
            return;
        }

        passwordResetTokenRepository.findByEmailAndUsedFalse(email).ifPresent(existing -> {
            existing.setUsed(true);
            passwordResetTokenRepository.save(existing);
        });

        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .email(email)
                .token(token)
                .expiryDate(LocalDateTime.now().plusHours(1))
                .used(false)
                .build();
        passwordResetTokenRepository.save(resetToken);

        try {
            String resetUrl = "https://ganesh-mandal-new-react-tan.vercel.app/reset-password?token=" + token;
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(email);
            message.setSubject("Password Reset Request - Ganesh Mandal");
            message.setText("Dear " + user.getName() + ",\n\n"
                    + "You have requested to reset your password.\n\n"
                    + "Please click the link below to reset your password:\n"
                    + resetUrl + "\n\n"
                    + "This link will expire in 1 hour.\n\n"
                    + "If you did not request this, please ignore this email.\n\n"
                    + "Regards,\nGanesh Mandal Team");
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Failed to send email: " + e.getMessage());
        }
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(request.getToken())
                .orElseThrow(() -> new IllegalArgumentException("Invalid or expired reset token"));

        if (resetToken.getUsed()) {
            throw new IllegalArgumentException("Reset token has already been used");
        }

        if (resetToken.isExpired()) {
            throw new IllegalArgumentException("Reset token has expired");
        }

        User user = userRepository.findByUsername(resetToken.getEmail()).orElse(null);
        if (user == null) {
            user = userRepository.findByEmail(resetToken.getEmail())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setFirstLogin(false);
        user.setFailedLoginAttempts(0);
        user.setAccountLocked(false);
        user.setPasswordUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        resetToken.setUsed(true);
        passwordResetTokenRepository.save(resetToken);
    }

    @Transactional
    public void changePassword(Long userId, ChangePasswordRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setFirstLogin(false);
        user.setPasswordUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    public AuthResponse getCurrentUserProfile(HttpServletRequest httpRequest) {
        Long userId = authorizationService.getCurrentUserId(httpRequest);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<UserRole> userRoles = userRoleRepository.findByUserId(user.getId());
        List<String> roles = userRoles.stream()
                .map(ur -> ur.getRole().getRoleName())
                .collect(Collectors.toList());
        List<String> permissions = authorizationService.getUserPermissions(user.getId());

        String email = user.getEmail() != null ? user.getEmail() : "";
        String mobile = user.getMobile() != null ? user.getMobile() : "";

        return AuthResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .name(user.getName())
                .email(email)
                .mobile(mobile)
                .roles(roles)
                .permissions(permissions)
                .firstLogin(user.getFirstLogin())
                .status(user.getStatus())
                .build();
    }
}
