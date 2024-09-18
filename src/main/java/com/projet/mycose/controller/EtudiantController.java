package com.projet.mycose.controller;

import com.projet.mycose.service.EtudiantService;
import com.projet.mycose.service.dto.EtudiantDTO;
import com.projet.mycose.service.dto.RegisterEtudiantDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("etudiant")
public class EtudiantController {
    private final EtudiantService etudiantService;

    public EtudiantController(EtudiantService etudiantService) {
        this.etudiantService = etudiantService;
    }

    @PostMapping("/register")
    public ResponseEntity<HttpStatus> CreationDeCompte(@RequestBody RegisterEtudiantDTO nouveauCompteEtudiant) {
        try {
            EtudiantDTO etudiantResultat;
            if (etudiantService.credentialsDejaPris(nouveauCompteEtudiant.getCourriel(), nouveauCompteEtudiant.getNumeroDeTelephone())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            } else {
                etudiantResultat = etudiantService.creationDeCompte(nouveauCompteEtudiant.getPrenom(),
                        nouveauCompteEtudiant.getNom(),
                        nouveauCompteEtudiant.getNumeroDeTelephone(),
                        nouveauCompteEtudiant.getCourriel(),
                        nouveauCompteEtudiant.getMotDePasse());
            }
            return etudiantResultat != null ? ResponseEntity.status(HttpStatus.CREATED).build() : ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
