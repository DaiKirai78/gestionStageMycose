package com.projet.mycose.controller;

import com.projet.mycose.service.EtudiantService;
import com.projet.mycose.service.dto.EtudiantDTO;
import com.projet.mycose.service.dto.RegisterEtudiantDTO;
import com.projet.mycose.service.dto.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

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
    public ResponseEntity<List<OffreStageDTO>> getStages(@RequestHeader("Authorization") String token, @RequestParam int pageNumber) {
        try {
            return ResponseEntity.status(HttpStatus.ACCEPTED).contentType(MediaType.APPLICATION_JSON).body(
                    etudiantService.getStages(token, pageNumber));
        } catch (Exception e) {
            return ResponseEntity.noContent().build();
        }
    }

    @GetMapping("/pages")
    public ResponseEntity<Integer> getAmountOfPages(@RequestHeader("Authorization") String token) {
        try {
            return ResponseEntity.status(HttpStatus.ACCEPTED).contentType(MediaType.APPLICATION_JSON).body(
                    etudiantService.getAmountOfPages(token));
        } catch (Exception e) {
            return ResponseEntity.noContent().build();
        }
    }

    @PostMapping("/recherche-offre")
    public ResponseEntity<List<OffreStageDTO>> rechercherOffres(@RequestHeader("Authorization") String token, @RequestParam int pageNumber, @RequestParam String recherche) {
        try {
            return ResponseEntity.status(HttpStatus.ACCEPTED).contentType(MediaType.APPLICATION_JSON).body(
                    etudiantService.getStagesByRecherche(token, pageNumber, recherche));
        } catch (Exception e) {
            return ResponseEntity.noContent().build();
        }
    }


}
