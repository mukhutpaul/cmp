package com.cm_policier.effectifs.repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.criteria.Predicate;

import org.springframework.stereotype.Repository;

import com.cm_policier.effectifs.model.Controle;
import com.cm_policier.effectifs.model.Policier;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;

@Repository
public class ControleRepositoryImpl implements ControleRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Controle> searchByIdentite(
            String nom,
            String postnom,
            String prenom,
            LocalDate dateNaissance) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Controle> cq = cb.createQuery(Controle.class);

        Root<Controle> c = cq.from(Controle.class);
        Join<Controle, Policier> p = c.join("policier");

        List<Predicate> predicates = new ArrayList<>();

        if (nom != null && !nom.isBlank()) {
            predicates.add(cb.like(cb.lower(p.get("nom")), "%" + nom.toLowerCase() + "%"));
        }

        if (postnom != null && !postnom.isBlank()) {
            predicates.add(cb.like(cb.lower(p.get("postnom")), "%" + postnom.toLowerCase() + "%"));
        }

        if (prenom != null && !prenom.isBlank()) {
            predicates.add(cb.like(cb.lower(p.get("prenom")), "%" + prenom.toLowerCase() + "%"));
        }

        if (dateNaissance != null) {
            predicates.add(cb.equal(p.get("dateNaissance"), dateNaissance));
        }

        cq.where(predicates.toArray(new Predicate[0]));

        return em.createQuery(cq).getResultList();
    }
}