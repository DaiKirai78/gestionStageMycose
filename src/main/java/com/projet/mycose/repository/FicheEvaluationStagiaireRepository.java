package com.projet.mycose.repository;

import com.projet.mycose.modele.Etudiant;
import com.projet.mycose.modele.FicheEvaluationStagiaire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FicheEvaluationStagiaireRepository extends JpaRepository<FicheEvaluationStagiaire, Long> {

    @Query("SELECT e FROM Etudiant e " +
            "WHERE e.contractStatus = Etudiant.ContractStatus.ACTIVE " +
            "AND e IN (" +
            "SELECT c.etudiant FROM Contrat c " +
            "WHERE c.employeur.id = :employeurId " +
            "AND c NOT IN (SELECT f.contrat FROM FicheEvaluationStagiaire f))")
    Optional<List<Etudiant>> findAllEtudiantWhereNotEvaluated(Long employeurId);
}
