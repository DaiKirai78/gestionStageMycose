package com.projet.mycose.controller;

import com.projet.mycose.service.EtudiantService;
import com.projet.mycose.service.dto.CourrielTelephoneDTO;
import com.projet.mycose.service.dto.EtudiantDTO;
import com.projet.mycose.service.dto.RegisterEtudiantDTO;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("etudiant")
public class EtudiantController {
    private final EtudiantService etudiantService;

    public EtudiantController(EtudiantService etudiantService) {
        this.etudiantService = etudiantService;
    }


    @PostMapping("/register")
    public ResponseEntity<HttpStatus> CreationDeCompte(@RequestBody RegisterEtudiantDTO nouveauCompteEtudiant) {
        try {
            EtudiantDTO etudiantResultat = etudiantService.creationDeCompte(nouveauCompteEtudiant.getPrenom(),
                    nouveauCompteEtudiant.getNom(),
                    nouveauCompteEtudiant.getNumeroDeTelephone(),
                    nouveauCompteEtudiant.getCourriel(),
                    nouveauCompteEtudiant.getMotDePasse());
            return etudiantResultat != null ? ResponseEntity.status(HttpStatus.CREATED).build() : ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/register/check-for-conflict")
    public ResponseEntity<HttpStatus> CreationDeCompte_CheckForConflict(@RequestBody CourrielTelephoneDTO courrielTelephoneDTO) {
        try {
            if (etudiantService.credentialsDejaPris(courrielTelephoneDTO.getCourriel(), courrielTelephoneDTO.getTelephone()))
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            else
                return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
