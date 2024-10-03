package com.projet.mycose.service;

import com.projet.mycose.modele.Employeur;
import com.projet.mycose.modele.Enseignant;
import com.projet.mycose.modele.Etudiant;
import com.projet.mycose.modele.Utilisateur;
import com.projet.mycose.repository.EmployeurRepository;
import com.projet.mycose.repository.EnseignantRepository;
import com.projet.mycose.repository.EtudiantRepository;
import com.projet.mycose.repository.UtilisateurRepository;
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
                case GESTIONNAIRE_STAGE -> null; // TODO: RETOURNER UN getGestionnaireDTO
                case ETUDIANT -> getEtudiantDTO(utilisateur.getId());
                case EMPLOYEUR -> getEmployeurDTO(utilisateur.getId());
                case ENSEIGNANT -> getEnseignantDTO(utilisateur.getId());
            };
        }
        throw new AccessDeniedException("Accès refusé : Token manquant");
    }

    private EtudiantDTO getEtudiantDTO(Long id) {
        final Optional<Etudiant> etudiantOptional = etudiantRepository.findById(id);
        return etudiantOptional.isPresent() ?
                EtudiantDTO.toDTO(etudiantOptional.get()) :
                EtudiantDTO.empty();
    }

    private EmployeurDTO getEmployeurDTO(Long id) {
        final Optional<Employeur> employeurOptional = employeurRepository.findById(id);
        return employeurOptional.isPresent() ?
                EmployeurDTO.toDTO(employeurOptional.get()) :
                EmployeurDTO.empty();
    }

    private EnseignantDTO getEnseignantDTO(Long id) {
        final Optional<Enseignant> enseignantOptional = enseignantRepository.findById(id);
        return enseignantOptional.isPresent() ?
                EnseignantDTO.toDTO(enseignantOptional.get()) :
                EnseignantDTO.empty();
    }

    public Long getUserIdByToken(String token) {
        try{
            UtilisateurDTO userRecu = getMe(token);
            return userRecu.getId();
        } catch (Exception e) {
            return null;
        }
    }

    // TODO: AJOUTER getGestionnaireDTO
}
