package com.projet.mycose.service;

import com.projet.mycose.modele.FichierOffreStage;
import com.projet.mycose.repository.FichierOffreStageRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class FichierOffreStageService {
    private final FichierOffreStageRepository fileRepository;

    public FichierOffreStageService(FichierOffreStageRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    public FichierOffreStage saveFile(MultipartFile file) throws IOException {
        FichierOffreStage fichierOffreStage = new FichierOffreStage();
        fichierOffreStage.setFilename(file.getOriginalFilename());
        fichierOffreStage.setData(file.getBytes());
        return fileRepository.save(fichierOffreStage);
    }

    public FichierOffreStage getFile(Long id) {
        return fileRepository.findById(id).orElseThrow(() -> new RuntimeException("Fichier non trouvé"));
    }
}
