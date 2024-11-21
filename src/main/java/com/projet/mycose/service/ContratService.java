package com.projet.mycose.service;

import com.projet.mycose.dto.ContratDTO;
import com.projet.mycose.dto.EmployeurDTO;
import com.projet.mycose.dto.EtudiantDTO;
import com.projet.mycose.exceptions.ResourceConflictException;
import com.projet.mycose.exceptions.ResourceNotFoundException;
import com.projet.mycose.exceptions.UserNotFoundException;
import com.projet.mycose.modele.Contrat;
import com.projet.mycose.modele.Etudiant;
import com.projet.mycose.repository.*;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ContratService {

    private final ContratRepository contratRepository;
    private final ModelMapper modelMapper;
    private final EtudiantRepository etudiantRepository;
    private final EmployeurRepository employeurRepository;
    private final GestionnaireStageRepository gestionnaireStageRepository;
    private final OffreStageRepository offreStageRepository;
    private final UtilisateurService utilisateurService;
    private final OffreStageService offreStageService;

    @Transactional
    @PreAuthorize("hasAuthority('GESTIONNAIRE_STAGE')")
    public ContratDTO save(Long etudiantId, Long employeurId, Long gestionnaireStageId, Long offreStageId) {
        if (utilisateurService.getEtudiantDTO(etudiantId) == null
                || utilisateurService.getEmployeurDTO(employeurId) == null
                || utilisateurService.getGestionnaireDTO(gestionnaireStageId) == null
        || offreStageService.getOffreStageWithUtilisateurInfo(offreStageId) == null)
            throw new UserNotFoundException();

        ContratDTO contratDTO = new ContratDTO();
        contratDTO.setEtudiantId(etudiantId);
        contratDTO.setEmployeurId(employeurId);
        contratDTO.setGestionnaireStageId(gestionnaireStageId);
        contratDTO.setOffreStageId(offreStageId);
        changeContractStatusToActive(etudiantId);
        return convertToDTO(contratRepository.save(convertToEntity(contratDTO)));
    }

    public void changeContractStatusToActive(Long etudiantId) {
        if (utilisateurService.getEtudiantDTO(etudiantId) == null)
            throw new UserNotFoundException();

        Etudiant etudiant = etudiantRepository.findEtudiantById(etudiantId);

        if (etudiant.getContractStatus() == Etudiant.ContractStatus.PENDING) {
            etudiant.setContractStatus(Etudiant.ContractStatus.ACTIVE);
            etudiantRepository.save(etudiant);
        } else
            throw new ResourceConflictException("L'étudiant a déjà un stage actif ou n'a pas fait de demande de stage");
    }

    public ContratDTO convertToDTO(Contrat contrat) {
        ContratDTO contratDTO = modelMapper.map(contrat, ContratDTO.class);

        contratDTO.setEtudiantId(contrat.getEtudiant().getId());
        System.out.println("contratDTO : " + contratDTO);
        contratDTO.setEmployeurId(contrat.getEmployeur().getId());
        System.out.println("contratDTO : " + contratDTO);
        contratDTO.setGestionnaireStageId(contrat.getGestionnaireStage().getId());
        System.out.println("contratDTO : " + contratDTO);
        contratDTO.setOffreStageId(contrat.getOffreStage().getId());
        System.out.println("contratDTO : " + contratDTO);
        return contratDTO;
    }

    public Contrat convertToEntity(ContratDTO dto) {
        Contrat contrat = new Contrat();
        contrat.setEtudiant(EtudiantDTO.toEntity(utilisateurService.getEtudiantDTO(dto.getEtudiantId())));
        contrat.setEmployeur(EmployeurDTO.(utilisateurService.getEmployeurDTO(dto.getEmployeurId())));

        contrat.setEtudiant(etudiantRepository.findById(dto.getEtudiantId()).orElseThrow(() -> new ResourceNotFoundException("Étudiant non trouvé")));
        System.out.println("contrat : " + contrat);
        contrat.setEmployeur(employeurRepository.findById(dto.getEmployeurId()).orElseThrow(() -> new ResourceNotFoundException("Employeur non trouvé")));
        System.out.println("contrat : " + contrat);
        contrat.setGestionnaireStage(gestionnaireStageRepository.findById(dto.getGestionnaireStageId()).orElseThrow(() -> new ResourceNotFoundException("Gestionnaire de stage non trouvé")));
        System.out.println("contrat : " + contrat);
        contrat.setOffreStage(offreStageRepository.findById(dto.getOffreStageId()).orElseThrow(() -> new ResourceNotFoundException("Offre de stage non trouvée")));
        System.out.println("contrat : " + contrat);
        return contrat;
    }

    public ContratDTO getContractById(Long contractId) {
        Optional<Contrat> contrat = contratRepository.findById(contractId);
        if (contrat.isPresent())
            return ContratDTO.toDTO(contrat.get());
        else throw new ResourceNotFoundException("Contract with id " + contractId + " not found");
    }
}
