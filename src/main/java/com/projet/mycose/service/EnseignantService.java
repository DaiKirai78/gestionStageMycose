package com.projet.mycose.service;

import com.projet.mycose.modele.Enseignant;
import com.projet.mycose.repository.EnseignantRepository;
import com.projet.mycose.dto.EnseignantDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EnseignantService {
    private final EnseignantRepository enseignantRepository;
    private final PasswordEncoder passwordEncoder;
    private final UtilisateurService utilisateurService;


    public EnseignantDTO creationDeCompte(String prenom, String nom, String numeroTelephone, String courriel, String motDePasse) {
        if (!utilisateurService.credentialsDejaPris(courriel, numeroTelephone))
            return EnseignantDTO.toDTO(enseignantRepository.save(new Enseignant(prenom, nom, numeroTelephone, courriel, passwordEncoder.encode(motDePasse))));
        else
            return null;
    }
}
