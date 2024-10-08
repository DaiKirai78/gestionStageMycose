package com.projet.mycose.service;

import com.projet.mycose.modele.FichierOffreStage;
import com.projet.mycose.modele.FormulaireOffreStage;
import com.projet.mycose.modele.Utilisateur;
import com.projet.mycose.modele.auth.Role;
import com.projet.mycose.repository.FichierOffreStageRepository;
import com.projet.mycose.repository.FormulaireOffreStageRepository;
import com.projet.mycose.repository.OffreStageRepository;
import com.projet.mycose.repository.UtilisateurRepository;
import com.projet.mycose.service.dto.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Base64;
import java.util.Set;

@Service
public class OffreStageService {
    private final OffreStageRepository offreStageRepository;
    private final ModelMapper modelMapper;
    private final Validator validator;
    private final UtilisateurService utilisateurService;
    private final UtilisateurRepository utilisateurRepository;
    private final FormulaireOffreStageRepository formulaireOffreStageRepository;
    private final FichierOffreStageRepository ficherOffreStageRepository;

    public OffreStageService(OffreStageRepository offreStageRepository, ModelMapper modelMapper, Validator validator, UtilisateurService utilisateurService, UtilisateurRepository utilisateurRepository, FormulaireOffreStageRepository formulaireOffreStageRepository, FichierOffreStageRepository ficherOffreStageRepository) {
        this.offreStageRepository = offreStageRepository;
        this.modelMapper = modelMapper;
        this.validator = validator;
        this.utilisateurService = utilisateurService;
        this.utilisateurRepository = utilisateurRepository;
        this.formulaireOffreStageRepository = formulaireOffreStageRepository;
        this.ficherOffreStageRepository = ficherOffreStageRepository;
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
            fichierOffreStageDTO.setEntrepriseName(uploadFicherOffreStageDTO.getEntrepriseName());
        } else {
            throw new IllegalArgumentException("Utilisateur n'est pas un employeur ou un gestionnaire de stage");
        }

        Set<ConstraintViolation<FichierOffreStageDTO>> violations = validator.validate(fichierOffreStageDTO);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }

        FichierOffreStage fichierOffreStage = convertToEntity(fichierOffreStageDTO);
        return convertToDTO(ficherOffreStageRepository.save(fichierOffreStage));
    }

    public FormulaireOffreStageDTO saveForm(FormulaireOffreStageDTO formulaireOffreStageDTO, String token) {
        Long createur_id = utilisateurService.getUserIdByToken(token);
        formulaireOffreStageDTO.setCreateur_id(createur_id);


        FormulaireOffreStage formulaireOffreStage = convertToEntity(formulaireOffreStageDTO);

        FormulaireOffreStage savedForm = formulaireOffreStageRepository.save(formulaireOffreStage);

        return convertToDTO(savedForm);
    }
}
