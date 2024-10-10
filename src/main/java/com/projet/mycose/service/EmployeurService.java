package com.projet.mycose.service;

import com.projet.mycose.modele.Employeur;
import com.projet.mycose.modele.OffreStage;
import com.projet.mycose.repository.EmployeurRepository;
import com.projet.mycose.repository.OffreStageRepository;
import com.projet.mycose.service.dto.EmployeurDTO;
import com.projet.mycose.service.dto.OffreStageDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmployeurService {
    private final EmployeurRepository employeurRepository;
    private final PasswordEncoder passwordEncoder;
    private final UtilisateurService utilisateurService;
    private final OffreStageRepository offreStageRepository;
    private final int LIMIT_PER_PAGE = 10;

    public EmployeurDTO creationDeCompte(String prenom, String nom, String numeroTelephone, String courriel, String motDePasse, String nomOrganisation) {
        if (!utilisateurService.credentialsDejaPris(courriel, numeroTelephone))
            return EmployeurDTO.toDTO(employeurRepository.save(new Employeur(prenom, nom, numeroTelephone, courriel, passwordEncoder.encode(motDePasse), nomOrganisation)));
        else
            return null;
    }

    private List<OffreStageDTO> listeOffreStageToDTO(List<OffreStage> listeAMapper) {
        System.out.println(listeAMapper);
        List<OffreStageDTO> listeMappee = new ArrayList<>();
        for(OffreStage offreStage : listeAMapper) {
            listeMappee.add(OffreStageDTO.toOffreStageInstaceDTO(offreStage));
        }
        return listeMappee;
    }

    public List<OffreStageDTO> getStages(String token, int page) {
        Long idEmployeur = utilisateurService.getUserIdByToken(token);
        PageRequest pageRequest = PageRequest.of(page, LIMIT_PER_PAGE);

        Page<OffreStage> offresRetourneesEnPages = offreStageRepository.findOffreStageByEmployeurId(idEmployeur, pageRequest);
        if(offresRetourneesEnPages.isEmpty()) {
            return null;
        }

        return listeOffreStageToDTO(offresRetourneesEnPages.getContent());
    }
}
