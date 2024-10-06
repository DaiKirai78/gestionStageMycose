package com.projet.mycose.repository;

import com.projet.mycose.modele.FichierCV;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface FichierCVRepository extends JpaRepository<FichierCV, Long> {
    Optional<List<FichierCV>> getFichierCVSByStatusEquals(FichierCV.Status status, Pageable pageable);

    @Query("""
            SELECT f
            FROM FichierCV f
            WHERE f.etudiant.id = :etudiant_id
               AND (f.status = :status OR f.status = :status2)
            ORDER BY f.id ASC
            LIMIT 1
            """
    )
    Optional<FichierCV> getFirstByEtudiant_IdAndStatusEqualsOrStatusEquals(Long etudiant_id, FichierCV.Status status, FichierCV.Status status2);
}