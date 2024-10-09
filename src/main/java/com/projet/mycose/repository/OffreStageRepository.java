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
    @Query("SELECT o FROM OffreStage o LEFT JOIN EtudiantOffreStagePrivee eop ON o.id = eop.offreStage.id WHERE eop.etudiant.id = :etudiantId OR eop.id IS NULL ORDER BY o.createdAt")
    Page<OffreStage> findOffresByEtudiantId(@Param("etudiantId") long etudiantId, Pageable pageable);

    long countByStatus(OffreStage.Status status);
    @Query("SELECT COUNT(o) FROM OffreStage o LEFT JOIN EtudiantOffreStagePrivee eop ON o.id = eop.offreStage.id WHERE eop.etudiant.id = :etudiantId OR eop.id IS NULL")
    int countByEtudiantsId(long etudiantId);

    @Query("SELECT o FROM OffreStage o LEFT JOIN o.applicationStages a ON a.etudiant.id = :etudiantId WHERE a.id IS NULL")
    List<OffreStage> findAllByEtudiantNotApplied(@Param("etudiantId") Long etudiantId);

    @Query("SELECT o FROM OffreStage o JOIN EtudiantOffreStagePrivee eop ON o.id = eop.offreStage.id WHERE eop.etudiant.id = :etudiantId OR eop.id IS NULL AND (o.title LIKE concat('%', :rechercheValue, '%') OR o.entrepriseName LIKE concat('%', :rechercheValue, '%'))")
    Page<OffreStage> findOffresByEtudiantIdWithSearch(@Param("etudiantId") long etudiantId, @Param("rechercheValue") String rechercheValue , Pageable pageable);
}
