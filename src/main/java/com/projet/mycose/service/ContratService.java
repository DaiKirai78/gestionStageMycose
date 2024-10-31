package com.projet.mycose.service;

import com.projet.mycose.dto.ContratDTO;
import com.projet.mycose.dto.EtudiantDTO;
import com.projet.mycose.dto.FichierCVDTO;
import com.projet.mycose.modele.Contrat;
import com.projet.mycose.modele.Etudiant;
import com.projet.mycose.modele.FichierCV;
import com.projet.mycose.repository.ContratRepository;
import com.projet.mycose.repository.EmployeurRepository;
import com.projet.mycose.repository.EtudiantRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class ContratService {

    private final ContratRepository contratRepository;
    private final ModelMapper modelMapper;
    private final EtudiantRepository etudiantRepository;
    private final EmployeurRepository employeurRepository;
    private final UtilisateurService utilisateurService;

    @Transactional
    public ContratDTO save(MultipartFile contratPDF, Long etudiantId, Long employeurId) throws IOException {
        ContratDTO contratDTO = new ContratDTO();
        contratDTO.setPdf(Base64.getEncoder().encodeToString(contratPDF.getBytes()));
        contratDTO.setEtudiantId(etudiantId);
        contratDTO.setEmployeurId(employeurId);

        Contrat contrat = convertToEntity(contratDTO);
        changeContractStatusToActive(etudiantId);
        return convertToDTO(contratRepository.save(contrat));
    }

    public void changeContractStatusToActive(Long etudiantId) {
        if (utilisateurService.getEtudiantDTO(etudiantId) == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "L'étudiant avec l'ID " + etudiantId + " est innexistant");


        Etudiant etudiant = etudiantRepository.findEtudiantById(etudiantId);

        if (etudiant.getContractStatus() == Etudiant.ContractStatus.PENDING) {
            etudiant.setContractStatus(Etudiant.ContractStatus.ACTIVE);
            etudiantRepository.save(etudiant);
        } else
            throw new ResponseStatusException(HttpStatus.CONFLICT, "L'étudiant a déjà un stage actif ou n'a pas fait de demande de stage");
    }

    public ContratDTO convertToDTO(Contrat contrat) {
        ContratDTO contratDTO = modelMapper.map(contrat, ContratDTO.class);

        contratDTO.setPdf(Base64.getEncoder().encodeToString(contrat.getPdf()));

        contratDTO.setEtudiantId(contrat.getEtudiant().getId());
        contratDTO.setEmployeurId(contrat.getEmployeur().getId());

        return contratDTO;
    }

    public Contrat convertToEntity(ContratDTO dto) {
        Contrat contrat = modelMapper.map(dto, Contrat.class);

        contrat.setPdf(Base64.getDecoder().decode(dto.getPdf()));

        contrat.setEtudiant(etudiantRepository.findById(dto.getEtudiantId()).orElseThrow(() -> new RuntimeException("Étudiant non trouvé")));
        contrat.setEmployeur(employeurRepository.findById(dto.getEmployeurId()).orElseThrow(() -> new RuntimeException("Employeur non trouvé")));

        return contrat;
    }

}
