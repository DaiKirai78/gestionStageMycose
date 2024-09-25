package com.projet.mycose.service;

import com.projet.mycose.modele.Etudiant;
import com.projet.mycose.repository.EtudiantRepository;
import com.projet.mycose.service.dto.EtudiantDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.projet.mycose.service.dto.EtudiantDTO.toDTO;

@Service
@RequiredArgsConstructor
public class EtudiantService {

    private final EtudiantRepository etudiantRepository;

    public EtudiantDTO creationDeCompte(String prenom, String nom, String numeroTelephone, String courriel, String motDePasse) {
        if (!credentialsDejaPris(courriel, numeroTelephone))
            return toDTO(etudiantRepository.save(new Etudiant(prenom, nom, numeroTelephone, courriel, motDePasse)));
        else
            return null;
    }

    public EtudiantDTO getEtudiantByCourriel(String courriel) {
        Optional<Etudiant> optionalEtudiant = etudiantRepository.findEtudiantByCourriel(courriel);
        return optionalEtudiant.map(EtudiantDTO::toDTO).orElse(null);
    }

    public EtudiantDTO getEtudiantByTelephone(String numero) {
        Optional<Etudiant> optionalEtudiant = etudiantRepository.findEtudiantByNumeroDeTelephone(numero);
        return optionalEtudiant.map(EtudiantDTO::toDTO).orElse(null);
    }

    public boolean credentialsDejaPris(String courriel, String numero) {
        return getEtudiantByCourriel(courriel) != null || getEtudiantByTelephone(numero) != null;
    }

}