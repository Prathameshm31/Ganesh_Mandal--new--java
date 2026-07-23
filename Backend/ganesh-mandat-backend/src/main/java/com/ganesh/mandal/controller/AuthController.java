package com.ganesh.mandal.controller;

import com.ganesh.mandal.dto.LoginRequest;
import com.ganesh.mandal.dto.LoginResponse;
import com.ganesh.mandal.entity.User;
import com.ganesh.mandal.repository.UserRepository;
import com.ganesh.mandal.service.AuthorizationService;
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
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final AuthorizationService authorizationService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername()).orElse(null);
        if (user == null || !user.getPassword().equals(request.getPassword())) {
            Map<String, Object> body = new LinkedHashMap<>();
            body.put("timestamp", LocalDateTime.now());
            body.put("status", HttpStatus.UNAUTHORIZED.value());
            body.put("error", "Unauthorized");
            body.put("message", "Invalid username or password");
            return new ResponseEntity<>(body, HttpStatus.UNAUTHORIZED);
        }

        List<String> permissions = authorizationService.getUserPermissions(user.getId());

        LoginResponse response = LoginResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .name(user.getName())
                .role(user.getStatus())
                .permissions(permissions)
                .build();

        return ResponseEntity.ok(response);
    }
}
