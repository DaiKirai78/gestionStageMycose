package com.projet.mycose.service;

import com.projet.mycose.dto.EtudiantDTO;
import com.projet.mycose.dto.OffreStageDTO;
import com.projet.mycose.dto.*;
import com.projet.mycose.exceptions.*;
import com.projet.mycose.modele.*;
import com.projet.mycose.repository.ApplicationStageRepository;
import com.projet.mycose.repository.EtudiantOffreStagePriveeRepository;
import com.projet.mycose.repository.EtudiantRepository;
import com.projet.mycose.repository.OffreStageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public ApplicationStageDTO applyToOffreStage(Long offreStageId) {
        Utilisateur utilisateur = null;
        try {
            utilisateur = utilisateurService.getMeUtilisateur();
        } catch (AccessDeniedException e) {
            throw new AuthenticationException(HttpStatus.UNAUTHORIZED, "Problème d'authentification");
        }
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
            throw new AuthenticationException(HttpStatus.FORBIDDEN, "User is not an Etudiant.");
        }
        return etudiant;
    }

    private OffreStage getValidatedOffreStage(Long offreStageId) {
        return offreStageRepository.findById(offreStageId)
                .orElseThrow(() -> new ResourceNotFoundException("OffreStage not found"));
    }

    private void ensureNotAlreadyApplied(Etudiant etudiant, OffreStage offreStage) {
        Optional<ApplicationStage> existingApplication = applicationStageRepository.findByEtudiantAndOffreStage(etudiant, offreStage);
        if (existingApplication.isPresent()) {
            throw new ResourceConflictException("Etudiant has already applied to this OffreStage.");
        }
    }

    private void checkAccessToOffreStage(Etudiant etudiant, OffreStage offreStage) {
        if (offreStage.getStatus() != OffreStage.Status.ACCEPTED) {
            throw new ResourceNotAvailableException("Offre de stage non disponible");
        }
        if (offreStage.getVisibility() == OffreStage.Visibility.PRIVATE) {
            boolean isAssociated = etudiantOffreStagePriveeRepository.existsByOffreStageAndEtudiant(offreStage, etudiant);
            if (!isAssociated) {
                throw new ResourceNotAvailableException("Offre de stage non disponible");
            }
        }
        if (offreStage.getProgramme() != etudiant.getProgramme()) {
            throw new ResourceNotAvailableException("Offre de stage non disponible car vous ne faites pas partie du programme associé à l'offre de stage");
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
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));
    }

    public List<ApplicationStageAvecInfosDTO> getAllApplicationsPourUneOffreByIdPendingOrSummoned(Long offreId) {
        return applicationStageRepository
                .findAllByOffreStageIdAndStatusIn(offreId, List.of(ApplicationStage.ApplicationStatus.PENDING, ApplicationStage.ApplicationStatus.SUMMONED))
                .stream()
                .map(this::convertToDTOAvecInfos)
                .toList();
    }

    @Transactional
    public ApplicationStageAvecInfosDTO accepterOuRefuserApplication(Long id, ApplicationStage.ApplicationStatus status) {
        ApplicationStage applicationStage = applicationStageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));

        if (applicationStage.getStatus() != ApplicationStage.ApplicationStatus.PENDING && applicationStage.getStatus() != ApplicationStage.ApplicationStatus.SUMMONED)
            throw new ResourceConflictException("La candidature a déjà été acceptée ou refusée et ne peut pas être modifiée.");

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
            throw new ResourceNotFoundException("L'étudiant avec l'ID " + etudiantId + " est innexistant");

        Etudiant etudiant = etudiantRepository.findEtudiantById(etudiantId);

        if (etudiant.getContractStatus() == Etudiant.ContractStatus.NO_CONTRACT) {
            etudiant.setContractStatus(Etudiant.ContractStatus.PENDING);
            return EtudiantDTO.toDTO(etudiantRepository.save(etudiant));
        } else
            throw new ResourceConflictException("L'étudiant a déjà un stage actif ou une demande de stage active");
    }

    @Transactional
    public ApplicationStageAvecInfosDTO summonEtudiant(Long id, SummonEtudiantDTO summonEtudiantDTO) {
        ApplicationStage applicationStage = applicationStageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));
        Long userId = utilisateurService.getMyUserId();
        Long applicationID = applicationStage.getOffreStage().getCreateur().getId();
        if (!userId.equals(applicationID)) {
            throw new ResourceForbiddenException("You are not allowed to summon this student");
        }
        if (applicationStage.getStatus() != ApplicationStage.ApplicationStatus.PENDING) {
            throw new ResourceNotAvailableException("Application is not pending");
        }
        createConvocation(applicationStage, summonEtudiantDTO);

        //Convocation should be properly persisted without accessing the convocationRepository as CascadeType is set to ALL in the Entity
        return convertToDTOAvecInfos(applicationStageRepository.save(applicationStage));
    }

    private void createConvocation(ApplicationStage applicationStage, SummonEtudiantDTO summonEtudiantDTO) {
        Convocation convocation = new Convocation(applicationStage, summonEtudiantDTO);
        applicationStage.setConvocation(convocation);
        applicationStage.setStatus(ApplicationStage.ApplicationStatus.SUMMONED);
    }


    public ApplicationStageAvecInfosDTO answerSummon(Long id, AnswerSummonDTO answer) {
        //Fetch type is set to EAGER for Convocation & Etudiant so no need for a special query to fetch the convocation
        ApplicationStage applicationStage = applicationStageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));
        Long userId = utilisateurService.getMyUserId();
        Long applicationID = applicationStage.getEtudiant().getId();
        if (!userId.equals(applicationID)) {
            throw new ResourceForbiddenException("You are not allowed to answer this summon");
        }
        if (applicationStage.getStatus() != ApplicationStage.ApplicationStatus.SUMMONED) {
            throw new ResourceNotAvailableException("Application is not summoned");
        }
        if (applicationStage.getConvocation().getStatus() != Convocation.ConvocationStatus.PENDING) {
            throw new ResourceNotAvailableException("Convocation is not pending");
        }
        if (answer.getStatus() != Convocation.ConvocationStatus.ACCEPTED && answer.getStatus() != Convocation.ConvocationStatus.REJECTED) {
            throw new ResourceNotAvailableException("Invalid status");
        }
        applicationStage.getConvocation().setMessageEtudiant(answer.getMessageEtudiant());
        applicationStage.getConvocation().setStatus(answer.getStatus());
        return convertToDTOAvecInfos(applicationStageRepository.save(applicationStage));
    }
}
