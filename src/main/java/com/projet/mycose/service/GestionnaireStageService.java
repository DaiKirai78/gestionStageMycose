package com.projet.mycose.service;

import com.projet.mycose.modele.GestionnaireStage;
import com.projet.mycose.repository.GestionnaireStageRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class GestionnaireStageService {

    private final UtilisateurService utilisateurService;
    private final PasswordEncoder passwordEncoder;
    private final GestionnaireStageRepository gestionnaireStageRepository;

    public GestionnaireStageService(UtilisateurService utilisateurService, PasswordEncoder passwordEncoder, GestionnaireStageRepository gestionnaireStageRepository) {
        this.utilisateurService = utilisateurService;
        this.passwordEncoder = passwordEncoder;
        this.gestionnaireStageRepository = gestionnaireStageRepository;
    }

    public void creationDeCompte(String prenom, String nom, String numeroTelephone, String courriel, String motDePasse) {
        if (!utilisateurService.credentialsDejaPris(courriel, numeroTelephone))
        {
            gestionnaireStageRepository.save(new GestionnaireStage(prenom, nom, numeroTelephone, courriel, passwordEncoder.encode(motDePasse)));
        }
    }
}
