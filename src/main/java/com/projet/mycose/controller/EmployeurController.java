package com.projet.mycose.controller;

import com.projet.mycose.dto.*;
import com.projet.mycose.modele.OffreStage;
import com.projet.mycose.service.EmployeurService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

    @PostMapping("/saveFicheEvaluation")
    public ResponseEntity<HttpStatus> enregistrerFicheEvaluationStagiaire(
            @RequestBody FicheEvaluationStagiaireDTO ficheEvaluationStagiaireDTO,
            @RequestParam Long etudiantId
    ) {
        employeurService.enregistrerFicheEvaluationStagiaire(ficheEvaluationStagiaireDTO, etudiantId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}

