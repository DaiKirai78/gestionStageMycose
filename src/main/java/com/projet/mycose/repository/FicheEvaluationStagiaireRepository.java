package com.projet.mycose.repository;

import com.projet.mycose.modele.FicheEvaluationStagiaire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FicheEvaluationStagiaireRepository extends JpaRepository<FicheEvaluationStagiaire, Long> {

}
