package com.projet.mycose.service;

import com.projet.mycose.modele.Etudiant;
import com.projet.mycose.modele.OffreStage;
import com.projet.mycose.modele.Programme;
import com.projet.mycose.repository.EtudiantRepository;
import com.projet.mycose.repository.OffreStageRepository;
import com.projet.mycose.service.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.projet.mycose.service.dto.EtudiantDTO.toDTO;

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
        System.out.println(listeAMapper);
        List<OffreStageDTO> listeMappee = new ArrayList<>();
        for(OffreStage offreStage : listeAMapper) {
            System.out.println("mec jadore loop");
            listeMappee.add(OffreStageDTO.toOffreStageInstaceDTO(offreStage));
            //System.out.println("mec jadore loop");
        }
        System.out.println("rnaodm");
        System.out.println(listeMappee);
        return listeMappee;
    }


    public List<OffreStageDTO> getStages(String token, int page) {
        Long idEtudiant = utilisateurService.getUserIdByToken(token);
        PageRequest pageRequest = PageRequest.of(page, LIMIT_PER_PAGE);

        System.out.print("ID ETUDIANT:");
        System.out.println(idEtudiant);

        Page<OffreStage> offresRetourneeEnPages = offreStageRepository.findOffresByEtudiantId(idEtudiant, pageRequest);
        //System.out.println(offresRetourneeEnPages.getContent());
        if(offresRetourneeEnPages.isEmpty()) {
            System.out.println("null?");
            return null;
        }
        System.out.println(listeOffreStageToDTO(offresRetourneeEnPages.getContent()) + "POURQUOI VIIIIDE");
        return listeOffreStageToDTO(offresRetourneeEnPages.getContent());
    }

    public Integer getAmountOfPages(String token) {
        Long etudiantId = utilisateurService.getUserIdByToken(token);
        long amountOfRows = offreStageRepository.countByEtudiantsId(etudiantId);

        return (int) Math.floor((double) amountOfRows / LIMIT_PER_PAGE);
    }

    public List<OffreStageDTO> getStagesByRecherche(String token, String recherche, int page) {
        Long idEtudiant = utilisateurService.getUserIdByToken(token);
        PageRequest pageRequest = PageRequest.of(page, LIMIT_PER_PAGE);

        //Page<OffreStage> offreStagesEnPages = offreStageRepository;
        return null;
    }

}
