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
        private EquipeRepository equipeRepository;

        @Autowired
        private MissionRepository missionRepository;

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

                // 4. chef équipe (user dans equipe)
                User chefEquipe = equipe.getUser();

                // 5. chef mission
                User chefMission = mission.getChargeMission();

                // 6. policiers de l’unité
                List<Policier> policiers = policierRepository.findByUnite(unite);

                List<Controle> controles = new ArrayList<>();
                Seance seance = seanceRepository.findByIsActiveTrue()
                                .orElseThrow(() -> new RuntimeException("Aucune séance active trouvée"));

                for (Policier p : policiers) {

                        Controle c = Controle.builder()
                                        .uid(equipe.getId().toString() + p.getId().toString() + "000000")
                                        .policier(p)

                                        .noms(p.getNom() + " " + p.getPostnom() + " " + p.getPrenom())
                                        .matricule(p.getMatricule())
                                        .unite(p.getUnite())
                                        .grade(p.getStatut())
                                        .sexe(p.getSexe())

                                        .controleur(controleur)
                                        .chefEquipe(chefEquipe)
                                        .chargeMission(chefMission)

                                        .seance(seance) // ou fetch repository
                                        .present(false)
                                        .justifie(false)
                                        .isControle(false)
                                        .isActif(false)
                                        .isSync(false)
                                        .face(null) // à remplir depuis FaceRepository
                                        .build();

                        controles.add(c);
                }

                return controleRepository.saveAll(controles);
        }
}