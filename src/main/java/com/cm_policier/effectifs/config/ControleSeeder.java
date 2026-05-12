package com.cm_policier.effectifs.config;

import com.cm_policier.effectifs.model.Controle;
import com.cm_policier.effectifs.model.Policier;
import com.cm_policier.effectifs.repository.ControleRepository;
import com.cm_policier.effectifs.repository.PolicierRepository;
import com.github.javafaker.Faker;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class ControleSeeder implements CommandLineRunner {

    private final ControleRepository controleRepository;
    private final PolicierRepository policierRepository;

    private final Faker faker = new Faker();
    private final Random random = new Random();

    @Override
    public void run(String... args) {

        if (controleRepository.count() > 0) {
            System.out.println("✔ Contrôles déjà générés");
            return;
        }

        List<Policier> policiers = policierRepository.findAll();

        if (policiers.isEmpty()) {
            System.out.println("❌ Aucun policier trouvé. Seeder annulé.");
            return;
        }

        int total = 2000;

        for (int i = 0; i < total; i++) {

            Policier policier = policiers.get(random.nextInt(policiers.size()));

            boolean present = random.nextBoolean();
            boolean justifie = present ? random.nextBoolean() : false;

            Controle controle = Controle.builder()
                    .uid("CTRL-" + (10000 + i))

                    .policier(policier)

                    .present(present)
                    .justifie(justifie)

                    .matricule("PNC-" + (1000 + random.nextInt(9000)))
                    .unite("UNITE-" + (1 + random.nextInt(10)))
                    .grade(random.nextBoolean() ? "CAPORAL" : "SERGENT")
                    .sexe(random.nextBoolean() ? "M" : "F")

                    .observation(faker.lorem().sentence(6))

                    .isControle(true)
                    .isActif(true)
                    .isCmd(false)

                    .fingerprint(null)
                    .fingerprint4(null)
                    .face(null)

                    .qrcode("QR-" + faker.number().digits(8))

                    .createdAt(LocalDateTime.now().minusDays(random.nextInt(30)))
                    .updatedAt(LocalDateTime.now())

                    .build();

            controleRepository.save(controle);
        }

        System.out.println("🚀 2000 contrôles générés avec succès !");
    }
}