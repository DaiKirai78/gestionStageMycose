package com.projet.mycose.controller;

import com.projet.mycose.dto.EtudiantDTO;
import com.projet.mycose.dto.OffreStageDTO;
import com.projet.mycose.dto.*;
import com.projet.mycose.modele.ApplicationStage;
import com.projet.mycose.service.ApplicationStageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

    @PostMapping("/apply")
    public ResponseEntity<ApplicationStageDTO> applyForStage(@RequestParam Long id) {
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

    @GetMapping("/status/{status}")
    public ResponseEntity<List<ApplicationStageAvecInfosDTO>> getApplicationsWithStatus(@PathVariable("status") ApplicationStage.ApplicationStatus status) {
        return new ResponseEntity<>(applicationStageService.getApplicationsWithStatus(status), HttpStatus.OK);
    }

    @GetMapping("/my-applications/{id}")
    public ResponseEntity<ApplicationStageAvecInfosDTO> getMyApplication(@PathVariable Long id) {
        return new ResponseEntity<>(applicationStageService.getApplicationById(id), HttpStatus.OK);
    }

    @GetMapping("/get/etudiant/{id}")
    public ResponseEntity
            <List<ApplicationStageAvecInfosDTO>> getApplicationsByEtudiantId(@PathVariable Long id) {
        return new ResponseEntity<>(applicationStageService.getApplicationsByEtudiant(id), HttpStatus.OK);
    }

    @PatchMapping("/application/{id}/accepter")
    public ResponseEntity<?> accepterApplication(@PathVariable Long id) {
        applicationStageService.accepterOuRefuserApplication(id, ApplicationStage.ApplicationStatus.ACCEPTED);
        return ResponseEntity.ok().body("Application acceptée");
    }

    @PatchMapping("/application/{id}/refuser")
    public ResponseEntity<?> refuserApplication(@PathVariable Long id) {
        applicationStageService.accepterOuRefuserApplication(id, ApplicationStage.ApplicationStatus.REJECTED);
        return ResponseEntity.ok().body("Application refusée");
    }

    @PatchMapping("/summon/{id}")
    public ResponseEntity<ApplicationStageAvecInfosDTO> summonEtudiant(@PathVariable Long id, @Valid @RequestBody SummonEtudiantDTO summonEtudiantDTO) {
        return new ResponseEntity<>(applicationStageService.summonEtudiant(id, summonEtudiantDTO), HttpStatus.OK);
    }

    @PatchMapping("/answer-summon/{id}")
    public ResponseEntity<ApplicationStageAvecInfosDTO> answerSummon(@PathVariable Long id, @Valid @RequestBody AnswerSummonDTO answer) {
        return new ResponseEntity<>(applicationStageService.answerSummon(id, answer), HttpStatus.OK);
    }

    @GetMapping("/getEtudiant/{applicationId}")
    public ResponseEntity<EtudiantDTO> getEtudiantFromApplication(@PathVariable Long applicationId) {
        EtudiantDTO etudiantDTO = applicationStageService.getEtudiantFromApplicationId(applicationId);
        return ResponseEntity.ok(etudiantDTO);
    }

    @GetMapping("/getOffreStage/{applicationId}")
    public ResponseEntity<OffreStageDTO> getOffreStageFromApplication(@PathVariable Long applicationId) {
        OffreStageDTO offreStageDTO = applicationStageService.getOffreStageFromApplicationId(applicationId);
        return ResponseEntity.ok(offreStageDTO);
    }
}
