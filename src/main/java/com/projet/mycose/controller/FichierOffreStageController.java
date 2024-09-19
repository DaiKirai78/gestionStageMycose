package com.projet.mycose.controller;


import com.projet.mycose.modele.FichierOffreStage;
import com.projet.mycose.service.FichierOffreStageService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/files")
public class FichierOffreStageController {

    private final FichierOffreStageService fichierOffreStageService;

    public FichierOffreStageController(FichierOffreStageService fichierOffreStageService) {
        this.fichierOffreStageService = fichierOffreStageService;
    }

    @PostMapping("/upload")
    public ResponseEntity<FichierOffreStage> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        FichierOffreStage savedFile = fichierOffreStageService.saveFile(file);
        return ResponseEntity.ok(savedFile);
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable Long id) {
        FichierOffreStage fichierOffreStage = fichierOffreStageService.getFile(id);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fichierOffreStage.getFilename() + "\"")
                .body(fichierOffreStage.getData());
    }
}
