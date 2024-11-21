package com.projet.mycose.service;

import com.projet.mycose.dto.EtudiantDTO;
import com.projet.mycose.dto.FicheEvaluationMilieuStageDTO;
import com.projet.mycose.exceptions.AuthenticationException;
import com.projet.mycose.exceptions.ResourceNotFoundException;
import com.projet.mycose.exceptions.UserNotFoundException;
import com.projet.mycose.modele.*;
import com.projet.mycose.repository.EnseignantRepository;
import com.projet.mycose.dto.EnseignantDTO;
import com.projet.mycose.repository.EtudiantRepository;
import com.projet.mycose.repository.FicheEvaluationMilieuStageRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EnseignantService {
    private final EnseignantRepository enseignantRepository;
    private final PasswordEncoder passwordEncoder;
    private final UtilisateurService utilisateurService;
    private final FicheEvaluationMilieuStageRepository ficheEvaluationMilieuStageRepository;
    private final ModelMapper modelMapper;

    private final int LIMIT_PER_PAGE = 10;
    private final EtudiantRepository etudiantRepository;

    public EnseignantDTO creationDeCompte(String prenom, String nom, String numeroTelephone, String courriel, String motDePasse) {
        if (!utilisateurService.credentialsDejaPris(courriel, numeroTelephone))
            return EnseignantDTO.toDTO(enseignantRepository.save(new Enseignant(prenom, nom, numeroTelephone, courriel, passwordEncoder.encode(motDePasse))));
        else
            return null;
    }

    public Page<EtudiantDTO> getAllEtudiantsAEvaluerParProf(Long enseignantId, int page) {
        PageRequest pageRequest = PageRequest.of(page, LIMIT_PER_PAGE);

        try {
            utilisateurService.getMeUtilisateur();
        } catch (AccessDeniedException e) {
            throw new AuthenticationException(HttpStatus.UNAUTHORIZED, "Problème d'authentification");
        }

        Page<Etudiant> listeEtudiants = ficheEvaluationMilieuStageRepository.findAllEtudiantsNonEvaluesByProf(enseignantId, Etudiant.ContractStatus.ACTIVE, pageRequest);

        if(listeEtudiants.isEmpty())
            throw new ResourceNotFoundException("Aucun Étudiant Trouvé");

        return listeEtudiants.map(
                etudiant -> modelMapper.map(etudiant, EtudiantDTO.class)
        );
    }

    Enseignant getValidatedEnseignant(Utilisateur utilisateur) {
        if (!(utilisateur instanceof Enseignant enseignant)) {
            throw new AuthenticationException(HttpStatus.FORBIDDEN, "User is not an Enseignant.");
        }
        return enseignant;
    }

    @Transactional
    public void enregistrerFicheEvaluationMilieuStage(FicheEvaluationMilieuStageDTO ficheEvaluationMilieuStageDTO, Long etudiantId) {
        Utilisateur utilisateur;

        try {
            utilisateur = utilisateurService.getMeUtilisateur();
        } catch (AccessDeniedException e) {
            throw new AuthenticationException(HttpStatus.UNAUTHORIZED, "Problème d'authentification");
        }

        Enseignant enseignant = getValidatedEnseignant(utilisateur);

        Etudiant etudiant = etudiantRepository.findById(etudiantId).orElseThrow(UserNotFoundException::new);

        FicheEvaluationMilieuStage ficheEvaluationMilieuStage = modelMapper.map(ficheEvaluationMilieuStageDTO, FicheEvaluationMilieuStage.class);

        ficheEvaluationMilieuStage.setEnseignant(enseignant);
        ficheEvaluationMilieuStage.setEtudiant(etudiant);

        ficheEvaluationMilieuStageRepository.save(ficheEvaluationMilieuStage);

        //TODO: AJOUTER LE CONTRAT POUR ASSOCIER L'ÉTUDIANT ET L'ENSEIGNANT À UN STAGE
        //TODO: FAIRE LE TEST QUAND SAM AURA FINI
    }
}
