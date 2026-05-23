package com.cm_policier.effectifs.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cm_policier.effectifs.model.Controle;
import com.cm_policier.effectifs.model.DetailEquipe;
import com.cm_policier.effectifs.model.Equipe;
import com.cm_policier.effectifs.model.Mission;
import com.cm_policier.effectifs.model.Policier;
import com.cm_policier.effectifs.model.Seance;
import com.cm_policier.effectifs.model.User;
import com.cm_policier.effectifs.repository.ControleRepository;
import com.cm_policier.effectifs.repository.DetailEquipeRepository;
import com.cm_policier.effectifs.repository.EquipeRepository;
import com.cm_policier.effectifs.repository.MissionRepository;
import com.cm_policier.effectifs.repository.PolicierRepository;
import com.cm_policier.effectifs.repository.SeanceRepository;
import com.cm_policier.effectifs.repository.UserRepository;

@Service
public class ControleServiceLoader {

        @Autowired
        private PolicierRepository policierRepository;

        @Autowired
        private ControleRepository controleRepository;

        @Autowired
        private DetailEquipeRepository detailEquipeRepository;

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private SeanceRepository seanceRepository;

        public List<Controle> chargerControle(String unite, Long userId) {

                // 1. contrôleur connecté
                User controleur = userRepository.findById(userId)
                                .orElseThrow(() -> new RuntimeException("Controleur introuvable"));

                // 2. équipe du contrôleur
                DetailEquipe detailEquipe = detailEquipeRepository.findByUser_Id(userId)
                                .orElseThrow(() -> new RuntimeException("Equipe introuvable"));

                Equipe equipe = detailEquipe.getEquipe();

                // 3. mission
                Mission mission = equipe.getMission();

                // 4. chef équipe
                User chefEquipe = equipe.getUser();

                // 5. chargé mission
                User chefMission = mission.getChargeMission();

                // 6. policiers unité
                List<Policier> policiers = policierRepository.findByUnit(unite);

                // 7. séance active
                Seance seance = seanceRepository.findByIsActiveTrue()
                                .orElseThrow(() -> new RuntimeException("Aucune séance active trouvée"));

                // =========================
                // RECUPERATION DERNIER UID
                // =========================

                int sequence = 1;

               String dernierUid = controleRepository.findLastUid();

                if (dernierUid != null) {

                        try {

                                String[] parts = dernierUid.split("-");

                                String lastNumber = parts[parts.length - 1];

                                sequence = Integer.parseInt(lastNumber) + 1;

                        } catch (Exception e) {

                                sequence = 1;
                        }
                }

                List<Controle> controles = new ArrayList<>();

                for (Policier p : policiers) {

                        String missionCode = mission.getNumero().replace("-", "");

                        String uid = String.format(
                                        "%s-CE%d-CTR%d-%06d",
                                        missionCode,
                                        chefEquipe.getId(),
                                        controleur.getId(),
                                        sequence++);

                        Controle c = Controle.builder()
                                        .uid(uid)

                                        // ================= RELATIONS =================
                                        .policier(p)
                                        .controleur(controleur)
                                        .chefEquipe(chefEquipe)
                                        .chargeMission(chefMission)
                                        .seance(seance)
                                        .equipe(equipe)
                                        .mission(mission)

                                        // ================= INFOS =================
                                        .noms(p.getLastname() + " "
                                                        + p.getPostname() + " "
                                                        + p.getFirstnames())

                                        .matricule(p.getMatricule())
                                        .unite(p.getUnit())
                                        .grade(p.getRank())
                                        .sexe(p.getGender())

                                        // ================= FLAGS =================
                                        .present(false)
                                        .justifie(false)
                                        .isControle(false)
                                        .isActif(false)
                                        .isSync(false)

                                        // ================= BIOMETRIE =================
                                        .pkPhoto(p.getPkPhoto())

                                        .build();

                        controles.add(c);
                }

                return controleRepository.saveAll(controles);
        }
}