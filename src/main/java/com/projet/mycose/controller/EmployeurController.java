package com.projet.mycose.controller;

import com.projet.mycose.service.EmployeurService;
import com.projet.mycose.dto.EmployeurDTO;
import com.projet.mycose.dto.OffreStageDTO;
import com.projet.mycose.dto.RegisterEmployeurDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<List<OffreStageDTO>> getOffresStagesPubliees(@RequestHeader("Authorization") String token, @RequestParam int pageNumber) {
        try {
            return ResponseEntity.status(HttpStatus.ACCEPTED).contentType(MediaType.APPLICATION_JSON).body(
                    employeurService.getStages(token, pageNumber));
        } catch (Exception e) {
            return ResponseEntity.noContent().build();
        }
    }

    @GetMapping("/pages")
    public ResponseEntity<Integer> getAmountOfPages(@RequestHeader("Authorization") String token) {
        try{
            return ResponseEntity.status(HttpStatus.ACCEPTED).contentType(MediaType.APPLICATION_JSON).body(
                    employeurService.getAmountOfPages(token));
        } catch(Exception e) {
            return ResponseEntity.noContent().build();
        }
    }
}

