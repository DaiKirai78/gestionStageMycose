package com.projet.mycose.controller;

import com.projet.mycose.dto.EnseignantDTO;
import com.projet.mycose.dto.EtudiantDTO;
import com.projet.mycose.service.GestionnaireStageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/gestionnaire")
public class GestionnaireController {
    private final GestionnaireStageService gestionnaireStageService;

    public GestionnaireController(GestionnaireStageService gestionnaireStageService) {
        this.gestionnaireStageService = gestionnaireStageService;
    }

    @PostMapping("/getEtudiants")
    public ResponseEntity<List<EtudiantDTO>> getEtudiantsSansEnseignant(@RequestParam int pageNumber)  {
        try {
            return ResponseEntity.status(HttpStatus.ACCEPTED).contentType(MediaType.APPLICATION_JSON).body(
                    gestionnaireStageService.getEtudiantsSansEnseignants(pageNumber));
        } catch (Exception e) {
            return ResponseEntity.noContent().build();
        }
    }

    @PostMapping("/rechercheEnseignants")
    public ResponseEntity<List<EnseignantDTO>> rechercherEnseignants(@RequestParam String search) {
        try{
            return ResponseEntity.status(HttpStatus.ACCEPTED).contentType(MediaType.APPLICATION_JSON).body(
                    gestionnaireStageService.getEnseignantsParRecherche(search));
        } catch (Exception e) {
            return ResponseEntity.noContent().build();
        }
    }
}
