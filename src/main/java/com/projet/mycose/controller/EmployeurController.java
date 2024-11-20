package com.projet.mycose.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.projet.mycose.dto.*;
import com.projet.mycose.modele.OffreStage;
import com.projet.mycose.service.EmployeurService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
@RequestMapping("entreprise")
public class EmployeurController {
    private final EmployeurService employeurService;

    @PostMapping("/register")
    public ResponseEntity<Object> CreationDeCompte(@Valid @RequestBody RegisterEmployeurDTO nouveauCompteEmployeur) {
        EmployeurDTO employeurResultat = employeurService.creationDeCompte(nouveauCompteEmployeur.getPrenom(),
                nouveauCompteEmployeur.getNom(),
                nouveauCompteEmployeur.getNumeroDeTelephone(),
                nouveauCompteEmployeur.getCourriel(),
                nouveauCompteEmployeur.getMotDePasse(),
                nouveauCompteEmployeur.getNomOrganisation());
        return employeurResultat != null ? ResponseEntity.status(HttpStatus.CREATED).body(employeurResultat) : ResponseEntity.status(HttpStatus.CONFLICT).body("L'employeur existe déjà ou les credentials sont invalides");
    }

    @PostMapping("/getContratsNonSignees")
    public ResponseEntity<List<ContratDTO>> getContrats(@RequestParam int pageNumber) {
            return ResponseEntity.status(HttpStatus.ACCEPTED).contentType(MediaType.APPLICATION_JSON).body(
                    employeurService.getAllContratsNonSignes(pageNumber));
    }

    @PostMapping(value = "/enregistrerSignature")
    public ResponseEntity<String> enregistrerSignature(
            @RequestParam("signature") MultipartFile signature,
            @RequestParam Long contratId,
            @RequestParam String password
    ) {
            return ResponseEntity.status(HttpStatus.ACCEPTED).contentType(MediaType.APPLICATION_JSON).body(
                    employeurService.enregistrerSignature(signature, password, contratId));
    }

    @GetMapping("/pagesContrats")
    public ResponseEntity<Integer> getAmountOfPagesOfCandidaturesNonSignees() {
            return ResponseEntity.status(HttpStatus.ACCEPTED).contentType(MediaType.APPLICATION_JSON).body(
                    employeurService.getAmountOfPagesOfContractNonSignees());
    }

    @PostMapping(value = "/saveFicheEvaluation", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<HttpStatus> enregistrerFicheEvaluationStagiaire(
            @RequestParam("ficheEvaluationStagiaireDTO") String  ficheEvaluationStagiaireDTOJson,
            @RequestParam Long etudiantId,
            @RequestParam("signature") MultipartFile signatureEmployeur
    ) throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();
        FicheEvaluationStagiaireDTO ficheEvaluationStagiaireDTO = objectMapper.readValue(ficheEvaluationStagiaireDTOJson, FicheEvaluationStagiaireDTO.class);

        employeurService.enregistrerFicheEvaluationStagiaire(ficheEvaluationStagiaireDTO, etudiantId, signatureEmployeur);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/getAllEtudiantsNonEvalues")
    public ResponseEntity<Page<EtudiantDTO>> getAllEtudiantsNonEvalues(@RequestParam Long employeurId, @RequestParam int pageNumber) {
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(employeurService.getAllEtudiantsNonEvalues(employeurId, pageNumber));
    }
}

