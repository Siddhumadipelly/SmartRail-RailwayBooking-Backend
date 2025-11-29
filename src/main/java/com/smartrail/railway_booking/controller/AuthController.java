package com.smartrail.railway_booking.controller;

import com.smartrail.railway_booking.dto.LoginRequest;
import com.smartrail.railway_booking.model.AppUser;
import com.smartrail.railway_booking.repository.AppUserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AppUserRepository userRepository;

    public AuthController(AppUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // ---------- SIGN UP ----------
    // POST /api/auth/register  { name, email, password }
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody LoginRequest request) {

        if (request.getEmail() == null || request.getEmail().isBlank()) {
            return ResponseEntity.badRequest().body("Email is required");
        }
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            return ResponseEntity.badRequest().body("Password is required");
        }

        String email = request.getEmail().trim();
        String name = (request.getName() == null || request.getName().isBlank())
                ? "User"
                : request.getName().trim();
        String password = request.getPassword().trim();

        if (userRepository.findByEmailIgnoreCase(email).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("User already exists. Please login.");
        }

        AppUser user = AppUser.builder()
                .name(name)
                .email(email)
                .password(password)      // ⚠ plain text – OK for demo
                .createdAt(LocalDateTime.now())
                .build();

        AppUser saved = userRepository.save(user);

        // Don’t send password back
        saved.setPassword(null);
        return ResponseEntity.ok(saved);
    }

    // ---------- LOGIN ----------
    // POST /api/auth/login  { email, password }
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {

        if (request.getEmail() == null || request.getEmail().isBlank()) {
            return ResponseEntity.badRequest().body("Email is required");
        }
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            return ResponseEntity.badRequest().body("Password is required");
        }

        String email = request.getEmail().trim();
        String password = request.getPassword().trim();

        return userRepository.findByEmailIgnoreCase(email)
                .map(user -> {
                    if (!password.equals(user.getPassword())) {
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                .body("Invalid email or password.");
                    }
                    user.setPassword(null); // hide password
                    return ResponseEntity.ok(user);
                })
                .orElseGet(() ->
                        ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                .body("Invalid email or password.")
                );
    }
}
