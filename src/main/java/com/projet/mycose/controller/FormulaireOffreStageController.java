package com.projet.mycose.controller;

import com.projet.mycose.service.FormulaireOffreStageService;
import com.projet.mycose.service.dto.FormulaireOffreStageDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/offres")
@CrossOrigin(origins = "http://localhost:5173")
public class FormulaireOffreStageController {

    private final FormulaireOffreStageService formulaireOffreStageService;

    public FormulaireOffreStageController(FormulaireOffreStageService formulaireOffreStageService) {
        this.formulaireOffreStageService = formulaireOffreStageService;
    }

    @PostMapping("/upload")
    public ResponseEntity<FormulaireOffreStageDTO> uploadForm(@RequestBody FormulaireOffreStageDTO formulaireOffreStageDTO) {
        FormulaireOffreStageDTO savedForm = formulaireOffreStageService.save(formulaireOffreStageDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedForm);
    }

    @GetMapping
    public ResponseEntity<List<FormulaireOffreStageDTO>> getAllForms() {
        List<FormulaireOffreStageDTO> forms = formulaireOffreStageService.findAll();
        return ResponseEntity.ok(forms);
    }
}
