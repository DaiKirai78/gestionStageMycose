package com.projet.mycose.service;

import com.projet.mycose.dto.*;
import com.projet.mycose.exceptions.AuthenticationException;
import com.projet.mycose.exceptions.ResourceNotFoundException;
import com.projet.mycose.exceptions.SignaturePersistenceException;
import com.projet.mycose.exceptions.UserNotFoundException;
import com.projet.mycose.modele.*;
import com.projet.mycose.repository.*;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.modelmapper.ModelMapper;
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
import java.nio.file.AccessDeniedException;
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
    private final ModelMapper modelMapper;
    private final FicheEvaluationStagiaireRepository ficheEvaluationStagiaireRepository;
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

    @Transactional
    public void enregistrerFicheEvaluationStagiaire(FicheEvaluationStagiaireDTO ficheEvaluationStagiaireDTO, Long etudiantId) {
        Utilisateur utilisateur = null;

        try {
            utilisateur = utilisateurService.getMeUtilisateur();
        } catch (AccessDeniedException e) {
            throw new AuthenticationException(HttpStatus.UNAUTHORIZED, "Problème d'authentification");
        }

        Employeur employeur = getValidatedEmployeur(utilisateur);
        Optional<Utilisateur> etudiantOpt = utilisateurRepository.findUtilisateurById(etudiantId);

        if(etudiantOpt.isEmpty())
            throw new UserNotFoundException();

        Etudiant etudiant = getValidatedEtudiant(etudiantOpt.get());

        FicheEvaluationStagiaire ficheEvaluationStagiaire = modelMapper.map(ficheEvaluationStagiaireDTO, FicheEvaluationStagiaire.class);

        Optional<Contrat> contratOpt = contratRepository.findContratActiveOfEtudiantAndEmployeur(etudiantId, employeur.getId(), Etudiant.ContractStatus.ACTIVE);
        if(contratOpt.isEmpty()) {
            throw new ResourceNotFoundException("Contrat de l'étudiant non trouvé");
        }

        Contrat contrat = contratOpt.get();

        ficheEvaluationStagiaire.setEmployeur(employeur);
        ficheEvaluationStagiaire.setEtudiant(etudiant);
        ficheEvaluationStagiaire.setContrat(contrat);

        ficheEvaluationStagiaireRepository.save(ficheEvaluationStagiaire);
    }

    private Employeur getValidatedEmployeur(Utilisateur utilisateur) {
        if (!(utilisateur instanceof Employeur employeur)) {
            throw new AuthenticationException(HttpStatus.FORBIDDEN, "User is not an Employeur.");
        }
        return employeur;
    }

    private Etudiant getValidatedEtudiant(Utilisateur utilisateur) {
        if (!(utilisateur instanceof Etudiant etudiant)) {
            throw new AuthenticationException(HttpStatus.FORBIDDEN, "User is not an Etudiant.");
        }
        return etudiant;
    }

    public Page<EtudiantDTO> getAllEtudiantsNonEvalues(Long employeurId, int page) {
        PageRequest pageRequest = PageRequest.of(page, LIMIT_PER_PAGE);

        try {
            utilisateurService.getMeUtilisateur();
        } catch (AccessDeniedException e) {
            throw new AuthenticationException(HttpStatus.UNAUTHORIZED, "Problème d'authentification");
        }

        Page<Etudiant> pageEtudiants = ficheEvaluationStagiaireRepository.findAllEtudiantWhereNotEvaluated(employeurId, Etudiant.ContractStatus.ACTIVE, pageRequest);

        if(pageEtudiants.isEmpty())
            throw new ResourceNotFoundException("Aucun Étudiant Trouvé");

        return pageEtudiants.map(
                etudiant -> modelMapper.map(etudiant, EtudiantDTO.class));
    }

}
