package com.projet.mycose.service;

import com.projet.mycose.dto.EtudiantDTO;
import com.projet.mycose.exceptions.AuthenticationException;
import com.projet.mycose.exceptions.ResourceNotFoundException;
import com.projet.mycose.modele.Enseignant;
import com.projet.mycose.modele.Etudiant;
import com.projet.mycose.repository.EnseignantRepository;
import com.projet.mycose.dto.EnseignantDTO;
import com.projet.mycose.repository.FicheEvaluationMilieuStageRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
}
