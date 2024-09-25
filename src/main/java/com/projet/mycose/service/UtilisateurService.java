package com.projet.mycose.service;

import com.projet.mycose.modele.Etudiant;
import com.projet.mycose.modele.Utilisateur;
import com.projet.mycose.repository.EtudiantRepository;
import com.projet.mycose.repository.UtilisateurRepository;
import com.projet.mycose.security.JwtTokenProvider;
import com.projet.mycose.service.dto.EtudiantDTO;
import com.projet.mycose.service.dto.LoginDTO;
import io.jsonwebtoken.JwtException;
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
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    public String authentificationUtilisateur(LoginDTO loginDTO) {
        System.out.println(loginDTO.getCourriel() + " " + loginDTO.getMotDePasse());
        if (loginDTO.getCourriel() == null || loginDTO.getMotDePasse() == null) {
            throw new IllegalArgumentException("Le courriel et le mot de passe ne doivent pas être null");
        }
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDTO.getCourriel(), loginDTO.getMotDePasse())
        );
        return jwtTokenProvider.generateToken(authentication);
    }

    public EtudiantDTO getMe(String token) throws AccessDeniedException {
        if (token != null) {
            token = token.startsWith("Bearer") ? token.substring(7) : token;
            String courriel = jwtTokenProvider.getEmailFromJWT(token);
            Utilisateur utilisateur = utilisateurRepository.findUtilisateurByCourriel(courriel).orElseThrow(UserNotFoundException::new);
            return switch (utilisateur.getRole()) {
                case GESTIONNAIRE_STAGE -> null; // TODO: RETOURNER UN getUtilisateurDTO
                case ETUDIANT -> getEtudiantDTO(utilisateur.getId());
                // TODO: AJOUTER LES AUTRES TYPES D'UTILISATEUR
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
}
