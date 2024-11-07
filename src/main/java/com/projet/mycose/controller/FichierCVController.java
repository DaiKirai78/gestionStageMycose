package com.projet.mycose.controller;


import com.fasterxml.jackson.databind.JsonNode;
import com.projet.mycose.modele.FichierCV;
import com.projet.mycose.exceptions.AuthenticationException;
import com.projet.mycose.service.FichierCVService;
import com.projet.mycose.service.UtilisateurService;
import com.projet.mycose.dto.FichierCVDTO;
import com.projet.mycose.dto.FichierCVStudInfoDTO;
import jakarta.validation.ConstraintViolationException;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
            Long etudiant_id = utilisateurService.getMyUserId();

            FichierCVDTO fichierCVDTO = fichierCVService.getCurrentCV_returnNullIfEmpty(etudiant_id);

            FichierCVDTO savedFileDTO;

            if(fichierCVDTO != null) {
                fichierCVService.deleteCurrentCV();
            }

            savedFileDTO = fichierCVService.saveFile(file);

            return ResponseEntity.status(HttpStatus.CREATED).body(savedFileDTO);
        } catch (ConstraintViolationException e) {
            Map<String, String> errors = new HashMap<>();
            e.getConstraintViolations().forEach(violation ->
                    errors.put(violation.getPropertyPath().toString(), violation.getMessage()));

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);

        } catch (IOException e) {
            // Handle IOException for file-related issues
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Fichier non trouvé");
        }
    }

    @PostMapping("/current")
    public ResponseEntity<?> getCV() {
        try {
            return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(fichierCVService.getCurrentCVDTO());

        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Fichier non trouvé");
        }
    }

    @GetMapping("/get-cv-by-etudiant-id/{id}")
    public ResponseEntity<?> getCV(@PathVariable Long id) {
        try {
            return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(fichierCVService.getCurrentCVByEtudiantID(id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Fichier non trouvé");
        }
    }

    @GetMapping("/waitingcv")
    public ResponseEntity<?> getWaitingCv(@RequestParam int page) {
         try {
             List<FichierCVStudInfoDTO> fichierCVDTOS = fichierCVService.getWaitingCv(page);
             return ResponseEntity.status(HttpStatus.OK).body(fichierCVDTOS);
         } catch (IllegalArgumentException e) {
             return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
         }
    }

    @PatchMapping("/delete_current")
    public ResponseEntity<?> deleteCurrentCV() {
        try {
            fichierCVService.deleteCurrentCV();
            return ResponseEntity.status(HttpStatus.OK).body("CV supprimé avec succès");
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Fichier non trouvé");
        }
    }

    @GetMapping("/pages")
    public ResponseEntity<Integer> getAmountOfPages() {
        return ResponseEntity.status((HttpStatus.OK)).body(fichierCVService.getAmountOfPages());
    }

    @GetMapping("/totalwaitingcvs")
    public ResponseEntity<Long> getTotalWaitingCVs() {
        return ResponseEntity.status(HttpStatus.OK).body(fichierCVService.getTotalWaitingCVs());
    }

    @PatchMapping("/accept")
    public ResponseEntity<?> acceptCV(@RequestParam Long id,@RequestBody JsonNode jsonNode) {
        JsonNode descriptionNode = jsonNode.get("commentaire");

        if (descriptionNode == null || descriptionNode.isNull()) {
            return ResponseEntity.badRequest().body("Description field is missing");
        }

        String description = descriptionNode.asText();
        fichierCVService.changeStatus(id, FichierCV.Status.ACCEPTED, description);
        return ResponseEntity.ok().build();
    }
    @PatchMapping("/refuse")
    public ResponseEntity<?> refuseCV(@RequestParam Long id, @RequestBody JsonNode jsonNode) {
            JsonNode descriptionNode = jsonNode.get("commentaire");

            if (descriptionNode == null || descriptionNode.isNull() || descriptionNode.asText().isEmpty()) {
                return ResponseEntity.badRequest().body("Description field is missing");
            }

            String description = descriptionNode.asText();
            fichierCVService.changeStatus(id, FichierCV.Status.REFUSED, description);
            return ResponseEntity.ok().build();
    }
}
