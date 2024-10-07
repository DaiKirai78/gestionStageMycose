package com.projet.mycose.service;

import com.projet.mycose.modele.FichierOffreStage;
import com.projet.mycose.modele.Utilisateur;
import com.projet.mycose.repository.FichierOffreStageRepository;
import com.projet.mycose.repository.UtilisateurRepository;
import com.projet.mycose.service.dto.FichierOffreStageDTO;
import jakarta.persistence.EntityNotFoundException;
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
public class FichierOffreStageService {
    private final FichierOffreStageRepository fileRepository;
    private final ModelMapper modelMapper;
    private final Validator validator;
    private final UtilisateurRepository utilisateurRepository;

    public FichierOffreStageService(FichierOffreStageRepository fileRepository, ModelMapper modelMapper, Validator validator, UtilisateurRepository utilisateurRepository) {
        this.fileRepository = fileRepository;
        this.modelMapper = modelMapper;
        this.validator = validator;
        this.utilisateurRepository = utilisateurRepository;
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


    public FichierOffreStageDTO saveFile(MultipartFile file, Long createur_id) throws ConstraintViolationException, IOException {

        FichierOffreStageDTO fichierOffreStageDTO = new FichierOffreStageDTO();

        fichierOffreStageDTO.setFilename(file.getOriginalFilename());
        fichierOffreStageDTO.setFileData(Base64.getEncoder().encodeToString(file.getBytes()));
        fichierOffreStageDTO.setCreateur_id(createur_id);
        Set<ConstraintViolation<FichierOffreStageDTO>> violations = validator.validate(fichierOffreStageDTO);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }

        FichierOffreStage fichierOffreStage = convertToEntity(fichierOffreStageDTO);
        return convertToDTO(fileRepository.save(fichierOffreStage));
    }
}
