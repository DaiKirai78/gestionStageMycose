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


    public EnseignantDTO creationDeCompte(String prenom, String nom, String numeroTelephone, String courriel, String motDePasse) {
        if (!utilisateurService.credentialsDejaPris(courriel, numeroTelephone))
            return EnseignantDTO.toDTO(enseignantRepository.save(new Enseignant(prenom, nom, numeroTelephone, courriel, passwordEncoder.encode(motDePasse))));
        else
            return null;
    }

    public List<EtudiantDTO> getAllEtudiantsAEvaluerParProf(Long enseignantId) {
        try {
            utilisateurService.getMeUtilisateur();
        } catch (AccessDeniedException e) {
            throw new AuthenticationException(HttpStatus.UNAUTHORIZED, "Problème d'authentification");
        }

        Optional<List<Etudiant>> listeEtudiantsOpt = ficheEvaluationMilieuStageRepository.findAllEtudiantsNonEvaluesByProf(enseignantId, Etudiant.ContractStatus.ACTIVE);

        if(listeEtudiantsOpt.isEmpty())
            throw new ResourceNotFoundException("Aucun Étudiant Trouvé");

        List<Etudiant> listeAEnvoyer = listeEtudiantsOpt.get();
        return listeAEnvoyer.stream().map(
                etudiant -> modelMapper.map(etudiant, EtudiantDTO.class)
        ).toList();
    }
}
