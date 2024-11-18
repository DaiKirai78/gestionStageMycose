package com.projet.mycose.service;

import com.projet.mycose.dto.*;
import com.projet.mycose.exceptions.AuthenticationException;
import com.projet.mycose.exceptions.ResourceNotFoundException;
import com.projet.mycose.modele.*;
import com.projet.mycose.modele.auth.Role;
import com.projet.mycose.modele.utils.SessionEcoleUtil;
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
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
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
    private final FichierOffreStageRepository fichierOffreStageRepository;

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

        if (utilisateurDTO.getRole() != Role.EMPLOYEUR && utilisateurDTO.getRole() != Role.GESTIONNAIRE_STAGE) {
            throw new AccessDeniedException("Utilisateur n'est pas un employeur");
        }

        if (utilisateurDTO.getRole() == Role.EMPLOYEUR) {
            formulaireOffreStageDTO.setEntrepriseName(((EmployeurDTO) utilisateurDTO).getEntrepriseName());
            formulaireOffreStageDTO.setEmployerName(((EmployeurDTO) utilisateurDTO).getPrenom() + " " + ((EmployeurDTO) utilisateurDTO).getNom());
            formulaireOffreStageDTO.setVisibility(OffreStage.Visibility.UNDEFINED);
        } else {
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
        Optional<List<OffreStage>> optionalOffreStageList = offreStageRepository.getOffreStageWithStudentInfoByStatusEquals(OffreStage.Status.WAITING,
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

    public List<OffreStageDTO> getAvailableOffreStagesForEtudiantFiltered(int page, Integer annee, OffreStage.SessionEcole session, String title) {
        checkAnneeAndSessionTogether(annee, session);
        EtudiantDTO etudiantDTO;
        try {
            etudiantDTO = (EtudiantDTO) utilisateurService.getMe();
        } catch (AccessDeniedException e) {
            throw new AuthenticationException(HttpStatus.FORBIDDEN, "Authentication error");
        }
        PageRequest pageRequest = PageRequest.of(page, LIMIT_PER_PAGE);

        Year year = (annee != null) ? Year.of(annee) : null;

        if (title == null) {
            title = "";
        }

        return offreStageRepository.findAllByEtudiantNotAppliedFilteredWithTitle(etudiantDTO.getId(), etudiantDTO.getProgramme(), year, session, title, pageRequest).stream().map(this::convertToDTO).toList();
    }

    public Integer getAmountOfPagesForEtudiantFiltered(Integer annee, OffreStage.SessionEcole sessionEcole, String title) {
        checkAnneeAndSessionTogether(annee, sessionEcole);
        EtudiantDTO etudiantDTO;
        try {
            etudiantDTO = (EtudiantDTO) utilisateurService.getMe();
        } catch (AccessDeniedException e) {
            throw new AuthenticationException(HttpStatus.FORBIDDEN, "Authentication error");
        }

        if (title == null) {
            title = "";
        }

        Year year = (annee != null) ? Year.of(annee) : null;

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

    public List<OffreStageDTO> getWaitingOffreStage() {
        Optional<List<OffreStage>> optionalOffreStageList = offreStageRepository.getOffreStagesByStatusEquals(OffreStage.Status.WAITING);
        if (optionalOffreStageList.isEmpty()) {
            return new ArrayList<>();
        }
        List<OffreStage> offreStages = optionalOffreStageList.get();

        return offreStages.stream().map(OffreStageDTO::toOffreStageInstanceDTONoData).toList();
    }

    public List<OffreStageDTO> getAcceptedOffreStage() {
        Optional<List<OffreStage>> optionalOffreStageList = offreStageRepository.getOffreStagesByStatusEquals(OffreStage.Status.ACCEPTED);
        if (optionalOffreStageList.isEmpty()) {
            return new ArrayList<>();
        }
        List<OffreStage> offreStages = optionalOffreStageList.get();

        return offreStages.stream().map(OffreStageDTO::toOffreStageInstanceDTONoData).toList();
    }

    public long getTotalWaitingOffresStage() {
        return offreStageRepository.countByStatus(OffreStage.Status.WAITING);
    }

    public void refuseOffreDeStage(Long id, String description) {
        if (!utilisateurService.checkRole(Role.GESTIONNAIRE_STAGE)) {
            throw new AuthenticationException(HttpStatus.FORBIDDEN, "Vous n'avez pas les droits pour refuser une offre de stage");
        }

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
        if (!utilisateurService.checkRole(Role.GESTIONNAIRE_STAGE)) {
            throw new AuthenticationException(HttpStatus.FORBIDDEN, "Vous n'avez pas les droits pour accepter une offre de stage");
        }


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
        } else {
            return null;
            }
        return etudiantDTOList;
    }

    @Deprecated
    public List<String> getSessions() {
        return new ArrayList<>(Arrays.stream(OffreStage.SessionEcole.values())
                .map(OffreStage.SessionEcole::toString)
                .toList());
    }

    @Deprecated
    public List<Integer> getFutureYears() {
        return Stream.of(Year.now(), Year.now().plusYears(1), Year.now().plusYears(2), Year.now().plusYears(3), Year.now().plusYears(4))
                .map(Year::getValue)
                .toList();
    }

    private List<OffreStageDTO> listeOffreStageToDTO(List<OffreStage> listeAMapper) {
        List<OffreStageDTO> listeMappee = new ArrayList<>();
        for(OffreStage offreStage : listeAMapper) {
            listeMappee.add(OffreStageDTO.toOffreStageInstanceDTOAll(offreStage));
        }
        return listeMappee;
    }

    @PreAuthorize("hasAuthority('EMPLOYEUR') or hasAuthority('GESTIONNAIRE_STAGE')")
    public List<OffreStageDTO> getStagesFiltered(int page, Integer annee, OffreStage.SessionEcole session) {
        checkAnneeAndSessionTogether(annee, session);

        Long idCreateur = utilisateurService.getMyUserId();
        PageRequest pageRequest = PageRequest.of(page, LIMIT_PER_PAGE);

        Page<OffreStage> offresRetourneesEnPages = null;
        offresRetourneesEnPages = offreStageRepository.findOffreStageByCreateurIdFiltered(idCreateur, (annee != null) ? Year.of(annee) : null, session, pageRequest);

        if(offresRetourneesEnPages.isEmpty()) {
            return null;
        }

        return listeOffreStageToDTO(offresRetourneesEnPages.getContent());
    }

    @PreAuthorize("hasAuthority('EMPLOYEUR') or hasAuthority('GESTIONNAIRE_STAGE')")
    public Integer getAmountOfPagesForCreateurFiltered(Integer annee, OffreStage.SessionEcole session) {
        checkAnneeAndSessionTogether(annee, session);
        Long createurId = utilisateurService.getMyUserId();
        long amountOfRows = 0;
        amountOfRows = offreStageRepository.countOffreStageByCreateurIdFiltered(createurId, (annee != null) ? Year.of(annee) : null, session);

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

    private static void checkAnneeAndSessionTogether(Integer annee, OffreStage.SessionEcole session) {
        if ((annee != null && session == null) || (annee == null && session != null)) {
            throw new IllegalArgumentException("Session and year must be provided together");
        }
    }


    public EmployeurDTO getEmployeurByOffreStageId(Long offreStageId) {
        Employeur employeur = offreStageRepository.findEmployeurByOffreStageId(offreStageId);
        if (employeur == null)
            throw new ResourceNotFoundException("Aucun employeur associé à l'offre de stage id " + offreStageId + " n'existe.");
        return EmployeurDTO.toDTO(employeur);
    }

    public SessionInfoDTO getNextSession() {
        return SessionEcoleUtil.getSessionInfo(LocalDateTime.now());
    }

    public List<SessionInfoDTO> getAllSessions() {
        return offreStageRepository.findDistinctSemesterAndYearAll();
    }

    @PreAuthorize("hasAuthority('EMPLOYEUR') or hasAuthority('GESTIONNAIRE_STAGE')")
    public List<SessionInfoDTO> getSessionsForCreateur() {
        Long createurId = utilisateurService.getMyUserId();
        return offreStageRepository.findDistinctSemesterAndYearByCreateurId(createurId);
    }

    public Page<OffreStageDTO> getAllOffreStagesForEtudiantFiltered(int pageNumber, Integer annee, OffreStage.SessionEcole sessionEcole, String title) {
        checkAnneeAndSessionTogether(annee, sessionEcole);
        EtudiantDTO etudiantDTO;
        try {
            etudiantDTO = (EtudiantDTO) utilisateurService.getMe();
        } catch (AccessDeniedException e) {
            throw new AuthenticationException(HttpStatus.FORBIDDEN, "Authentication error");
        }
        PageRequest pageRequest = PageRequest.of(pageNumber, LIMIT_PER_PAGE);

        Year year = (annee != null) ? Year.of(annee) : null;

        if (title == null) {
            title = "";
        }
        return offreStageRepository.findAllByEtudiantFilteredWithTitle(etudiantDTO.getId(), etudiantDTO.getProgramme(), year, sessionEcole, title, pageRequest).map(this::convertToDTO);
    }

    public FichierOffreStageDTO updateOffreStage(UploadFicherOffreStageDTO uploadFicherOffreStageDTO, Long offreStageId) throws IOException {
        UtilisateurDTO utilisateurDTO = utilisateurService.getMe();
        Long createur_id = utilisateurDTO.getId();

        Optional<FichierOffreStage> optionalFichierOffreStage = ficherOffreStageRepository.findById(offreStageId);
       FichierOffreStage fichierOffreStage = optionalFichierOffreStage.orElseThrow(() -> new ResourceNotFoundException("FichierOffreStage not found with ID: " + offreStageId));

        if (!fichierOffreStage.getCreateur().getId().equals(createur_id)) {
            throw new AccessDeniedException("Vous n'avez pas les droits pour modifier cette offre de stage");
        }

        if (uploadFicherOffreStageDTO.getEntrepriseName() != null) {
            fichierOffreStage.setEntrepriseName(uploadFicherOffreStageDTO.getEntrepriseName());
        }

        if (uploadFicherOffreStageDTO.getTitle() != null) {
            fichierOffreStage.setTitle(uploadFicherOffreStageDTO.getTitle());
        }

        if (uploadFicherOffreStageDTO.getFile() != null) {
            fichierOffreStage.setData(uploadFicherOffreStageDTO.getFile().getBytes());
            fichierOffreStage.setFilename(uploadFicherOffreStageDTO.getFile().getOriginalFilename());
        }

        return convertToDTO(fichierOffreStageRepository.save(fichierOffreStage));
    }

    public FormulaireOffreStageDTO updateOffreStage(FormulaireOffreStageDTO formulaireOffreStageDTO, Long offreStageId) throws AccessDeniedException {
        UtilisateurDTO utilisateurDTO = utilisateurService.getMe();
        Long createur_id = utilisateurDTO.getId();

        Optional<FormulaireOffreStage> optionalFormulaireOffreStage = formulaireOffreStageRepository.findById(offreStageId);
        FormulaireOffreStage formulaireOffreStage = optionalFormulaireOffreStage.orElseThrow(() -> new ResourceNotFoundException("FormulaireOffreStage not found with ID: " + offreStageId));

        if (!formulaireOffreStage.getCreateur().getId().equals(createur_id)) {
            throw new AccessDeniedException("Vous n'avez pas les droits pour modifier cette offre de stage");
        }

        if (formulaireOffreStageDTO.getEntrepriseName() != null) {
            formulaireOffreStage.setEntrepriseName(formulaireOffreStageDTO.getEntrepriseName());
        }

        if (formulaireOffreStageDTO.getEmployerName() != null) {
            formulaireOffreStage.setEmployerName(formulaireOffreStageDTO.getEmployerName());
        }

        if (formulaireOffreStageDTO.getEmail() != null) {
            formulaireOffreStage.setEmail(formulaireOffreStageDTO.getEmail());
        }

        if (formulaireOffreStageDTO.getWebsite() != null) {
            formulaireOffreStage.setWebsite(formulaireOffreStageDTO.getWebsite());
        }


        if (formulaireOffreStageDTO.getTitle() != null) {
            formulaireOffreStage.setTitle(formulaireOffreStageDTO.getTitle());
        }

        if (formulaireOffreStageDTO.getLocation() != null) {
            formulaireOffreStage.setLocation(formulaireOffreStageDTO.getLocation());
        }

        if (formulaireOffreStageDTO.getSalary() != null) {
            formulaireOffreStage.setSalary(formulaireOffreStageDTO.getSalary());
        }

        if (formulaireOffreStageDTO.getDescription() != null) {
            formulaireOffreStage.setDescription(formulaireOffreStageDTO.getDescription());
        }

        //TODO: Ajouter les nouvelles variables de Sam

        return convertToDTO(formulaireOffreStageRepository.save(formulaireOffreStage));
    }
}