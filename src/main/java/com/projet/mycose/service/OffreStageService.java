package com.projet.mycose.service;

import com.projet.mycose.modele.*;
import com.projet.mycose.modele.auth.Role;
import com.projet.mycose.repository.FichierOffreStageRepository;
import com.projet.mycose.repository.FormulaireOffreStageRepository;
import com.projet.mycose.repository.OffreStageRepository;
import com.projet.mycose.repository.UtilisateurRepository;
import com.projet.mycose.service.dto.FichierOffreStageDTO;
import com.projet.mycose.service.dto.FormulaireOffreStageDTO;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import org.hibernate.dialect.pagination.OffsetFetchLimitHandler;
import org.modelmapper.ModelMapper;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.Optional;
import java.util.Set;

@Service
public class OffreStageService {
    private final OffreStageRepository offreStageRepository;
    private final ModelMapper modelMapper;
    private final Validator validator;
    private final UtilisateurService utilisateurService;
    private final UtilisateurRepository utilisateurRepository;
    private final FormulaireOffreStageRepository formulaireOffreStageRepository;
    private final FichierOffreStageRepository ficherOffreStageRepository;
    private static final int LIMIT_PER_PAGE = 10;

    public OffreStageService(OffreStageRepository offreStageRepository, ModelMapper modelMapper, Validator validator, UtilisateurRepository utilisateurRepository) {
        this.offreStageRepository = offreStageRepository;
        this.modelMapper = modelMapper;
        this.validator = validator;
        this.utilisateurService = utilisateurService;
        this.utilisateurRepository = utilisateurRepository;
        this.formulaireOffreStageRepository = formulaireOffreStageRepository;
        this.ficherOffreStageRepository = ficherOffreStageRepository;
    }

    public OffreStageDTO convertToDTO(OffreStage offreStage){
        if (offreStage instanceof FormulaireOffreStage) {
            return convertToDTO((FormulaireOffreStage) offreStage);
        } else {
            return convertToDTO((FichierOffreStage) offreStage);
        }
    }


    // Convert Entity to DTO
    public FichierOffreStageDTO convertToDTO(FichierOffreStage fichierOffreStage) {
        FichierOffreStageDTO fichierOffreStageDTO = modelMapper.map(fichierOffreStage, FichierOffreStageDTO.class);

        // Convert byte[] data to Base64 string
        fichierOffreStageDTO.setFileData(Base64.getEncoder().encodeToString(fichierOffreStage.getData()));

        if (fichierOffreStage.getCreateur() != null) {
            fichierOffreStageDTO.setCreateur_id(fichierOffreStage.getCreateur().getId());
        }

        return fichierOffreStageDTO;
    }

    // Convert DTO to Entity
    public FichierOffreStage convertToEntity(FichierOffreStageDTO dto) {
        FichierOffreStage fichierOffreStage = modelMapper.map(dto, FichierOffreStage.class);

        // Convert Base64 string back to byte[]
        fichierOffreStage.setData(Base64.getDecoder().decode(dto.getFileData()));

        if (dto.getCreateur_id() != null) {
            Utilisateur createur = utilisateurRepository.findById(dto.getCreateur_id())
                    .orElseThrow(() -> new EntityNotFoundException("Utilisateur not found with ID: " + dto.getCreateur_id()));
            fichierOffreStage.setCreateur(createur);
        } else {
            throw new IllegalArgumentException("createur_id cannot be null");
        }

        return fichierOffreStage;
    }

    public FormulaireOffreStageDTO convertToDTO(FormulaireOffreStage formulaireOffreStage) {
        FormulaireOffreStageDTO formulaireOffreStageDTO = modelMapper.map(formulaireOffreStage, FormulaireOffreStageDTO.class);

    public FichierOffreStageDTO saveFile(MultipartFile file) throws ConstraintViolationException, IOException {

        FichierOffreStageDTO fichierOffreStageDTO = new FichierOffreStageDTO();

        fichierOffreStageDTO.setFilename(file.getOriginalFilename());
        fichierOffreStageDTO.setFileData(Base64.getEncoder().encodeToString(file.getBytes()));

        Set<ConstraintViolation<FichierOffreStageDTO>> violations = validator.validate(fichierOffreStageDTO);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }

        FichierOffreStage fichierOffreStage = convertToEntity(fichierOffreStageDTO);
        return convertToDTO(ficherOffreStageRepository.save(fichierOffreStage));
    }

        return convertToDTO(offreStageRepository.save(fichierOffreStage));
    }

    public FormulaireOffreStageDTO saveForm(FormulaireOffreStageDTO formulaireOffreStageDTO) {
        //FormulaireOffreStage formulaireOffreStage = modelMapper.map(formulaireOffreStageDTO, FormulaireOffreStage.class);
        Optional<Utilisateur> createur = utilisateurRepository.findById(formulaireOffreStageDTO.getCreateur_id());
        if(createur.isEmpty()) {
            return null;
        }

        FormulaireOffreStage formulaireOffreStage = new FormulaireOffreStage(
                formulaireOffreStageDTO.getId(),
                formulaireOffreStageDTO.getTitle(),
                formulaireOffreStageDTO.getEntrepriseName(),
                formulaireOffreStageDTO.getEmployerName(),
                formulaireOffreStageDTO.getEmail(),
                formulaireOffreStageDTO.getWebsite(),
                formulaireOffreStageDTO.getLocation(),
                formulaireOffreStageDTO.getSalary(),
                formulaireOffreStageDTO.getDescription(),
                createur.get());
        FormulaireOffreStage savedForm = offreStageRepository.save(formulaireOffreStage);
        return modelMapper.map(savedForm, FormulaireOffreStageDTO.class);
    }

    @Transactional
    public void assignerOffre(long etudiantId, long offreStageId) {
        if(!etudiantIdEtOffreStageIdValides(etudiantId, offreStageId)) {
            return;
        }

        Etudiant etudiant = (Etudiant) utilisateurRepository.findById(etudiantId).get();
        OffreStage offreStage = offreStageRepository.findById(offreStageId).get();
        etudiant.getOffres().add(offreStage);
        offreStage.getEtudiants().add(etudiant);
        utilisateurRepository.save(etudiant);
    }

    private boolean etudiantIdEtOffreStageIdValides(long etudiantId, long offreStageId) {
        if(!utilisateurRepository.existsById(etudiantId) || !offreStageRepository.existsById(offreStageId)){
            return false;
        }

        return utilisateurRepository.findById(etudiantId).get() instanceof Etudiant;
    }
}
