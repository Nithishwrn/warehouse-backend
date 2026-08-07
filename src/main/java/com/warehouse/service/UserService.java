package com.warehouse.service;

import com.warehouse.dto.AuthDtos.AuthResponse;
import com.warehouse.dto.AuthDtos.LoginRequest;
import com.warehouse.dto.AuthDtos.RegisterRequest;
import com.warehouse.exception.ConflictException;
import com.warehouse.model.Role;
import com.warehouse.model.User;
import com.warehouse.repository.UserRepository;
import com.warehouse.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new ConflictException("Username '" + request.username() + "' is already taken");
        }
        Role role;
        try {
            role = Role.valueOf(request.role().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Role must be one of ADMIN, MANAGER, STAFF");
        }

        User user = User.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .fullName(request.fullName())
                .role(role)
                .enabled(true)
                .build();
        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name());
        return new AuthResponse(token, user.getUsername(), user.getFullName(), user.getRole());
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found in database"));

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name());
        return new AuthResponse(token, user.getUsername(), user.getFullName(), user.getRole());
    }

    public List<User> listUsers() {
        return userRepository.findAll();
    }

    public User setRole(Long userId, String roleStr) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new com.warehouse.exception.ResourceNotFoundException("User not found: " + userId));
        user.setRole(Role.valueOf(roleStr.toUpperCase()));
        return userRepository.save(user);
    }

    public User setEnabled(Long userId, boolean enabled) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new com.warehouse.exception.ResourceNotFoundException("User not found: " + userId));
        user.setEnabled(enabled);
        return userRepository.save(user);
    }
}
