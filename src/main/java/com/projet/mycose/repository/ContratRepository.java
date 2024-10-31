package com.projet.mycose.repository;

import com.projet.mycose.modele.Contrat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContratRepository extends JpaRepository<Contrat, Long> {

    Page<Contrat> findContratsBySignatureEmployeurIsNullAndEmployeur_Id(Long emprunteurId, Pageable pageable);
}
