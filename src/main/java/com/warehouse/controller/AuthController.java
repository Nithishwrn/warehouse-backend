package com.warehouse.controller;

import com.warehouse.dto.AuthDtos.AuthResponse;
import com.warehouse.dto.AuthDtos.LoginRequest;
import com.warehouse.dto.AuthDtos.RegisterRequest;
import com.warehouse.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    /**
     * Self-registration. In production you'd likely restrict this to ADMIN-only
     * user creation via /api/users; left open here so the project is runnable end-to-end
     * out of the box without a pre-seeded account (seeded demo accounts are also provided).
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(userService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(userService.login(request));
    }
}
