package com.projet.mycose.repository;

import com.projet.mycose.modele.FichierOffreStage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FichierOffreStageRepository extends JpaRepository<FichierOffreStage, Long> {
}