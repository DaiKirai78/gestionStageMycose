package com.projet.mycose.controller;


import com.projet.mycose.dto.ContratDTO;
import com.projet.mycose.exceptions.AuthenticationException;
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
    public ResponseEntity<?> upload(@RequestParam Long etudiantId, @RequestParam Long employeurId, @RequestParam Long gestionnaireStageId) {
        ContratDTO contratDTO = contratService.save(etudiantId, employeurId, gestionnaireStageId);
        return ResponseEntity.status(HttpStatus.CREATED).body(contratDTO);
    }

    @GetMapping("/getContractById")
    public ResponseEntity<ContratDTO> getContract(@RequestParam Long contratId) {
        ContratDTO contratDTO = contratService.getContractById(contratId);
        return ResponseEntity.status(HttpStatus.OK).body(contratDTO);
    }
}
