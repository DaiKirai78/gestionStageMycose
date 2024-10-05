package com.projet.mycose.controller;


import com.projet.mycose.modele.FichierCV;
import com.projet.mycose.service.FichierCVService;
import com.projet.mycose.service.UtilisateurService;
import com.projet.mycose.service.dto.FichierCVDTO;
import com.projet.mycose.service.dto.FichierOffreStageDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cv")
@CrossOrigin(origins = "http://localhost:5173")
public class FichierCVController {

    private final FichierCVService fichierCVService;
    private final ModelMapper modelMapper;
    private final UtilisateurService utilisateurService;

    public FichierCVController(FichierCVService fichierCVService, ModelMapper modelMapper, UtilisateurService utilisateurService) {
        this.fichierCVService = fichierCVService;
        this.modelMapper = modelMapper;
        this.utilisateurService = utilisateurService;
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            FichierCVDTO savedFileDTO = fichierCVService.saveFile(file);

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

    @PostMapping("/getCV")
    public ResponseEntity<FichierCVDTO> getCV(HttpServletRequest request) {

        try {
            Long id = utilisateurService.getUserIdByToken(request.getHeader("Authorization"));
            return ResponseEntity.status(HttpStatus.ACCEPTED).contentType(MediaType.APPLICATION_JSON).body(fichierCVService.getFile(id));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping("/waitingcv")
    public ResponseEntity<List<FichierCVDTO>> getWaitingCv(@RequestParam int page) {
         List<FichierCVDTO> fichierCVDTOS = fichierCVService.getWaitingCv(page);
         return ResponseEntity.status(HttpStatus.OK).body(fichierCVDTOS);
    }
}
