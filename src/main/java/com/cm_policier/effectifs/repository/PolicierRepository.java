package com.cm_policier.effectifs.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.cm_policier.effectifs.model.Policier;

@Repository
public interface PolicierRepository extends JpaRepository<Policier, Long> {
    //Optional<Policier> findByMatricule(String matricule);
    Optional<Policier> findByMatricule(String matricule);
    List<Policier> findByUnit(String unit);

    
    boolean existsByMatricule(String matricule);
    //Policier findByMatricule(String matricule);
    Optional<Policier> findByLastnameAndPostnameAndFirstnamesAndBirthDate(
            String lastname,
            String postname,
            String firstnames,
            LocalDate birthDate
    );
}
