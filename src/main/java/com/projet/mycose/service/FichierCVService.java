package com.projet.mycose.service;

import com.projet.mycose.modele.FichierCV;
import com.projet.mycose.repository.FichierCVRepository;
import com.projet.mycose.service.dto.FichierCVDTO;
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
public class FichierCVService {
    private final FichierCVRepository fileRepository;
    private final ModelMapper modelMapper;
    private final Validator validator;

    public FichierCVService(FichierCVRepository fileRepository, ModelMapper modelMapper, Validator validator) {
        this.fileRepository = fileRepository;
        this.modelMapper = modelMapper;
        this.validator = validator;
    }

    // Convert Entity to DTO
    public FichierCVDTO convertToDTO(FichierCV fichierCV) {
        FichierCVDTO fichierCVDTO = modelMapper.map(fichierCV, FichierCVDTO.class);

        // Convert byte[] data to Base64 string
        fichierCVDTO.setFileData(Base64.getEncoder().encodeToString(fichierCV.getData()));

        return fichierCVDTO;
    }

    // Convert DTO to Entity
    public FichierCV convertToEntity(FichierCVDTO dto) {
        FichierCV fichierCV = modelMapper.map(dto, FichierCV.class);

        // Convert Base64 string back to byte[]
        fichierCV.setData(Base64.getDecoder().decode(dto.getFileData()));

        return fichierCV;
    }


    public FichierCVDTO saveFile(MultipartFile file) throws ConstraintViolationException, IOException {

        FichierCVDTO fichierCVDTO = new FichierCVDTO();

        fichierCVDTO.setFilename(file.getOriginalFilename());
        fichierCVDTO.setFileData(Base64.getEncoder().encodeToString(file.getBytes()));

        Set<ConstraintViolation<FichierCVDTO>> violations = validator.validate(fichierCVDTO);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }

        FichierCV fichierCV = convertToEntity(fichierCVDTO);

        return convertToDTO(fileRepository.save(fichierCV));
    }
//    public FichierOffreStage getFile(Long id) {
//        return fileRepository.findById(id).orElseThrow(() -> new RuntimeException("Fichier non trouvé"));
//    }
}
