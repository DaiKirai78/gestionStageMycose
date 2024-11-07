package com.projet.mycose.controller;

import com.projet.mycose.dto.ContratDTO;
import com.projet.mycose.dto.OffreStageDTO;
import com.projet.mycose.service.EtudiantService;
import com.projet.mycose.dto.EtudiantDTO;
import com.projet.mycose.dto.RegisterEtudiantDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("etudiant")
public class EtudiantController {
    private final EtudiantService etudiantService;

    public EtudiantController(EtudiantService etudiantService) {
        this.etudiantService = etudiantService;
    }


    @PostMapping("/register")
    public ResponseEntity<Object> CreationDeCompte(@Valid @RequestBody RegisterEtudiantDTO nouveauCompteEtudiant) {
            EtudiantDTO etudiantResultat = etudiantService.creationDeCompte(nouveauCompteEtudiant.getPrenom(),
                    nouveauCompteEtudiant.getNom(),
                    nouveauCompteEtudiant.getNumeroDeTelephone(),
                    nouveauCompteEtudiant.getCourriel(),
                    nouveauCompteEtudiant.getMotDePasse(),
                    nouveauCompteEtudiant.getProgramme());
            return etudiantResultat != null ? ResponseEntity.status(HttpStatus.CREATED).body(etudiantResultat) : ResponseEntity.status(HttpStatus.CONFLICT).body("L'étudiant existe déjà ou les credentials sont invalides");
        }

    @PostMapping("/getStages")
    public ResponseEntity<List<OffreStageDTO>> getStages(@RequestParam int pageNumber) {
        try {
            return ResponseEntity.status(HttpStatus.ACCEPTED).contentType(MediaType.APPLICATION_JSON).body(
                    etudiantService.getStages(pageNumber));
        } catch (Exception e) {
            return ResponseEntity.noContent().build();
        }
    }

    @GetMapping("/pages")
    public ResponseEntity<Integer> getAmountOfPages() {
        try {
            return ResponseEntity.status(HttpStatus.ACCEPTED).contentType(MediaType.APPLICATION_JSON).body(
                    etudiantService.getAmountOfPages());
        } catch (Exception e) {
            return ResponseEntity.noContent().build();
        }
    }

    @PostMapping("/recherche-offre")
    public ResponseEntity<List<OffreStageDTO>> rechercherOffres(@RequestParam int pageNumber, @RequestParam String recherche) {
        try {
            return ResponseEntity.status(HttpStatus.ACCEPTED).contentType(MediaType.APPLICATION_JSON).body(
                    etudiantService.getStagesByRecherche(pageNumber, recherche));
        } catch (Exception e) {
            return ResponseEntity.noContent().build();
        }
    }

    @PostMapping(value = "/enregistrerSignature")
    public ResponseEntity<String> enregistrerSignature(
            @RequestParam("signature") MultipartFile signature,
            @RequestParam Long contratId,
            @RequestParam String password
    ) {
        String responseMessage = etudiantService.enregistrerSignature(signature, password, contratId);
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(responseMessage);
    }

    @GetMapping("/getContratsNonSignees")
    public ResponseEntity<List<ContratDTO>> getAllContratsNonSignes(@RequestParam int page) {
        List<ContratDTO> contrats = etudiantService.getAllContratsNonSignes(page);
        return ResponseEntity.ok(contrats);
    }

    @GetMapping("/pagesContrats")
    public ResponseEntity<Integer> getAmountOfPagesOfCandidaturesNonSignees() {
        try{
            return ResponseEntity.status(HttpStatus.ACCEPTED).contentType(MediaType.APPLICATION_JSON).body(
                    etudiantService.getAmountOfPagesOfContractNonSignees());
        } catch(Exception e) {
            return ResponseEntity.noContent().build();
        }
    }


}
