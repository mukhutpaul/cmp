package com.cm_policier.effectifs.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.cm_policier.effectifs.model.Unite;

public interface UniteRepository extends JpaRepository<Unite, Long> {
 boolean existsByName(String name);
}