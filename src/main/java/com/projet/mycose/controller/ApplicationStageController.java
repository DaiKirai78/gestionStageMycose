package com.projet.mycose.controller;

import com.projet.mycose.dto.EtudiantDTO;
import com.projet.mycose.modele.ApplicationStage;
import com.projet.mycose.service.ApplicationStageService;
import com.projet.mycose.dto.ApplicationStageAvecInfosDTO;
import com.projet.mycose.dto.ApplicationStageDTO;
import com.projet.mycose.service.OffreStageService;
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
    private final OffreStageService offreStageService;

    @PostMapping("/apply")
    public ResponseEntity<ApplicationStageDTO> applyForStage(@RequestParam Long id, @RequestHeader("Authorization") String token) throws AccessDeniedException, ChangeSetPersister.NotFoundException {
        ApplicationStageDTO applicationStageDTO = applicationStageService.applyToOffreStage(token, id);
        return new ResponseEntity<>(applicationStageDTO, HttpStatus.CREATED);
    }

    @GetMapping("/my-applications")
    public ResponseEntity<List<ApplicationStageAvecInfosDTO>> getMyApplications(@RequestHeader("Authorization") String token) {
        return new ResponseEntity<>(applicationStageService.getApplicationsByEtudiant(token), HttpStatus.OK);
    }

    @GetMapping("/my-applications/status/{status}")
    public ResponseEntity<List<ApplicationStageAvecInfosDTO>> getMyApplicationsWithStatus(@RequestHeader("Authorization") String token, @PathVariable("status") ApplicationStage.ApplicationStatus status) {
        return new ResponseEntity<>(applicationStageService.getApplicationsByEtudiantWithStatus(token, status), HttpStatus.OK);
    }

    @GetMapping("/my-applications/{id}")
    public ResponseEntity<ApplicationStageAvecInfosDTO> getMyApplication(@RequestHeader("Authorization") String token, @PathVariable Long id) throws ChangeSetPersister.NotFoundException {
        return new ResponseEntity<>(applicationStageService.getApplicationById(token, id), HttpStatus.OK);
    }

    @GetMapping("/offre-applications/{id}")
    public ResponseEntity<List<EtudiantDTO>> getAllEtudiantQuiOntAppliquesAUneOffre(@PathVariable Long id) {
        List<ApplicationStageDTO> applicationStageDTOList = applicationStageService.getAllApplicationsPourUneOffreById(id);
        return new ResponseEntity<>(offreStageService.getEtudiantsQuiOntAppliquesAUneOffre(applicationStageDTOList), HttpStatus.OK);
    }
}
