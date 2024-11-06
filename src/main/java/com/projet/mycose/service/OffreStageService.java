package com.projet.mycose.service;

import com.projet.mycose.dto.*;
import com.projet.mycose.modele.*;
import com.projet.mycose.modele.auth.Role;
import com.projet.mycose.repository.*;
import com.projet.mycose.modele.FichierOffreStage;
import com.projet.mycose.modele.FormulaireOffreStage;
import com.projet.mycose.modele.OffreStage;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.time.Year;
import java.util.*;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class OffreStageService {
    private final OffreStageRepository offreStageRepository;
    private final ModelMapper modelMapper;
    private final Validator validator;
    private final UtilisateurService utilisateurService;
    private final UtilisateurRepository utilisateurRepository;
    private final FormulaireOffreStageRepository formulaireOffreStageRepository;
    private final FichierOffreStageRepository ficherOffreStageRepository;
    private static final int LIMIT_PER_PAGE = 10;
    private final EtudiantRepository etudiantRepository;
    private final EtudiantOffreStagePriveeRepository etudiantOffreStagePriveeRepository;

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

        fichierOffreStageDTO.setAnnee(fichierOffreStage.getAnnee().getValue());

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

        fichierOffreStage.setAnnee(Year.of(dto.getAnnee()));

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

        formulaireOffreStageDTO.setAnnee(formulaireOffreStage.getAnnee().getValue());

        if (formulaireOffreStage.getCreateur() != null) {
            formulaireOffreStageDTO.setCreateur_id(formulaireOffreStage.getCreateur().getId());
        }

        return formulaireOffreStageDTO;
    }

    public FormulaireOffreStage convertToEntity(FormulaireOffreStageDTO dto) {
        FormulaireOffreStage formulaireOffreStage = modelMapper.map(dto, FormulaireOffreStage.class);

        formulaireOffreStage.setAnnee(Year.of(dto.getAnnee()));

        if (dto.getCreateur_id() != null) {
            Utilisateur createur = utilisateurRepository.findById(dto.getCreateur_id())
                    .orElseThrow(() -> new EntityNotFoundException("Utilisateur not found with ID: " + dto.getCreateur_id()));
            formulaireOffreStage.setCreateur(createur);
        } else {
            throw new IllegalArgumentException("createur_id cannot be null");
        }

        return formulaireOffreStage;
    }

    @Transactional
    public FichierOffreStageDTO saveFile(@Valid UploadFicherOffreStageDTO uploadFicherOffreStageDTO) throws ConstraintViolationException, IOException {

        UtilisateurDTO utilisateurDTO = utilisateurService.getMe();
        Long createur_id = utilisateurDTO.getId();

        FichierOffreStageDTO fichierOffreStageDTO = new FichierOffreStageDTO(uploadFicherOffreStageDTO, createur_id);

        checkYear(fichierOffreStageDTO);

        // Si l'utilisateur est un employeur, on prend directement le champ entrepriseName de son entité
        // Sinon, s'il s'agit d'un gestionnaire de stage, on prend le champ entrepriseName du formulaire
        // Sinon, on renvoit une erreur
        if (utilisateurDTO.getRole() == Role.EMPLOYEUR) {
            fichierOffreStageDTO.setEntrepriseName(((EmployeurDTO) utilisateurDTO).getEntrepriseName());
            fichierOffreStageDTO.setVisibility(OffreStage.Visibility.UNDEFINED);
        } else if (utilisateurDTO.getRole() == Role.GESTIONNAIRE_STAGE) {
            if (uploadFicherOffreStageDTO.getEntrepriseName() == null) {
                throw new IllegalArgumentException("entrepriseName cannot be null");
            }
            fichierOffreStageDTO.setEntrepriseName(uploadFicherOffreStageDTO.getEntrepriseName());
            fichierOffreStageDTO.setStatus(OffreStage.Status.ACCEPTED);
            if (uploadFicherOffreStageDTO.getProgramme() != Programme.NOT_SPECIFIED) {
                fichierOffreStageDTO.setProgramme(uploadFicherOffreStageDTO.getProgramme());
                if (uploadFicherOffreStageDTO.getEtudiantsPrives() != null) {
                    fichierOffreStageDTO.setVisibility(OffreStage.Visibility.PRIVATE);
                } else {
                    fichierOffreStageDTO.setVisibility(OffreStage.Visibility.PUBLIC);
                }
            } else {
                throw new IllegalArgumentException("Programme or etudiantsPrives must be provided when uploaded by a gestionnaire de stage");
            }
        } else {
            throw new IllegalArgumentException("Utilisateur n'est pas un employeur ou un gestionnaire de stage");
        }

        Set<ConstraintViolation<FichierOffreStageDTO>> violations = validator.validate(fichierOffreStageDTO);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }

        FichierOffreStage fichierOffreStage = convertToEntity(fichierOffreStageDTO);

        ficherOffreStageRepository.save(fichierOffreStage);

        if (fichierOffreStage.getVisibility() == OffreStage.Visibility.PRIVATE) {
            associateEtudiantsPrives(fichierOffreStage, uploadFicherOffreStageDTO.getEtudiantsPrives());
        }

        return convertToDTO(fichierOffreStage);
    }

    @Transactional
    public FormulaireOffreStageDTO saveForm(FormulaireOffreStageDTO formulaireOffreStageDTO) throws AccessDeniedException {
        UtilisateurDTO utilisateurDTO = utilisateurService.getMe();
        Long createur_id = utilisateurDTO.getId();

        checkYear(formulaireOffreStageDTO);

        if (utilisateurDTO.getRole() != Role.EMPLOYEUR && utilisateurDTO.getRole() != Role.GESTIONNAIRE_STAGE) {
            throw new AccessDeniedException("Utilisateur n'est pas un employeur");
        }

        if (utilisateurDTO.getRole() == Role.EMPLOYEUR) {
            formulaireOffreStageDTO.setEntrepriseName(((EmployeurDTO) utilisateurDTO).getEntrepriseName());
            formulaireOffreStageDTO.setVisibility(OffreStage.Visibility.UNDEFINED);
        } else if (utilisateurDTO.getRole() == Role.GESTIONNAIRE_STAGE) {
            formulaireOffreStageDTO.setStatus(OffreStage.Status.ACCEPTED);
            formulaireOffreStageDTO.setVisibility(OffreStage.Visibility.UNDEFINED);
            if (formulaireOffreStageDTO.getProgramme() != Programme.NOT_SPECIFIED) {
                formulaireOffreStageDTO.setProgramme(formulaireOffreStageDTO.getProgramme());
                if (formulaireOffreStageDTO.getEtudiantsPrives() != null) {
                    formulaireOffreStageDTO.setVisibility(OffreStage.Visibility.PRIVATE);
                } else {
                    formulaireOffreStageDTO.setVisibility(OffreStage.Visibility.PUBLIC);
                }
            } else {
                throw new IllegalArgumentException("Programme or etudiantsPrives must be provided when uploaded by a gestionnaire de stage");
            }
        }

        formulaireOffreStageDTO.setCreateur_id(createur_id);

        FormulaireOffreStage formulaireOffreStage = convertToEntity(formulaireOffreStageDTO);

        FormulaireOffreStage savedForm = formulaireOffreStageRepository.save(formulaireOffreStage);

        if (formulaireOffreStage.getVisibility() == OffreStage.Visibility.PRIVATE) {
            associateEtudiantsPrives(formulaireOffreStage, formulaireOffreStageDTO.getEtudiantsPrives());
        }

        return convertToDTO(savedForm);
    }

    private void checkYear(OffreStageDTO offreStageDTO) {
        if (!getFutureYears().contains(offreStageDTO.getAnnee())) {
            throw new IllegalArgumentException("Annee must be in the future");
        }
    }

    private OffreStage associateEtudiantsPrives(OffreStage offreStage, List<Long> etudiantsPrives) {
        List<Etudiant> etudiants = etudiantRepository.findAllById(etudiantsPrives);
        for (Etudiant etudiant : etudiants) {
            EtudiantOffreStagePrivee association = new EtudiantOffreStagePrivee();
            association.setEtudiant(etudiant);
            association.setOffreStage(offreStage);
            etudiantOffreStagePriveeRepository.save(association);
        }
        return offreStage;
    }

    public List<OffreStageAvecUtilisateurInfoDTO> getWaitingOffreStage(int page) {
        Optional<List<OffreStage>> optionalOffreStageList = offreStageRepository.getOffreStageByStatusEquals(OffreStage.Status.WAITING,
                PageRequest.of(page - 1, LIMIT_PER_PAGE));
        if (optionalOffreStageList.isEmpty()) {
            return new ArrayList<>();
        }
        List<OffreStage> offreStages = optionalOffreStageList.get();

        return offreStages.stream().map(OffreStageAvecUtilisateurInfoDTO::toDto).toList();
    }
    public Integer getAmountOfPages() {
        long amountOfRows = offreStageRepository.countByStatus(OffreStage.Status.WAITING);
        if (amountOfRows == 0)
            return 0;
        int nombrePages = (int) Math.floor((double) amountOfRows / LIMIT_PER_PAGE);
        if (amountOfRows % 10 > 0) {
            nombrePages++;
        }
        return nombrePages;
    }
    public OffreStageAvecUtilisateurInfoDTO getOffreStageWithUtilisateurInfo(Long id) {
        OffreStage offreStage = offreStageRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("OffreStage not found with ID: " + id));
        return OffreStageAvecUtilisateurInfoDTO.toDto(offreStage);
    }
    public List<OffreStageDTO> getAvailableOffreStagesForEtudiant() throws AccessDeniedException {
        EtudiantDTO etudiantDTO = (EtudiantDTO) utilisateurService.getMe();
        return offreStageRepository.findAllByEtudiantNotApplied(etudiantDTO.getId(), etudiantDTO.getProgramme()).stream().map(this::convertToDTO).toList();
    }

    public List<OffreStageDTO> getAvailableOffreStagesForEtudiantFiltered(int page, Integer annee, OffreStage.SessionEcole session, String title) throws AccessDeniedException {
        EtudiantDTO etudiantDTO = (EtudiantDTO) utilisateurService.getMe();
        PageRequest pageRequest = PageRequest.of(page, LIMIT_PER_PAGE);

        if (title == null) {
            title = "";
        }

        return offreStageRepository.findAllByEtudiantNotAppliedFilteredWithTitle(etudiantDTO.getId(), etudiantDTO.getProgramme(), annee, session, title, pageRequest).stream().map(this::convertToDTO).toList();
    }

    public Integer getAmountOfPagesForEtudiantFiltered(Integer year, OffreStage.SessionEcole sessionEcole, String title) throws AccessDeniedException {
        EtudiantDTO etudiantDTO = (EtudiantDTO) utilisateurService.getMe();

        if (title == null) {
            title = "";
        }

        long amountOfRows = offreStageRepository.countByEtudiantIdNotAppliedFilteredWithTitle(etudiantDTO.getId(), etudiantDTO.getProgramme(), year, sessionEcole, title);

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

    public long getTotalWaitingOffreStages() {
        return offreStageRepository.countByStatus(OffreStage.Status.WAITING);
    }
    public void refuseOffreDeStage(Long id, String description) {
        Optional<OffreStage> offreStageOptional = offreStageRepository.findById(id);

        if (offreStageOptional.isEmpty()) {
            throw new EntityNotFoundException("OffreStage not found with ID: " + id);
        }
        OffreStage offreStage = offreStageOptional.get();

        if (offreStage.getStatus() != OffreStage.Status.WAITING) {
            throw new IllegalArgumentException("OffreStage is not waiting");
        }
        offreStage.setStatus(OffreStage.Status.REFUSED);
        offreStage.setStatusDescription(description);
        offreStageRepository.save(offreStage);
    }
    public void acceptOffreDeStage(AcceptOffreDeStageDTO acceptOffreDeStageDTO) {
        Optional<OffreStage> offreStageOptional = offreStageRepository.findById(acceptOffreDeStageDTO.getId());
        if (offreStageOptional.isEmpty()) {
            throw new EntityNotFoundException("OffreStage not found with ID: " + acceptOffreDeStageDTO.getId());
        }
        OffreStage offreStage = offreStageOptional.get();
        if (offreStage.getStatus() != OffreStage.Status.WAITING) {
            throw new IllegalArgumentException("OffreStage is not waiting");
        }
        offreStage.setStatus(OffreStage.Status.ACCEPTED);
        offreStage.setStatusDescription(acceptOffreDeStageDTO.getStatusDescription());
        offreStage.setProgramme(acceptOffreDeStageDTO.getProgramme());
        if (offreStage.getProgramme() != Programme.NOT_SPECIFIED) {
            offreStage.setProgramme(acceptOffreDeStageDTO.getProgramme());
            if (acceptOffreDeStageDTO.getEtudiantsPrives() != null) {
                offreStage.setVisibility(OffreStage.Visibility.PRIVATE);
                associateEtudiantsPrives(offreStage, acceptOffreDeStageDTO.getEtudiantsPrives());
            } else {
                offreStage.setVisibility(OffreStage.Visibility.PUBLIC);
            }
        } else {
            throw new IllegalArgumentException("Programme or etudiantsPrives must be provided when uploaded by a gestionnaire de stage");
        }
        offreStageRepository.save(offreStage);
    }
    public List<EtudiantDTO> getEtudiantsQuiOntAppliquesAUneOffre(List<ApplicationStageAvecInfosDTO> applicationStageDTOList) {
        List<EtudiantDTO> etudiantDTOList = new ArrayList<>();
        if (!applicationStageDTOList.isEmpty()) {
            for (ApplicationStageAvecInfosDTO applicationStageDTO : applicationStageDTOList)
                etudiantDTOList.add(EtudiantDTO.toDTO(etudiantRepository.findEtudiantById(applicationStageDTO.getEtudiant_id())));
        } else
            return null;
        return etudiantDTOList;
    }

    public List<String> getSessions() {
        return new ArrayList<>(Arrays.stream(OffreStage.SessionEcole.values())
                .map(OffreStage.SessionEcole::toString)
                .toList());
    }

    public List<Integer> getFutureYears() {
        return Stream.of(Year.now(), Year.now().plusYears(1), Year.now().plusYears(2), Year.now().plusYears(3), Year.now().plusYears(4))
                .map(Year::getValue)
                .toList();
    }

    private List<OffreStageDTO> listeOffreStageToDTO(List<OffreStage> listeAMapper) {
        List<OffreStageDTO> listeMappee = new ArrayList<>();
        for(OffreStage offreStage : listeAMapper) {
            listeMappee.add(OffreStageDTO.toOffreStageInstaceDTOAll(offreStage));
        }
        return listeMappee;
    }

    public List<OffreStageDTO> getStages(int page) {
        Long idCreateur = utilisateurService.getMyUserId();
        PageRequest pageRequest = PageRequest.of(page, LIMIT_PER_PAGE);

        Page<OffreStage> offresRetourneesEnPages = offreStageRepository.findOffreStageByCreateurId(idCreateur, pageRequest);
        if(offresRetourneesEnPages.isEmpty()) {
            return null;
        }

        return listeOffreStageToDTO(offresRetourneesEnPages.getContent());
    }

    public List<OffreStageDTO> getStagesFiltered(int page, Integer annee, OffreStage.SessionEcole session) {
        Long idCreateur = utilisateurService.getMyUserId();
        PageRequest pageRequest = PageRequest.of(page, LIMIT_PER_PAGE);

        Page<OffreStage> offresRetourneesEnPages = offreStageRepository.findOffreStageByCreateurIdFiltered(idCreateur, annee, session, pageRequest);
        if(offresRetourneesEnPages.isEmpty()) {
            return null;
        }

        return listeOffreStageToDTO(offresRetourneesEnPages.getContent());
    }



    public Integer getAmountOfPagesForCreateur() {
        Long createurId = utilisateurService.getMyUserId();
        long amountOfRows = offreStageRepository.countByCreateurId(createurId);

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