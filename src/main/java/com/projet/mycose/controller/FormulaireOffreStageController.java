package com.projet.mycose.controller;

import com.projet.mycose.service.FormulaireOffreStageService;
import com.projet.mycose.service.UtilisateurService;
import com.projet.mycose.service.dto.FormulaireOffreStageDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/offres")
@CrossOrigin(origins = "http://localhost:5173")
public class FormulaireOffreStageController {

    private final FormulaireOffreStageService formulaireOffreStageService;
    private final UtilisateurService utilisateurService;

    public FormulaireOffreStageController(FormulaireOffreStageService formulaireOffreStageService, UtilisateurService utilisateurService) {
        this.formulaireOffreStageService = formulaireOffreStageService;
        this.utilisateurService = utilisateurService;
    }

    @PostMapping("/upload")
    public ResponseEntity<FormulaireOffreStageDTO> uploadForm(
        @Valid @RequestBody FormulaireOffreStageDTO formulaireOffreStageDTO, @RequestHeader("Authorization") String token) {
        Long createur_id = utilisateurService.getUserIdByToken(token);
        formulaireOffreStageDTO.setCreateur_id(createur_id);
        FormulaireOffreStageDTO savedForm = formulaireOffreStageService.save(formulaireOffreStageDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedForm);
    }

//    @GetMapping
//    public ResponseEntity<List<FormulaireOffreStageDTO>> getAllForms() {
//        List<FormulaireOffreStageDTO> forms = formulaireOffreStageService.findAll();
//        return ResponseEntity.ok(forms);
//    }
}
