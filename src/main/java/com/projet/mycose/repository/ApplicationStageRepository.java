package com.projet.mycose.repository;

import com.projet.mycose.modele.ApplicationStage;
import com.projet.mycose.modele.Etudiant;
import com.projet.mycose.modele.OffreStage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationStageRepository extends JpaRepository<ApplicationStage, Long> {
    List<ApplicationStage> findByEtudiantId(Long etudiantId);
    List<ApplicationStage> findByOffreStageId(Long offreStageId);

    Optional<ApplicationStage> findByEtudiantAndOffreStage(Etudiant etudiant, OffreStage offreStage);

    Optional<ApplicationStage> findByEtudiantIdAndOffreStageId(Long etudiantId, Long offreStageId);

    List<ApplicationStage> findByEtudiantIdAndStatusEquals(Long etudiantId, ApplicationStage.ApplicationStatus status);
    List<ApplicationStage> findAllByOffreStageId(Long offreStageId);
}