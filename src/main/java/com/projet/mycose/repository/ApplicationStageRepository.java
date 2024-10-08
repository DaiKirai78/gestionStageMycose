package com.projet.mycose.repository;

import com.projet.mycose.modele.ApplicationStage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApplicationStageRepository extends JpaRepository<ApplicationStage, Long> {
    List<ApplicationStage> findByEtudiantId(Long etudiantId);
    List<ApplicationStage> findByOffreStageId(Long offreStageId);
}