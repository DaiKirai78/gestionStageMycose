package com.projet.mycose.repository;

import com.projet.mycose.modele.Etudiant;
import com.projet.mycose.modele.EtudiantOffreStagePrivee;
import com.projet.mycose.modele.OffreStage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EtudiantOffreStagePriveeRepository extends JpaRepository<EtudiantOffreStagePrivee, Long> {

    boolean existsByOffreStageAndEtudiant(OffreStage offreStage, Etudiant etudiant);
}