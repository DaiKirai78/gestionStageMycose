package com.projet.mycose.controller;


import com.projet.mycose.dto.EtudiantDTO;
import com.projet.mycose.dto.OffreStageDTO;
import com.projet.mycose.service.EmployeurService;
import com.projet.mycose.service.EnseignantService;
import com.projet.mycose.service.EtudiantService;
import com.projet.mycose.service.OffreStageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/rapports")
@RequiredArgsConstructor
public class RapportsController {

    private final OffreStageService offreStageService;
    private final EtudiantService etudiantService;
    private final EmployeurService employeurService;
    private final EnseignantService enseignantService;

    @GetMapping("/offres-non-validees")
    public ResponseEntity<List<OffreStageDTO>> rapportOffresNonValidees() {
        return ResponseEntity.status(HttpStatus.OK).body(offreStageService.getWaitingOffreStage());
    }

    @GetMapping("/offres-validees")
    public ResponseEntity<List<OffreStageDTO>> rapportOffresValidees() {
        return ResponseEntity.status(HttpStatus.OK).body(offreStageService.getAcceptedOffreStage());
    }

    @GetMapping("/all-etudiants")
    public ResponseEntity<List<EtudiantDTO>> rapportAllEtudiants() {
        return ResponseEntity.status(HttpStatus.OK).body(etudiantService.getAllEtudiants());
    }

    @GetMapping("/etudiants-sans-cv")
    public ResponseEntity<List<EtudiantDTO>> rapportEtudiantsSansCV() {
        return ResponseEntity.status(HttpStatus.OK).body(etudiantService.getEtudiantsSansFichierCV());
    }

    @GetMapping("/etudiants-avec-cv-waiting")
    public ResponseEntity<List<EtudiantDTO>> rapportEtudiantsAvecCVWaiting() {
        return ResponseEntity.status(HttpStatus.OK).body(etudiantService.getEtudiantsWithFichierCVWaiting());
    }

    @GetMapping("/etudiants-sans-convocation")
    public ResponseEntity<List<EtudiantDTO>> rapportEtudiantsSansConvocation() {
        return ResponseEntity.status(HttpStatus.OK).body(etudiantService.getEtudiantsSansConvocation());
    }

    @GetMapping("/etudiants-avec-convocation")
    public ResponseEntity<List<EtudiantDTO>> rapportEtudiantsAvecConvocation() {
        return ResponseEntity.status(HttpStatus.OK).body(etudiantService.getEtudiantsAvecConvocation());
    }

    @GetMapping("/etudiants-interviewed")
    public ResponseEntity<List<EtudiantDTO>> rapportEtudiantsInterviewed() {
        return ResponseEntity.status(HttpStatus.OK).body(etudiantService.getEtudiantsInterviewed());
    }

    @GetMapping("/etudiants-non-evalues")
    public ResponseEntity<List<EtudiantDTO>> rapportsEtudiantsNonEvalues() {
        return ResponseEntity.status(HttpStatus.OK).body(employeurService.getAllEtudiantsNonEvalues());
    }

    @GetMapping("/etudiants-a-evaluer-milieu-de-stage")
    public ResponseEntity<List<EtudiantDTO>> rapportsEtudiantsAEvaluerMilieuDeStage() {
        return ResponseEntity.status(HttpStatus.OK).body(enseignantService.getAllEtudiantsAEvaluerMilieuDeStage());
    }
}