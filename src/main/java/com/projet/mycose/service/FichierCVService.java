package com.projet.mycose.service;

import com.projet.mycose.modele.FichierCV;
import com.projet.mycose.repository.EtudiantRepository;
import com.projet.mycose.repository.FichierCVRepository;
import com.projet.mycose.service.dto.FichierCVDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.modelmapper.ModelMapper;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
public class FichierCVService {
    private static final int LIMIT_PER_PAGE = 10;
    private final FichierCVRepository fileRepository;
    private final ModelMapper modelMapper;
    private final Validator validator;
    private final EtudiantRepository etudiantRepository;


    public FichierCVService(FichierCVRepository fileRepository, ModelMapper modelMapper, Validator validator, EtudiantRepository etudiantRepository) {
        this.fileRepository = fileRepository;
        this.modelMapper = modelMapper;
        this.validator = validator;
        this.etudiantRepository = etudiantRepository;
    }

    // Convert Entity to DTO
    public FichierCVDTO convertToDTO(FichierCV fichierCV) {
        FichierCVDTO fichierCVDTO = modelMapper.map(fichierCV, FichierCVDTO.class);

        // Convert byte[] data to Base64 string
        fichierCVDTO.setFileData(Base64.getEncoder().encodeToString(fichierCV.getData()));

        fichierCVDTO.setEtudiant_id(fichierCV.getEtudiant().getId());

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


    public FichierCVDTO saveFile(MultipartFile file, Long etudiant_id) throws ConstraintViolationException, IOException {

        FichierCVDTO fichierCVDTO = new FichierCVDTO();

        fichierCVDTO.setFilename(file.getOriginalFilename());
        fichierCVDTO.setFileData(Base64.getEncoder().encodeToString(file.getBytes()));
        fichierCVDTO.setEtudiant_id(etudiant_id);

        Set<ConstraintViolation<FichierCVDTO>> violations = validator.validate(fichierCVDTO);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }

        FichierCV fichierCV = convertToEntity(fichierCVDTO);
        System.out.println(fichierCV.toString());

        return convertToDTO(fileRepository.save(fichierCV));
    }

    public List<FichierCVDTO> getWaitingCv(int page) {
        Optional<List<FichierCV>> fichierCVSOptional = fileRepository.getFichierCVSByStatusEquals(FichierCV.Status.WAITING,
                PageRequest.of(page, LIMIT_PER_PAGE));

        if (fichierCVSOptional.isEmpty()) {
            return new ArrayList<>();
        }

        List<FichierCV> fichierCVS = fichierCVSOptional.get();

        return fichierCVS.stream().map(this::convertToDTO).toList();
    }

    public Integer getAmountOfPages() {
        long amountOfRows = fileRepository.count();

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

    public void changeStatus(Long id, FichierCV.Status status, String description) throws ChangeSetPersister.NotFoundException {
        Optional<FichierCV> fichierCVOptional = fileRepository.findById(id);

        if (fichierCVOptional.isEmpty())
            throw new ChangeSetPersister.NotFoundException();

        FichierCV fichierCV = fichierCVOptional.get();
        fichierCV.setStatus(status);
        fichierCV.setStatusDescription(description);
        fileRepository.save(fichierCV);
    }

    public FichierCVDTO getFile(Long id) {
         FichierCV fichierCV = fileRepository.findById(id).orElseThrow(() -> new RuntimeException("Fichier non trouvé"));
         return convertToDTO(fichierCV);
    }

    public FichierCVDTO getCurrentCV(Long etudiant_id) {
        FichierCV fichierCV = fileRepository.getCurrentCvByEtudiant_id(etudiant_id).orElseThrow(() -> new RuntimeException("Fichier non trouvé"));
        return convertToDTO(fichierCV);
    }

    public FichierCVDTO getCurrentCV_returnNullIfEmpty(Long etudiant_id) {
        Optional<FichierCV> fichierCV = fileRepository.
                getCurrentCvByEtudiant_id(etudiant_id);
        return fichierCV.isPresent() ?
                convertToDTO(fichierCV.get()) :
                null;
    }

    public FichierCVDTO deleteCurrentCV(Long id) {
        FichierCV fichierCV = fileRepository.getCurrentCvByEtudiant_id(id).orElseThrow(() -> new RuntimeException("Fichier non trouvé"));
        fichierCV.setStatus(FichierCV.Status.DELETED);
        return convertToDTO(fileRepository.save(fichierCV));
    }
}
