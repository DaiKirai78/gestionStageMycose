package com.projet.mycose.service;

import com.projet.mycose.modele.FichierOffreStage;
import com.projet.mycose.modele.FormulaireOffreStage;
import com.projet.mycose.repository.OffreStageRepository;
import com.projet.mycose.service.dto.FichierOffreStageDTO;
import com.projet.mycose.service.dto.FormulaireOffreStageDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.Set;

@Service
public class OffreStageService {
    private final OffreStageRepository offreStageRepository;
    private final ModelMapper modelMapper;
    private final Validator validator;

    public OffreStageService(OffreStageRepository offreStageRepository, ModelMapper modelMapper, Validator validator) {
        this.offreStageRepository = offreStageRepository;
        this.modelMapper = modelMapper;
        this.validator = validator;
    }


    public FichierOffreStageDTO convertToDTO(FichierOffreStage fichierOffreStage) {
        FichierOffreStageDTO fichierOffreStageDTO = modelMapper.map(fichierOffreStage, FichierOffreStageDTO.class);

        // Convert byte[] data to Base64 string
        fichierOffreStageDTO.setFileData(Base64.getEncoder().encodeToString(fichierOffreStage.getData()));

        return fichierOffreStageDTO;
    }

    // Convert DTO to Entity
    public FichierOffreStage convertToEntity(FichierOffreStageDTO dto) {
        FichierOffreStage fichierOffreStage = modelMapper.map(dto, FichierOffreStage.class);

        // Convert Base64 string back to byte[]
        fichierOffreStage.setData(Base64.getDecoder().decode(dto.getFileData()));

        return fichierOffreStage;
    }


    public FichierOffreStageDTO saveFile(MultipartFile file) throws ConstraintViolationException, IOException {

        FichierOffreStageDTO fichierOffreStageDTO = new FichierOffreStageDTO();

        fichierOffreStageDTO.setFilename(file.getOriginalFilename());
        fichierOffreStageDTO.setFileData(Base64.getEncoder().encodeToString(file.getBytes()));

        Set<ConstraintViolation<FichierOffreStageDTO>> violations = validator.validate(fichierOffreStageDTO);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }

        FichierOffreStage fichierOffreStage = convertToEntity(fichierOffreStageDTO);

        return convertToDTO(offreStageRepository.save(fichierOffreStage));
    }

    public FormulaireOffreStageDTO saveForm(FormulaireOffreStageDTO formulaireOffreStageDTO) {
        FormulaireOffreStage formulaireOffreStage = modelMapper.map(formulaireOffreStageDTO, FormulaireOffreStage.class);
        FormulaireOffreStage savedForm = offreStageRepository.save(formulaireOffreStage);
        return modelMapper.map(savedForm, FormulaireOffreStageDTO.class);
    }
}
