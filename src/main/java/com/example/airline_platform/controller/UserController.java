package com.example.airline_platform.controller;

import com.example.airline_platform.Entity.Role;
import com.example.airline_platform.repository.UserRepository;
import com.example.airline_platform.Entity.User;
import com.example.airline_platform.util.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;


@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordUtil passwordUtil;

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registerUser(@RequestBody User user) {
        Map<String, Object> response = new HashMap<>();
        if (userRepository.existsByEmail(user.getEmail())) {
            response.put("status", "error");
            response.put("message", "Email is already in use!");
            return ResponseEntity.badRequest().body(response);
        }
        String encryptedPassword = passwordUtil.encodePassword(user.getPassword());
        user.setPassword(encryptedPassword);

        User savedUser= userRepository.save(user);
        response.put("status", "success");
        response.put("message", "User registered successfully!");
        response.put("userID", savedUser.getId());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> loginUser(@RequestBody Map<String, String> loginRequest) {
        Map<String, Object> response = new HashMap<>();
        String email = loginRequest.get("email");
        String password = loginRequest.get("password");
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (passwordUtil.matches(password, user.getPassword())) {
                response.put("status", "success");
                response.put("message", "Login successful!");
                response.put("userID", user.getId());
                return ResponseEntity.ok(response);
            } else {
                response.put("status", "error");
                response.put("message", "Invalid password!");
                return ResponseEntity.status(401).body(response);
            }
        }

        response.put("status", "error");
        response.put("message", "User not found!");
        return ResponseEntity.status(404).body(response);
    }


    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> filterUser(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String role) {

        try {
            List<User> users;

            if (name != null || email != null || role != null) {
                users = userRepository.findAll().stream().filter(user -> {
                    boolean matches = true;
                    if (name != null) {
                        matches &= user.getName().equalsIgnoreCase(name);
                    }
                    if (email != null) {
                        matches &= user.getEmail().equalsIgnoreCase(email);
                    }
                    if (role != null) {
                        // Now Role should be recognized
                        matches &= user.getRole() == Role.valueOf(role.toUpperCase());
                    }
                    return matches;
                }).toList();
            } else {
                users = userRepository.findAll();
            }

            return ResponseEntity.ok(Map.of("status", "success", "data", users));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "message", "Invalid role. Use: USER or ADMIN"
            ));
        }
    }

    @PutMapping("/{userId}")
    public ResponseEntity<Map<String, Object>> updateUser(
            @PathVariable Long userId,
            @RequestBody Map<String, String> updateRequest) {

        Map<String, Object> response = new HashMap<>();

        Optional<User> userOpt = userRepository.findById(userId);

        if (userOpt.isEmpty()) {
            response.put("status", "error");
            response.put("message", "User not found!");
            return ResponseEntity.status(404).body(response);
        }

        User existingUser = userOpt.get();
        boolean isUpdated = false;

        // Update name if provided
        if (updateRequest.containsKey("name") && updateRequest.get("name") != null) {
            existingUser.setName(updateRequest.get("name"));
            isUpdated = true;
        }

        // Update role if provided
        if (updateRequest.containsKey("role") && updateRequest.get("role") != null) {
            try {
                Role newRole = Role.valueOf(updateRequest.get("role").toUpperCase());
                existingUser.setRole(newRole);
                isUpdated = true;
            } catch (IllegalArgumentException e) {
                response.put("status", "error");
                response.put("message", "Invalid role. Use: USER or ADMIN");
                return ResponseEntity.badRequest().body(response);
            }
        }

        // Save only if something was updated
        if (isUpdated) {
            userRepository.save(existingUser);
            response.put("status", "success");
            response.put("message", "User updated successfully!");
            response.put("updatedUser", Map.of(
                    "id", existingUser.getId(),
                    "name", existingUser.getName(),
                    "email", existingUser.getEmail(),
                    "role", existingUser.getRole()
            ));
            return ResponseEntity.ok(response);
        } else {
            response.put("status", "error");
            response.put("message", "No valid fields to update! Provide 'name' or 'role'");
            return ResponseEntity.badRequest().body(response);
        }
    }
}
