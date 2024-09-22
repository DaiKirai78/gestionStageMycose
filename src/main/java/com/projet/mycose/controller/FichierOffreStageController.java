package com.projet.mycose.controller;


import com.projet.mycose.service.FichierOffreStageService;
import com.projet.mycose.service.dto.FichierOffreStageDTO;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/files")
@CrossOrigin(origins = "http://localhost:5173")
public class FichierOffreStageController {

    private final FichierOffreStageService fichierOffreStageService;
    private final ModelMapper modelMapper;

    public FichierOffreStageController(FichierOffreStageService fichierOffreStageService, ModelMapper modelMapper) {
        this.fichierOffreStageService = fichierOffreStageService;
        this.modelMapper = modelMapper;
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            FichierOffreStageDTO savedFileDTO = fichierOffreStageService.saveFile(file);

            return ResponseEntity.status(HttpStatus.CREATED).body(savedFileDTO);
        } catch (ConstraintViolationException e) {
        Map<String, String> errors = new HashMap<>();
        e.getConstraintViolations().forEach(violation ->
                errors.put(violation.getPropertyPath().toString(), violation.getMessage()));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);

        } catch (IOException e) {
            // Handle IOException for file-related issues
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

//    @GetMapping("/download/{id}")
//    public ResponseEntity<FichierOffreStageDTO> downloadFile(@PathVariable Long id) {
//        FichierOffreStage fichierOffreStage = fichierOffreStageService.getFile(id);
//
//        // Convert the entity to DTO
//        FichierOffreStageDTO fileDTO = modelMapper.map(fichierOffreStage, FichierOffreStageDTO.class);
//
//        // Encode the byte[] data to a Base64 string
//        fileDTO.setFileData(Base64.getEncoder().encodeToString(fichierOffreStage.getData()));
//
//        return ResponseEntity.ok(fileDTO);
//    }
}
