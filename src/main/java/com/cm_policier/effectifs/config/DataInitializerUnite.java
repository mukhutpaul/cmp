package com.cm_policier.effectifs.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.cm_policier.effectifs.model.Unite;
import com.cm_policier.effectifs.repository.UniteRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DataInitializerUnite implements CommandLineRunner {

    private final UniteRepository uniteRepository;

    @Override
    public void run(String... args) throws Exception {

        // éviter doublons
        if (uniteRepository.count() > 0) {
            return;
        }

        for (int i = 1; i <= 2001; i++) {

            Unite unite = Unite.builder()
                    .name("unite" + i)
                    .build();

            uniteRepository.save(unite);
        }

        System.out.println("✅ 100 unités générées");
    }
}