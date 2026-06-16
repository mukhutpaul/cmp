package com.cm_policier.effectifs.service;

import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import com.cm_policier.effectifs.model.Profile;
import com.cm_policier.effectifs.repository.ProfileRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ProfileService {


    private final ProfileRepository profileRepository;

    /**
     * ➕ CREATE
     */
    public Profile create(Profile profile) {

        if (profileRepository.existsByName(profile.getName())) {
            throw new RuntimeException("Ce profil existe déjà");
        }

        return profileRepository.save(profile);
    }

    /**
     * 📄 GET ALL
     */
    public List<Profile> getAll() {
        return profileRepository.findAll();
    }

    /**
     * 👁 GET BY ID
     */
    public Profile getById(Long id) {

        return profileRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Profil introuvable"));
    }

    /**
     * ✏️ PATCH UPDATE
     */
    public Profile update(Long id, Map<String, Object> updates) {

        Profile profile = getById(id);

        if (updates.containsKey("name")) {

            String name = updates.get("name").toString();

            profile.setName(name);
        }

        return profileRepository.save(profile);
    }

    /**
     * 🗑 DELETE
     */
    public void delete(Long id) {

        Profile profile = getById(id);

        profileRepository.delete(profile);
    }
}