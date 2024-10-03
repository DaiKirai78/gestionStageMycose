package com.projet.mycose.controller;

import com.projet.mycose.service.EtudiantService;
import com.projet.mycose.service.dto.CourrielTelephoneDTO;
import com.projet.mycose.service.dto.EtudiantDTO;
import com.projet.mycose.service.dto.RegisterEnseignantDTO;
import com.projet.mycose.service.dto.RegisterEtudiantDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

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
}
