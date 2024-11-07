package com.projet.mycose.service;

import com.projet.mycose.dto.ContratDTO;
import com.projet.mycose.dto.EtudiantDTO;
import com.projet.mycose.dto.OffreStageDTO;
import com.projet.mycose.exceptions.AuthenticationException;
import com.projet.mycose.exceptions.ResourceNotFoundException;
import com.projet.mycose.exceptions.SignaturePersistenceException;
import com.projet.mycose.modele.*;
import com.projet.mycose.repository.ContratRepository;
import com.projet.mycose.repository.EtudiantRepository;
import com.projet.mycose.repository.OffreStageRepository;
import com.projet.mycose.repository.UtilisateurRepository;
import com.projet.mycose.exceptions.UserNotFoundException;
import lombok.RequiredArgsConstructor;
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
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EtudiantService {

    private final EtudiantRepository etudiantRepository;
    private final PasswordEncoder passwordEncoder;
    private final UtilisateurService utilisateurService;
    private final OffreStageRepository offreStageRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final AuthenticationManager authenticationManager;
    private final ContratRepository contratRepository;
    private static final int LIMIT_PER_PAGE = 10;

    public EtudiantDTO creationDeCompte(String prenom, String nom, String numeroTelephone, String courriel, String motDePasse, Programme programme) {
        if (!utilisateurService.credentialsDejaPris(courriel, numeroTelephone))
            return EtudiantDTO.toDTO(etudiantRepository.save(new Etudiant(prenom, nom, numeroTelephone, courriel, passwordEncoder.encode(motDePasse), programme)));
        else
            return null;
    }

    private List<OffreStageDTO> listeOffreStageToDTO(List<OffreStage> listeAMapper) {
        List<OffreStageDTO> listeMappee = new ArrayList<>();
         for(OffreStage offreStage : listeAMapper) {
            listeMappee.add(OffreStageDTO.toOffreStageInstaceDTO(offreStage));
        }

        return listeMappee;
    }


    public List<OffreStageDTO> getStages(int page) {
        Long idEtudiant = utilisateurService.getMyUserId();
        PageRequest pageRequest = PageRequest.of(page, LIMIT_PER_PAGE);

        Page<OffreStage> offresRetourneeEnPages = offreStageRepository.findOffresByEtudiantId(idEtudiant, pageRequest);
        if(offresRetourneeEnPages.isEmpty()) {
            return null;
        }
        return listeOffreStageToDTO(offresRetourneeEnPages.getContent());
    }

    public Integer getAmountOfPages() {
        Long etudiantId = utilisateurService.getMyUserId();
        long amountOfRows = offreStageRepository.countByEtudiantsId(etudiantId);

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

    public List<OffreStageDTO> getStagesByRecherche(int page, String recherche) {
        Long idEtudiant = utilisateurService.getMyUserId();
        PageRequest pageRequest = PageRequest.of(page, LIMIT_PER_PAGE);

        Page<OffreStage> offreStagesEnPages = offreStageRepository.findOffresByEtudiantIdWithSearch(idEtudiant, recherche, pageRequest);

        return listeOffreStageToDTO(offreStagesEnPages.getContent());
    }

    public List<EtudiantDTO> findEtudiantsByProgramme(Programme programme) {
        List<Etudiant> etudiants = etudiantRepository.findAllByProgramme(programme);
        return etudiants.stream().map(EtudiantDTO::toDTO).collect(Collectors.toList());
    }

    public List<EtudiantDTO> getEtudiantsContratEnDemande() {
        List<Etudiant> etudiants = etudiantRepository.findEtudiantsByContractStatusEquals(Etudiant.ContractStatus.PENDING);
        return etudiants.stream().map(EtudiantDTO::toDTO).collect(Collectors.toList());
    }

    public Integer getEtudiantsSansContratPages() {
        long amountOfRows = etudiantRepository.countByContractStatusEquals(Etudiant.ContractStatus.PENDING);

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
    public String enregistrerSignature(MultipartFile signature, String password, Long contratId) {
        Long gestionnaireId = utilisateurService.getMyUserId();
        Utilisateur utilisateur = utilisateurRepository.findUtilisateurById(gestionnaireId)
                .orElseThrow(UserNotFoundException::new);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(utilisateur.getCourriel(), password)
        );

        if(!authentication.isAuthenticated())
            throw new AuthenticationException(HttpStatus.UNAUTHORIZED, "Email ou mot de passe invalide.");

        Contrat contratDispo = contratRepository.findById(contratId)
                .orElseThrow(() -> new ResourceNotFoundException("Contrat non trouvé"));
        try {
            contratDispo.setSignatureEtudiant(signature.getBytes());
        } catch (IOException e) {
            throw new SignaturePersistenceException("Error while saving signature");
        }
        contratRepository.save(contratDispo);
        return "Signature sauvegardée";
    }

    public List<ContratDTO> getAllContratsNonSignes(int page) {
        Long etudiantId = utilisateurService.getMyUserId();
        PageRequest pageRequest = PageRequest.of(page, LIMIT_PER_PAGE);
        Page<Contrat> contratsRetournessEnPages = contratRepository.findContratsBySignatureEtudiantIsNullAndEtudiant_Id(etudiantId, pageRequest);
        if(contratsRetournessEnPages.isEmpty()) {
            return Collections.emptyList();
        }
        return contratsRetournessEnPages.stream().map(ContratDTO::toDTO).toList();
    }

    public Integer getAmountOfPagesOfContractNonSignees() {
        Long etudiantId = utilisateurService.getMyUserId();
        long amountOfRows = contratRepository.countBySignatureEtudiantIsNullAndEtudiantId(etudiantId);

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