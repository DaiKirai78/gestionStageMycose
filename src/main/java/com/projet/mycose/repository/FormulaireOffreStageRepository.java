package com.projet.mycose.repository;

import com.projet.mycose.modele.FormulaireOffreStage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FormulaireOffreStageRepository extends JpaRepository<FormulaireOffreStage, Long> {
}