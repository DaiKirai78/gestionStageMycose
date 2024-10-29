package com.projet.mycose.controller;

import com.projet.mycose.dto.ApplicationStageDTO;
import com.projet.mycose.dto.EnseignantDTO;
import com.projet.mycose.dto.EtudiantDTO;
import com.projet.mycose.modele.Employeur;
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
    public ResponseEntity<List<EtudiantDTO>> getEtudiantsSansEnseignant(@RequestParam int pageNumber, @RequestParam Programme programme) {
        try {
            return ResponseEntity.status(HttpStatus.ACCEPTED).contentType(MediaType.APPLICATION_JSON).body(
                    gestionnaireStageService.getEtudiantsSansEnseignants(pageNumber, programme));
        } catch (Exception e) {
            return ResponseEntity.noContent().build();
        }
    }

    @GetMapping("/getEtudiantsPages")
    public ResponseEntity<Integer> getAmountOfPages(@RequestParam Programme programme) {
        try {
            return ResponseEntity.status(HttpStatus.ACCEPTED).contentType(MediaType.APPLICATION_JSON).body(
                    gestionnaireStageService.getAmountOfPages(programme));
        } catch (Exception e) {
            return ResponseEntity.noContent().build();
        }
    }

    @PostMapping("/rechercheEnseignants")
    public ResponseEntity<List<EnseignantDTO>> rechercherEnseignants(@RequestParam String search) {
        try {
            return ResponseEntity.status(HttpStatus.ACCEPTED).contentType(MediaType.APPLICATION_JSON).body(
                    gestionnaireStageService.getEnseignantsParRecherche(search));
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

    @PostMapping("/assignerEnseignantEtudiant")
    public ResponseEntity<?> assignerEnseignantVersEtudiant(@RequestParam Long idEtudiant, @RequestParam Long idEnseignant) {
        try {
            gestionnaireStageService.assignerEnseigantEtudiant(idEtudiant, idEnseignant);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.noContent().build();
        }
    }

    @GetMapping("/getEtudiantsContratEnDemande")
    public ResponseEntity<List<EtudiantDTO>> getEtudiantsContratEnDemande() {
        try {
            return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(etudiantService.getEtudiantsContratEnDemande());
        } catch (Exception e) {
            System.err.println("Une erreur est survenue lors de la récupération des étudiants en demande de contrat : " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}
