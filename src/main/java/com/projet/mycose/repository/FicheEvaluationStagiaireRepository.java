package com.projet.mycose.repository;

import com.projet.mycose.modele.Etudiant;
import com.projet.mycose.modele.FicheEvaluationStagiaire;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FicheEvaluationStagiaireRepository extends JpaRepository<FicheEvaluationStagiaire, Long> {

    @Query("SELECT e FROM Etudiant e " +
            "WHERE e.contractStatus = :contractStatus " +
            "AND e IN (" +
            "SELECT c.etudiant FROM Contrat c " +
            "WHERE c.employeur.id = :employeurId " +
            "AND c NOT IN (SELECT f.contrat FROM FicheEvaluationStagiaire f))")
    Page<Etudiant> findAllEtudiantWhereNotEvaluatedByEmployeeID(Long employeurId, Etudiant.ContractStatus contractStatus, Pageable pageable);

    @Query("SELECT e FROM Etudiant e " +
            "WHERE e.contractStatus = 'ACTIVE' " +
            "AND e IN (" +
            "SELECT c.etudiant FROM Contrat c " +
            "WHERE c NOT IN (SELECT f.contrat FROM FicheEvaluationStagiaire f)" +
            ")")
    List<Etudiant> findAllEtudiantWhereNotEvaluated();
}
