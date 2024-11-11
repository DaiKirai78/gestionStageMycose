package com.projet.mycose.service;

import com.projet.mycose.dto.ContratDTO;
import com.projet.mycose.exceptions.ResourceConflictException;
import com.projet.mycose.exceptions.ResourceNotFoundException;
import com.projet.mycose.exceptions.UserNotFoundException;
import com.projet.mycose.modele.Contrat;
import com.projet.mycose.modele.Etudiant;
import com.projet.mycose.repository.ContratRepository;
import com.projet.mycose.repository.EmployeurRepository;
import com.projet.mycose.repository.EtudiantRepository;
import com.projet.mycose.repository.GestionnaireStageRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ContratService {

    private final ContratRepository contratRepository;
    private final ModelMapper modelMapper;
    private final EtudiantRepository etudiantRepository;
    private final EmployeurRepository employeurRepository;
    private final GestionnaireStageRepository gestionnaireStageRepository;
    private final UtilisateurService utilisateurService;

    @Transactional
    @PreAuthorize("hasAuthority('GESTIONNAIRE_STAGE')")
    public ContratDTO save(Long etudiantId, Long employeurId, Long gestionnaireStageId) {
        if (utilisateurService.getEtudiantDTO(etudiantId) == null
                || utilisateurService.getEmployeurDTO(employeurId) == null
                || utilisateurService.getGestionnaireDTO(gestionnaireStageId) == null)
            throw new UserNotFoundException();

        ContratDTO contratDTO = new ContratDTO();
        contratDTO.setEtudiantId(etudiantId);
        contratDTO.setEmployeurId(employeurId);
        contratDTO.setGestionnaireStageId(gestionnaireStageId);
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
        contratDTO.setEmployeurId(contrat.getEmployeur().getId());
        contratDTO.setGestionnaireStageId(contrat.getGestionnaireStage().getId());

        return contratDTO;
    }

    public Contrat convertToEntity(ContratDTO dto) {
        Contrat contrat = modelMapper.map(dto, Contrat.class);

        contrat.setEtudiant(etudiantRepository.findById(dto.getEtudiantId()).orElseThrow(() -> new ResourceNotFoundException("Étudiant non trouvé")));
        contrat.setEmployeur(employeurRepository.findById(dto.getEmployeurId()).orElseThrow(() -> new ResourceNotFoundException("Employeur non trouvé")));
        contrat.setGestionnaireStage(gestionnaireStageRepository.findById(dto.getGestionnaireStageId()).orElseThrow(() -> new ResourceNotFoundException("Gestionnaire de stage non trouvé")));

        return contrat;
    }

}
