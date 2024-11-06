package com.projet.mycose.repository;

import com.projet.mycose.modele.Contrat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ContratRepository extends JpaRepository<Contrat, Long> {

    Page<Contrat> findContratsBySignatureEmployeurIsNullAndEmployeur_Id(Long emprunteurId, Pageable pageable);
    int countBySignatureEmployeurIsNullAndEmployeurId(Long emloyeurId);

    Page<Contrat> findContratsBySignatureEmployeurIsNotNullAndSignatureEtudiantIsNotNullAndSignatureGestionnaireIsNull(Pageable pageable);
    int countContratsBySignatureEmployeurIsNotNullAndSignatureEtudiantIsNotNullAndSignatureGestionnaireIsNull();

    @Query(value = "SELECT c FROM contrat c WHERE c.signature_gestionnaire IS NOT NULL AND EXTRACT(YEAR FROM c.created_at) = :annee", nativeQuery = true)
    Page<Contrat> findContratSigneeParGestionnaire(int annee, Pageable pageable);
    @Query(value = "SELECT COUNT(*) FROM contrat c WHERE c.signature_gestionnaire IS NOT NULL AND EXTRACT(YEAR FROM c.created_at) = :annee", nativeQuery = true)
    int countByContratSigneeParGestionnaire(int annee);

    @Query(value = "SELECT DISTINCT DATE(created_at) FROM contrat " +
            "WHERE signature_etudiant IS NOT NULL " +
            "AND signature_employeur IS NOT NULL " +
            "AND signature_gestionnaire IS NOT NULL " +
            "ORDER BY DATE(created_at) ASC", nativeQuery = true)
    List<LocalDateTime> findDistinctCreatedAtForSignedContrats();





    Page<Contrat> findContratsBySignatureEtudiantIsNullAndEtudiant_Id(Long etudiantId, Pageable pageable);

    int countBySignatureEtudiantIsNullAndEtudiantId(Long etudiantId);
}
