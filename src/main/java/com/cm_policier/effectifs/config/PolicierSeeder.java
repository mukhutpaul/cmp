
package com.cm_policier.effectifs.config;

import com.cm_policier.effectifs.model.Policier;
import com.cm_policier.effectifs.repository.PolicierRepository;
import com.github.javafaker.Faker;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class PolicierSeeder implements CommandLineRunner {

    private final PolicierRepository repository;

    private final Faker faker = new Faker(new Locale("fr"));
    private final Random random = new Random();

    @Override
    public void run(String... args) {

        // Eviter doublons si déjà rempli
        if (repository.count() > 0) {
            System.out.println("Base déjà remplie.");
            return;
        }

        int total = 200_000;
        int batchSize = 1000;

        List<Policier> batch = new ArrayList<>();

        for (int i = 1; i <= total; i++) {

            Policier policier = Policier.builder()
                    .matricule("PNC-" + i)
                    .nom(faker.name().lastName())
                    .postnom(faker.name().lastName())
                    .prenom(faker.name().firstName())
                    .sexe(random.nextBoolean() ? "M" : "F")
                    .dateNaissance(
                            faker.date()
                                    .birthday(20, 60)
                                    .toInstant()
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDate()
                    )
                    .lieuNaissance(faker.address().city())
                    .villeNaissance(faker.address().city())
                    .villageNaissance(faker.address().streetName())
                    .paysDeNaissance("RDC")
                    .taille(150 + random.nextInt(50))
                    .couleurYeux("Noir")
                    .telephone("09" + (10000000 + random.nextInt(89999999)))
                    .email("policier" + i + "@gmail.com")
                    .adresse(faker.address().fullAddress())
                    .commune("Gombe")
                    .dateEntreePolice(LocalDate.now().minusYears(random.nextInt(20)))
                    .statut("ACTIF")
                    .groupeSanguin("O+")
                    .provinceOrigin("Kinshasa")
                    .profession("Policier")
                    .villeIntegration("Kinshasa")
                    .etatcivil(random.nextBoolean() ? "Marié" : "Célibataire")
                    .sport("Football")
                    .francaisParle(true)
                    .francaisEcrit(true)
                    .lingalaParle(true)
                    .lingalaEcrit(true)
                    .swahiliParle(random.nextBoolean())
                    .swahiliEcrit(random.nextBoolean())
                    .englishParle(random.nextBoolean())
                    .englishEcrit(random.nextBoolean())
                    .autresLangues("Teke")
                    .pcount(i)
                    .position("ACTIVE")
                    .build();

            batch.add(policier);

            if (batch.size() == batchSize) {
                repository.saveAll(batch);
                System.out.println("Enregistré : " + i);
                batch.clear();
            }
        }

        if (!batch.isEmpty()) {
            repository.saveAll(batch);
        }

        System.out.println("===== 200000 policiers générés =====");
    }
}
