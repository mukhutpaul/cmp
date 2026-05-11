package com.cm_policier.effectifs.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.cm_policier.effectifs.dto.ApiResponse;
import com.cm_policier.effectifs.dto.MobileLoginRequest;
import com.cm_policier.effectifs.model.DetailUnite;
import com.cm_policier.effectifs.model.Seance;
import com.cm_policier.effectifs.model.Session;
import com.cm_policier.effectifs.model.User;
import com.cm_policier.effectifs.security.JwtUtil;
import com.cm_policier.effectifs.service.SeanceService;
import com.cm_policier.effectifs.service.SessionService;
import com.cm_policier.effectifs.service.UserService;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(
    origins = "*",
    allowedHeaders = "*",
    methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE}
)
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;
    private SessionService sessionService;

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
                "id", user.getId(),
                "username", user.getUsername(),
                "email", user.getEmail(),
                "noms", user.getNoms(),

                "profile", user.getProfile() != null
                        ? user.getProfile().getName()
                        : null,

                // 🔥 ENTITÉ USER COMPLETE
                "user", user));

        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body(Map.of(
                    "message", "Invalid credentials",
                    "error", e.getMessage()));
        }
    }

    @Autowired
    private SeanceService seanceService;

    @PostMapping("/mobile/login")
    public ResponseEntity<?> mobileLogin(@RequestBody MobileLoginRequest request) {

        try {

            // 1. séance active obligatoire
            Seance seanceActive = seanceService.getActiveSeance();

            if (seanceActive == null) {
                return ResponseEntity.status(403).body(
                        new ApiResponse<>(false, "Aucune séance active", null));
            }

            // 2. login user
            User user = userService.login(request.getUsername(), request.getPassword());

            // 3. contrôle rôle
            if (user.getProfile() == null ||
                    !user.getProfile().getName().equals("CONTROLEUR")) {

                return ResponseEntity.status(403).body(
                        new ApiResponse<>(false, "Accès refusé : contrôleurs uniquement", null));
            }

            // 4. token
            String token = jwtUtil.generateToken(user.getUsername());

            // 5. unités
            List<DetailUnite> unites = userService.getUnitesByUserId(user.getId());

            // 6. SESSION ACTIVE (IMPORTANT)
            Session session = sessionService.createSession(user, seanceActive);

            // 7. RESPONSE PROPRE
            Map<String, Object> payload = Map.of(
                    "token", token,
                    "user", Map.of(
                            "id", user.getId(),
                            "username", user.getUsername(),
                            "noms", user.getNoms(),
                            "profile", user.getProfile().getName()),
                    "seance", seanceActive,
                    "session", session, // 🔥 ajouté
                    "unites", unites);

            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Connexion réussie", payload));

        } catch (RuntimeException e) {

            return ResponseEntity.status(401).body(
                    new ApiResponse<>(false, "Identifiants invalides", e.getMessage()));
        }
    }

    @PostMapping("/mobile/logout/{userId}")
    public ResponseEntity<?> logout(@PathVariable Long userId) {

        try {

            Session session = sessionService.closeActiveSessionByUser(userId);

            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Session fermée", session));

        } catch (RuntimeException e) {

            return ResponseEntity.status(400).body(
                    new ApiResponse<>(false, "Erreur fermeture session", e.getMessage()));
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

    // =========================
// GET ALL USERS
// =========================
@GetMapping("/users")
public ResponseEntity<?> getUsers() {

    try {

        List<User> users = userService.getAllUsers();

        // Masquer les mots de passe
        users.forEach(user -> user.setPassword(null));

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Liste des utilisateurs", users));

    } catch (Exception e) {

        return ResponseEntity.status(500).body(
                new ApiResponse<>(false, "Erreur récupération utilisateurs", e.getMessage()));
    }
}

}