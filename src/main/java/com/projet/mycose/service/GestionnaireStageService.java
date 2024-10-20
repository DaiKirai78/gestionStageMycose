package com.projet.mycose.service;

import com.projet.mycose.dto.EnseignantDTO;
import com.projet.mycose.dto.EtudiantDTO;
import com.projet.mycose.modele.Enseignant;
import com.projet.mycose.modele.Etudiant;
import com.projet.mycose.modele.GestionnaireStage;
import com.projet.mycose.repository.GestionnaireStageRepository;
import com.projet.mycose.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public List<EtudiantDTO> getEtudiantsSansEnseignants(int pageNumber) {
        PageRequest pageRequest = PageRequest.of(pageNumber, LIMIT_PER_PAGE);

        Page<Etudiant> pageEtudiantsRetournee = utilisateurRepository.findAllEtudiantsSansEnseignants(pageRequest);
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

    public Integer getAmountOfPages() {
        long amountOfRows = utilisateurRepository.countAllEtudiantsSansEnseignants();

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
