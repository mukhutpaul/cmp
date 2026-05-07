package com.cm_policier.effectifs.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.cm_policier.effectifs.model.Profile;
import com.cm_policier.effectifs.model.User;
import com.cm_policier.effectifs.repository.ProfileRepository;
import com.cm_policier.effectifs.repository.UserRepository;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initData(
            ProfileRepository profileRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {

            // =========================
            // 1. PROFILES
            // =========================
            Profile adminProfile = createIfNotExists(profileRepository, "ADMIN");
            createIfNotExists(profileRepository, "SUPERVISEUR");
            createIfNotExists(profileRepository, "CONTROLEUR");
            createIfNotExists(profileRepository, "MANAGER");

            // =========================
            // 2. ADMIN USER
            // =========================
            if (userRepository.findByUsername("admin").isEmpty()) {

                User admin = User.builder()
                        .username("admin")
                        .password(passwordEncoder.encode("12345"))
                        .email("admin@pnc.local")
                        .noms("Administrateur Système")
                        .profile(adminProfile)
                        .build();

                userRepository.save(admin);

                System.out.println("✅ ADMIN USER CREATED: admin / 12345");
            }
        };
    }

    private Profile createIfNotExists(ProfileRepository repo, String name) {
        return repo.findByName(name)
                .orElseGet(() -> repo.save(
                        Profile.builder()
                                .name(name)
                                .build()
                ));
    }
}