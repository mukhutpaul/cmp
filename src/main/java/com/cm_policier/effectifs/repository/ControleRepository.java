package com.cm_policier.effectifs.repository;

import com.cm_policier.effectifs.model.Controle;
import com.cm_policier.effectifs.model.Equipe;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ControleRepository extends JpaRepository<Controle, UUID>, ControleRepositoryCustom {
  @Query("""
          SELECT c FROM Controle c
          LEFT JOIN c.policier p
          WHERE (:search IS NULL OR
                LOWER(c.matricule) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(c.uid) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(c.noms) LIKE LOWER(CONCAT('%', :search, '%'))
          )
      """)
  Page<Controle> search(String search, Pageable pageable);

  @Query("""
      SELECT c FROM Controle c
      JOIN c.policier p
      WHERE
      LOWER(p.lastname) LIKE LOWER(CONCAT('%', :lastname, '%'))
      AND LOWER(p.postname) LIKE LOWER(CONCAT('%', :postname, '%'))
      AND LOWER(p.firstnames) LIKE LOWER(CONCAT('%', :firstname, '%'))
      AND p.birthDate = :birthDate
      """)
  List<Controle> searchByPolicierIdentite(
      @Param("lastname") String lastname,
      @Param("postname") String postname,
      @Param("firstname") String firstname,
      @Param("birthDate") LocalDate birthDate);

  long count();

  long countByPresentTrue();

  long countByJustifieTrue();

  long countByJustifieFalse();

  long countByPresentFalseAndJustifieFalse();

  List<Controle> findAllByOrderByUpdatedAtDesc();

  Optional<Controle> findByMatricule(String matricule);

  Optional<Controle> findTopByOrderByIdDesc();

  long countByEquipe(Equipe equipe);

  long countByEquipeAndPresentTrue(Equipe equipe);

  long countByEquipeAndJustifieTrue(Equipe equipe);

  long countByEquipeAndPresentFalseAndJustifieFalse(Equipe equipe);

  @Query(value = """
          SELECT uid
          FROM controle
          ORDER BY CAST(
              SPLIT_PART(uid, '-', 4)
              AS INTEGER
          ) DESC
          LIMIT 1
      """, nativeQuery = true)
  String findLastUid();

  List<Controle> findByMissionId(Long missionId);

  Long countByMissionId(Long missionId);

  Long countByMissionIdAndPresentTrue(Long missionId);

  Long countByMissionIdAndJustifieTrue(Long missionId);

  Long countByMissionIdAndPresentFalseAndJustifieFalse(Long missionId);

  Long countByEquipeId(Long equipeId);

  Long countByEquipeIdAndPresentTrue(Long equipeId);

  Long countByEquipeIdAndJustifieTrue(Long equipeId);

  Long countByEquipeIdAndPresentFalseAndJustifieFalse(
      Long equipeId);

  Optional<Controle> findByUid(String uid);

  @Query("""
      SELECT COUNT(c)
      FROM Controle c
      WHERE c.present = true
      AND c.isSync = false
      AND c.seance.id = :seanceId
      """)
  Long countPresenceToSync(UUID seanceId);

  @Query("""
      SELECT COUNT(c)
      FROM Controle c
      WHERE c.present = false
      AND c.isSync = false
      AND c.seance.id = :seanceId
      AND c.seance.isActive = false
      """)
  Long countAbsenceToSync(UUID seanceId);

  @Query("""
      SELECT c
      FROM Controle c
      WHERE c.isSync = false
      AND (
          (
            c.present = true
            AND c.seance.isActive = true
          )
          OR
          (
            c.seance.isActive = false
          )
      )
      """)
  List<Controle> findReadyForSync(Pageable pageable);

  @Query("""
          SELECT COUNT(c)
          FROM Controle c
          WHERE c.seance.id = :seanceId
          AND c.justifie = true
          AND c.seance.isActive = false
      """)
  Long countJustifie(UUID seanceId);

   @Query("""
        SELECT c FROM Controle c
        WHERE c.updatedAt BETWEEN :start AND :end
    """)
    List<Controle> findByUpdatedAtToday(LocalDateTime start, LocalDateTime end);
}