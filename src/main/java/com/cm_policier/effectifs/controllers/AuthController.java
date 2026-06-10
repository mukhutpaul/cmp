package com.cm_policier.effectifs.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.cm_policier.effectifs.dto.ApiResponse;
import com.cm_policier.effectifs.dto.MobileLoginRequest;
import com.cm_policier.effectifs.dto.PcSyncLoginDTO;
import com.cm_policier.effectifs.dto.SyncResponseDTO;
import com.cm_policier.effectifs.model.DetailUnite;
import com.cm_policier.effectifs.model.Seance;
import com.cm_policier.effectifs.model.Session;
import com.cm_policier.effectifs.model.User;
import com.cm_policier.effectifs.security.JwtUtil;
import com.cm_policier.effectifs.service.LogUserService;
import com.cm_policier.effectifs.service.PcLocalSyncService;
import com.cm_policier.effectifs.service.PcSyncClient;
import com.cm_policier.effectifs.service.SeanceService;
import com.cm_policier.effectifs.service.SessionService;
import com.cm_policier.effectifs.service.UserService;
import com.cm_policier.effectifs.util.CurrentUserUtil;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {
        RequestMethod.GET,
        RequestMethod.POST,
        RequestMethod.PUT,
        RequestMethod.PATCH,
        RequestMethod.DELETE
})
@RequiredArgsConstructor
public class AuthController {

    // =========================
    // SERVICES
    // =========================

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final SessionService sessionService;
    private final SeanceService seanceService;

    private final PcSyncClient pcSyncClient;
    private final PcLocalSyncService pcLocalSyncService;

    private final LogUserService logUserService;

    // =========================
    // REGISTER
    // =========================
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            String username = CurrentUserUtil.getCurrentUsername();
            User users = userService.findByUsername(username);
            logUserService.saveLog(users, "Ajout " + user.getUsername());
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
    // LOGIN LOCAL
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
                    "id", user.getId(),
                    "username", user.getUsername(),
                    "email", user.getEmail(),
                    "noms", user.getNoms(),
                    "profile", user.getProfile() != null ? user.getProfile().getName() : null,
                    "user", user));

        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body(Map.of(
                    "message", "Invalid credentials",
                    "error", e.getMessage()));
        }
    }

    // =========================
    // LOGIN DISTANT + SYNC LOCAL
    // =========================
    @PostMapping("/login-distant")
    public ResponseEntity<?> loginDistant(@RequestBody Map<String, String> request) {

        try {

            System.out.println("LOGIN DISTANT START");

            String username = request.get("username");
            String password = request.get("password");

            PcSyncLoginDTO dto = new PcSyncLoginDTO();
            dto.setUsername(username);
            dto.setPassword(password);

            // ===== APPEL SERVEUR DISTANT =====
            System.out.println("CALL DISTANT SERVER");

            SyncResponseDTO response = pcSyncClient.sync(dto);

            // ===== SAVE LOCAL DB =====
            System.out.println("SAVE LOCAL DB");

            pcLocalSyncService.saveSyncData(response); // ✔️ ICI CORRECTION
            String usernames = CurrentUserUtil.getCurrentUsername();
            User users = userService.findByUsername(usernames);
            logUserService.saveLog(users, "Connexion distante de " + dto.getUsername());

            return ResponseEntity.ok(response);

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "message", "Invalid credentials",
                            "error", e.getMessage()));
        }
    }

    // =========================
    // MOBILE LOGIN
    // =========================
    @PostMapping("/mobile/login")
    public ResponseEntity<?> mobileLogin(@RequestBody MobileLoginRequest request) {

        try {

            Seance seanceActive = seanceService.getActiveSeance();

            if (seanceActive == null) {
                return ResponseEntity.status(403).body(
                        new ApiResponse<>(false, "Aucune séance active", null));
            }

            User user = userService.login(request.getUsername(), request.getPassword());

            if (user.getProfile() == null ||
                    !user.getProfile().getName().equals("CONTROLEUR")) {

                return ResponseEntity.status(403).body(
                        new ApiResponse<>(false, "Accès refusé : contrôleurs uniquement", null));
            }

            String token = jwtUtil.generateToken(user.getUsername());

            List<DetailUnite> unites = userService.getUnitesByUserId(user.getId());

            Session session = sessionService.createSession(user, seanceActive);

            Map<String, Object> payload = Map.of(
                    "token", token,
                    "user", Map.of(
                            "id", user.getId(),
                            "username", user.getUsername(),
                            "noms", user.getNoms(),
                            "profile", user.getProfile().getName()),
                    "seance", seanceActive,
                    "session", session,
                    "unites", unites);
            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Connexion réussie", payload));

        } catch (RuntimeException e) {

            return ResponseEntity.status(401).body(
                    new ApiResponse<>(false, "Identifiants invalides", e.getMessage()));
        }
    }

    // =========================
    // LOGOUT MOBILE
    // =========================
    // @PostMapping("/mobile/logout/{userId}")
    // public ResponseEntity<?> logout(@PathVariable Long userId) {

    // try {

    // Session session = sessionService.closeActiveSessionByUser(userId);

    // return ResponseEntity.ok(
    // new ApiResponse<>(true, "Session fermée", session));

    // } catch (RuntimeException e) {

    // return ResponseEntity.status(400).body(
    // new ApiResponse<>(false, "Erreur fermeture session", e.getMessage()));
    // }
    // }

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

    // =========================
    // GET ALL USERS
    // =========================
    @GetMapping("/users")
    public ResponseEntity<?> getUsers() {

        try {

            List<User> users = userService.getAllUsers();
            users.forEach(user -> user.setPassword(null));

            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Liste des utilisateurs", users));

        } catch (Exception e) {

            return ResponseEntity.status(500).body(
                    new ApiResponse<>(false, "Erreur récupération utilisateurs", e.getMessage()));
        }
    }
}