package com.projet.mycose.controller;

import com.projet.mycose.modele.FichierCV;
import com.projet.mycose.modele.OffreStage;
import com.projet.mycose.service.OffreStageService;
import com.projet.mycose.service.dto.FichierOffreStageDTO;
import com.projet.mycose.service.dto.FormulaireOffreStageDTO;
import com.projet.mycose.service.dto.OffreStageAvecUtilisateurInfoDTO;
import com.projet.mycose.service.dto.UploadFicherOffreStageDTO;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
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
@RequestMapping("/api/offres-stages")
@CrossOrigin(origins = "http://localhost:5173")
public class OffreStageController {

    private final OffreStageService offreStageService;

    public OffreStageController(OffreStageService offreStageService) {
        this.offreStageService = offreStageService;
    }


    @PostMapping(value = "/upload-file", consumes = "multipart/form-data")
    public ResponseEntity<?> uploadFile(@Valid @ModelAttribute UploadFicherOffreStageDTO uploadFicherOffreStageDTO,  @RequestHeader("Authorization") String token) {
        try {

            FichierOffreStageDTO savedFileDTO = offreStageService.saveFile(uploadFicherOffreStageDTO, token);

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

    @PostMapping("/upload-form")
    public ResponseEntity<FormulaireOffreStageDTO> uploadForm(
            @Valid @RequestBody FormulaireOffreStageDTO formulaireOffreStageDTO, @RequestHeader("Authorization") String token) {
        FormulaireOffreStageDTO savedForm = offreStageService.saveForm(formulaireOffreStageDTO, token);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedForm);
    }

    @GetMapping("/waiting-offres-stage")
    public ResponseEntity<List<OffreStageAvecUtilisateurInfoDTO>> getWaitingOffreStage(@RequestParam int page) {
        List<OffreStageAvecUtilisateurInfoDTO> offreStagesDTOList = offreStageService.getWaitingOffreStage(page);
        return ResponseEntity.status(HttpStatus.OK).body(offreStagesDTOList);
    }

    @GetMapping("/pages")
    public ResponseEntity<Integer> getAmountOfPages() {
        return ResponseEntity.status((HttpStatus.OK)).body(offreStageService.getAmountOfPages());
    }

    @PatchMapping("/accept")
    public ResponseEntity<?> acceptOffreStage(@RequestParam Long id, @RequestBody String description) {
        try {
            offreStageService.changeStatus(id, OffreStage.Status.ACCEPTED, description);
            return ResponseEntity.ok().build();
        } catch (ChangeSetPersister.NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
    @PatchMapping("/refuse")
    public ResponseEntity<?> refuseOffreStage(@RequestParam Long id, @RequestBody String description) {
        try {
            offreStageService.changeStatus(id, OffreStage.Status.REFUSED, description);
            return ResponseEntity.ok().build();
        } catch (ChangeSetPersister.NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/offre-stage/{id}")
    public ResponseEntity<OffreStageAvecUtilisateurInfoDTO> getOffreStage(@PathVariable Long id) {
        OffreStageAvecUtilisateurInfoDTO offreStageDTO = offreStageService.getOffreStageWithUtilisateurInfo(id);
        return ResponseEntity.status(HttpStatus.OK).body(offreStageDTO);
    }
}
