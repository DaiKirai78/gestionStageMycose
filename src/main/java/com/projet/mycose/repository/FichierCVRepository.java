package com.projet.mycose.repository;

import com.projet.mycose.modele.FichierCV;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

@Repository
public interface FichierCVRepository extends JpaRepository<FichierCV, Long> {
    Optional<List<FichierCV>> getFichierCVSByStatusEquals(FichierCV.Status status, Pageable pageable);

    Optional<FichierCV> getFirstByEtudiant_IdAndStatusEqualsOrStatusEqualsOrStatusEquals(Long etudiant_id, FichierCV.Status status, FichierCV.Status status1, FichierCV.Status status2);

    Optional<FichierCV> getCurrentCvByEtudiant_id(Long etudiant_id);

    long countAllByStatusEquals(FichierCV.Status status);
}
