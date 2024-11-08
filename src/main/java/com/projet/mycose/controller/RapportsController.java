package com.projet.mycose.controller;


import com.projet.mycose.dto.EtudiantDTO;
import com.projet.mycose.dto.OffreStageDTO;
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
}
