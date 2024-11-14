package com.projet.mycose.repository;

import com.projet.mycose.dto.SessionInfoDTO;
import com.projet.mycose.modele.Employeur;
import com.projet.mycose.modele.OffreStage;
import com.projet.mycose.modele.Programme;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Year;
import java.util.List;
import java.util.Optional;

@Repository
public interface OffreStageRepository extends JpaRepository<OffreStage, Long> {

    Optional<List<OffreStage>> getOffreStageWithStudentInfoByStatusEquals(OffreStage.Status status, Pageable pageable);

    @Query("SELECT o FROM OffreStage o" +
            " WHERE o.status = :status " +
            " ORDER BY o.createdAt")
    Optional<List<OffreStage>> getOffreStagesByStatusEquals(OffreStage.Status status);


    @Query("SELECT o FROM OffreStage o LEFT JOIN EtudiantOffreStagePrivee eop ON o.id = eop.offreStage.id " +
            "WHERE (eop.etudiant.id = :etudiantId OR eop.id IS NULL) " +
            "AND o.status = 'ACCEPTED' " +
            "AND o.programme = (SELECT e.programme FROM Etudiant e WHERE e.id = :etudiantId) " +
            "ORDER BY o.createdAt")
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

    @Query("SELECT o FROM OffreStage o " +
            "WHERE (o.createur.id = :idEmployeur) " +
            "AND (:annee IS NULL OR o.annee = :annee) " +
            "AND (:sessionEcole IS NULL OR o.session = :sessionEcole)")
    Page<OffreStage> findOffreStageByCreateurIdFiltered(
            @Param("idEmployeur") Long idEmployeur,
            @Param("annee") Year annee,
            @Param("sessionEcole") OffreStage.SessionEcole sessionEcole,
            Pageable pageable
    );

    @Query("SELECT COUNT(o) FROM OffreStage o " +
            "WHERE o.createur.id = :idEmployeur " +
            "AND (:annee IS NULL OR o.annee = :annee) " +
            "AND (:sessionEcole IS NULL OR o.session = :sessionEcole)")
    Long countOffreStageByCreateurIdFiltered(
            @Param("idEmployeur") Long idEmployeur,
            @Param("annee") Year annee,
            @Param("sessionEcole") OffreStage.SessionEcole sessionEcole
    );

    @Query("SELECT o FROM OffreStage o " +
            "LEFT JOIN EtudiantOffreStagePrivee eop ON o.id = eop.offreStage.id " +
            "LEFT JOIN o.applicationStages a ON a.etudiant.id = :etudiantId " +
            "LEFT JOIN FichierOffreStage f ON o.id = f.id " + // Left join with subclass
            "WHERE a.id IS NULL " +
            "AND (:annee IS NULL OR o.annee = :annee) " +
            "AND (:session IS NULL OR o.session = :session) " +
            "AND (" +
            "(o.visibility = 'PUBLIC' AND o.programme = :programme) " +
            "OR " +
            "(o.visibility = 'PRIVATE' AND eop.etudiant.id = :etudiantId)" +
            ") " +
            "AND LOWER(o.title) LIKE LOWER(CONCAT('%', :title, '%')) " +
            "AND (" +
            "f.id IS NULL " + // Not a Fichier
            "OR " +
            "f.data IS NOT NULL" + // If Fichier, data must not be null
            ")")
    Page<OffreStage> findAllByEtudiantNotAppliedFilteredWithTitle(
            @Param("etudiantId") Long id,
            @Param("programme") Programme programme,
            @Param("annee") Year annee,
            @Param("session") OffreStage.SessionEcole session,
            @Param("title") String title,
            Pageable pageable
    );

    @Query("SELECT o FROM OffreStage o " +
            "LEFT JOIN EtudiantOffreStagePrivee eop ON o.id = eop.offreStage.id " +
            "LEFT JOIN o.applicationStages a ON a.etudiant.id = :etudiantId " +
            "LEFT JOIN FichierOffreStage f ON o.id = f.id " + // Left join with subclass
            "WHERE a.id IS NOT NULL " +
            "AND (:annee IS NULL OR o.annee = :annee) " +
            "AND (:session IS NULL OR o.session = :session) " +
            "AND (" +
            "(o.visibility = 'PUBLIC' AND o.programme = :programme) " +
            "OR " +
            "(o.visibility = 'PRIVATE' AND eop.etudiant.id = :etudiantId)" +
            ") " +
            "AND LOWER(o.title) LIKE LOWER(CONCAT('%', :title, '%')) " +
            "AND (" +
            "f.id IS NULL " + // Not a Fichier
            "OR " +
            "f.data IS NOT NULL" + // If Fichier, data must not be null
            ")")
    Page<OffreStage> findAllByEtudiantFilteredWithTitle(
            @Param("etudiantId") Long id,
            @Param("programme") Programme programme,
            @Param("annee") Year annee,
            @Param("session") OffreStage.SessionEcole session,
            @Param("title") String title,
            Pageable pageable
    );




    @Query("SELECT COUNT(o) FROM OffreStage o " +
            "LEFT JOIN EtudiantOffreStagePrivee eop ON o.id = eop.offreStage.id " +
            "LEFT JOIN o.applicationStages a ON a.etudiant.id = :etudiantId " +
            "LEFT JOIN FichierOffreStage f ON o.id = f.id " + // Left join with subclass
            "WHERE a.id IS NULL " +
            "AND (:annee IS NULL OR o.annee = :annee) " +
            "AND (:session IS NULL OR o.session = :session) " +
            "AND (" +
            "(o.visibility = 'PUBLIC' AND o.programme = :programme) " +
            "OR " +
            "(o.visibility = 'PRIVATE' AND eop.etudiant.id = :etudiantId)" +
            ") " +
            "AND LOWER(o.title) LIKE LOWER(CONCAT('%', :title, '%')) " +
            "AND (" +
            "f.id IS NULL " + // Not a Fichier
            "OR " +
            "f.data IS NOT NULL" + // If Fichier, data must not be null
            ")")
    long countByEtudiantIdNotAppliedFilteredWithTitle(
            @Param("etudiantId") Long etudiantId,
            @Param("programme") Programme programme,
            @Param("annee") Year annee,
            @Param("session") OffreStage.SessionEcole sessionEcole,
            @Param("title") String title
    );

    @Query("SELECT e FROM OffreStage o JOIN Employeur e ON o.createur.id = e.id " +
            "WHERE o.id = :offreStageId")
    Employeur findEmployeurByOffreStageId(@Param("offreStageId") Long offreStageId);

    @Query("SELECT new com.projet.mycose.dto.SessionInfoDTO(o.session, o.annee) " +
            "FROM OffreStage o " +
            "WHERE o.createur.id = :createurId " +
            "GROUP BY o.session, o.annee " +
            "ORDER BY o.annee ASC, " +
            "CASE " +
            "  WHEN o.session = com.projet.mycose.modele.OffreStage.SessionEcole.HIVER THEN 1 " +
            "  WHEN o.session = com.projet.mycose.modele.OffreStage.SessionEcole.ETE THEN 2 " +
            "  WHEN o.session = com.projet.mycose.modele.OffreStage.SessionEcole.AUTOMNE THEN 3 " +
            "  ELSE 4 " +
            "END ASC")
    List<SessionInfoDTO> findDistinctSemesterAndYearByCreateurId(@Param("createurId") Long createurId);

    @Query("SELECT new com.projet.mycose.dto.SessionInfoDTO(o.session, o.annee) " +
            "FROM OffreStage o " +
            "GROUP BY o.session, o.annee " +
            "ORDER BY o.annee ASC, " +
            "CASE " +
            "  WHEN o.session = com.projet.mycose.modele.OffreStage.SessionEcole.HIVER THEN 1 " +
            "  WHEN o.session = com.projet.mycose.modele.OffreStage.SessionEcole.ETE THEN 2 " +
            "  WHEN o.session = com.projet.mycose.modele.OffreStage.SessionEcole.AUTOMNE THEN 3 " +
            "  ELSE 4 " +
            "END ASC")
    List<SessionInfoDTO> findDistinctSemesterAndYearAll();
}
