package com.cm_policier.effectifs.config;


import com.cm_policier.effectifs.model.Controle;
import com.cm_policier.effectifs.model.Person;
import com.cm_policier.effectifs.model.Policier;
import com.cm_policier.effectifs.repository.ControleRepository;
import com.cm_policier.effectifs.repository.PersonRepository;
import com.cm_policier.effectifs.repository.PolicierRepository;
import com.github.javafaker.Faker;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ControleSeeder implements CommandLineRunner {

    private final ControleRepository controleRepository;
    private final PolicierRepository policeRepository;

    private final Faker faker = new Faker();
    private final Random random = new Random();

    @Override
    public void run(String... args) {

        if (controleRepository.count() > 0) {
            System.out.println("✔ Contrôles déjà générés");
            return;
        }

        List<Policier> policiers = policeRepository.findAll();

        if (policiers.isEmpty()) {
            System.out.println("❌ Aucun person trouvé. Seeder annulé.");
            return;
        }

        int total = 2000;

        for (int i = 0; i < total; i++) {

            Policier policier = policiers.get(random.nextInt(policiers.size()));

            Controle controle = Controle.builder()
                    .id(UUID.randomUUID())
                    .uid("CTRL-" + (10000 + i))

                    .policier(policier)

                    .present(random.nextBoolean())
                    .justifie(random.nextBoolean())

                    .situation(random.nextBoolean() ? "NORMAL" : "SUSPICION")
                    .status(random.nextBoolean() ? "VALID" : "PENDING")

                    .matricule("PNC-" + (1000 + random.nextInt(9000)))
                    .unite("UNITE-" + (1 + random.nextInt(10)))
                    .grade(random.nextBoolean() ? "CAPORAL" : "SERGENT")

                    .isActif(true)

                    .createdAt(LocalDateTime.now().minusDays(random.nextInt(30)))
                    .build();

            controleRepository.save(controle);
        }

        System.out.println("🚀 2000 contrôles générés avec succès !");
    }
}

