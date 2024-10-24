package com.projet.mycose.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.projet.mycose.dto.*;
import com.projet.mycose.service.ApplicationStageService;
import com.projet.mycose.service.OffreStageService;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/offres-stages")
@CrossOrigin(origins = "http://localhost:5173")
public class OffreStageController {

    private final OffreStageService offreStageService;
    private final ApplicationStageService applicationStageService;

    public OffreStageController(OffreStageService offreStageService, ApplicationStageService applicationStageService) {
        this.offreStageService = offreStageService;
        this.applicationStageService = applicationStageService;
    }


    @PostMapping(value = "/upload-file", consumes = "multipart/form-data")
    public ResponseEntity<?> uploadFile(@Valid @ModelAttribute UploadFicherOffreStageDTO uploadFicherOffreStageDTO) {
        try {

            FichierOffreStageDTO savedFileDTO = offreStageService.saveFile(uploadFicherOffreStageDTO);

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
            @Valid @RequestBody FormulaireOffreStageDTO formulaireOffreStageDTO) throws AccessDeniedException {
        FormulaireOffreStageDTO savedForm = offreStageService.saveForm(formulaireOffreStageDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedForm);
    }

    @GetMapping("/waiting")
    public ResponseEntity<List<OffreStageAvecUtilisateurInfoDTO>> getWaitingOffreStage(@RequestParam int page) {
        List<OffreStageAvecUtilisateurInfoDTO> offreStagesDTOList = offreStageService.getWaitingOffreStage(page);
        return ResponseEntity.status(HttpStatus.OK).body(offreStagesDTOList);
    }

    @GetMapping("/pages")
    public ResponseEntity<Integer> getAmountOfPages() {
        return ResponseEntity.status((HttpStatus.OK)).body(offreStageService.getAmountOfPages());
    }

    @GetMapping("/totalwaitingoffres")
    public ResponseEntity<Long> getTotalWaitingOffres() {
        return ResponseEntity.status(HttpStatus.OK).body(offreStageService.getTotalWaitingOffreStages());
    }

    @PatchMapping(value = "/accept")
    public ResponseEntity<?> acceptOffreStage(@Valid @RequestBody AcceptOffreDeStageDTO acceptOffreDeStageDTO) {
            offreStageService.acceptOffreDeStage(acceptOffreDeStageDTO);
            return ResponseEntity.ok().build();
    }

    @PatchMapping("/refuse")
    public ResponseEntity<?> refuseOffreStage(@RequestParam Long id, @RequestBody JsonNode jsonNode) {
            JsonNode descriptionNode = jsonNode.get("commentaire");

            if (descriptionNode == null || descriptionNode.isNull()) {
                return ResponseEntity.badRequest().body("Description field is missing");
            }

            String description = descriptionNode.asText();
            offreStageService.refuseOffreDeStage(id, description);
            return ResponseEntity.ok().build();
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<OffreStageAvecUtilisateurInfoDTO> getOffreStage(@PathVariable Long id) {
        OffreStageAvecUtilisateurInfoDTO offreStageDTO = offreStageService.getOffreStageWithUtilisateurInfo(id);
        return ResponseEntity.status(HttpStatus.OK).body(offreStageDTO);
    }

    @GetMapping("/my-offres")
    public ResponseEntity<List<OffreStageDTO>> getMyOffres() throws AccessDeniedException {
        List<OffreStageDTO> offreStageDTOList = offreStageService.getAvailableOffreStagesForEtudiant();
        return ResponseEntity.status(HttpStatus.OK).body(offreStageDTOList);
    }


    // On passe le id de l'offre de stage
    @GetMapping("/offre-applications/{id}")
    public ResponseEntity<List<EtudiantDTO>> getAllEtudiantQuiOntAppliquesAUneOffre(@PathVariable Long id) {
        List<ApplicationStageAvecInfosDTO> applicationStageDTOList = applicationStageService.getAllApplicationsPourUneOffreByIdPendingOrSummoned(id);
        return new ResponseEntity<>(offreStageService.getEtudiantsQuiOntAppliquesAUneOffre(applicationStageDTOList), HttpStatus.OK);
    }
}
