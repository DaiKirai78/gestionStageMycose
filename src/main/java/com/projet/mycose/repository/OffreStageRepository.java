package com.projet.mycose.repository;

import com.projet.mycose.modele.FichierCV;
import com.projet.mycose.modele.OffreStage;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OffreStageRepository extends JpaRepository<OffreStage, Long> {
    Optional<List<OffreStage>> getOffreStageByStatusEquals(OffreStage.Status status, Pageable pageable);

    long countByStatus(OffreStage.Status status);
}
