package com.projet.mycose.controller;

import com.projet.mycose.service.EmployeurService;
import com.projet.mycose.service.dto.CourrielTelephoneDTO;
import com.projet.mycose.service.dto.EmployeurDTO;
import com.projet.mycose.service.dto.RegisterEmployeurDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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


    @PostMapping("/register/check-for-conflict")
    public ResponseEntity<Object> CreationDeCompte_CheckForConflict(@Valid @RequestBody CourrielTelephoneDTO courrielTelephoneDTO) {
        if (employeurService.credentialsDejaPris(courrielTelephoneDTO.getCourriel(), courrielTelephoneDTO.getTelephone()))
            return ResponseEntity.status(HttpStatus.CONFLICT).body("L'employeur existe déjà ou les credentials sont invalides");
        else
            return ResponseEntity.status(HttpStatus.OK).body(courrielTelephoneDTO);
    }
}

