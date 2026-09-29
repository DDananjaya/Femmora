package com.femmora.authservice.controller;

import com.femmora.authservice.model.User;
import com.femmora.authservice.repository.UserRepository;
import com.femmora.authservice.service.EmailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.Random;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final EmailService emailService;

    public AuthController(UserRepository userRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        Optional<User> existing = userRepository.findByEmail(user.getEmail());
        if (existing.isPresent()) {
            return ResponseEntity.badRequest().body("Email already in use.");
        }
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("CUSTOMER");
        }
        User savedUser = userRepository.save(user);
        return ResponseEntity.ok(savedUser);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User loginRequest) {
        Optional<User> userOpt = userRepository.findByEmail(loginRequest.getEmail());
        if (userOpt.isPresent() && userOpt.get().getPassword().equals(loginRequest.getPassword())) {
            return ResponseEntity.ok(userOpt.get());
        }
        return ResponseEntity.status(401).body("Invalid email or password.");
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found.");
        }

        String code = String.format("%06d", new Random().nextInt(999999));
        User user = userOpt.get();
        user.setResetCode(code);
        userRepository.save(user);

        try {
            emailService.sendPasswordResetEmail(user.getEmail(), code);
            return ResponseEntity.ok("Verification code successfully sent to email.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to send email: " + e.getMessage());
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam String email, @RequestParam String code, @RequestParam String newPassword) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent() && code.equals(userOpt.get().getResetCode())) {
            User user = userOpt.get();
            user.setPassword(newPassword);
            user.setResetCode(null);
            userRepository.save(user);
            return ResponseEntity.ok("Password updated successfully.");
        }
        return ResponseEntity.badRequest().body("Invalid verification code.");
    }

    // --- NEW ENDPOINTS FOR FRONTEND PROFILE UPDATES ---

    @PostMapping("/request-email-change")
    public ResponseEntity<?> requestEmailChange(@RequestParam Long userId, @RequestParam String newEmail) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found.");
        }

        Optional<User> existingEmail = userRepository.findByEmail(newEmail);
        if (existingEmail.isPresent()) {
            return ResponseEntity.badRequest().body("Email is already registered by another account.");
        }

        String code = String.format("%06d", new Random().nextInt(999999));
        User user = userOpt.get();
        user.setPendingEmail(newEmail);
        user.setEmailVerificationCode(code);
        userRepository.save(user);

        try {
            emailService.sendEmailVerificationCode(newEmail, code);
            return ResponseEntity.ok("Verification code sent to new email address.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to dispatch verification email: " + e.getMessage());
        }
    }

    @PostMapping("/verify-email-change")
    public ResponseEntity<?> verifyEmailChange(@RequestParam Long userId, @RequestParam String code) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (code.equals(user.getEmailVerificationCode()) && user.getPendingEmail() != null) {
                user.setEmail(user.getPendingEmail());
                user.setPendingEmail(null);
                user.setEmailVerificationCode(null);
                userRepository.save(user);
                return ResponseEntity.ok(user); // Returns updated user profile object
            }
        }
        return ResponseEntity.badRequest().body("Invalid confirmation code.");
    }
}