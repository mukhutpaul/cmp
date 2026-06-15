package com.cm_policier.effectifs.service;

import java.util.List;
import org.springframework.stereotype.Service;

import com.cm_policier.effectifs.model.Equipe;
import com.cm_policier.effectifs.model.EquipeUnite;
import com.cm_policier.effectifs.model.Unite;
import com.cm_policier.effectifs.model.User;
import com.cm_policier.effectifs.repository.EquipeRepository;
import com.cm_policier.effectifs.repository.EquipeUniteRepository;
import com.cm_policier.effectifs.repository.UniteRepository;
import com.cm_policier.effectifs.util.CurrentUserUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EquipeUniteService {

        private final EquipeUniteRepository repository;
        private final EquipeRepository equipeRepository;
        private final UniteRepository uniteRepository;
        private final LogUserService logUserService;

        private final UserService userService;

        // CREATE
        public EquipeUnite create(Long equipeId, Long uniteId) {

                Equipe equipe = equipeRepository.findById(equipeId)
                                .orElseThrow(() -> new RuntimeException("Equipe introuvable"));

                Unite unite = uniteRepository.findById(uniteId)
                                .orElseThrow(() -> new RuntimeException("Unité introuvable"));

                EquipeUnite eu = EquipeUnite.builder()
                                .equipe(equipe)
                                .unite(unite)
                                .isActive(true)
                                .build();
                String username = CurrentUserUtil.getCurrentUsername();
                User user = userService.findByUsername(username);
                logUserService.saveLog(user, "Affectation unité:" + eu.getUnite() + " à l'équipe: "
                                + eu.getEquipe().getUser().getUsername());

                return repository.save(eu);
        }

        // GET ALL
        public List<EquipeUnite> getAll() {
                return repository.findAll();
        }

        // GET BY ID
        public EquipeUnite getById(Long id) {
                return repository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Relation non trouvée"));
        }

        // UPDATE
        public EquipeUnite update(Long id, Long equipeId, Long uniteId) {

                EquipeUnite existing = getById(id);

                Equipe equipe = equipeRepository.findById(equipeId)
                                .orElseThrow(() -> new RuntimeException("Equipe introuvable"));

                Unite unite = uniteRepository.findById(uniteId)
                                .orElseThrow(() -> new RuntimeException("Unité introuvable"));

                existing.setEquipe(equipe);
                existing.setUnite(unite);

                return repository.save(existing);
        }

        public void delete(Long id) {

                if (!repository.existsById(id)) {
                        throw new RuntimeException("Cette affectation n'existe pas");
                }

                repository.deleteById(id);
        }

        public List<Unite> getUnitesByEquipe(Long equipeId) {

                List<EquipeUnite> relations = repository.findByEquipeId(equipeId);

                return relations.stream()
                                .map(EquipeUnite::getUnite)
                                .toList();
        }

        public List<EquipeUnite> findByEquipe(Long equipeId) {
                return repository.findByEquipeId(equipeId);
        }
}