package com.cm_policier.effectifs.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cm_policier.effectifs.model.Profile;
import com.cm_policier.effectifs.repository.ProfileRepository;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initProfiles(ProfileRepository profileRepository) {
        return args -> {
            createIfNotExists(profileRepository, "ADMIN");
            createIfNotExists(profileRepository, "USER");
            createIfNotExists(profileRepository, "MANAGER");

        };
    }

    private void createIfNotExists(ProfileRepository repo, String name) {
        if (repo.findByName(name).isEmpty()) {
            repo.save(Profile.builder().name(name).build());
        }
    }
}
