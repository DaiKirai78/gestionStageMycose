package com.projet.mycose.controller;

import com.projet.mycose.dto.*;
import com.projet.mycose.modele.ApplicationStage;
import com.projet.mycose.modele.Etudiant;
import com.projet.mycose.security.exception.AuthenticationException;
import com.projet.mycose.service.ApplicationStageService;
import com.projet.mycose.service.EtudiantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@RequestMapping("/api/application-stage")
@RequiredArgsConstructor
public class ApplicationStageController {
    private final ApplicationStageService applicationStageService;
    private final EtudiantService etudiantService;

    @PostMapping("/apply")
    public ResponseEntity<ApplicationStageDTO> applyForStage(@RequestParam Long id) throws AccessDeniedException {
        ApplicationStageDTO applicationStageDTO = applicationStageService.applyToOffreStage(id);
        return new ResponseEntity<>(applicationStageDTO, HttpStatus.CREATED);
    }

    @GetMapping("/my-applications")
    public ResponseEntity<List<ApplicationStageAvecInfosDTO>> getMyApplications() {
        return new ResponseEntity<>(applicationStageService.getApplicationsByEtudiant(), HttpStatus.OK);
    }

    @GetMapping("/my-applications/status/{status}")
    public ResponseEntity<List<ApplicationStageAvecInfosDTO>> getMyApplicationsWithStatus(@PathVariable("status") ApplicationStage.ApplicationStatus status) {
        return new ResponseEntity<>(applicationStageService.getApplicationsByEtudiantWithStatus(status), HttpStatus.OK);
    }

    @GetMapping("/my-applications/{id}")
    public ResponseEntity<ApplicationStageAvecInfosDTO> getMyApplication(@PathVariable Long id) {
        return new ResponseEntity<>(applicationStageService.getApplicationById(id), HttpStatus.OK);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity
            <List<ApplicationStageAvecInfosDTO>> getApplication(@PathVariable Long id) {
        return new ResponseEntity<>(applicationStageService.getApplicationsByEtudiant(id), HttpStatus.OK);
    }

    @PatchMapping("/application/{id}/accepter")
    public ResponseEntity<?> accepterApplication(@PathVariable Long id) {
        try {
            applicationStageService.accepterOuRefuserApplication(id, ApplicationStage.ApplicationStatus.ACCEPTED);
            return ResponseEntity.ok().body("Application acceptée");
        } catch (Exception e) {
            System.err.println("Une erreur est survenue lors de la tentative de récupération des étudiants qui ont appliqués à une offre: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/application/{id}/refuser")
    public ResponseEntity<?> refuserApplication(@PathVariable Long id) {
        try {
            applicationStageService.accepterOuRefuserApplication(id, ApplicationStage.ApplicationStatus.REJECTED);
            return ResponseEntity.ok().body("Application refusée");
        } catch (Exception e) {
            System.err.println("Une erreur est survenue lors de la tentative de récupération des étudiants qui ont appliqués à une offre: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/summon/{id}")
    public ResponseEntity<ApplicationStageAvecInfosDTO> summonEtudiant(@PathVariable Long id, @Valid @RequestBody SummonEtudiantDTO summonEtudiantDTO) {
        return new ResponseEntity<>(applicationStageService.summonEtudiant(id, summonEtudiantDTO), HttpStatus.OK);
    }

    @PatchMapping("/answer-summon/{id}")
    public ResponseEntity<ApplicationStageAvecInfosDTO> answerSummon(@PathVariable Long id, @Valid @RequestBody AnswerSummonDTO answer) {
        return new ResponseEntity<>(applicationStageService.answerSummon(id, answer), HttpStatus.OK);
    }
}
