// package com.cm_policier.effectifs.config;

// import com.cm_policier.effectifs.model.Controle;
// import com.cm_policier.effectifs.model.Policier;
// import com.cm_policier.effectifs.repository.ControleRepository;
// import com.cm_policier.effectifs.repository.PolicierRepository;
// import com.github.javafaker.Faker;
// import lombok.RequiredArgsConstructor;
// import org.springframework.boot.CommandLineRunner;
// import org.springframework.stereotype.Component;

// import java.time.LocalDateTime;
// import java.util.List;
// import java.util.Random;
// import java.util.stream.Stream;

// @Component
// @RequiredArgsConstructor
// public class ControleSeeder implements CommandLineRunner {

//     private final ControleRepository controleRepository;
//     private final PolicierRepository policierRepository;

//     private final Faker faker = new Faker();
//     private final Random random = new Random();

//     @Override
//     public void run(String... args) {

//         if (controleRepository.count() > 0) {
//             System.out.println("✔ Contrôles déjà générés");
//             return;
//         }

//         List<Policier> policiers = policierRepository.findAll();

//         if (policiers.isEmpty()) {
//             System.out.println("❌ Aucun policier trouvé. Seeder annulé.");
//             return;
//         }

//         int total = 2000;

//         for (int i = 0; i < total; i++) {

//             Policier policier = policiers.get(random.nextInt(policiers.size()));

//             boolean present = random.nextBoolean();
//             boolean justifie = present && random.nextBoolean();

//             // 🔥 Construction du nom complet
//             String nomComplet = Stream.of(
//                             policier.getPrenom(),
//                             policier.getNom(),
//                             policier.getPostnom()
//                     )
//                     .filter(s -> s != null && !s.isBlank())
//                     .reduce((a, b) -> a + " " + b)
//                     .orElse("N/A");

//             Controle controle = Controle.builder()

//                     // UID
//                     .uid("CTRL-" + (10000 + i))

//                     // Relation
//                     .policier(policier)

//                     // Statut
//                     .present(present)
//                     .justifie(justifie)

//                     // 🔥 Infos récupérées depuis Policier
//                     .matricule(policier.getMatricule())
//                     .grade(policier.getOriginAdminGrade())
//                     .sexe(policier.getSexe())
//                     .noms(nomComplet)

//                     // Observation
//                     .observation(faker.lorem().sentence(6))

//                     // Flags
//                     .isControle(true)
//                     .isActif(true)
//                     .isCmd(false)

//                     // Biométrie
//                     .fingerprint(null)
//                     .fingerprint4(null)
//                     .face(null)

//                     // QR Code
//                     .qrcode("QR-" + faker.number().digits(8))

//                     // Dates
//                     .createdAt(LocalDateTime.now().minusDays(random.nextInt(30)))
//                     .updatedAt(LocalDateTime.now())

//                     .build();

//             controleRepository.save(controle);
//         }

//         System.out.println("🚀 2000 contrôles générés avec succès !");
//     }
// }