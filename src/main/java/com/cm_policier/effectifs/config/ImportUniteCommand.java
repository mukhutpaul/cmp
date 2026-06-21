// package com.cm_policier.effectifs.config;

// import com.cm_policier.effectifs.model.Unite;
// import com.cm_policier.effectifs.repository.PolicierRepository;
// import com.cm_policier.effectifs.repository.UniteRepository;
// import lombok.RequiredArgsConstructor;
// import org.springframework.boot.CommandLineRunner;
// import org.springframework.stereotype.Component;

// import java.util.HashSet;
// import java.util.List;
// import java.util.Set;

// @Component
// @RequiredArgsConstructor
// public class ImportUniteCommand implements CommandLineRunner {

//     private final PolicierRepository policierRepository;
//     private final UniteRepository uniteRepository;

//     @Override
//     public void run(String... args) throws Exception {

//         System.out.println("🚀 Début import unités...");

//         // ================================
//         // 1. récupérer toutes les unités distinctes depuis Policier
//         // ================================
//         List<String> rawUnits = policierRepository.findAllUnits();

//         if (rawUnits == null || rawUnits.isEmpty()) {
//             System.out.println("❌ Aucune unité trouvée dans Policier");
//             return;
//         }

//         // ================================
//         // 2. nettoyage + unique
//         // ================================
//         Set<String> uniqueUnits = new HashSet<>();

//         for (String unit : rawUnits) {
//             if (unit == null) continue;

//             unit = unit.trim();

//             if (unit.isEmpty()) continue;

//             uniqueUnits.add(unit);
//         }

//         // ================================
//         // 3. insertion en base
//         // ================================
//         int inserted = 0;
//         int skipped = 0;

//         for (String unitName : uniqueUnits) {

//             if (uniteRepository.existsByName(unitName)) {
//                 skipped++;
//                 continue;
//             }

//             Unite unite = Unite.builder()
//                     .name(unitName)
//                     .commandant(null)
//                     .signature(null)
//                     .equipeaf(null)
//                     .build();

//             uniteRepository.save(unite);
//             inserted++;

//             System.out.println("✔ Unite ajoutée : " + unitName);
//         }

//         System.out.println("\n===== IMPORT UNITÉS TERMINÉ =====");
//         System.out.println("✔ Insérées : " + inserted);
//         System.out.println("⏭ Déjà existantes : " + skipped);
//     }
// }