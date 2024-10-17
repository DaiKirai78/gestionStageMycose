package com.projet.mycose.service;

import com.projet.mycose.modele.*;
import com.projet.mycose.repository.ApplicationStageRepository;
import com.projet.mycose.repository.EtudiantOffreStagePriveeRepository;
import com.projet.mycose.repository.OffreStageRepository;
import com.projet.mycose.dto.ApplicationStageAvecInfosDTO;
import com.projet.mycose.dto.ApplicationStageDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ApplicationStageService {
    private final ApplicationStageRepository applicationStageRepository;
    private final UtilisateurService utilisateurService;
    private final OffreStageRepository offreStageRepository;
    private final EtudiantOffreStagePriveeRepository etudiantOffreStagePriveeRepository;

    @Transactional
    public ApplicationStageDTO applyToOffreStage(Long offreStageId) throws AccessDeniedException {
        Utilisateur utilisateur = utilisateurService.getMeUtilisateur();
        Etudiant etudiant = getValidatedEtudiant(utilisateur);

        OffreStage offreStage = getValidatedOffreStage(offreStageId);
        ensureNotAlreadyApplied(etudiant, offreStage);

        checkAccessToOffreStage(etudiant, offreStage);

        ApplicationStage applicationStage = ApplicationStage.builder()
                .offreStage(offreStage)
                .etudiant(etudiant)
                .build();
        return convertToDTO(applicationStageRepository.save(applicationStage));
    }

    private Etudiant getValidatedEtudiant(Utilisateur utilisateur) {
        if (!(utilisateur instanceof Etudiant etudiant)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User is not an Etudiant.");
        }
        return etudiant;
    }

    private OffreStage getValidatedOffreStage(Long offreStageId) {
        return offreStageRepository.findById(offreStageId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "OffreStage not found"));
    }

    private void ensureNotAlreadyApplied(Etudiant etudiant, OffreStage offreStage) {
        Optional<ApplicationStage> existingApplication = applicationStageRepository.findByEtudiantAndOffreStage(etudiant, offreStage);
        if (existingApplication.isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Etudiant has already applied to this OffreStage.");
        }
    }

    private void checkAccessToOffreStage(Etudiant etudiant, OffreStage offreStage) throws AccessDeniedException {
        if (offreStage.getStatus() != OffreStage.Status.ACCEPTED) {
            throw new AccessDeniedException("Offre de stage non disponible");
        }
        if (offreStage.getVisibility() == OffreStage.Visibility.PRIVATE) {
            boolean isAssociated = etudiantOffreStagePriveeRepository.existsByOffreStageAndEtudiant(offreStage, etudiant);
            if (!isAssociated) {
                throw new AccessDeniedException("Offre de stage non disponible");
            }
        }
        if (offreStage.getProgramme() != etudiant.getProgramme()) {
            throw new AccessDeniedException("Offre de stage non disponible car vous ne faites pas partie du programme associé à l'offre de stage");
        }
    }


    private ApplicationStageDTO convertToDTO(ApplicationStage applicationStage) {
        return ApplicationStageDTO.toDTO(applicationStage);
    }

    private ApplicationStageAvecInfosDTO convertToDTOAvecInfos(ApplicationStage applicationStage) {
        return ApplicationStageAvecInfosDTO.toDTO(applicationStage);
    }

    public List<ApplicationStageAvecInfosDTO> getApplicationsByEtudiant() {
        Long etudiantId = utilisateurService.getMyUserId();
        return applicationStageRepository.findByEtudiantId(etudiantId).stream().map(this::convertToDTOAvecInfos).toList();
    }

    public List<ApplicationStageAvecInfosDTO> getApplicationsByEtudiantWithStatus(ApplicationStage.ApplicationStatus status) {
        Long etudiantId = utilisateurService.getMyUserId();
        return applicationStageRepository.findByEtudiantIdAndStatusEquals(etudiantId, status).stream().map(this::convertToDTOAvecInfos).toList();
    }

    public ApplicationStageAvecInfosDTO getApplicationById(Long applicationId) {
        Long etudiantId = utilisateurService.getMyUserId();
        return applicationStageRepository.findByEtudiantIdAndOffreStageId(etudiantId, applicationId)
                .map(this::convertToDTOAvecInfos)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Application not found"));
    }

    public List<ApplicationStageDTO> getAllApplicationsPourUneOffreById(Long offreId) {
        return applicationStageRepository.findAllByOffreStageId(offreId).stream().map(this::convertToDTO).toList();
    }
}
