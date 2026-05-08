package com.cm_policier.effectifs.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.cm_policier.effectifs.model.Profile;
import com.cm_policier.effectifs.service.ProfileService;

@RestController
@RequestMapping("/api/profiles")
@CrossOrigin("*")
public class ProfileController {

    @Autowired
    private ProfileService profileService;

    /**
     * ➕ CREATE PROFILE
     */
    @PostMapping
    public ResponseEntity<?> create(
            @RequestBody Profile profile
    ) {

        Profile saved = profileService.create(profile);

        return ResponseEntity.ok(saved);
    }

    /**
     * 📄 GET ALL PROFILES
     */
    @GetMapping
    public ResponseEntity<List<Profile>> getAll() {

        return ResponseEntity.ok(
                profileService.getAll()
        );
    }

    /**
     * 👁 GET PROFILE BY ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Profile> getById(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                profileService.getById(id)
        );
    }

    /**
     * ✏️ PATCH PROFILE
     */
    @PatchMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestBody Map<String, Object> updates
    ) {

        Profile updated = profileService.update(id, updates);

        return ResponseEntity.ok(updated);
    }

    /**
     * 🗑 DELETE PROFILE
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(
            @PathVariable Long id
    ) {

        profileService.delete(id);

        return ResponseEntity.ok(
                Map.of(
                        "message",
                        "Profil supprimé avec succès"
                )
        );
    }
}