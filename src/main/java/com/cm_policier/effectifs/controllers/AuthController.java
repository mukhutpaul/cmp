package com.cm_policier.effectifs.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.cm_policier.effectifs.dto.UpdateUserRequest;
import com.cm_policier.effectifs.model.User;
import com.cm_policier.effectifs.security.JwtUtil;
import com.cm_policier.effectifs.service.UserService;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    // =========================
    // REGISTER
    // =========================
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            User savedUser = userService.register(user);
            savedUser.setPassword(null);

            return ResponseEntity.ok(Map.of(
                    "message", "User created successfully",
                    "user", savedUser));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Registration failed",
                    "error", e.getMessage()));
        }
    }

    // =========================
    // LOGIN
    // =========================
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {

        try {
            String username = request.get("username");
            String password = request.get("password");

            User user = userService.login(username, password);

            String token = jwtUtil.generateToken(user.getUsername());

            return ResponseEntity.ok(Map.of(
                    "token", token,
                    "username", user.getUsername(),
                    "email", user.getEmail(),
                    "noms", user.getNoms(),
                    "profile", user.getProfile() != null
                            ? user.getProfile().getName()
                            : null));

        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body(Map.of(
                    "message", "Invalid credentials",
                    "error", e.getMessage()));
        }
    }

    // =========================
    // GET ALL USERS
    // =========================
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();

        // 🔒 hide passwords
        users.forEach(u -> u.setPassword(null));

        return ResponseEntity.ok(users);
    }

    // =========================
    // GET USER BY ID
    // =========================
    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        try {
            User user = userService.getUserById(id);
            user.setPassword(null);

            return ResponseEntity.ok(user);

        } catch (Exception e) {
            return ResponseEntity.status(404).body(Map.of(
                    "message", "User not found",
                    "error", e.getMessage()));
        }
    }

    // =========================
    // UPDATE USER (PATCH)
    // =========================
    @PatchMapping("/users/{id}")
    public ResponseEntity<?> updateUser(
            @PathVariable Long id,
            @RequestBody UpdateUserRequest request) {
        try {
            User updated = userService.updateUser(id, request);
            updated.setPassword(null);

            return ResponseEntity.ok(Map.of(
                    "message", "User updated successfully",
                    "user", updated));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Update failed",
                    "error", e.getMessage()));
        }
    }

    // =========================
    // DELETE USER
    // =========================
    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);

            return ResponseEntity.ok(Map.of(
                    "message", "User deleted successfully"));

        } catch (Exception e) {
            return ResponseEntity.status(404).body(Map.of(
                    "message", "Delete failed",
                    "error", e.getMessage()));
        }
    }
}