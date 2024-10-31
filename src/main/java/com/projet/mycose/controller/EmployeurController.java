package com.projet.mycose.controller;

import com.projet.mycose.dto.*;
import com.projet.mycose.service.EmployeurService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @PostMapping("/getOffresPosted")
    public ResponseEntity<List<OffreStageDTO>> getOffresStagesPubliees(@RequestParam int pageNumber) {
        try {
            return ResponseEntity.status(HttpStatus.ACCEPTED).contentType(MediaType.APPLICATION_JSON).body(
                    employeurService.getStages(pageNumber));
        } catch (Exception e) {
            return ResponseEntity.noContent().build();
        }
    }

    @GetMapping("/pages")
    public ResponseEntity<Integer> getAmountOfPages() {
        try{
            return ResponseEntity.status(HttpStatus.ACCEPTED).contentType(MediaType.APPLICATION_JSON).body(
                    employeurService.getAmountOfPages());
        } catch(Exception e) {
            return ResponseEntity.noContent().build();
        }
    }

    @PostMapping("/getContratsNonSignees")
    public ResponseEntity<List<ContratDTO>> getContrats(@RequestParam int pageNumber) {
        try {
            return ResponseEntity.status(HttpStatus.ACCEPTED).contentType(MediaType.APPLICATION_JSON).body(
                    employeurService.getAllContratsNonSignes(pageNumber));
        } catch (Exception e) {
            return ResponseEntity.noContent().build();
        }
    }

    @PostMapping("/enregistrerSignature")
    public ResponseEntity<String> enregistrerSignature(@RequestParam MultipartFile signature, LoginDTO loginDTO, Long contratId) {
        try {
            return ResponseEntity.status(HttpStatus.ACCEPTED).contentType(MediaType.APPLICATION_JSON).body(
                    employeurService.enregistrerSignature(signature, loginDTO, contratId));
        } catch (Exception e) {
            return ResponseEntity.noContent().build();
        }
    }

    @GetMapping("/pagesContrats")
    public ResponseEntity<Integer> getAmountOfPagesOfCandidaturesNonSignees() {
        try{
            return ResponseEntity.status(HttpStatus.ACCEPTED).contentType(MediaType.APPLICATION_JSON).body(
                    employeurService.getAmountOfPagesOfContractNonSignees());
        } catch(Exception e) {
            return ResponseEntity.noContent().build();
        }
    }
}

