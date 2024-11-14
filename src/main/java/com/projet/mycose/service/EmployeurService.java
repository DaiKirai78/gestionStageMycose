package com.projet.mycose.service;

import com.projet.mycose.dto.ContratDTO;
import com.projet.mycose.dto.LoginDTO;
import com.projet.mycose.exceptions.AuthenticationException;
import com.projet.mycose.exceptions.ResourceNotFoundException;
import com.projet.mycose.exceptions.SignaturePersistenceException;
import com.projet.mycose.exceptions.UserNotFoundException;
import com.projet.mycose.modele.Contrat;
import com.projet.mycose.modele.Employeur;
import com.projet.mycose.modele.OffreStage;
import com.projet.mycose.modele.Utilisateur;
import com.projet.mycose.repository.ContratRepository;
import com.projet.mycose.repository.EmployeurRepository;
import com.projet.mycose.repository.OffreStageRepository;
import com.projet.mycose.dto.EmployeurDTO;
import com.projet.mycose.dto.OffreStageDTO;
import com.projet.mycose.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmployeurService {
    private final EmployeurRepository employeurRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;
    private final UtilisateurService utilisateurService;
    private final OffreStageRepository offreStageRepository;
    private final ContratRepository contratRepository;
    private final AuthenticationManager authenticationManager;
    private final int LIMIT_PER_PAGE = 10;

    public EmployeurDTO creationDeCompte(String prenom, String nom, String numeroTelephone, String courriel, String motDePasse, String nomOrganisation) {
        if (!utilisateurService.credentialsDejaPris(courriel, numeroTelephone))
            return EmployeurDTO.toDTO(employeurRepository.save(new Employeur(prenom, nom, numeroTelephone, courriel, passwordEncoder.encode(motDePasse), nomOrganisation)));
        else
            return null;
    }

    public List<ContratDTO> getAllContratsNonSignes(int page) {
        Long employeurId = utilisateurService.getMyUserId();
        PageRequest pageRequest = PageRequest.of(page, LIMIT_PER_PAGE);

        Page<Contrat> contratsRetournessEnPages = contratRepository.findContratsBySignatureEmployeurIsNullAndEmployeur_Id(employeurId, pageRequest);
        if(contratsRetournessEnPages.isEmpty()) {
            return Collections.emptyList();
        }

        return contratsRetournessEnPages.stream().map(ContratDTO::toDTO).toList();

    }

    @Transactional
    public String enregistrerSignature(MultipartFile signature, String password, Long contratId) {
        Long employeurId = utilisateurService.getMyUserId();
        Optional<Utilisateur> utilisateurOpt = utilisateurRepository.findUtilisateurById(employeurId);

        if (utilisateurOpt.isEmpty())
            throw new UserNotFoundException();

        Utilisateur utilisateur = utilisateurOpt.get();

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(utilisateur.getCourriel(), password)
        );
        if(!authentication.isAuthenticated())
            throw new AuthenticationException(HttpStatus.UNAUTHORIZED, "Email ou mot de passe invalide.");

        Optional<Contrat> contrat = contratRepository.findById(contratId);
        if(contrat.isEmpty())
            throw new ResourceNotFoundException("Contrat non trouvé");

        Contrat contratDispo = contrat.get();
        try {
            contratDispo.setSignatureEmployeur(signature.getBytes());
            contratDispo.setDateSignatureEmployeur(LocalDateTime.now());
        } catch (IOException e) {
            throw new SignaturePersistenceException("Error while saving signature");
        }
        contratRepository.save(contratDispo);
        return "Signature sauvegardée";
    }

    public Integer getAmountOfPagesOfContractNonSignees() {
        Long employeurId = utilisateurService.getMyUserId();
        long amountOfRows = contratRepository.countBySignatureEmployeurIsNullAndEmployeurId(employeurId);

        if (amountOfRows == 0)
            return 0;

        int nombrePages = (int) Math.floor((double) amountOfRows / LIMIT_PER_PAGE);

        if (amountOfRows % 10 > 0) {
            // Return ++ (équivalent -> nombrePage + 1) parce que
            // floor(13/10) = 1 mais il y a 2 page et pas 1
            nombrePages++;
        }

        return nombrePages;
    }

}
