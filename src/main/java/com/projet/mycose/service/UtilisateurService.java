package com.projet.mycose.service;

import com.projet.mycose.dto.*;
import com.projet.mycose.modele.*;
import com.projet.mycose.repository.*;
import com.projet.mycose.security.JwtTokenProvider;
import com.projet.mycose.service.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import com.projet.mycose.security.exception.UserNotFoundException;

import java.nio.file.AccessDeniedException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;
    private final EtudiantRepository etudiantRepository;
    private final EmployeurRepository employeurRepository;
    private final EnseignantRepository enseignantRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final GestionnaireStageRepository gestionnaireStageRepository;

    public String authentificationUtilisateur(LoginDTO loginDTO) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDTO.getCourriel(), loginDTO.getMotDePasse())
        );
        return jwtTokenProvider.generateToken(authentication);
    }

    public UtilisateurDTO getMe(String token) throws AccessDeniedException {
        if (token != null && token.startsWith("Bearer")) {
            token = token.substring(7);
            String courriel = jwtTokenProvider.getEmailFromJWT(token);
            Utilisateur utilisateur = utilisateurRepository.findUtilisateurByCourriel(courriel).orElseThrow(UserNotFoundException::new);
            return switch (utilisateur.getRole()) {
                case GESTIONNAIRE_STAGE -> getGestionnaireDTO(utilisateur.getId());
                case ETUDIANT -> getEtudiantDTO(utilisateur.getId());
                case EMPLOYEUR -> getEmployeurDTO(utilisateur.getId());
                case ENSEIGNANT -> getEnseignantDTO(utilisateur.getId());
            };
        }
        throw new AccessDeniedException("Accès refusé : Token manquant");
    }

    public Utilisateur getMeUtilisateur(String token) throws AccessDeniedException {
        if (token != null && token.startsWith("Bearer")) {
            token = token.substring(7);
            String courriel = jwtTokenProvider.getEmailFromJWT(token);
            return utilisateurRepository.findUtilisateurByCourriel(courriel).orElseThrow(UserNotFoundException::new);
        }
        throw new AccessDeniedException("Accès refusé : Token manquant");
    }

    public EtudiantDTO getEtudiantDTO(Long id) {
        final Optional<Etudiant> etudiantOptional = etudiantRepository.findById(id);
        return etudiantOptional.isPresent() ?
                EtudiantDTO.toDTO(etudiantOptional.get()) :
                EtudiantDTO.empty();
    }

    public EmployeurDTO getEmployeurDTO(Long id) {
        final Optional<Employeur> employeurOptional = employeurRepository.findById(id);
        return employeurOptional.isPresent() ?
                EmployeurDTO.toDTO(employeurOptional.get()) :
                EmployeurDTO.empty();
    }

    public EnseignantDTO getEnseignantDTO(Long id) {
        final Optional<Enseignant> enseignantOptional = enseignantRepository.findById(id);
        return enseignantOptional.isPresent() ?
                EnseignantDTO.toDTO(enseignantOptional.get()) :
                EnseignantDTO.empty();
    }

    public GestionnaireStageDTO getGestionnaireDTO(Long id) {
        final Optional<GestionnaireStage> gestionnaireStageOptional = gestionnaireStageRepository.findById(id);
        return gestionnaireStageOptional.map(GestionnaireStageDTO::toDTO).orElseGet(GestionnaireStageDTO::empty);
    }

    public UtilisateurDTO getUtilisateurByCourriel(String courriel) {
        Optional<Utilisateur> optionalUtilisateur = utilisateurRepository.findUtilisateurByCourriel(courriel);
        return optionalUtilisateur.map(UtilisateurDTO::toDTO).orElse(null);
    }

    public UtilisateurDTO getUtilisateurByTelephone(String numero) {
        Optional<Utilisateur> optionalUtilisateur = utilisateurRepository.findUtilisateurByNumeroDeTelephone(numero);
        return optionalUtilisateur.map(UtilisateurDTO::toDTO).orElse(null);
    }

    public boolean credentialsDejaPris(String courriel, String numero) {
        return getUtilisateurByCourriel(courriel) != null || getUtilisateurByTelephone(numero) != null;
    }

    public Long getUserIdByToken(String token) {
        try{
            UtilisateurDTO userRecu = getMe(token);
            return userRecu.getId();
        } catch (Exception e) {
            return null;
        }
    }

    // TODO: Finir les tests non couverts
}
