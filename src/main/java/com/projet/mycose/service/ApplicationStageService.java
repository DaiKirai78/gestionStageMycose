package com.projet.mycose.service;

import com.projet.mycose.modele.*;
import com.projet.mycose.repository.ApplicationStageRepository;
import com.projet.mycose.repository.EtudiantOffreStagePriveeRepository;
import com.projet.mycose.repository.OffreStageRepository;
import com.projet.mycose.dto.ApplicationStageAvecInfosDTO;
import com.projet.mycose.dto.ApplicationStageDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApplicationStageService {
    private final ApplicationStageRepository applicationStageRepository;
    private final UtilisateurService utilisateurService;
    private final OffreStageRepository offreStageRepository;
    private final EtudiantOffreStagePriveeRepository etudiantOffreStagePriveeRepository;

    public ApplicationStageDTO applyToOffreStage(String token, Long offreStageId) throws AccessDeniedException {
        Utilisateur utilisateur = utilisateurService.getMeUtilisateur(token);
        if (!(utilisateur instanceof Etudiant etudiant)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User is not an Etudiant.");
        }
        OffreStage offreStage = offreStageRepository.findById(offreStageId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "OffreStage not found"));
        Optional<ApplicationStage> existingApplication = applicationStageRepository.findByEtudiantAndOffreStage(etudiant, offreStage);
        //Check if the student has already applied to this OffreStage
        if (existingApplication.isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Etudiant has already applied to this OffreStage.");
        }
        checkAccessToOffreStage(etudiant, offreStage);
        ApplicationStage applicationStage = ApplicationStage.builder()
                .offreStage(offreStage)
                .etudiant(etudiant)
                .build();
        return convertToDTO(applicationStageRepository.save(applicationStage));
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
