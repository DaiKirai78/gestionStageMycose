package com.projet.mycose.controller;


import com.projet.mycose.dto.ContratDTO;
import com.projet.mycose.security.exception.AuthenticationException;
import com.projet.mycose.service.ContratService;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
@RequestMapping("contrat")
public class ContratController {
    private final ContratService contratService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> upload(@RequestParam MultipartFile contratPDF, @RequestParam Long etudiantId, @RequestParam Long employeurId) {
        try {
            ContratDTO contratDTO = contratService.save(contratPDF, etudiantId, employeurId);
            System.out.println("ContratDTO créé : " + contratDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(contratDTO);
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access : " + e.getMessage());
        } catch (RuntimeException | IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Fichier non trouvé : " + e.getMessage());
        }
    }
}
