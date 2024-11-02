package com.projet.mycose.repository;

import com.projet.mycose.modele.Contrat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContratRepository extends JpaRepository<Contrat, Long> {

    Page<Contrat> findContratsBySignatureEmployeurIsNullAndEmployeur_Id(Long emprunteurId, Pageable pageable);
    int countBySignatureEmployeurIsNullAndEmployeurId(Long emloyeurId);

    Page<Contrat> findContratsBySignatureGestionnaireIsNull(Pageable pageable);
    int countBySignatureGestionnaireIsNull();

    Page<Contrat> findContratsBySignatureGestionnaireIsNotNullAndCreatedAt_Year(int annee, Pageable pageable);
    int countBySignatureGestionnaireIsNotNullAndCreatedAt_Year(int annee);
}
