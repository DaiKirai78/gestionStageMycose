package com.projet.mycose.repository;

import com.projet.mycose.modele.Contrat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ContratRepository extends JpaRepository<Contrat, Long> {

    Page<Contrat> findContratsBySignatureEmployeurIsNullAndEmployeur_Id(Long emprunteurId, Pageable pageable);
    int countBySignatureEmployeurIsNullAndEmployeurId(Long emloyeurId);

    Page<Contrat> findContratsBySignatureEmployeurIsNotNullAndSignatureEtudiantIsNotNullAndSignatureGestionnaireIsNotNull(Pageable pageable);
    int countContratsBySignatureEmployeurIsNotNullAndSignatureEtudiantIsNotNullAndSignatureGestionnaireIsNotNull();

    @Query(value = "SELECT * FROM contrat c WHERE c.signature_gestionnaire IS NOT NULL AND EXTRACT(YEAR FROM c.created_at) = :annee", nativeQuery = true)
    Page<Contrat> findContratSigneeParGestionnaire(int annee, Pageable pageable);
    @Query(value = "SELECT COUNT(*) FROM contrat c WHERE c.signature_gestionnaire IS NOT NULL AND EXTRACT(YEAR FROM c.created_at) = :annee", nativeQuery = true)
    int countByContratSigneeParGestionnaire(int annee);

}
