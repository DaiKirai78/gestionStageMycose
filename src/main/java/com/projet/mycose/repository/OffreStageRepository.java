package com.projet.mycose.repository;

import com.projet.mycose.modele.OffreStage;
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
    @Query("SELECT o FROM OffreStage o LEFT JOIN o.etudiants e WHERE e.id = :etudiantId OR e.id IS NULL")
    Page<OffreStage> findOffresByEtudiantId(@Param("etudiantId") long etudiantId, Pageable pageable);

    long countByStatus(OffreStage.Status status);

    @Query("SELECT count(o) FROM OffreStage o LEFT JOIN o.etudiants e WHERE e.id = :id OR e.id IS NULL")
    int countByEtudiantsId(long id);

    @Query("SELECT o FROM OffreStage o LEFT JOIN o.applicationStages a ON a.etudiant.id = :etudiantId WHERE a.id IS NULL")
    List<OffreStage> findAllByEtudiantNotApplied(@Param("etudiantId") Long etudiantId);

    @Query(
            "SELECT o " +
                    "FROM OffreStage o " +
                    "LEFT JOIN o.etudiants e " +
                    "WHERE (e.id = :etudiantId OR e.id IS NULL) " +
                    "AND LOWER(o.title) LIKE LOWER(CONCAT('%',:rechercheValue, '%')) " +
                    "OR LOWER(o.entrepriseName) LIKE LOWER(CONCAT('%',:rechercheValue, '%'))"
    )
    Page<OffreStage> findOffresByEtudiantIdWithSearch(@Param("etudiantId") long etudiantId, @Param("rechercheValue") String rechercheValue , Pageable pageable);
}
