package com.projet.mycose.controller;


import com.projet.mycose.modele.FichierOffreStage;
import com.projet.mycose.service.FichierOffreStageService;
import com.projet.mycose.service.dto.FichierOffreStageDTO;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;

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

    @PostMapping("/upload")
    public ResponseEntity<FichierOffreStageDTO> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        try {
            // Save the file entity
            FichierOffreStage savedFile = fichierOffreStageService.saveFile(file);

            // Convert the entity to DTO
            FichierOffreStageDTO fileDTO = modelMapper.map(savedFile, FichierOffreStageDTO.class);

            // Optionally, encode byte[] data to Base64 string
            fileDTO.setFileData(Base64.getEncoder().encodeToString(savedFile.getData()));

            return ResponseEntity.status(HttpStatus.CREATED).body(fileDTO);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<FichierOffreStageDTO> downloadFile(@PathVariable Long id) {
        FichierOffreStage fichierOffreStage = fichierOffreStageService.getFile(id);

        // Convert the entity to DTO
        FichierOffreStageDTO fileDTO = modelMapper.map(fichierOffreStage, FichierOffreStageDTO.class);

        // Encode the byte[] data to a Base64 string
        fileDTO.setFileData(Base64.getEncoder().encodeToString(fichierOffreStage.getData()));

        return ResponseEntity.ok(fileDTO);
    }
}
