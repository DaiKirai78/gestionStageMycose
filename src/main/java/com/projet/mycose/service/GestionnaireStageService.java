package com.projet.mycose.service;

import com.projet.mycose.dto.EnseignantDTO;
import com.projet.mycose.dto.EtudiantDTO;
import com.projet.mycose.modele.*;
import com.projet.mycose.repository.GestionnaireStageRepository;
import com.projet.mycose.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GestionnaireStageService {

    private final UtilisateurService utilisateurService;
    private final PasswordEncoder passwordEncoder;
    private final GestionnaireStageRepository gestionnaireStageRepository;
    private final UtilisateurRepository utilisateurRepository;

    private final int LIMIT_PER_PAGE = 10;

    public void creationDeCompte(String prenom, String nom, String numeroTelephone, String courriel, String motDePasse) {
        if (!utilisateurService.credentialsDejaPris(courriel, numeroTelephone)) {
            gestionnaireStageRepository.save(new GestionnaireStage(prenom, nom, numeroTelephone, courriel, passwordEncoder.encode(motDePasse)));
        }
    }

    public List<EtudiantDTO> getEtudiantsSansEnseignants(int pageNumber, Programme programme) {
        PageRequest pageRequest = PageRequest.of(pageNumber, LIMIT_PER_PAGE);

        Page<Etudiant> pageEtudiantsRetournee = utilisateurRepository.findAllEtudiantsSansEnseignants(programme ,pageRequest);
        if(pageEtudiantsRetournee.isEmpty()) {
            return null;
        }

        return pageEtudiantsRetournee.getContent().stream().map(EtudiantDTO::toDTO).toList();
    }

    public List<EnseignantDTO> getEnseignantsParRecherche(String recherche) {
        List<Enseignant>  listeRecu = utilisateurRepository.findAllEnseignantsBySearch(recherche);

        if(listeRecu.isEmpty())
            return null;

        return listeRecu.stream().map(EnseignantDTO::toDTO).toList();
    }

    public Integer getAmountOfPages(Programme programme) {
        long amountOfRows = utilisateurRepository.countAllEtudiantsSansEnseignants(programme);

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
    public void assignerEnseigantEtudiant(Long idEtudiant, Long idEnseignant) {
        Objects.requireNonNull(idEtudiant, "ID Étudiant ne peut pas être NULL");
        Objects.requireNonNull(idEnseignant, "ID Enseignant ne peut pas être NULL");

        Optional<Utilisateur> etudiantRecu = utilisateurRepository.findUtilisateurById(idEtudiant);
        Optional<Utilisateur> enseignantRecu = utilisateurRepository.findUtilisateurById(idEnseignant);

        if(etudiantRecu.isPresent() && enseignantRecu.isPresent()) {
            if(etudiantRecu.get() instanceof Etudiant etudiant && enseignantRecu.get() instanceof Enseignant enseignant) {
                etudiant.setEnseignantAssignee(enseignant);
                enseignant.getEtudiantsAssignees().add(etudiant);

                utilisateurRepository.save(etudiant);
                utilisateurRepository.save(enseignant);
            } else {
                throw new RuntimeException();
            }

        }

    }

}