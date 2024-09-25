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
        //infosValidation(loginDTO.getCourriel(), loginDTO.getMotDePasse());
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDTO.getCourriel(), loginDTO.getMotDePasse())
        );
        return jwtTokenProvider.generateToken(authentication);
    }

    public UtilisateurDTO getMe(String token) throws AccessDeniedException {
        if (token != null && token.startsWith("Bearer")) {
            token = token.startsWith("Bearer") ? token.substring(7) : token;
            String courriel = jwtTokenProvider.getEmailFromJWT(token);
            Utilisateur utilisateur = utilisateurRepository.findUtilisateurByCourriel(courriel).orElseThrow(UserNotFoundException::new);
            return switch (utilisateur.getRole()) {
                case GESTIONNAIRE_STAGE -> null; // TODO: RETOURNER UN getGestionnaireDTO
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
    // TODO: AJOUTER LES AUTRES MÉTHODES GET POUR CHAQUE ROLES

//    public void infosValidation(String courriel, String motDePasse) {
//        if (courriel == null || courriel.isEmpty() || !courriel.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"))
//            throw new IllegalArgumentException("Le courriel de l'utilisateur est invalide");
//        if (motDePasse == null || motDePasse.isEmpty() || !motDePasse.matches("^\\$2[abxy]?\\$\\d{2}\\$[./A-Za-z0-9]{53}$"))
//            throw new IllegalArgumentException("Le mot de passe de l'utilisateur est invalide");
//    }
}
