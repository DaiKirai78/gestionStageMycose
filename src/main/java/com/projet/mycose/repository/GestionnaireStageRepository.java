package com.projet.mycose.repository;

import com.projet.mycose.modele.GestionnaireStage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GestionnaireStageRepository extends JpaRepository<GestionnaireStage, Long> {
}
