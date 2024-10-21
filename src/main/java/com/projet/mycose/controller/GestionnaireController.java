package com.projet.mycose.controller;

import com.projet.mycose.dto.EtudiantDTO;
import com.projet.mycose.modele.Programme;
import com.projet.mycose.service.EtudiantService;
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
    private final EtudiantService etudiantService;

    public GestionnaireController(GestionnaireStageService gestionnaireStageService, EtudiantService etudiantService) {
        this.gestionnaireStageService = gestionnaireStageService;
        this.etudiantService = etudiantService;
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

    @GetMapping("/getEtudiantsParProgramme")
    public ResponseEntity<List<EtudiantDTO>> getEtudiantsByProgramme(@RequestParam Programme programme) {
        try {
            return ResponseEntity.status(HttpStatus.ACCEPTED).contentType(MediaType.APPLICATION_JSON).body(
                    etudiantService.findEtudiantsByProgramme(programme));
        } catch (Exception e) {
            return ResponseEntity.noContent().build();
        }
    }
}
