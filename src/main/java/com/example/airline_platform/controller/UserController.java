package com.example.airline_platform.controller;

import  com.example.airline_platform.repository.UserRepository;
import jakarta.persistence.Enumerated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import  com.example.airline_platform.Entity.User;
import com.example.airline_platform.Entity.*;
import org.springframework.web.bind.annotation.*;
import  java.util.*;
import  com.example.airline_platform.util.PasswordUtil;

import javax.management.relation.Role;



@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordUtil passwordUtil;

    @PostMapping("/register")
    public ResponseEntity registerUser(@RequestBody User user) {
        Map<String, String> response = new HashMap<>();
        if (userRepository.existsByEmail(user.getEmail())) {
            response.put("status", "error");
            response.put("message", "Email is already in use!");
            return ResponseEntity.badRequest().body(response);
        }
        String encryptedPassword = passwordUtil.encodePassword(user.getPassword());
        user.setPassword(encryptedPassword);

        userRepository.save(user);
        response.put("status", "success");
        response.put("message", "User registered successfully!");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/all")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }



}
