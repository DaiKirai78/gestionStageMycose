package com.projet.mycose.repository;

import com.projet.mycose.modele.Etudiant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FicheEvaluationMilieuStageRepository extends JpaRepository<FicheEvaluationMilieuStageRepository, Long> {

    Optional<List<Etudiant>> findAllEtudiantsNonEvaluesByProf(Long enseignantId);
}
