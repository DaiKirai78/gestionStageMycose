package com.projet.mycose.controller;


import com.fasterxml.jackson.databind.JsonNode;
import com.projet.mycose.modele.FichierCV;
import com.projet.mycose.security.exception.AuthenticationException;
import com.projet.mycose.service.FichierCVService;
import com.projet.mycose.service.UtilisateurService;
import com.projet.mycose.service.dto.FichierCVDTO;
import com.projet.mycose.service.dto.FichierCVStudInfoDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.modelmapper.ModelMapper;
import org.springframework.data.crossstore.ChangeSetPersister;
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
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file, @RequestHeader("Authorization") String token) {
        try {
            Long etudiant_id = utilisateurService.getUserIdByToken(token);

            FichierCVDTO fichierCVDTO = fichierCVService.getCurrentCV_returnNullIfEmpty(etudiant_id);

            FichierCVDTO savedFileDTO;

            if(fichierCVDTO != null) {
                fichierCVService.deleteCurrentCV(token);
            }

            savedFileDTO = fichierCVService.saveFile(file, token);

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
    public ResponseEntity<?> getCV(@RequestHeader("Authorization") String token) {
        try {
            return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(fichierCVService.getCurrentCV(token));

        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access");
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
    public ResponseEntity<?> deleteCurrentCV(@RequestHeader("Authorization") String token) {
        try {
            fichierCVService.deleteCurrentCV(token);
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

    @PatchMapping("/accept")
    public ResponseEntity<?> acceptCV(@RequestParam Long id,@RequestBody JsonNode jsonNode) {
        try {
            JsonNode descriptionNode = jsonNode.get("commentaire");

            if (descriptionNode == null || descriptionNode.isNull()) {
                return ResponseEntity.badRequest().body("Description field is missing");
            }

            String description = descriptionNode.asText();
            fichierCVService.changeStatus(id, FichierCV.Status.ACCEPTED, description);
            return ResponseEntity.ok().build();
        } catch (ChangeSetPersister.NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
    @PatchMapping("/refuse")
    public ResponseEntity<?> refuseCV(@RequestParam Long id, @RequestBody JsonNode jsonNode) {
        try {
            JsonNode descriptionNode = jsonNode.get("commentaire");

            if (descriptionNode == null || descriptionNode.isNull()) {
                return ResponseEntity.badRequest().body("Description field is missing");
            }

            String description = descriptionNode.asText();
            fichierCVService.changeStatus(id, FichierCV.Status.REFUSED, description);
            return ResponseEntity.ok().build();
        } catch (ChangeSetPersister.NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
