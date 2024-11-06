package com.projet.mycose.controller;

import com.projet.mycose.dto.ApplicationStageDTO;
import com.projet.mycose.dto.ContratDTO;
import com.projet.mycose.dto.EnseignantDTO;
import com.projet.mycose.dto.EtudiantDTO;
import com.projet.mycose.modele.Employeur;
import com.projet.mycose.modele.Programme;
import com.projet.mycose.security.exception.UserNotFoundException;
import com.projet.mycose.service.EtudiantService;
import com.projet.mycose.service.GestionnaireStageService;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Collections;
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

    @GetMapping("/getEtudiantsSansContratPages")
    public ResponseEntity<Integer> getEtudiantsSansContratPages() {
        try {
            return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(
                    etudiantService.getEtudiantsSansContratPages());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/contrats/non-signes")
    public ResponseEntity<List<ContratDTO>> getAllContratsNonSignes(@RequestParam int page) {
        List<ContratDTO> contrats = gestionnaireStageService.getAllContratsNonSignes(page);
        return ResponseEntity.ok(contrats);
    }

    @GetMapping("/contrats/non-signes/pages")
    public ResponseEntity<Integer> getAmountOfPagesOfContratNonSignee() {
        return ResponseEntity.status(HttpStatus.ACCEPTED).contentType(MediaType.APPLICATION_JSON).body(
                gestionnaireStageService.getAmountOfPagesOfContractNonSignees());
    }

    @GetMapping("/contrats/signes")
    public ResponseEntity<List<ContratDTO>> getAllContratsSignes(@RequestParam int page, @RequestParam int annee) {
            List<ContratDTO> contrats = gestionnaireStageService.getAllContratsSignes(page, annee);
            return ResponseEntity.ok(contrats);
    }

    @GetMapping("/contrats/signes/pages")
    public ResponseEntity<Integer> getAmountOfPagesOfContratSignee(@RequestParam int annee) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).contentType(MediaType.APPLICATION_JSON).body(
                gestionnaireStageService.getAmountOfPagesOfContractSignees(annee));
    }

    @PostMapping(value = "/enregistrerSignature")
    public ResponseEntity<String> enregistrerSignature(
            @RequestParam("signature") MultipartFile signature,
            @RequestParam Long contratId,
            @RequestParam String password
    ) {
        String responseMessage = gestionnaireStageService.enregistrerSignature(signature, password, contratId);
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(responseMessage);
    }

    @GetMapping("/contrat/print")
    public ResponseEntity<?> imprimerContrat(@RequestParam long id) {
        try {
            return ResponseEntity.ok(gestionnaireStageService.getContratSignee(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("");
        }
    }
}
