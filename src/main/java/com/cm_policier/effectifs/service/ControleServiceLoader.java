package com.cm_policier.effectifs.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
import com.cm_policier.effectifs.repository.PolicierRepository;
import com.cm_policier.effectifs.repository.SeanceRepository;
import com.cm_policier.effectifs.repository.UserRepository;
import com.cm_policier.effectifs.util.CurrentUserUtil;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ControleServiceLoader {

        
        private final PolicierRepository policierRepository;

        
        private final ControleRepository controleRepository;

        
        private final DetailEquipeRepository detailEquipeRepository;

        private final UserRepository userRepository;

        private final SeanceRepository seanceRepository;

        private final LogUserService logUserService;

        private final UserService userService;

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
                                        "%s-%d-%d-%06d",
                                        missionCode,
                                        chefEquipe.getUsername(),
                                        controleur.getUsername(),
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
                        String username = CurrentUserUtil.getCurrentUsername();
                        User user = userService.findByUsername(username);
                        logUserService.saveLog(user,
                                        "Chargement policiers de l'unité " + c.getUnite() + " au contrôle");
                       
                }


                return controleRepository.saveAll(controles);
        }
}