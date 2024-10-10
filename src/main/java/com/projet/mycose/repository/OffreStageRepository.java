package com.projet.mycose.repository;

import com.projet.mycose.modele.FichierCV;
import com.projet.mycose.modele.OffreStage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OffreStageRepository extends JpaRepository<OffreStage, Long> {
    Optional<List<OffreStage>> getOffreStageByStatusEquals(OffreStage.Status status, Pageable pageable);

    long countByStatus(OffreStage.Status status);

    @Query("SELECT o FROM OffreStage o LEFT JOIN o.applicationStages a ON a.etudiant.id = :etudiantId WHERE a.id IS NULL")
    List<OffreStage> findAllByEtudiantNotApplied(@Param("etudiantId") Long etudiantId);

    Page<OffreStage> findOffreStageByCreateurId(@Param("employeurId") Long employeurId, Pageable pageable);

    int countByCreateurId(Long employeurId);
}
