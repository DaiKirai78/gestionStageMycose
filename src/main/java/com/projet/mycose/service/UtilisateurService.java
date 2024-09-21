package com.projet.mycose.service;

import com.projet.mycose.modele.Etudiant;
import com.projet.mycose.modele.Utilisateur;
import com.projet.mycose.repository.EtudiantRepository;
import com.projet.mycose.repository.UtilisateurRepository;
import com.projet.mycose.security.JwtTokenProvider;
import com.projet.mycose.service.dto.EtudiantDTO;
import com.projet.mycose.service.dto.LoginDTO;
import com.projet.mycose.service.dto.UtilisateurDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import com.projet.mycose.security.exception.UserNotFoundException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;
    private final EtudiantRepository etudiantRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    public String authentificationEtudiant(LoginDTO loginDTO) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDTO.getCourriel(), loginDTO.getMotDePasse())
        );
        return jwtTokenProvider.generateToken(authentication);
    }

    public EtudiantDTO getMe(String token) {
        token = token.startsWith("Bearer") ? token.substring(7) : token;
        String courriel = jwtTokenProvider.getEmailFromJWT(token);
        Utilisateur utilisateur = utilisateurRepository.findUtilisateurByCourriel(courriel).orElseThrow(UserNotFoundException::new);
        return switch(utilisateur.getRole()){
            case GESTIONNAIRE_STAGE -> null; // TODO: RETOURNER UN getUtilisateurDTO
            case ETUDIANT -> getEtudiantDTO(utilisateur.getId());
            // TODO: AJOUTER LES AUTRES TYPES D'UTILISATEUR
        };
    }

    private EtudiantDTO getEtudiantDTO(Long id) {
        final Optional<Etudiant> etudiantOptional = etudiantRepository.findById(id);
        return etudiantOptional.isPresent() ?
                EtudiantDTO.toDTO(etudiantOptional.get()) :
                EtudiantDTO.empty();
    }
}
