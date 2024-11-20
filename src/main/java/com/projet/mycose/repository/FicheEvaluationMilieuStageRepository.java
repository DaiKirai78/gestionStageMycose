package com.projet.mycose.repository;

import com.projet.mycose.modele.Etudiant;
import com.projet.mycose.modele.FicheEvaluationMilieuStage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface FicheEvaluationMilieuStageRepository extends JpaRepository<FicheEvaluationMilieuStage, Long> {

    @Query("SELECT e FROM Etudiant e " +
            "JOIN e.contrat c " +
            "WHERE e.contractStatus = :contractStatus " +
            "AND e.enseignantAssignee.id = :enseignantId " +
            "AND c NOT IN (" +
            "    SELECT f.contrat FROM FicheEvaluationMilieuStage f" +
            ")")
    Optional<List<Etudiant>> findAllEtudiantsNonEvaluesByProf(Long enseignantId, Etudiant.ContractStatus contractStatus);
}
