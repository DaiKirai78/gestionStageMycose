package com.projet.mycose.service;

import com.projet.mycose.modele.Etudiant;
import com.projet.mycose.modele.FichierOffreStage;
import com.projet.mycose.modele.FormulaireOffreStage;
import com.projet.mycose.modele.Programme;
import com.projet.mycose.repository.EtudiantRepository;
import com.projet.mycose.repository.FichierOffreStageRepository;
import com.projet.mycose.repository.FormulaireOffreStageRepository;
import com.projet.mycose.service.dto.EtudiantDTO;
import com.projet.mycose.service.dto.FichierOffreStageDTO;
import com.projet.mycose.service.dto.FormulaireOffreStageDTO;
import com.projet.mycose.service.dto.OffresStagesDTO;
import lombok.RequiredArgsConstructor;
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
    private final FichierOffreStageRepository fichierOffreStageRepository;
    private final FormulaireOffreStageRepository formulaireOffreStageRepository;

    public EtudiantDTO creationDeCompte(String prenom, String nom, String numeroTelephone, String courriel, String motDePasse, Programme programme) {
        if (!utilisateurService.credentialsDejaPris(courriel, numeroTelephone))
            return EtudiantDTO.toDTO(etudiantRepository.save(new Etudiant(prenom, nom, numeroTelephone, courriel, passwordEncoder.encode(motDePasse), programme)));
        else
            return null;
    }

    public EtudiantDTO getEtudiantByCourriel(String courriel) {
        Optional<Etudiant> optionalEtudiant = etudiantRepository.findEtudiantByCourriel(courriel);
        return optionalEtudiant.map(EtudiantDTO::toDTO).orElse(null);
    }

    public EtudiantDTO getEtudiantByTelephone(String numero) {
        Optional<Etudiant> optionalEtudiant = etudiantRepository.findEtudiantByNumeroDeTelephone(numero);
        return optionalEtudiant.map(EtudiantDTO::toDTO).orElse(null);
    }

    public boolean credentialsDejaPris(String courriel, String numero) {
        return getEtudiantByCourriel(courriel) != null || getEtudiantByTelephone(numero) != null;
    }

    private List<FichierOffreStageDTO> listeFichierToDTO(Optional<List<FichierOffreStage>> listeAMapper) {
        if(listeAMapper.isEmpty()) {
            return null;
        }

        List<FichierOffreStageDTO> listeMappee = new ArrayList<>();
        for(FichierOffreStage fichierOffreStage : listeAMapper.get()) {
            listeMappee.add(FichierOffreStageDTO.toDTO(fichierOffreStage));
        }

        return listeMappee;
    }

    private List<FormulaireOffreStageDTO> listeFormulaireToDTO(Optional<List<FormulaireOffreStage>> listeAMapper) {
        if(listeAMapper.isEmpty()) {
            return null;
        }

        List<FormulaireOffreStageDTO> listeMappee = new ArrayList<>();
        for(FormulaireOffreStage formulaireOffreStage : listeAMapper.get()) {
            listeMappee.add(FormulaireOffreStageDTO.toDTO(formulaireOffreStage));
        }

        return listeMappee;
    }

    /*public OffresStagesDTO getStages() {
        Optional<List<FichierOffreStage>> listeFichiersRetournee = Optional.of(fichierOffreStageRepository.findAll());
        Optional<List<FormulaireOffreStage>> listeFormulairesRetournee = Optional.of(formulaireOffreStageRepository.findAll());

        List<FichierOffreStageDTO> listeFichiersEnvoyer = listeFichierToDTO(listeFichiersRetournee);
        List<FormulaireOffreStageDTO> listeFormulairesEnvoyer = listeFormulaireToDTO(listeFormulairesRetournee);

        return new OffresStagesDTO(listeFichiersEnvoyer, listeFormulairesEnvoyer);
    }*/

}
