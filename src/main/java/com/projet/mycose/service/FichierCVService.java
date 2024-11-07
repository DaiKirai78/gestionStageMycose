package com.projet.mycose.service;

import com.projet.mycose.exceptions.ResourceNotFoundException;
import com.projet.mycose.modele.FichierCV;
import com.projet.mycose.repository.EtudiantRepository;
import com.projet.mycose.repository.FichierCVRepository;
import com.projet.mycose.dto.FichierCVDTO;
import com.projet.mycose.dto.FichierCVStudInfoDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.modelmapper.ModelMapper;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.*;

@Service
public class FichierCVService {
    private static final int LIMIT_PER_PAGE = 10;
    private final FichierCVRepository fileRepository;
    private final ModelMapper modelMapper;
    private final Validator validator;
    private final EtudiantRepository etudiantRepository;
    private final UtilisateurService utilisateurService;


    public FichierCVService(FichierCVRepository fileRepository, ModelMapper modelMapper, Validator validator, EtudiantRepository etudiantRepository, UtilisateurService utilisateurService) {
        this.fileRepository = fileRepository;
        this.modelMapper = modelMapper;
        this.validator = validator;
        this.etudiantRepository = etudiantRepository;
        this.utilisateurService = utilisateurService;
    }

    // Convert Entity to DTO
    public FichierCVDTO convertToDTO(FichierCV fichierCV) {
        FichierCVDTO fichierCVDTO = modelMapper.map(fichierCV, FichierCVDTO.class);

        // Convert byte[] data to Base64 string
        fichierCVDTO.setFileData(Base64.getEncoder().encodeToString(fichierCV.getData()));

        fichierCVDTO.setEtudiant_id(fichierCV.getEtudiant().getId());

        fichierCVDTO.setStatus(fichierCV.getStatus().toString());

        return fichierCVDTO;
    }

    // Convert DTO to Entity
    public FichierCV convertToEntity(FichierCVDTO dto) {
        FichierCV fichierCV = modelMapper.map(dto, FichierCV.class);

        // Convert Base64 string back to byte[]
        fichierCV.setData(Base64.getDecoder().decode(dto.getFileData()));

        fichierCV.setEtudiant(etudiantRepository.findById(dto.getEtudiant_id()).orElseThrow(() -> new RuntimeException("Etudiant non trouvé")));

        return fichierCV;
    }

    private FichierCVDTO createDTOFromFile(MultipartFile file, Long etudiantId) throws IOException {
        return new FichierCVDTO(file.getOriginalFilename(), Base64.getEncoder().encodeToString(file.getBytes()), etudiantId);
    }


    @Transactional
    public FichierCVDTO saveFile(MultipartFile file) throws ConstraintViolationException, IOException {
        Long etudiant_id = utilisateurService.getMyUserId();

        FichierCVDTO fichierCVDTO = createDTOFromFile(file, etudiant_id);

        Set<ConstraintViolation<FichierCVDTO>> violations = validator.validate(fichierCVDTO);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }

        FichierCV fichierCV = convertToEntity(fichierCVDTO);

        return convertToDTO(fileRepository.save(fichierCV));
    }

    public List<FichierCVStudInfoDTO> getWaitingCv(int page) throws IllegalArgumentException {
        //Pageable commence a 0 mais page va commencer a 1
        page--;

        if(page < 0) {
            throw new IllegalArgumentException("Page commence à 1");
        }

        List<FichierCV> fichierCVS = fileRepository.getFichierCVSByStatusEquals(FichierCV.Status.WAITING,
                PageRequest.of(page, LIMIT_PER_PAGE)).orElse(new ArrayList<>());

        return fichierCVS.stream().map(FichierCVStudInfoDTO::toDto).toList();
    }

    public Integer getAmountOfPages() {
        long amountOfRows = fileRepository.countAllByStatusEquals(FichierCV.Status.WAITING);

        if (amountOfRows == 0)
            return 0;

        int nombrePages = (int) Math.floor((double) amountOfRows / LIMIT_PER_PAGE);

        if (amountOfRows % 10 > 0) {
            // Return ++ parce que floor(13/10) = 1
            // mais il y a 2 page et pas 1
            nombrePages++;
        }

        return nombrePages;
    }

    @Transactional
    public void changeStatus(Long id, FichierCV.Status status, String description) {
        FichierCV fichierCV = fileRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Fichier non trouvé"));
        fichierCV.setStatus(status);
        fichierCV.setStatusDescription(description);
        fileRepository.save(fichierCV);
    }

    public FichierCVDTO getFile(Long id) {
         FichierCV fichierCV = fileRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Fichier non trouvé"));
         return convertToDTO(fichierCV);
    }

    public FichierCVDTO getCurrentCVDTO() {
        return convertToDTO(getCurrentCV());
    }

    public FichierCV getCurrentCV() {
        Long etudiant_id = utilisateurService.getMyUserId();
        return fileRepository.getCurrentCvByEtudiant_id(etudiant_id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Fichier non trouvé"));
    }

    public FichierCVDTO getCurrentCVByEtudiantID(Long etudiant_id) {
        return convertToDTO(fileRepository.getCurrentCvByEtudiant_id(etudiant_id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Fichier non trouvé")));
    }

    public FichierCVDTO getCurrentCV_returnNullIfEmpty(Long etudiant_id) {
        Optional<FichierCV> fichierCV = fileRepository.
                getCurrentCvByEtudiant_id(etudiant_id);
        return fichierCV.map(this::convertToDTO).orElse(null);
    }

    @Transactional
    public FichierCVDTO deleteCurrentCV() {
        FichierCV fichierCV = getCurrentCV();
        fichierCV.setStatus(FichierCV.Status.DELETED);
        return convertToDTO(fileRepository.save(fichierCV));
    }

    public long getTotalWaitingCVs() {
        return fileRepository.countAllByStatusEquals(FichierCV.Status.WAITING);
    }
}
