package com.projet.mycose.service;

import com.projet.mycose.modele.ApplicationStage;
import com.projet.mycose.modele.Etudiant;
import com.projet.mycose.modele.OffreStage;
import com.projet.mycose.repository.ApplicationStageRepository;
import com.projet.mycose.repository.OffreStageRepository;
import com.projet.mycose.service.dto.ApplicationStageDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ApplicationStageService {
    private final ApplicationStageRepository applicationStageRepository;
    private final UtilisateurService utilisateurService;
    private final OffreStageRepository offreStageRepository;

    public ApplicationStageDTO applyToOffreStage(String token, Long offreStageId) throws AccessDeniedException, ChangeSetPersister.NotFoundException {
        Etudiant etudiant = (Etudiant) utilisateurService.getMeUtilisateur(token);
        OffreStage offreStage = offreStageRepository.findById(offreStageId).orElseThrow(ChangeSetPersister.NotFoundException::new);
        if (offreStage.getStatus() != OffreStage.Status.ACCEPTED) {
            throw new AccessDeniedException("Offre de stage non disponible");
        }
        ApplicationStage applicationStage = ApplicationStage.builder()
                .offreStage(offreStage)
                .etudiant(etudiant)
                .build();
        return convertToDTO(applicationStageRepository.save(applicationStage));
    }

    private ApplicationStageDTO convertToDTO(ApplicationStage applicationStage) {
        return ApplicationStageDTO.toDTO(applicationStage);
    }

    public List<ApplicationStageDTO> getApplicationsByEtudiant(Long etudiantId) {
        return applicationStageRepository.findByEtudiantId(etudiantId).stream().map(this::convertToDTO).toList();
    }

    public List<ApplicationStageDTO> getApplicationsByOffreStage(Long offreStageId) {
        return applicationStageRepository.findByOffreStageId(offreStageId).stream().map(this::convertToDTO).toList();

    }
}
