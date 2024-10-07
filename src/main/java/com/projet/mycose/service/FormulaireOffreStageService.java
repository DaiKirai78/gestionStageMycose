package com.projet.mycose.service;

import com.projet.mycose.modele.Utilisateur;
import com.projet.mycose.modele.FormulaireOffreStage;
import com.projet.mycose.repository.UtilisateurRepository;
import com.projet.mycose.repository.FormulaireOffreStageRepository;
import com.projet.mycose.service.dto.FormulaireOffreStageDTO;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.Set;

@Service
public class FormulaireOffreStageService {

    private final FormulaireOffreStageRepository formulaireOffreStageRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final ModelMapper modelMapper;
    private final Validator validator;

    public FormulaireOffreStageService(FormulaireOffreStageRepository formulaireOffreStageRepository,
                                       UtilisateurRepository utilisateurRepository,
                                       ModelMapper modelMapper,
                                       Validator validator) {
        this.formulaireOffreStageRepository = formulaireOffreStageRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.modelMapper = modelMapper;
        this.validator = validator;
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

    public FormulaireOffreStageDTO save(FormulaireOffreStageDTO formulaireOffreStageDTO) {

        FormulaireOffreStage formulaireOffreStage = convertToEntity(formulaireOffreStageDTO);

        FormulaireOffreStage savedForm = formulaireOffreStageRepository.save(formulaireOffreStage);

        return convertToDTO(savedForm);
    }
}
