package com.ganesh.mandal.controller;

import com.ganesh.mandal.dto.*;
import com.ganesh.mandal.service.AuthService;
import com.ganesh.mandal.service.AuthorizationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final AuthorizationService authorizationService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        String ipAddress = httpRequest.getRemoteAddr();
        String userAgent = httpRequest.getHeader("User-Agent");
        AuthResponse response = authService.login(request, ipAddress, userAgent);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        RegisterResponse response = authService.register(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            authService.logout(authHeader.substring(7));
        }
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("message", "Logged out successfully");
        return ResponseEntity.ok(body);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request);
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("message", "If the email is registered, a password reset link has been sent");
        return ResponseEntity.ok(body);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("message", "Password has been reset successfully");
        return ResponseEntity.ok(body);
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordRequest request,
                                              HttpServletRequest httpRequest) {
        Long userId = authorizationService.getCurrentUserId(httpRequest);
        authService.changePassword(userId, request);
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("message", "Password changed successfully");
        return ResponseEntity.ok(body);
    }

    @GetMapping("/validate-session")
    public ResponseEntity<?> validateSession(HttpServletRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        try {
            authorizationService.getCurrentUserId(request);
            body.put("valid", true);
        } catch (Exception e) {
            body.put("valid", false);
        }
        return ResponseEntity.ok(body);
    }

    @GetMapping("/profile")
    public ResponseEntity<AuthResponse> getProfile(HttpServletRequest request) {
        AuthResponse profile = authService.getCurrentUserProfile(request);
        return ResponseEntity.ok(profile);
    }
}
