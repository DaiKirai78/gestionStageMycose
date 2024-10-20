package com.projet.mycose.repository;

import com.projet.mycose.modele.OffreStage;
import com.projet.mycose.modele.Programme;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OffreStageRepository extends JpaRepository<OffreStage, Long> {
    Optional<List<OffreStage>> getOffreStageByStatusEquals(OffreStage.Status status, Pageable pageable);
    @Query("SELECT o FROM OffreStage o LEFT JOIN EtudiantOffreStagePrivee eop ON o.id = eop.offreStage.id WHERE eop.etudiant.id = :etudiantId OR eop.id IS NULL AND o.status = 'ACCEPTED' ORDER BY o.createdAt")
    Page<OffreStage> findOffresByEtudiantId(@Param("etudiantId") long etudiantId, Pageable pageable);

    long countByStatus(OffreStage.Status status);

    @Query("SELECT COUNT(o) FROM OffreStage o LEFT JOIN EtudiantOffreStagePrivee eop ON o.id = eop.offreStage.id WHERE eop.etudiant.id = :etudiantId OR eop.id IS NULL")
    int countByEtudiantsId(long etudiantId);

    @Query("SELECT o FROM OffreStage o " +
            "LEFT JOIN EtudiantOffreStagePrivee eop ON o.id = eop.offreStage.id " +
            "LEFT JOIN o.applicationStages a ON a.etudiant.id = :etudiantId " +
            "WHERE a.id IS NULL AND ((o.visibility = 'PUBLIC' AND o.programme = :programme) " +
            "OR (o.visibility = 'PRIVATE' AND eop.etudiant.id = :etudiantId))")
    List<OffreStage> findAllByEtudiantNotApplied(@Param("etudiantId") Long etudiantId, @Param("programme") Programme programme);

    @Query("SELECT o FROM OffreStage o " +
            "LEFT JOIN EtudiantOffreStagePrivee eop ON o.id = eop.offreStage.id " +
            "WHERE (eop.etudiant.id = :etudiantId OR eop.id IS NULL) " +
            "AND (LOWER(o.title) LIKE LOWER(concat('%', :rechercheValue, '%')) " +
            "OR LOWER(o.entrepriseName) LIKE LOWER(concat('%', :rechercheValue, '%'))) " +
            "ORDER BY o.createdAt")
    Page<OffreStage> findOffresByEtudiantIdWithSearch(@Param("etudiantId") long etudiantId, @Param("rechercheValue") String rechercheValue, Pageable pageable);

    Page<OffreStage> findOffreStageByCreateurId(@Param("employeurId") Long employeurId, Pageable pageable);

    int countByCreateurId(Long employeurId);
}
