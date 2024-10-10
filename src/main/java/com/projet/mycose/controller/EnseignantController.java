package com.projet.mycose.controller;

import com.projet.mycose.service.EnseignantService;
import com.projet.mycose.service.dto.EnseignantDTO;
import com.projet.mycose.service.dto.RegisterEnseignantDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
@RequestMapping("enseignant")
public class EnseignantController {
    private final EnseignantService enseignantService;

    @PostMapping("/register")
    public ResponseEntity<Object> CreationDeCompte(@Valid @RequestBody RegisterEnseignantDTO nouveauCompteEnseignant) {
        EnseignantDTO enseignantResultat = enseignantService.creationDeCompte(nouveauCompteEnseignant.getPrenom(),
                nouveauCompteEnseignant.getNom(),
                nouveauCompteEnseignant.getNumeroDeTelephone(),
                nouveauCompteEnseignant.getCourriel(),
                nouveauCompteEnseignant.getMotDePasse());
        return enseignantResultat != null ? ResponseEntity.status(HttpStatus.CREATED).body(enseignantResultat) : ResponseEntity.status(HttpStatus.CONFLICT).body("L'enseignant existe déjà ou les credentials sont invalides");
    }
}
