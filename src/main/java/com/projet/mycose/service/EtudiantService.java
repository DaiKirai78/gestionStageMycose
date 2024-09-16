package com.projet.mycose.service;

import com.projet.mycose.repository.EtudiantRepository;
import com.projet.mycose.service.dto.EtudiantDTO;
import org.springframework.stereotype.Service;

import static com.projet.mycose.service.dto.EtudiantDTO.toDTO;
import static com.projet.mycose.service.dto.EtudiantDTO.toEntity;

@Service
public class EtudiantService {

    private final EtudiantRepository etudiantRepository;

    public EtudiantService(EtudiantRepository etudiantRepository) {
        this.etudiantRepository = etudiantRepository;
    }

    public EtudiantDTO CreationDeCompte(EtudiantDTO etudiantInformations) {
        if (etudiantInformations != null)
            return toDTO(etudiantRepository.save(toEntity(etudiantInformations)));
        throw new IllegalArgumentException("Les informations de l'étudiant ne peuvent pas être nulles.");
    }
}