package com.projet.mycose.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.projet.mycose.dto.*;
import com.projet.mycose.modele.FormulaireOffreStage;
import com.projet.mycose.modele.OffreStage;
import com.projet.mycose.service.ApplicationStageService;
import com.projet.mycose.service.OffreStageService;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.*;

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
    public ResponseEntity<FichierOffreStageDTO> uploadFile(@Valid @ModelAttribute UploadFicherOffreStageDTO uploadFicherOffreStageDTO) {
        try {

            FichierOffreStageDTO savedFileDTO = offreStageService.saveFile(uploadFicherOffreStageDTO);

            return ResponseEntity.status(HttpStatus.CREATED).body(savedFileDTO);
        } catch (ConstraintViolationException e) {
            Map<String, String> errors = new HashMap<>();
            e.getConstraintViolations().forEach(violation ->
                    errors.put(violation.getPropertyPath().toString(), violation.getMessage()));

            throw new ConstraintViolationException(errors.toString(), e.getConstraintViolations());

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
        return ResponseEntity.status(HttpStatus.OK).body(offreStageService.getTotalWaitingOffresStage());
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

    @GetMapping("form/id/{id}")
    public ResponseEntity<FormulaireOffreStageDTO> getOffreStageForm(@PathVariable Long id) {
        FormulaireOffreStageDTO offreStageDTO = offreStageService.getOffreStageFormulaire(id);
        return ResponseEntity.status(HttpStatus.OK).body(offreStageDTO);
    }


    // On passe le id de l'offre de stage
    @GetMapping("/offre-applications/{id}")
    public ResponseEntity<List<EtudiantDTO>> getAllEtudiantQuiOntAppliquesAUneOffre(@PathVariable Long id) {
        List<ApplicationStageAvecInfosDTO> applicationStageDTOList = applicationStageService.getAllApplicationsPourUneOffreByIdPendingOrSummoned(id);
        return new ResponseEntity<>(offreStageService.getEtudiantsQuiOntAppliquesAUneOffre(applicationStageDTOList), HttpStatus.OK);
    }

    //Pourra être utiliser pour que les étudiants regardent automatiquement la bonne session (la prochaine)
    @GetMapping("/get-next-session")
    public ResponseEntity<SessionInfoDTO> getNextSession() {
        return ResponseEntity.status(HttpStatus.OK).body(offreStageService.getNextSession());
    }

    @GetMapping("get-all-sessions")
    public ResponseEntity<List<SessionInfoDTO>> getAllSessions() {
        return ResponseEntity.status(HttpStatus.OK).body(offreStageService.getAllSessions());
    }

    @GetMapping("/get-sessions-for-createur")
    public ResponseEntity<List<SessionInfoDTO>> getSessionsForCreateur() {
        return ResponseEntity.status(HttpStatus.OK).body(offreStageService.getSessionsForCreateur());
    }

    @GetMapping("/my-offres")
    public ResponseEntity<List<OffreStageDTO>> getMyOffresByYearAndSessionEcole(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) OffreStage.SessionEcole sessionEcole,
            @RequestParam int pageNumber,
            @RequestParam(value = "title", required = false, defaultValue = "") String title
    ) {
        List<OffreStageDTO> offreStageDTOList = offreStageService.getAvailableOffreStagesForEtudiantFiltered(pageNumber, year, sessionEcole, title);
        return ResponseEntity.status(HttpStatus.OK).body(offreStageDTOList);
    }

    @GetMapping("/my-offres-pages")
    public ResponseEntity<Integer> getAmountOfPagesForMyOffres(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) OffreStage.SessionEcole sessionEcole,
            @RequestParam(value = "title", required = false, defaultValue = "") String title
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(offreStageService.getAmountOfPagesForEtudiantFiltered(year, sessionEcole, title));
    }

    @GetMapping("/my-offres-all")
    public ResponseEntity<Page<OffreStageDTO>> getMyOffresByYearAndSessionEcoleAll(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) OffreStage.SessionEcole sessionEcole,
            @RequestParam int pageNumber,
            @RequestParam(value = "title", required = false, defaultValue = "") String title
    ) {
        Page<OffreStageDTO> offreStageDTOList = offreStageService.getAllOffreStagesForEtudiantFiltered(pageNumber, year, sessionEcole, title);
        return ResponseEntity.status(HttpStatus.OK).body(offreStageDTOList);
    }

    //Offres postées par un Gestionnaire ou un Employeur
    @GetMapping("/getOffresPosted")
    public ResponseEntity<List<OffreStageDTO>> getOffresStagesPublieesFiltre(
            @RequestParam int pageNumber,
            @RequestParam(required = false) Integer annee,
            @RequestParam(required = false) OffreStage.SessionEcole session) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).contentType(MediaType.APPLICATION_JSON).body(
                offreStageService.getStagesFiltered(pageNumber, annee, session));
    }

    @GetMapping("/pagesForCreateur")
    public ResponseEntity<Integer> getAmountOfPagesForCreateurFiltered(
            @RequestParam(required = false) Integer annee,
            @RequestParam(required = false) OffreStage.SessionEcole session) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).contentType(MediaType.APPLICATION_JSON).body(
                offreStageService.getAmountOfPagesForCreateurFiltered(annee, session));
    }

    @GetMapping("/getEmployeur/{offreStageId}")
    public ResponseEntity<EmployeurDTO> getEmployeurFromOffreStage(@PathVariable Long offreStageId) {
        EmployeurDTO employeurDTO = offreStageService.getEmployeurByOffreStageId(offreStageId);
        return ResponseEntity.ok(employeurDTO);
    }

    @PatchMapping(value = "/update-fichier", consumes = "multipart/form-data")
    public ResponseEntity<FichierOffreStageDTO> updateFichierOffreStage(
            @ModelAttribute UploadFicherOffreStageDTO uploadFicherOffreStageDTO,
            @RequestParam Long offreStageId) throws IOException {
        FichierOffreStageDTO savedFileDTO = offreStageService.updateOffreStage(uploadFicherOffreStageDTO, offreStageId);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedFileDTO);
    }

    @PatchMapping("/update-formulaire")
    public ResponseEntity<FormulaireOffreStageDTO> updateFormulaireOffreStage(
            //On ne @Valid pas car on veut pouvoir changer seulement les champs qu'on veut
            @ModelAttribute FormulaireOffreStageDTO formulaireOffreStageDTO,
            @RequestParam Long offreStageId) throws AccessDeniedException {
        FormulaireOffreStageDTO savedForm = offreStageService.updateOffreStage(formulaireOffreStageDTO, offreStageId);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedForm);
    }
}