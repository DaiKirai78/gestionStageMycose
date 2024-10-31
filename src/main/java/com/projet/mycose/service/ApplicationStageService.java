package com.projet.mycose.service;

import com.projet.mycose.dto.EtudiantDTO;
import com.projet.mycose.dto.OffreStageDTO;
import com.projet.mycose.modele.*;
import com.projet.mycose.repository.ApplicationStageRepository;
import com.projet.mycose.repository.EtudiantOffreStagePriveeRepository;
import com.projet.mycose.repository.EtudiantRepository;
import com.projet.mycose.repository.OffreStageRepository;
import com.projet.mycose.dto.ApplicationStageAvecInfosDTO;
import com.projet.mycose.dto.ApplicationStageDTO;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
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
    private final EtudiantRepository etudiantRepository;

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

    private void checkAccessToOffreStage(Etudiant etudiant, OffreStage offreStage) {
        if (offreStage.getStatus() != OffreStage.Status.ACCEPTED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Offre de stage non disponible");
        }
        if (offreStage.getVisibility() == OffreStage.Visibility.PRIVATE) {
            boolean isAssociated = etudiantOffreStagePriveeRepository.existsByOffreStageAndEtudiant(offreStage, etudiant);
            if (!isAssociated) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Offre de stage non disponible");
            }
        }
        if (offreStage.getProgramme() != etudiant.getProgramme()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Offre de stage non disponible car vous ne faites pas partie du programme associé à l'offre de stage");
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

    public List<ApplicationStageAvecInfosDTO> getApplicationsWithStatus(ApplicationStage.ApplicationStatus status) {
        return applicationStageRepository.findByStatusEquals(status).stream().map(this::convertToDTOAvecInfos).toList();
    }

    public ApplicationStageAvecInfosDTO getApplicationById(Long applicationId) {
        Long etudiantId = utilisateurService.getMyUserId();
        return applicationStageRepository.findByEtudiantIdAndOffreStageId(etudiantId, applicationId)
                .map(this::convertToDTOAvecInfos)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Application not found"));
    }

    public List<ApplicationStageAvecInfosDTO> getAllApplicationsPourUneOffreByIdPendingOrSummoned(Long offreId) {
        return applicationStageRepository
                .findAllByOffreStageIdAndStatusIn(offreId, List.of(ApplicationStage.ApplicationStatus.PENDING, ApplicationStage.ApplicationStatus.SUMMONED))
                .stream()
                .map(this::convertToDTOAvecInfos)
                .toList();
    }

    public List<ApplicationStageAvecInfosDTO> getApplicationsByEtudiant(Long etudiantId) {
        return applicationStageRepository.findByEtudiantId(etudiantId).stream().map(this::convertToDTOAvecInfos).toList();
    }

    @Transactional
    public ApplicationStageAvecInfosDTO accepterOuRefuserApplication(Long id, ApplicationStage.ApplicationStatus status) {
        ApplicationStage applicationStage = applicationStageRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Application not found"));

        if (applicationStage.getStatus() == ApplicationStage.ApplicationStatus.ACCEPTED || applicationStage.getStatus() == ApplicationStage.ApplicationStatus.REJECTED)
            throw new ResponseStatusException(HttpStatus.CONFLICT, "La candidature a déjà été acceptée ou refusée et ne peut pas être modifiée.");

        ApplicationStageAvecInfosDTO applicationStageAvecInfosDTO = convertToDTOAvecInfos(applicationStage);

        Etudiant etudiant = EtudiantDTO.toEntity(utilisateurService.getEtudiantDTO(applicationStageAvecInfosDTO.getEtudiant_id()));
        OffreStage offreStage = getValidatedOffreStage(applicationStageAvecInfosDTO.getOffreStage_id());

        if (status == ApplicationStage.ApplicationStatus.ACCEPTED)
            changeContractStatusToPending(applicationStageAvecInfosDTO.getEtudiant_id());

        applicationStage = mettreAJourApplication(applicationStageAvecInfosDTO, etudiant, offreStage, status);
        return ApplicationStageAvecInfosDTO.toDTO(applicationStageRepository.save(applicationStage));
    }

    protected ApplicationStage mettreAJourApplication(ApplicationStageAvecInfosDTO applicationStageAvecInfosDTO,
                                                      Etudiant etudiant,
                                                      OffreStage offreStage,
                                                      ApplicationStage.ApplicationStatus status) {
        ApplicationStage applicationStage = new ApplicationStage();
        applicationStage.setId(applicationStageAvecInfosDTO.getId());
        applicationStage.setOffreStage(offreStage);
        applicationStage.setEtudiant(etudiant);
        applicationStage.setStatus(status);

        return applicationStage;
    }

    public EtudiantDTO changeContractStatusToPending(Long etudiantId) {
        if (utilisateurService.getEtudiantDTO(etudiantId) == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "L'étudiant avec l'ID " + etudiantId + " est innexistant");

        Etudiant etudiant = EtudiantDTO.toEntity(utilisateurService.getEtudiantDTO(etudiantId));

        if (etudiant.getContractStatus() == Etudiant.ContractStatus.NO_CONTRACT) {
            etudiant.setContractStatus(Etudiant.ContractStatus.PENDING);
            return EtudiantDTO.toDTO(etudiantRepository.save(etudiant));
        } else
            throw new ResponseStatusException(HttpStatus.CONFLICT, "L'étudiant a déjà un stage actif ou une demande de stage active");
    }

    public ApplicationStageAvecInfosDTO summonEtudiant(Long id) {
        ApplicationStage applicationStage = applicationStageRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Application not found"));
        Long userId = utilisateurService.getMyUserId();
        Long applicationID = applicationStage.getOffreStage().getCreateur().getId();
        if (!userId.equals(applicationID)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to summon this student");
        }
        if (applicationStage.getStatus() != ApplicationStage.ApplicationStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Application is not pending");
        }
        applicationStage.setStatus(ApplicationStage.ApplicationStatus.SUMMONED);
        return convertToDTOAvecInfos(applicationStageRepository.save(applicationStage));
    }

    public EtudiantDTO getEtudiantFromApplicationId(Long applicationId) {
        ApplicationStage application = applicationStageRepository.findById(applicationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Application non trouvée"));
        return EtudiantDTO.toDTO(application.getEtudiant());
    }

    public OffreStageDTO getOffreStageFromApplicationId(Long applicationId) {
        ApplicationStage application = applicationStageRepository.findById(applicationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Application non trouvée"));
        return OffreStageDTO.toOffreStageInstaceDTO(application.getOffreStage());
    }
}
