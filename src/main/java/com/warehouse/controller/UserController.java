package com.warehouse.controller;

import com.warehouse.model.User;
import com.warehouse.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/** All endpoints here require ROLE_ADMIN (enforced in SecurityConfig). */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<UserSummary> list() {
        return userService.listUsers().stream().map(UserSummary::from).toList();
    }

    @PatchMapping("/{id}/role")
    public UserSummary setRole(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return UserSummary.from(userService.setRole(id, body.get("role")));
    }

    @PatchMapping("/{id}/enabled")
    public UserSummary setEnabled(@PathVariable Long id, @RequestBody Map<String, Boolean> body) {
        return UserSummary.from(userService.setEnabled(id, body.getOrDefault("enabled", true)));
    }

    // Never expose the password hash to clients.
    public record UserSummary(Long id, String username, String fullName, String role, boolean enabled) {
        public static UserSummary from(User u) {
            return new UserSummary(u.getId(), u.getUsername(), u.getFullName(), u.getRole().name(), u.isEnabled());
        }
    }
}
