package com.projet.mycose.controller;


import com.projet.mycose.modele.FichierCV;
import com.projet.mycose.service.FichierCVService;
import com.projet.mycose.service.dto.FichierCVDTO;
import jakarta.validation.ConstraintViolationException;
import org.springframework.data.crossstore.ChangeSetPersister;
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

    public FichierCVController(FichierCVService fichierCVService) {
        this.fichierCVService = fichierCVService;
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

    @GetMapping("/waitingcv")
    public ResponseEntity<List<FichierCVDTO>> getWaitingCv(@RequestParam int page) {
         List<FichierCVDTO> fichierCVDTOS = fichierCVService.getWaitingCv(page);
         return ResponseEntity.status(HttpStatus.OK).body(fichierCVDTOS);
    }

    @GetMapping("/pages")
    public ResponseEntity<Integer> getAmountOfPages() {
        return ResponseEntity.status((HttpStatus.OK)).body(fichierCVService.getAmountOfPages());
    }

    @PostMapping("/accept")
    public ResponseEntity<?> acceptCV(@RequestParam Long id, @RequestBody String description) {
        try {
            fichierCVService.changeStatus(id, FichierCV.Status.ACCEPTED, description);
            return ResponseEntity.ok().build();
        } catch (ChangeSetPersister.NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
    @PostMapping("/refuse")
    public ResponseEntity<?> refuseCV(@RequestParam Long id, @RequestBody String description) {
        try {
            fichierCVService.changeStatus(id, FichierCV.Status.REFUSED, description);
            return ResponseEntity.ok().build();
        } catch (ChangeSetPersister.NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
