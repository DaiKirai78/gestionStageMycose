package com.projet.mycose.service;

import com.projet.mycose.dto.EtudiantDTO;
import com.projet.mycose.dto.OffreStageDTO;
import com.projet.mycose.modele.Etudiant;
import com.projet.mycose.modele.OffreStage;
import com.projet.mycose.modele.Programme;
import com.projet.mycose.repository.EtudiantRepository;
import com.projet.mycose.repository.OffreStageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.projet.mycose.dto.EtudiantDTO.toDTO;

@Service
@RequiredArgsConstructor
public class EtudiantService {

    private final EtudiantRepository etudiantRepository;
    private final PasswordEncoder passwordEncoder;
    private final UtilisateurService utilisateurService;
    private final OffreStageRepository offreStageRepository;

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

}
