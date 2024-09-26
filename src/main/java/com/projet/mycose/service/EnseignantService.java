package com.projet.mycose.service;

import com.projet.mycose.modele.Enseignant;
import com.projet.mycose.repository.EnseignantRepository;
import com.projet.mycose.service.dto.EnseignantDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EnseignantService {
    private final EnseignantRepository enseignantRepository;
    private final PasswordEncoder passwordEncoder;

    public EnseignantDTO creationDeCompte(String prenom, String nom, String numeroTelephone, String courriel, String motDePasse) {
        if (!credentialsDejaPris(courriel, numeroTelephone))
            return EnseignantDTO.toDTO(enseignantRepository.save(new Enseignant(prenom, nom, numeroTelephone, courriel, passwordEncoder.encode(motDePasse))));
        else
            return null;
    }

    public EnseignantDTO getEnseignantByCourriel(String courriel) {
        Optional<Enseignant> optionalEnseignant = enseignantRepository.findEnseignantByCourriel(courriel);
        return optionalEnseignant.map(EnseignantDTO::toDTO).orElse(null);
    }

    public EnseignantDTO getEnseignantByTelephone(String numero) {
        Optional<Enseignant> optionalEnseignant = enseignantRepository.findEnseignantByNumeroDeTelephone(numero);
        return optionalEnseignant.map(EnseignantDTO::toDTO).orElse(null);
    }

    public boolean credentialsDejaPris(String courriel, String numero) {
        return getEnseignantByCourriel(courriel) != null || getEnseignantByTelephone(numero) != null;
    }
}
