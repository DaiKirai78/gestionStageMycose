package com.projet.mycose.service;

import com.projet.mycose.dto.ContratDTO;
import com.projet.mycose.dto.LoginDTO;
import com.projet.mycose.modele.Contrat;
import com.projet.mycose.modele.Employeur;
import com.projet.mycose.modele.OffreStage;
import com.projet.mycose.repository.ContratRepository;
import com.projet.mycose.repository.EmployeurRepository;
import com.projet.mycose.repository.OffreStageRepository;
import com.projet.mycose.dto.EmployeurDTO;
import com.projet.mycose.dto.OffreStageDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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
    private final ContratRepository contratRepository;
    private final AuthenticationManager authenticationManager;
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
            listeMappee.add(OffreStageDTO.toOffreStageInstaceDTOAll(offreStage));
        }
        return listeMappee;
    }

    public List<OffreStageDTO> getStages(int page) {
        Long idEmployeur = utilisateurService.getMyUserId();
        PageRequest pageRequest = PageRequest.of(page, LIMIT_PER_PAGE);

        Page<OffreStage> offresRetourneesEnPages = offreStageRepository.findOffreStageByCreateurId(idEmployeur, pageRequest);
        if(offresRetourneesEnPages.isEmpty()) {
            return null;
        }

        return listeOffreStageToDTO(offresRetourneesEnPages.getContent());
    }

    public List<ContratDTO> getAllContratsNonSignes(int page) {
        Long employeurId = utilisateurService.getMyUserId();
        PageRequest pageRequest = PageRequest.of(page, LIMIT_PER_PAGE);

        Page<Contrat> contratsRetournessEnPages = contratRepository.findContratsBySignatureEmployeurIsNullAndEmployeur_Id(employeurId, pageRequest);
        if(contratsRetournessEnPages.isEmpty()) {
            return null;
        }

        return contratsRetournessEnPages.stream().map(ContratDTO::toDTO).toList();

    }

    @Transactional
    public String enregistrerSignature(MultipartFile signature, LoginDTO loginDTO, Long contratId) throws Exception{
        Long employeurId = utilisateurService.getMyUserId();

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDTO.getCourriel(), loginDTO.getMotDePasse())
        );
        if(!authentication.isAuthenticated())
            return "Mauvais mot de passe";

        Optional<Contrat> contrat = contratRepository.findById(contratId);
        if(contrat.isEmpty())
            return "Aucun contrat trouvé";

        Contrat contratDispo = contrat.get();
        contratDispo.setSignatureEmployeur(signature.getBytes());
        contratRepository.save(contratDispo);
        return "Signature sauvegardé";
    }

    public Integer getAmountOfPages() {
        Long employeurId = utilisateurService.getMyUserId();
        long amountOfRows = offreStageRepository.countByCreateurId(employeurId);

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

}
