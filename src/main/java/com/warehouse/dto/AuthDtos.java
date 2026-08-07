package com.warehouse.dto;

import com.warehouse.model.Role;
import jakarta.validation.constraints.NotBlank;

public class AuthDtos {

    public record LoginRequest(
            @NotBlank String username,
            @NotBlank String password
    ) {}

    public record RegisterRequest(
            @NotBlank String username,
            @NotBlank String password,
            @NotBlank String fullName,
            @NotBlank String role // ADMIN | MANAGER | STAFF
    ) {}

    public record AuthResponse(
            String token,
            String username,
            String fullName,
            Role role
    ) {}
}
