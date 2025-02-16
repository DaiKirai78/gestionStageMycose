package com.projet.mycose.controller;

import com.projet.mycose.dto.ContratDTO;
import com.projet.mycose.dto.EnseignantDTO;
import com.projet.mycose.dto.EtudiantDTO;
import com.projet.mycose.dto.UtilisateurDTO;
import com.projet.mycose.modele.Programme;
import com.projet.mycose.service.EtudiantService;
import com.projet.mycose.service.GestionnaireStageService;
import com.projet.mycose.service.UtilisateurService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/gestionnaire")
public class GestionnaireController {
    private final GestionnaireStageService gestionnaireStageService;
    private final EtudiantService etudiantService;
    private final UtilisateurService utilisateurService;

    @PostMapping("/getEtudiants")
    public ResponseEntity<List<EtudiantDTO>> getEtudiantsSansEnseignant(@RequestParam int pageNumber, @RequestParam Programme programme) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).contentType(MediaType.APPLICATION_JSON).body(
            gestionnaireStageService.getEtudiantsSansEnseignants(pageNumber, programme));
    }

    @GetMapping("/getEtudiantsPages")
    public ResponseEntity<Integer> getAmountOfPages(@RequestParam Programme programme) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).contentType(MediaType.APPLICATION_JSON).body(
            gestionnaireStageService.getAmountOfPages(programme));
    }

    @PostMapping("/rechercheEnseignants")
    public ResponseEntity<List<EnseignantDTO>> rechercherEnseignants(@RequestParam String search) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).contentType(MediaType.APPLICATION_JSON).body(
            gestionnaireStageService.getEnseignantsParRecherche(search));
    }

    @GetMapping("/getEtudiantsParProgramme")
    public ResponseEntity<List<EtudiantDTO>> getEtudiantsByProgramme(@RequestParam Programme programme) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).contentType(MediaType.APPLICATION_JSON).body(
                etudiantService.findEtudiantsByProgramme(programme));
    }

    @PostMapping("/assignerEnseignantEtudiant")
    public ResponseEntity<?> assignerEnseignantVersEtudiant(@RequestParam Long idEtudiant, @RequestParam Long idEnseignant) {
        gestionnaireStageService.assignerEnseigantEtudiant(idEtudiant, idEnseignant);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/getEtudiantsContratEnDemande")
    public ResponseEntity<List<EtudiantDTO>> getEtudiantsContratEnDemande() {
        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(etudiantService.getEtudiantsContratEnDemande());
    }

    @GetMapping("/getEtudiantsSansContratPages")
    public ResponseEntity<Integer> getEtudiantsSansContratPages() {
        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON).body(
                    etudiantService.getEtudiantsSansContratPages());
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

    @GetMapping("/contrats/signes/anneeminimum")
    public ResponseEntity<Set<Integer>> getYearFirstContratUploaded() {
        return ResponseEntity.status(HttpStatus.ACCEPTED).contentType(MediaType.APPLICATION_JSON).body(
                gestionnaireStageService.getYearFirstContratUploaded());
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

    @GetMapping("/getUtilisateurById")
    public ResponseEntity<UtilisateurDTO> getUtilisateurById(@RequestParam Long id) {
        UtilisateurDTO utilisateurDTO = utilisateurService.getUtilisateurById(id);
        return ResponseEntity.status(HttpStatus.OK).body(utilisateurDTO);
    }
}
