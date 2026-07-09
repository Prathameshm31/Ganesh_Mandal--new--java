package com.ganesh.mandal.controller;

import com.ganesh.mandal.dto.LoginRequest;
import com.ganesh.mandal.dto.LoginResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private static final Map<String, User> USERS = Map.of(
        "admin", new User(1L, "admin", "Admin User", "admin", "admin123"),
        "user", new User(2L, "user", "Regular User", "user", "user123")
    );

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        User matched = USERS.get(request.getUsername());
        if (matched == null || !matched.password.equals(request.getPassword())) {
            Map<String, Object> body = new LinkedHashMap<>();
            body.put("timestamp", LocalDateTime.now());
            body.put("status", HttpStatus.UNAUTHORIZED.value());
            body.put("error", "Unauthorized");
            body.put("message", "Invalid username or password");
            return new ResponseEntity<>(body, HttpStatus.UNAUTHORIZED);
        }

        LoginResponse response = LoginResponse.builder()
                .id(matched.id)
                .username(matched.username)
                .name(matched.name)
                .role(matched.role)
                .build();

        return ResponseEntity.ok(response);
    }

    private record User(Long id, String username, String name, String role, String password) {}
}
