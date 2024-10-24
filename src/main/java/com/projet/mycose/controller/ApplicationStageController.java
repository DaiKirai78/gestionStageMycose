package com.projet.mycose.controller;

import com.projet.mycose.modele.ApplicationStage;
import com.projet.mycose.service.ApplicationStageService;
import com.projet.mycose.dto.ApplicationStageAvecInfosDTO;
import com.projet.mycose.dto.ApplicationStageDTO;
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

    @PatchMapping("/application/{id}/accepter")
    public ResponseEntity<ApplicationStageDTO> accepterApplication(@PathVariable Long id) {
        return new ResponseEntity<>(applicationStageService.accepterOuRefuserApplication(id,
                ApplicationStage.ApplicationStatus.ACCEPTED), HttpStatus.OK);
    }

    @PatchMapping("/application/{id}/refuser")
    public ResponseEntity<ApplicationStageDTO> refuserApplication(@PathVariable Long id) {
        return new ResponseEntity<>(applicationStageService.accepterOuRefuserApplication(id,
                ApplicationStage.ApplicationStatus.REJECTED), HttpStatus.OK);
    }

    @PostMapping("/summon/{id}")
    public ResponseEntity<ApplicationStageAvecInfosDTO> summonEtudiant(@PathVariable Long id) {
        return new ResponseEntity<>(applicationStageService.summonEtudiant(id), HttpStatus.OK);
    }

}
