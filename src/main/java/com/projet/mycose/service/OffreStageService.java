package com.projet.mycose.service;

import com.projet.mycose.dto.*;
import com.projet.mycose.modele.*;
import com.projet.mycose.modele.auth.Role;
import com.projet.mycose.repository.*;
import com.projet.mycose.modele.FichierOffreStage;
import com.projet.mycose.modele.FormulaireOffreStage;
import com.projet.mycose.modele.OffreStage;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import org.modelmapper.ModelMapper;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.*;

@Service
public class OffreStageService {
    private final OffreStageRepository offreStageRepository;
    private final ModelMapper modelMapper;
    private final Validator validator;
    private final UtilisateurService utilisateurService;
    private final UtilisateurRepository utilisateurRepository;
    private final FormulaireOffreStageRepository formulaireOffreStageRepository;
    private final FichierOffreStageRepository ficherOffreStageRepository;
    private static final int LIMIT_PER_PAGE = 10;
    private final EtudiantRepository etudiantRepository;
    private final EtudiantOffreStagePriveeRepository etudiantOffreStagePriveeRepository;

    public OffreStageService(OffreStageRepository offreStageRepository, ModelMapper modelMapper, Validator validator, UtilisateurRepository utilisateurRepository, UtilisateurService utilisateurService, FormulaireOffreStageRepository formulaireOffreStageRepository, FichierOffreStageRepository ficherOffreStageRepository, EtudiantRepository etudiantRepository, EtudiantOffreStagePriveeRepository etudiantOffreStagePriveeRepository) {
        this.offreStageRepository = offreStageRepository;
        this.modelMapper = modelMapper;
        this.validator = validator;
        this.utilisateurService = utilisateurService;
        this.utilisateurRepository = utilisateurRepository;
        this.formulaireOffreStageRepository = formulaireOffreStageRepository;
        this.ficherOffreStageRepository = ficherOffreStageRepository;
        this.etudiantRepository = etudiantRepository;
        this.etudiantOffreStagePriveeRepository = etudiantOffreStagePriveeRepository;
    }

    public OffreStageDTO convertToDTO(OffreStage offreStage){
        if (offreStage instanceof FormulaireOffreStage) {
            return convertToDTO((FormulaireOffreStage) offreStage);
        } else {
            return convertToDTO((FichierOffreStage) offreStage);
        }
    }


    // Convert Entity to DTO
    public FichierOffreStageDTO convertToDTO(FichierOffreStage fichierOffreStage) {
        FichierOffreStageDTO fichierOffreStageDTO = modelMapper.map(fichierOffreStage, FichierOffreStageDTO.class);

        // Convert byte[] data to Base64 string
        fichierOffreStageDTO.setFileData(Base64.getEncoder().encodeToString(fichierOffreStage.getData()));

        if (fichierOffreStage.getCreateur() != null) {
            fichierOffreStageDTO.setCreateur_id(fichierOffreStage.getCreateur().getId());
        }

        return fichierOffreStageDTO;
    }

    // Convert DTO to Entity
    public FichierOffreStage convertToEntity(FichierOffreStageDTO dto) {
        FichierOffreStage fichierOffreStage = modelMapper.map(dto, FichierOffreStage.class);

        // Convert Base64 string back to byte[]
        fichierOffreStage.setData(Base64.getDecoder().decode(dto.getFileData()));

        if (dto.getCreateur_id() != null) {
            Utilisateur createur = utilisateurRepository.findById(dto.getCreateur_id())
                    .orElseThrow(() -> new EntityNotFoundException("Utilisateur not found with ID: " + dto.getCreateur_id()));
            fichierOffreStage.setCreateur(createur);
        } else {
            throw new IllegalArgumentException("createur_id cannot be null");
        }

        return fichierOffreStage;
    }

    public FormulaireOffreStageDTO convertToDTO(FormulaireOffreStage formulaireOffreStage) {
        FormulaireOffreStageDTO formulaireOffreStageDTO = modelMapper.map(formulaireOffreStage, FormulaireOffreStageDTO.class);

        if (formulaireOffreStage.getCreateur() != null) {
            formulaireOffreStageDTO.setCreateur_id(formulaireOffreStage.getCreateur().getId());
        }

        return formulaireOffreStageDTO;
    }

    public FormulaireOffreStage convertToEntity(FormulaireOffreStageDTO dto) {
        FormulaireOffreStage formulaireOffreStage = modelMapper.map(dto, FormulaireOffreStage.class);

        if (dto.getCreateur_id() != null) {
            Utilisateur createur = utilisateurRepository.findById(dto.getCreateur_id())
                    .orElseThrow(() -> new EntityNotFoundException("Utilisateur not found with ID: " + dto.getCreateur_id()));
            formulaireOffreStage.setCreateur(createur);
        } else {
            throw new IllegalArgumentException("createur_id cannot be null");
        }

        return formulaireOffreStage;
    }

    public FichierOffreStageDTO saveFile(@Valid UploadFicherOffreStageDTO uploadFicherOffreStageDTO, String token) throws ConstraintViolationException, IOException {

        UtilisateurDTO utilisateurDTO = utilisateurService.getMe(token);
        Long createur_id = utilisateurDTO.getId();

        FichierOffreStageDTO fichierOffreStageDTO = new FichierOffreStageDTO(uploadFicherOffreStageDTO, createur_id);

        // Si l'utilisateur est un employeur, on prend directement le champ entrepriseName de son entité
        // Sinon, s'il s'agit d'un gestionnaire de stage, on prend le champ entrepriseName du formulaire
        // Sinon, on renvoit une erreur
        if (utilisateurDTO.getRole() == Role.EMPLOYEUR) {
            fichierOffreStageDTO.setEntrepriseName(((EmployeurDTO) utilisateurDTO).getEntrepriseName());
        } else if (utilisateurDTO.getRole() == Role.GESTIONNAIRE_STAGE) {
            if (uploadFicherOffreStageDTO.getEntrepriseName() == null) {
                throw new IllegalArgumentException("entrepriseName cannot be null");
            }
            fichierOffreStageDTO.setEntrepriseName(uploadFicherOffreStageDTO.getEntrepriseName());
            fichierOffreStageDTO.setStatus(OffreStage.Status.ACCEPTED);
            if (uploadFicherOffreStageDTO.getProgramme() != null) {
                fichierOffreStageDTO.setProgramme(uploadFicherOffreStageDTO.getProgramme());
                fichierOffreStageDTO.setVisibility(OffreStage.Visibility.PUBLIC);
            } else {
                fichierOffreStageDTO.setVisibility(OffreStage.Visibility.PRIVATE);
            }
        } else {
            throw new IllegalArgumentException("Utilisateur n'est pas un employeur ou un gestionnaire de stage");
        }

        Set<ConstraintViolation<FichierOffreStageDTO>> violations = validator.validate(fichierOffreStageDTO);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }

        FichierOffreStage fichierOffreStage = convertToEntity(fichierOffreStageDTO);

        ficherOffreStageRepository.save(fichierOffreStage);

        if (fichierOffreStage.getVisibility() == OffreStage.Visibility.PRIVATE) {
            associateEtudiantsPrivees(fichierOffreStage, uploadFicherOffreStageDTO.getEtudiantsPrives());
        }

        return convertToDTO(fichierOffreStage);
    }

    public FormulaireOffreStageDTO saveForm(FormulaireOffreStageDTO formulaireOffreStageDTO, String token) throws AccessDeniedException {
        UtilisateurDTO utilisateurDTO = utilisateurService.getMe(token);
        Long createur_id = utilisateurDTO.getId();

        if (utilisateurDTO.getRole() != Role.EMPLOYEUR && utilisateurDTO.getRole() != Role.GESTIONNAIRE_STAGE) {
            throw new AccessDeniedException("Utilisateur n'est pas un employeur");
        }

        if(utilisateurDTO.getRole() == Role.GESTIONNAIRE_STAGE) {
            formulaireOffreStageDTO.setStatus(OffreStage.Status.ACCEPTED);
            if (formulaireOffreStageDTO.getProgramme() != null) {
                formulaireOffreStageDTO.setVisibility(OffreStage.Visibility.PUBLIC);
            } else {
                formulaireOffreStageDTO.setVisibility(OffreStage.Visibility.PRIVATE);
            }
        }

        formulaireOffreStageDTO.setCreateur_id(createur_id);

        FormulaireOffreStage formulaireOffreStage = convertToEntity(formulaireOffreStageDTO);

        FormulaireOffreStage savedForm = formulaireOffreStageRepository.save(formulaireOffreStage);

        if (formulaireOffreStage.getVisibility() == OffreStage.Visibility.PRIVATE) {
            associateEtudiantsPrivees(formulaireOffreStage, formulaireOffreStageDTO.getEtudiantsPrives());
        }

        return convertToDTO(savedForm);
    }

    private OffreStage associateEtudiantsPrivees(OffreStage offreStage, List<Long> etudiantsPrives) {
        List<Etudiant> etudiants = etudiantRepository.findAllById(etudiantsPrives);

        for (Etudiant etudiant : etudiants) {
            EtudiantOffreStagePrivee association = new EtudiantOffreStagePrivee();
            association.setEtudiant(etudiant);
            association.setOffreStage(offreStage);
            etudiantOffreStagePriveeRepository.save(association);
        }

        return offreStage;
    }

    public List<OffreStageAvecUtilisateurInfoDTO> getWaitingOffreStage(int page) {
        Optional<List<OffreStage>> optionalOffreStageList = offreStageRepository.getOffreStageByStatusEquals(OffreStage.Status.WAITING,
                PageRequest.of(page - 1, LIMIT_PER_PAGE));

        if (optionalOffreStageList.isEmpty()) {
            return new ArrayList<>();
        }

        List<OffreStage> offreStages = optionalOffreStageList.get();

        return offreStages.stream().map(OffreStageAvecUtilisateurInfoDTO::toDto).toList();
    }

    public Integer getAmountOfPages() {
        long amountOfRows = offreStageRepository.countByStatus(OffreStage.Status.WAITING);

        if (amountOfRows == 0)
            return 0;

        int nombrePages = (int) Math.floor((double) amountOfRows / LIMIT_PER_PAGE);

        if (amountOfRows % 10 > 0) {
            nombrePages++;
        }

        return nombrePages;
    }

    public void changeStatus(Long id, OffreStage.Status status, String description) throws ChangeSetPersister.NotFoundException {
        Optional<OffreStage> offreStageOptional = offreStageRepository.findById(id);

        if (offreStageOptional.isEmpty())
            throw new ChangeSetPersister.NotFoundException();

        OffreStage offreStage = offreStageOptional.get();
        offreStage.setStatus(status);
        offreStage.setStatusDescription(description);
        offreStageRepository.save(offreStage);
    }

    public OffreStageAvecUtilisateurInfoDTO getOffreStageWithUtilisateurInfo(Long id) {
        OffreStage offreStage = offreStageRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("OffreStage not found with ID: " + id));
        return OffreStageAvecUtilisateurInfoDTO.toDto(offreStage);
    }

    public List<OffreStageDTO> getAvailableOffreStagesForEtudiant(String token) throws AccessDeniedException {
        EtudiantDTO etudiantDTO = (EtudiantDTO) utilisateurService.getMe(token);
        return offreStageRepository.findAllByEtudiantNotApplied(etudiantDTO.getId(), etudiantDTO.getProgramme()).stream().map(this::convertToDTO).toList();
    }

    public long getTotalWaitingOffreStages() {
        return offreStageRepository.countByStatus(OffreStage.Status.WAITING);
    }

    public void acceptCV(AcceptCVDTO acceptCVDTO) {
        Optional<OffreStage> offreStageOptional = offreStageRepository.findById(acceptCVDTO.getId());

        if (offreStageOptional.isEmpty()) {
            throw new EntityNotFoundException("OffreStage not found with ID: " + acceptCVDTO.getId());
        }

        OffreStage offreStage = offreStageOptional.get();
        offreStage.setStatus(OffreStage.Status.ACCEPTED);
        offreStage.setStatusDescription(acceptCVDTO.getStatusDescription());
        offreStage.setProgramme(acceptCVDTO.getProgramme());

        if (offreStage.getProgramme() == Programme.NOT_SPECIFIED) {
            offreStage.setVisibility(OffreStage.Visibility.PRIVATE);
            associateEtudiantsPrivees(offreStage, acceptCVDTO.getEtudiantsPrives());
        } else {
            offreStage.setVisibility(OffreStage.Visibility.PUBLIC);
        }

        offreStageRepository.save(offreStage);
    }
}
