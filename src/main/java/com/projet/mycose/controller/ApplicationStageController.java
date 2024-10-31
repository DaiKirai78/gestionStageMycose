package com.projet.mycose.controller;

import com.projet.mycose.dto.EtudiantDTO;
import com.projet.mycose.dto.OffreStageDTO;
import com.projet.mycose.modele.ApplicationStage;
import com.projet.mycose.modele.Etudiant;
import com.projet.mycose.security.exception.AuthenticationException;
import com.projet.mycose.service.ApplicationStageService;
import com.projet.mycose.dto.ApplicationStageAvecInfosDTO;
import com.projet.mycose.dto.ApplicationStageDTO;
import com.projet.mycose.service.EtudiantService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@RequestMapping("/api/application-stage")
@RequiredArgsConstructor
public class ApplicationStageController {
    private final ApplicationStageService applicationStageService;

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
        try {
            return new ResponseEntity<>(applicationStageService.getApplicationsByEtudiant(id), HttpStatus.OK);
        } catch (Exception e) {
            System.err.println("Une erreur est survenue lors de la tentative de récupération des applications d'un étudiant :" + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/application/{id}/accepter")
    public ResponseEntity<?> accepterApplication(@PathVariable Long id) {
        try {
            applicationStageService.accepterOuRefuserApplication(id, ApplicationStage.ApplicationStatus.ACCEPTED);
            return ResponseEntity.ok().body("Application acceptée");
        } catch (Exception e) {
            System.err.println("Une erreur est survenue lors de la tentative d'acceptation d'une candidature à une offre : " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/application/{id}/refuser")
    public ResponseEntity<?> refuserApplication(@PathVariable Long id) {
        try {
            applicationStageService.accepterOuRefuserApplication(id, ApplicationStage.ApplicationStatus.REJECTED);
            return ResponseEntity.ok().body("Application refusée");
        } catch (Exception e) {
            System.err.println("Une erreur est survenue lors de la tentative de refus d'une candidature à une offre : " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/summon/{id}")
    public ResponseEntity<ApplicationStageAvecInfosDTO> summonEtudiant(@PathVariable Long id) {
        return new ResponseEntity<>(applicationStageService.summonEtudiant(id), HttpStatus.OK);
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
