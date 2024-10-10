package com.projet.mycose.service;

import com.projet.mycose.modele.ApplicationStage;
import com.projet.mycose.modele.Etudiant;
import com.projet.mycose.modele.OffreStage;
import com.projet.mycose.repository.ApplicationStageRepository;
import com.projet.mycose.repository.OffreStageRepository;
import com.projet.mycose.service.dto.ApplicationStageAvecInfosDTO;
import com.projet.mycose.service.dto.ApplicationStageDTO;
import com.sun.jdi.request.DuplicateRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ApplicationStageService {
    private final ApplicationStageRepository applicationStageRepository;
    private final UtilisateurService utilisateurService;
    private final OffreStageRepository offreStageRepository;

    public ApplicationStageDTO applyToOffreStage(String token, Long offreStageId) throws AccessDeniedException, ChangeSetPersister.NotFoundException {
        Etudiant etudiant = (Etudiant) utilisateurService.getMeUtilisateur(token);
        OffreStage offreStage = offreStageRepository.findById(offreStageId).orElseThrow(ChangeSetPersister.NotFoundException::new);
        Optional<ApplicationStage> existingApplication = applicationStageRepository.findByEtudiantAndOffreStage(etudiant, offreStage);
        if (existingApplication.isPresent()) {
            throw new DuplicateRequestException("Etudiant has already applied to this OffreStage.");
            //Should throw 409, conflict
        }
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

    private ApplicationStageAvecInfosDTO convertToDTOAvecInfos(ApplicationStage applicationStage) {
        return ApplicationStageAvecInfosDTO.toDTO(applicationStage);
    }

    public List<ApplicationStageAvecInfosDTO> getApplicationsByEtudiant(String token) {
        Long etudiantId = utilisateurService.getUserIdByToken(token);
        return applicationStageRepository.findByEtudiantId(etudiantId).stream().map(this::convertToDTOAvecInfos).toList();
    }

    public List<ApplicationStageAvecInfosDTO> getApplicationsByEtudiantWithStatus(String token, ApplicationStage.ApplicationStatus status) {
        Long etudiantId = utilisateurService.getUserIdByToken(token);
        return applicationStageRepository.findByEtudiantIdAndStatusEquals(etudiantId, status).stream().map(this::convertToDTOAvecInfos).toList();
    }

    public ApplicationStageAvecInfosDTO getApplicationById(String token, Long applicationId) throws ChangeSetPersister.NotFoundException {
        Long etudiantId = utilisateurService.getUserIdByToken(token);
        return applicationStageRepository.findByEtudiantIdAndOffreStageId(etudiantId, applicationId).map(this::convertToDTOAvecInfos).orElseThrow(ChangeSetPersister.NotFoundException::new);
    }
}
