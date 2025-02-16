package com.projet.mycose.service;

import com.projet.mycose.dto.*;
import com.projet.mycose.exceptions.AuthenticationException;
import com.projet.mycose.exceptions.ResourceNotFoundException;
import com.projet.mycose.modele.*;
import com.projet.mycose.modele.auth.Credentials;
import com.projet.mycose.modele.auth.Role;
import com.projet.mycose.modele.utils.SessionEcoleUtil;
import com.projet.mycose.repository.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.hibernate.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OffreStageServiceTest {

    private static final int LIMIT_PER_PAGE = 10;
    @Mock
    private OffreStageRepository offreStageRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private Validator validator;

    @Mock
    private UtilisateurService utilisateurService;

    @Mock
    private UtilisateurRepository utilisateurRepository;
    @Mock
    private EtudiantRepository etudiantRepository;

    @Mock
    private FormulaireOffreStageRepository formulaireOffreStageRepository;

    @Mock
    private FichierOffreStageRepository ficherOffreStageRepository;

    @Mock
    private MultipartFile file;

    @Mock
    private EtudiantOffreStagePriveeRepository etudiantOffreStagePriveeRepository;

    @InjectMocks
    private OffreStageService offreStageService;

    private UtilisateurDTO employeurDTO;
    private UtilisateurDTO gestionnaireDTO;
    private UploadFicherOffreStageDTO uploadFicherOffreStageDTO;
    private FichierOffreStageDTO fichierOffreStageDTO;
    private FichierOffreStage fichierOffreStage;
    private FormulaireOffreStageDTO formulaireOffreStageDTO;
    private FormulaireOffreStage formulaireOffreStage;
    private Employeur employeur;
    private Etudiant etudiant;
    private GestionnaireStage gestionnaire;


    private List<Long> etudiantsPrivesIds;
    private List<Etudiant> etudiantsPrives;


    private AcceptOffreDeStageDTO acceptDtoPrivate;
    private AcceptOffreDeStageDTO acceptDtoPublic;
    private AcceptOffreDeStageDTO acceptDtoNonExistent;
    private AcceptOffreDeStageDTO acceptDtoInvalidStatus;
    private AcceptOffreDeStageDTO acceptDtoProgramNotSpecified;

    private Clock fixedClock;

    private static final String SAMPLE_DATA = "sampleData";
    private static final String BASE64_SAMPLE_DATA = "c2FtcGxlRGF0YQ==";
    private static final String VALID_TOKEN = "validToken";
    private static final String INVALID_TOKEN = "invalidRoleToken";

    @BeforeEach
    public void setUp() {

        employeurDTO = EmployeurDTO.empty();
        employeurDTO.setId(1L);
        employeurDTO.setRole(Role.EMPLOYEUR);

        gestionnaireDTO = GestionnaireStageDTO.empty();
        gestionnaireDTO.setId(2L);
        gestionnaireDTO.setRole(Role.GESTIONNAIRE_STAGE);

        uploadFicherOffreStageDTO = new UploadFicherOffreStageDTO();
        uploadFicherOffreStageDTO.setFile(file);
        uploadFicherOffreStageDTO.setEntrepriseName("Sample Entreprise");

        fichierOffreStageDTO = new FichierOffreStageDTO();
        fichierOffreStageDTO.setFileData(BASE64_SAMPLE_DATA); // Base64 for "sampleData"
        fichierOffreStageDTO.setCreateur_id(1L);
        fichierOffreStageDTO.setEntrepriseName("Sample Entreprise");
        fichierOffreStageDTO.setSession(OffreStage.SessionEcole.AUTOMNE);
        fichierOffreStageDTO.setAnnee(2024);

        fichierOffreStage = new FichierOffreStage();
        fichierOffreStage.setId(1L);
        fichierOffreStage.setData(SAMPLE_DATA.getBytes());
        fichierOffreStage.setEntrepriseName("Sample Entreprise");
        fichierOffreStage.setAnnee(Year.of(2024));

        formulaireOffreStageDTO = new FormulaireOffreStageDTO();
        formulaireOffreStageDTO.setCreateur_id(1L);
        formulaireOffreStageDTO.setSession(OffreStage.SessionEcole.AUTOMNE);
        formulaireOffreStageDTO.setAnnee(2024);

        formulaireOffreStage = new FormulaireOffreStage();
        formulaireOffreStage.setId(2L);
        formulaireOffreStage.setCreateur(new Employeur());
        formulaireOffreStage.setAnnee(Year.of(2024));

        etudiant = Etudiant.builder()
                .id(1L)
                .prenom("Roberto")
                .nom("Berrios")
                .numeroDeTelephone("438-508-3245")
                .courriel("roby@gmail.com")
                .motDePasse("Roby123$")
                .programme(Programme.TECHNIQUE_INFORMATIQUE)
                .build();

        employeur = Employeur.builder()
                .id(1L)
                .prenom("Jane")
                .nom("Smith")
                .numeroDeTelephone("098-765-4321")
                .courriel("jane.smith@techcorp.com")
                .motDePasse("securePass!")
                .entrepriseName("TechCorp")
                .build();

        formulaireOffreStage.setCreateur(employeur);
        fichierOffreStage.setCreateur(employeur);

        gestionnaire = GestionnaireStage.builder()
                .id(2L)
                .prenom("John")
                .nom("Doe")
                .numeroDeTelephone("123-456-7890")
                .courriel("elie@gmail.com")
                .motDePasse("JohnDoe123$")
                .build();

        Etudiant etudiant2 = new Etudiant();
        etudiant2.setId(2L);
        Etudiant etudiant3 = new Etudiant();
        etudiant3.setId(3L);
        etudiantsPrives = Arrays.asList(etudiant, etudiant2, etudiant3);
        etudiantsPrivesIds = Arrays.asList(1L, 2L, 3L);



        // Initialize AcceptOffreDeStageDTO for PRIVATE visibility
        acceptDtoPrivate = new AcceptOffreDeStageDTO();
        acceptDtoPrivate.setId(1L);
        acceptDtoPrivate.setStatusDescription("Approved for the position");
        acceptDtoPrivate.setProgramme(Programme.GENIE_LOGICIEL);
        etudiantsPrivesIds = Arrays.asList(10L, 20L);
        acceptDtoPrivate.setEtudiantsPrives(etudiantsPrivesIds);

        // Initialize AcceptOffreDeStageDTO for PUBLIC visibility
        acceptDtoPublic = new AcceptOffreDeStageDTO();
        acceptDtoPublic.setId(1L);
        acceptDtoPublic.setStatusDescription("Approved for the position");
        acceptDtoPublic.setProgramme(Programme.GENIE_LOGICIEL);
        acceptDtoPublic.setEtudiantsPrives(null);

        // Initialize AcceptOffreDeStageDTO for non-existent OffreStage
        acceptDtoNonExistent = new AcceptOffreDeStageDTO();
        acceptDtoNonExistent.setId(99L);
        acceptDtoNonExistent.setStatusDescription("Approved for the position");
        acceptDtoNonExistent.setProgramme(Programme.GENIE_LOGICIEL);
        acceptDtoNonExistent.setEtudiantsPrives(etudiantsPrivesIds);

        // Initialize AcceptOffreDeStageDTO for invalid status (non-WAITING)
        acceptDtoInvalidStatus = new AcceptOffreDeStageDTO();
        acceptDtoInvalidStatus.setId(2L);
        acceptDtoInvalidStatus.setStatusDescription("Approved for the position");
        acceptDtoInvalidStatus.setProgramme(Programme.GENIE_LOGICIEL);
        acceptDtoInvalidStatus.setEtudiantsPrives(etudiantsPrivesIds);

        // Initialize AcceptOffreDeStageDTO with PROGRAMME_NOT_SPECIFIED
        acceptDtoProgramNotSpecified = new AcceptOffreDeStageDTO();
        acceptDtoProgramNotSpecified.setId(1L);
        acceptDtoProgramNotSpecified.setStatusDescription("Approved for the position");
        acceptDtoProgramNotSpecified.setProgramme(Programme.NOT_SPECIFIED);
        acceptDtoProgramNotSpecified.setEtudiantsPrives(etudiantsPrivesIds);
    }


    @Test
    public void shouldSaveFileSuccessfully_WhenUserIsEmployeur() throws IOException {
        // Arrange
        when(utilisateurService.getMe()).thenReturn(employeurDTO);

        // Specific stubbing for this test
        when(modelMapper.map(any(FichierOffreStageDTO.class), eq(FichierOffreStage.class))).thenReturn(fichierOffreStage);
        when(ficherOffreStageRepository.save(any(FichierOffreStage.class))).thenReturn(fichierOffreStage);
        when(modelMapper.map(fichierOffreStage, FichierOffreStageDTO.class)).thenReturn(fichierOffreStageDTO);
        when(validator.validate(any(FichierOffreStageDTO.class))).thenReturn(Collections.emptySet());
        when(file.getBytes()).thenReturn(SAMPLE_DATA.getBytes());
        when(file.getOriginalFilename()).thenReturn("sampleFile.txt");
        when(utilisateurRepository.findById(1L)).thenReturn(Optional.of(employeur));

        // Act
        FichierOffreStageDTO result = offreStageService.saveFile(uploadFicherOffreStageDTO);

        // Assert
        assertNotNull(result);
        assertEquals(fichierOffreStageDTO.getCreateur_id(), result.getCreateur_id());
        assertEquals(fichierOffreStageDTO.getEntrepriseName(), result.getEntrepriseName());
        verify(ficherOffreStageRepository, times(1)).save(any(FichierOffreStage.class));
    }

    @Test
    public void shouldSaveFileSuccessfully_WhenUserIsGestionnaireStage() throws IOException {
        // Arrange
        uploadFicherOffreStageDTO.setProgramme(Programme.GENIE_LOGICIEL);
        when(utilisateurService.getMe()).thenReturn(gestionnaireDTO);

        // Specific stubbing for this test
        when(modelMapper.map(any(FichierOffreStageDTO.class), eq(FichierOffreStage.class))).thenReturn(fichierOffreStage);
        when(ficherOffreStageRepository.save(any(FichierOffreStage.class))).thenReturn(fichierOffreStage);
        when(modelMapper.map(fichierOffreStage, FichierOffreStageDTO.class)).thenReturn(fichierOffreStageDTO);
        when(validator.validate(any(FichierOffreStageDTO.class))).thenReturn(Collections.emptySet());
        when(file.getBytes()).thenReturn(SAMPLE_DATA.getBytes());
        when(file.getOriginalFilename()).thenReturn("sampleFile.txt");
        when(utilisateurRepository.findById(2L)).thenReturn(Optional.of(new GestionnaireStage()));

        // Act
        FichierOffreStageDTO result = offreStageService.saveFile(uploadFicherOffreStageDTO);

        // Assert
        assertNotNull(result);
        assertEquals(fichierOffreStageDTO.getCreateur_id(), result.getCreateur_id());
        assertEquals(fichierOffreStageDTO.getEntrepriseName(), result.getEntrepriseName());
        verify(ficherOffreStageRepository, times(1)).save(any(FichierOffreStage.class));
    }

    @Test
    public void shouldThrowIllegalArgumentException_WhenUserIsNotEmployeurOrGestionnaire() throws IOException {
        // Arrange
        String token = INVALID_TOKEN;
        UtilisateurDTO invalidUser = new EtudiantDTO();
        invalidUser.setId(3L);
        invalidUser.setRole(Role.ETUDIANT); // Assuming ETUDIANT is another role
        when(utilisateurService.getMe()).thenReturn(invalidUser);
        when(file.getBytes()).thenReturn(SAMPLE_DATA.getBytes());
        when(file.getOriginalFilename()).thenReturn("sampleFile.txt");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            offreStageService.saveFile(uploadFicherOffreStageDTO);
        });

        assertEquals("Utilisateur n'est pas un employeur ou un gestionnaire de stage", exception.getMessage());
        verify(ficherOffreStageRepository, never()).save(any(FichierOffreStage.class));
    }

    @Test
    public void shouldThrowConstraintViolationException_WhenValidationFails() throws AccessDeniedException, IOException {
        // Arrange
        String token = VALID_TOKEN;
        when(utilisateurService.getMe()).thenReturn(employeurDTO);

        // Specific stubbing for this test

        Set<ConstraintViolation<FichierOffreStageDTO>> violations = new HashSet<>();
        ConstraintViolation<FichierOffreStageDTO> violation = mock(ConstraintViolation.class);
        when(violation.getMessage()).thenReturn("Validation error");
        violations.add(violation);
        when(validator.validate(any(FichierOffreStageDTO.class))).thenReturn(violations);
        when(file.getBytes()).thenReturn(SAMPLE_DATA.getBytes());
        when(file.getOriginalFilename()).thenReturn("sampleFile.txt");

        // Act & Assert
        ConstraintViolationException exception = assertThrows(ConstraintViolationException.class, () -> {
            offreStageService.saveFile(uploadFicherOffreStageDTO);
        });

        assertFalse(exception.getConstraintViolations().isEmpty());
        verify(ficherOffreStageRepository, never()).save(any(FichierOffreStage.class));
    }

    @Test
    public void shouldThrowEntityNotFoundException_WhenCreateurNotFound() throws IOException {
        // Arrange
        String token = VALID_TOKEN;
        when(utilisateurService.getMe()).thenReturn(employeurDTO);

        // Specific stubbing for this test
        when(modelMapper.map(any(FichierOffreStageDTO.class), eq(FichierOffreStage.class)))
                .thenThrow(new EntityNotFoundException("Utilisateur not found with ID: 1"));
        when(file.getBytes()).thenReturn(SAMPLE_DATA.getBytes());
        when(file.getOriginalFilename()).thenReturn("sampleFile.txt");

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            offreStageService.saveFile(uploadFicherOffreStageDTO);
        });

        assertEquals("Utilisateur not found with ID: 1", exception.getMessage());
        verify(ficherOffreStageRepository, never()).save(any(FichierOffreStage.class));
    }


    @Test
    public void shouldSaveFormSuccessfully_WhenUserIsEmployeur() throws AccessDeniedException {
        // Arrange
        when(utilisateurService.getMe()).thenReturn(employeurDTO);

        // Mapping DTO to Entity
        when(modelMapper.map(formulaireOffreStageDTO, FormulaireOffreStage.class)).thenReturn(formulaireOffreStage);
        // Mapping Entity back to DTO
        when(modelMapper.map(formulaireOffreStage, FormulaireOffreStageDTO.class)).thenReturn(formulaireOffreStageDTO);

        when(formulaireOffreStageRepository.save(any(FormulaireOffreStage.class))).thenReturn(formulaireOffreStage);

        when(utilisateurRepository.findById(1L)).thenReturn(Optional.of(employeur));

        // Act
        FormulaireOffreStageDTO result = offreStageService.saveForm(formulaireOffreStageDTO);

        // Assert
        assertNotNull(result);
        assertEquals(formulaireOffreStageDTO.getCreateur_id(), result.getCreateur_id());
        verify(formulaireOffreStageRepository, times(1)).save(any(FormulaireOffreStage.class));
        verify(modelMapper, times(1)).map(formulaireOffreStageDTO, FormulaireOffreStage.class);
        verify(modelMapper, times(1)).map(formulaireOffreStage, FormulaireOffreStageDTO.class);
    }

    @Test
    void convertToEntityIllegalArgumentException() {
        // Arrange
        FormulaireOffreStageDTO formulaireOffreStageDTO = new FormulaireOffreStageDTO();
        formulaireOffreStageDTO.setCreateur_id(null);
        when(modelMapper.map(formulaireOffreStageDTO, FormulaireOffreStage.class)).thenReturn(formulaireOffreStage);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            offreStageService.convertToEntity(formulaireOffreStageDTO);
        });

        assertEquals("createur_id cannot be null", exception.getMessage());
        verify(utilisateurRepository, never()).findById(anyLong());
    }

    @Test
    void saveFileNullEntrepriseName() throws IOException {
        when(utilisateurService.getMe()).thenReturn(gestionnaireDTO);
        when(file.getBytes()).thenReturn(SAMPLE_DATA.getBytes());
        when(file.getOriginalFilename()).thenReturn("sampleFile.txt");
        uploadFicherOffreStageDTO.setEntrepriseName(null);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            offreStageService.saveFile(uploadFicherOffreStageDTO);
        });
    }

    @Test
    void saveFileWithEtudiantsPrives() throws IOException {
        // Arrange
        uploadFicherOffreStageDTO.setProgramme(Programme.GENIE_LOGICIEL);
        uploadFicherOffreStageDTO.setEtudiantsPrives(List.of(1L, 2L, 3L));

        FichierOffreStageDTO fichierOffreStageDTO = new FichierOffreStageDTO();
        fichierOffreStageDTO.setCreateur_id(2L);
        fichierOffreStageDTO.setEntrepriseName("Entreprise XYZ");

        FichierOffreStage fichierOffreStage = new FichierOffreStage();
        fichierOffreStage.setVisibility(OffreStage.Visibility.PRIVATE);

        // Mocking utilisateurService.getMe()
        when(utilisateurService.getMe()).thenReturn(gestionnaireDTO);

        // Mocking modelMapper.map for DTO to Entity
        when(modelMapper.map(any(FichierOffreStageDTO.class), eq(FichierOffreStage.class)))
                .thenReturn(fichierOffreStage);

        // Mocking ficherOffreStageRepository.save()
        when(ficherOffreStageRepository.save(any(FichierOffreStage.class)))
                .thenReturn(fichierOffreStage);

        // Mocking modelMapper.map for Entity to DTO
        when(modelMapper.map(fichierOffreStage, FichierOffreStageDTO.class))
                .thenReturn(fichierOffreStageDTO);

        // Mocking validator.validate()
        when(validator.validate(any(FichierOffreStageDTO.class)))
                .thenReturn(Collections.emptySet());

        // Mocking file operations if necessary
        when(file.getBytes()).thenReturn("Sample Data".getBytes());
        when(file.getOriginalFilename()).thenReturn("sampleFile.txt");

        // Mocking utilisateurRepository.findById()
        when(utilisateurRepository.findById(2L))
                .thenReturn(Optional.of(new GestionnaireStage()));

        // Mocking etudiantRepository.findAllById()
        Etudiant etudiant1 = new Etudiant();
        etudiant1.setId(1L);
        Etudiant etudiant2 = new Etudiant();
        etudiant2.setId(2L);
        Etudiant etudiant3 = new Etudiant();
        etudiant3.setId(3L);
        when(etudiantRepository.findAllById(List.of(1L, 2L, 3L)))
                .thenReturn(List.of(etudiant1, etudiant2, etudiant3));

        // Act
        FichierOffreStageDTO result = offreStageService.saveFile(uploadFicherOffreStageDTO);

        // Assert
        assertNotNull(result, "The result should not be null");
        assertEquals(fichierOffreStageDTO.getCreateur_id(), result.getCreateur_id(),
                "Createur ID should match");
        assertEquals(fichierOffreStageDTO.getEntrepriseName(), result.getEntrepriseName(),
                "Entreprise Name should match");

        // Verify that ficherOffreStageRepository.save() was called once
        verify(ficherOffreStageRepository, times(1)).save(any(FichierOffreStage.class));

        // Verify that etudiantRepository.findAllById() was called with the correct IDs
        verify(etudiantRepository, times(1)).findAllById(uploadFicherOffreStageDTO.getEtudiantsPrives());

        // Capture the arguments passed to etudiantOffreStagePriveeRepository.save()
        ArgumentCaptor<EtudiantOffreStagePrivee> captor = ArgumentCaptor.forClass(EtudiantOffreStagePrivee.class);
        verify(etudiantOffreStagePriveeRepository, times(3)).save(captor.capture());

        // Verify each association
        List<EtudiantOffreStagePrivee> savedAssociations = captor.getAllValues();
        assertEquals(3, savedAssociations.size(), "Three associations should be saved");

        for (int i = 0; i < savedAssociations.size(); i++) {
            EtudiantOffreStagePrivee association = savedAssociations.get(i);
            assertEquals(List.of(1L, 2L, 3L).get(i), association.getEtudiant().getId(),
                    "Etudiant ID should match");
            assertEquals(fichierOffreStage, association.getOffreStage(),
                    "OffreStage should match");
        }
    }

    @Test
    public void shouldSaveFormSuccessfully_WhenUserIsGestionnaire() throws AccessDeniedException {
        // Arrange
        when(utilisateurService.getMe()).thenReturn(gestionnaireDTO);

        // Mapping DTO to Entity
        when(modelMapper.map(formulaireOffreStageDTO, FormulaireOffreStage.class)).thenReturn(formulaireOffreStage);
        // Mapping Entity back to DTO
        when(modelMapper.map(formulaireOffreStage, FormulaireOffreStageDTO.class)).thenReturn(formulaireOffreStageDTO);

        when(formulaireOffreStageRepository.save(any(FormulaireOffreStage.class))).thenReturn(formulaireOffreStage);

        when(utilisateurRepository.findById(2L)).thenReturn(Optional.of(gestionnaire));

        // Act
        FormulaireOffreStageDTO result = offreStageService.saveForm(formulaireOffreStageDTO);

        // Assert
        assertNotNull(result);
        assertEquals(formulaireOffreStageDTO.getCreateur_id(), result.getCreateur_id());
        verify(formulaireOffreStageRepository, times(1)).save(any(FormulaireOffreStage.class));
        verify(modelMapper, times(1)).map(formulaireOffreStageDTO, FormulaireOffreStage.class);
        verify(modelMapper, times(1)).map(formulaireOffreStage, FormulaireOffreStageDTO.class);
    }

    @Test
    void saveFileProgrammeNotSpecified() throws IOException {
        // Arrange
        uploadFicherOffreStageDTO.setProgramme(Programme.NOT_SPECIFIED);
        when(utilisateurService.getMe()).thenReturn(gestionnaireDTO);
        when(file.getBytes()).thenReturn(SAMPLE_DATA.getBytes());
        when(file.getOriginalFilename()).thenReturn("sampleFile.txt");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            offreStageService.saveFile(uploadFicherOffreStageDTO);
        });
    }

    @Test
    void saveFormFailsBecauseNoProgrammeOrEtudiants() throws AccessDeniedException {
        // Arrange
        formulaireOffreStageDTO.setProgramme(Programme.NOT_SPECIFIED);
        when(utilisateurService.getMe()).thenReturn(gestionnaireDTO);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            offreStageService.saveForm(formulaireOffreStageDTO);
        });
    }

    @Test
    void saveFormWithEtudiantsPrives() throws AccessDeniedException {
        // Arrange
        formulaireOffreStageDTO.setEntrepriseName("Entreprise ABC");
        formulaireOffreStageDTO.setProgramme(Programme.GENIE_LOGICIEL);
        formulaireOffreStage.setEntrepriseName("Entreprise ABC");
        formulaireOffreStage.setProgramme(Programme.GENIE_LOGICIEL);
        formulaireOffreStage.setCreateur(gestionnaire);
        formulaireOffreStage.setVisibility(OffreStage.Visibility.PRIVATE);
        formulaireOffreStageDTO.setVisibility(OffreStage.Visibility.PRIVATE);
        formulaireOffreStageDTO.setEtudiantsPrives(etudiantsPrivesIds);

        // Mock utilisateurService.getMe() to return gestionnaireDTO
        when(utilisateurService.getMe()).thenReturn(gestionnaireDTO);

        // Mock utilisateurRepository.findById() to return gestionnaire
        when(utilisateurRepository.findById(2L)).thenReturn(Optional.of(gestionnaire));

        // Mock modelMapper.map() to convert DTO to Entity
        when(modelMapper.map(formulaireOffreStageDTO, FormulaireOffreStage.class))
                .thenReturn(formulaireOffreStage);

        // Mock formulaireOffreStageRepository.save() to return the saved entity
        when(formulaireOffreStageRepository.save(any(FormulaireOffreStage.class)))
                .thenReturn(formulaireOffreStage);

        // Mock modelMapper.map() to convert Entity back to DTO
        when(modelMapper.map(formulaireOffreStage, FormulaireOffreStageDTO.class))
                .thenReturn(formulaireOffreStageDTO);

        // Mock etudiantRepository.findAllById() to return the list of Etudiants
        when(etudiantRepository.findAllById(etudiantsPrivesIds))
                .thenReturn(etudiantsPrives);

        // Act
        FormulaireOffreStageDTO result = offreStageService.saveForm(formulaireOffreStageDTO);

        // Assert

        // Verify that the result is not null
        assertNotNull(result, "The returned FormulaireOffreStageDTO should not be null");

        // Verify that the createur_id is set correctly
        assertEquals(formulaireOffreStageDTO.getCreateur_id(), gestionnaireDTO.getId(),
                "The createur_id should match the gestionnaire's ID");

        // Verify that the entrepriseName is set correctly
        assertEquals("Entreprise ABC", result.getEntrepriseName(),
                "The entrepriseName should be correctly set in the DTO");

        // Verify that the programme is set correctly
        assertEquals(Programme.GENIE_LOGICIEL, result.getProgramme(),
                "The programme should be correctly set in the DTO");

        // Verify that visibility is set to PRIVATE
        assertEquals(OffreStage.Visibility.PRIVATE, result.getVisibility(),
                "The visibility should be set to PRIVATE");

        // Verify that formulaireOffreStageRepository.save() was called once
        verify(formulaireOffreStageRepository, times(1))
                .save(any(FormulaireOffreStage.class));

        // Verify that etudiantRepository.findAllById() was called with correct IDs
        verify(etudiantRepository, times(1)).findAllById(etudiantsPrivesIds);

        // Capture the arguments passed to etudiantOffreStagePriveeRepository.save()
        ArgumentCaptor<EtudiantOffreStagePrivee> captor = ArgumentCaptor.forClass(EtudiantOffreStagePrivee.class);
        verify(etudiantOffreStagePriveeRepository, times(etudiantsPrives.size()))
                .save(captor.capture());

        // Retrieve the captured associations
        List<EtudiantOffreStagePrivee> savedAssociations = captor.getAllValues();

        // Assert that the correct number of associations were saved
        assertEquals(etudiantsPrives.size(), savedAssociations.size(),
                "The number of saved associations should match the number of etudiantsPrives");

        // Verify each association
        for (int i = 0; i < etudiantsPrives.size(); i++) {
            EtudiantOffreStagePrivee association = savedAssociations.get(i);
            Etudiant expectedEtudiant = etudiantsPrives.get(i);

            assertEquals(expectedEtudiant.getId(), association.getEtudiant().getId(),
                    "The Etudiant ID in the association should match the expected ID");
            assertEquals(formulaireOffreStage, association.getOffreStage(),
                    "The OffreStage in the association should match the saved OffreStage");
        }
    }


    @Test
    public void shouldThrowAccessDeniedException_WhenUserIsNotEmployeur() throws AccessDeniedException {
        // Arrange
        String token = INVALID_TOKEN;
        UtilisateurDTO invalidUser = new EtudiantDTO();
        invalidUser.setId(3L);
        invalidUser.setRole(Role.ETUDIANT); // Assuming ETUDIANT is another role
        when(utilisateurService.getMe()).thenReturn(invalidUser);

        // Act & Assert
        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> {
            offreStageService.saveForm(formulaireOffreStageDTO);
        });

        assertEquals("Utilisateur n'est pas un employeur", exception.getMessage());
        verify(formulaireOffreStageRepository, never()).save(any(FormulaireOffreStage.class));
    }


    @Test
    public void shouldReturnWaitingOffreStages_WhenThereAreResults() {
        // Arrange
        int page = 1;
        fichierOffreStage.setCreateur(employeur);
        formulaireOffreStage.setCreateur(employeur);
        List<OffreStage> offreStageList = Arrays.asList(fichierOffreStage, formulaireOffreStage);
        when(offreStageRepository.getOffreStageWithStudentInfoByStatusEquals(eq(OffreStage.Status.WAITING), any()))
                .thenReturn(Optional.of(offreStageList));

        try (MockedStatic<OffreStageAvecUtilisateurInfoDTO> mockedStatic = Mockito.mockStatic(OffreStageAvecUtilisateurInfoDTO.class)) {
            // Stub the static toDto method
            mockedStatic.when(() -> OffreStageAvecUtilisateurInfoDTO.toDto(any(OffreStage.class)))
                    .thenAnswer(invocation -> {
                        OffreStage offreStage = invocation.getArgument(0);
                        if (offreStage == null) {
                            throw new NullPointerException("OffreStage is null");
                        }
                        OffreStageAvecUtilisateurInfoDTO dto = new OffreStageAvecUtilisateurInfoDTO();
                        dto.setId(offreStage.getId());
                        dto.setEntrepriseName(offreStage.getEntrepriseName());
                        dto.setCreateur_id(offreStage.getCreateur().getId());
                        return dto;
                    });

            // Act
            List<OffreStageAvecUtilisateurInfoDTO> result = offreStageService.getWaitingOffreStage(page);

            // Assert
            assertNotNull(result, "Result should not be null");
            assertEquals(2, result.size(), "Result size should be 2");

            // Verify repository interaction
            verify(offreStageRepository, times(1))
                    .getOffreStageWithStudentInfoByStatusEquals(eq(OffreStage.Status.WAITING), eq(PageRequest.of(page - 1, 10)));

            // Verify static method invocations
            mockedStatic.verify(() -> OffreStageAvecUtilisateurInfoDTO.toDto(fichierOffreStage), times(1));
            mockedStatic.verify(() -> OffreStageAvecUtilisateurInfoDTO.toDto(formulaireOffreStage), times(1));
        }
    }

    @Test
    public void shouldReturnEmptyList_WhenThereAreNoWaitingOffreStages() {
        // Arrange
        int page = 1;
        when(offreStageRepository.getOffreStageWithStudentInfoByStatusEquals(eq(OffreStage.Status.WAITING), any()))
                .thenReturn(Optional.empty());

        // Act
        List<OffreStageAvecUtilisateurInfoDTO> result = offreStageService.getWaitingOffreStage(page);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(offreStageRepository, times(1)).getOffreStageWithStudentInfoByStatusEquals(OffreStage.Status.WAITING, PageRequest.of(page - 1, 10));
    }


    @Test
    public void shouldReturnZeroPages_WhenNoRowsExist() {
        // Arrange
        when(offreStageRepository.countByStatus(OffreStage.Status.WAITING)).thenReturn(0L);

        // Act
        Integer result = offreStageService.getAmountOfPages();

        // Assert
        assertEquals(0, result);
        verify(offreStageRepository, times(1)).countByStatus(OffreStage.Status.WAITING);
    }

    @Test
    public void shouldReturnCorrectNumberOfPages_WhenRowCountIsExactMultiple() {
        // Arrange
        when(offreStageRepository.countByStatus(OffreStage.Status.WAITING)).thenReturn(20L);

        // Act
        Integer result = offreStageService.getAmountOfPages();

        // Assert
        assertEquals(2, result);
        verify(offreStageRepository, times(1)).countByStatus(OffreStage.Status.WAITING);
    }

    @Test
    public void shouldReturnCorrectNumberOfPages_WhenRowCountIsNonExactMultiple() {
        // Arrange
        when(offreStageRepository.countByStatus(OffreStage.Status.WAITING)).thenReturn(25L);

        // Act
        Integer result = offreStageService.getAmountOfPages();

        // Assert
        assertEquals(3, result);
        verify(offreStageRepository, times(1)).countByStatus(OffreStage.Status.WAITING);
    }



    @Test
    public void shouldReturnAvailableOffreStages_ForEtudiant() throws AccessDeniedException {
        // Arrange
        String token = VALID_TOKEN;
        Long etudiantId = 1L;
        Programme programme = Programme.NOT_SPECIFIED;
        EtudiantDTO etudiantDTO = EtudiantDTO.empty();
        etudiantDTO.setId(etudiantId);
        etudiantDTO.setProgramme(programme);
        when(utilisateurService.getMe()).thenReturn(etudiantDTO); // Changed to getMe(token)

        // Initialize DTOs
        FichierOffreStageDTO fichierOffreStageDTO = new FichierOffreStageDTO();
        fichierOffreStageDTO.setFileData("c2FtcGxlRGF0YQ=="); // Base64 for "sampleData"
        fichierOffreStageDTO.setCreateur_id(1L);
        fichierOffreStageDTO.setEntrepriseName("Sample Entreprise");
        fichierOffreStageDTO.setAnnee(2024);

        FormulaireOffreStageDTO formulaireOffreStageDTO = new FormulaireOffreStageDTO();
        formulaireOffreStageDTO.setCreateur_id(2L);
        formulaireOffreStageDTO.setEntrepriseName("Sample Entreprise");
        formulaireOffreStageDTO.setAnnee(2024);

        // Initialize Entities
        FichierOffreStage fichierOffreStage = new FichierOffreStage();
        fichierOffreStage.setId(1L);
        fichierOffreStage.setData("sampleData".getBytes());
        fichierOffreStage.setEntrepriseName("Sample Entreprise");
        fichierOffreStage.setAnnee(Year.of(2024));

        FormulaireOffreStage formulaireOffreStage = new FormulaireOffreStage();
        formulaireOffreStage.setId(2L);
        formulaireOffreStage.setCreateur(new GestionnaireStage());
        formulaireOffreStage.setEntrepriseName("Sample Entreprise");
        formulaireOffreStage.setAnnee(Year.of(2024));

        // Set OffreStages
        Page<OffreStage> availableOffres = new PageImpl<>(Arrays.asList(fichierOffreStage, formulaireOffreStage));
        when(offreStageRepository.findAllByEtudiantNotAppliedFilteredWithTitle(etudiantId, programme, null, null, "", PageRequest.of(1, LIMIT_PER_PAGE))).thenReturn(availableOffres);

        // Correct stubbing: map to specific DTO classes
        when(modelMapper.map(fichierOffreStage, FichierOffreStageDTO.class)).thenReturn(fichierOffreStageDTO);
        when(modelMapper.map(formulaireOffreStage, FormulaireOffreStageDTO.class)).thenReturn(formulaireOffreStageDTO);

        // Act
        List<OffreStageDTO> result = offreStageService.getAvailableOffreStagesForEtudiantFiltered(1, null, null, null);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(utilisateurService, times(1)).getMe(); // Changed to getMe(token)
        verify(offreStageRepository, times(1)).findAllByEtudiantNotAppliedFilteredWithTitle(etudiantId, programme, null, null, "", PageRequest.of(1, LIMIT_PER_PAGE));
        verify(modelMapper, times(1)).map(fichierOffreStage, FichierOffreStageDTO.class);
        verify(modelMapper, times(1)).map(formulaireOffreStage, FormulaireOffreStageDTO.class);
    }

    @Test
    public void shouldConvertFichierOffreStageToDTO() {
        // Arrange
        when(modelMapper.map(fichierOffreStage, FichierOffreStageDTO.class)).thenReturn(fichierOffreStageDTO);

        // Act
        FichierOffreStageDTO result = offreStageService.convertToDTO(fichierOffreStage);

        // Assert
        assertNotNull(result);
        assertEquals(fichierOffreStageDTO.getCreateur_id(), result.getCreateur_id());
        assertEquals(fichierOffreStageDTO.getEntrepriseName(), result.getEntrepriseName());
        assertEquals(BASE64_SAMPLE_DATA, result.getFileData());
        verify(modelMapper, times(1)).map(fichierOffreStage, FichierOffreStageDTO.class);
    }

    @Test
    public void shouldConvertFichierOffreStageDTOToEntity() {
        // Arrange
        when(modelMapper.map(fichierOffreStageDTO, FichierOffreStage.class)).thenReturn(fichierOffreStage);
        when(utilisateurRepository.findById(1L)).thenReturn(Optional.of(employeur));

        // Act
        FichierOffreStage result = offreStageService.convertToEntity(fichierOffreStageDTO);

        // Assert
        assertNotNull(result);
        assertArrayEquals(SAMPLE_DATA.getBytes(), result.getData());
        assertNotNull(result.getCreateur());
        assertEquals(employeur, result.getCreateur());
        verify(modelMapper, times(1)).map(fichierOffreStageDTO, FichierOffreStage.class);
        verify(utilisateurRepository, times(1)).findById(1L);
    }

    @Test
    public void shouldThrowIllegalArgumentException_WhenConvertToEntityWithNullCreateurId() {
        // Arrange
        FichierOffreStageDTO dto = new FichierOffreStageDTO();
        dto.setCreateur_id(null);
        dto.setFileData(BASE64_SAMPLE_DATA);
        dto.setEntrepriseName("Sample Entreprise");
        when(modelMapper.map(dto, FichierOffreStage.class)).thenReturn(fichierOffreStage);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            offreStageService.convertToEntity(dto);
        });

        assertEquals("createur_id cannot be null", exception.getMessage());
        verify(utilisateurRepository, never()).findById(anyLong());
    }

    @Test
    public void getEtudiantsQuiOntAppliquesAUneOffreSuccesTest() {
        //Arrange
        ApplicationStageAvecInfosDTO applicationStageDTO = new ApplicationStageAvecInfosDTO();
        applicationStageDTO.setEtudiant_id(1L);
        applicationStageDTO.setId(1L);
        applicationStageDTO.setOffreStage_id(1L);
        applicationStageDTO.setStatus(ApplicationStage.ApplicationStatus.ACCEPTED);
        List<ApplicationStageAvecInfosDTO> applicationStageDTOList = new ArrayList<>();
        applicationStageDTOList.add(applicationStageDTO);
        when(etudiantRepository.findEtudiantById(anyLong())).thenReturn(etudiant);

        //Act
        EtudiantDTO etudiantDTO = offreStageService.getEtudiantsQuiOntAppliquesAUneOffre(applicationStageDTOList).getFirst();

        //Assert
        assertEquals(etudiant.getId(), etudiantDTO.getId());
        assertEquals(etudiant.getPrenom(), etudiantDTO.getPrenom());
        assertEquals(etudiant.getNom(), etudiantDTO.getNom());
        assertEquals(etudiant.getCourriel(), etudiantDTO.getCourriel());
        assertEquals(etudiant.getNumeroDeTelephone(), etudiantDTO.getNumeroDeTelephone());
        assertEquals(etudiant.getProgramme(), etudiantDTO.getProgramme());
        assertEquals(etudiant.getRole(), etudiantDTO.getRole());
    }

    @Test
    public void getEtudiantsQuiOntAppliquesAUneOffreListeVideTest() {
        // Arrange
        List<ApplicationStageAvecInfosDTO> applicationStageDTOList = new ArrayList<>();

        // Act
        List<EtudiantDTO> etudiantDTOList = offreStageService.getEtudiantsQuiOntAppliquesAUneOffre(applicationStageDTOList);

        // Assert
        assertNull(etudiantDTOList);
    }

    @Test
    void getTotalWaitingOffreStages_WithWaitingOffresStage() {
        // Arrange
        long expectedCount = 5L;
        when(offreStageRepository.countByStatus(OffreStage.Status.WAITING)).thenReturn(expectedCount);

        // Act
        long actualCount = offreStageService.getTotalWaitingOffresStage();

        // Assert
        assertEquals(expectedCount, actualCount, "The count of waiting OffreStages should match the expected value");
        verify(offreStageRepository, times(1)).countByStatus(OffreStage.Status.WAITING);
    }

    @Test
    void getTotalWaitingOffreStages_NoWaitingOffresStage() {
        // Arrange
        long expectedCount = 0L;
        when(offreStageRepository.countByStatus(OffreStage.Status.WAITING)).thenReturn(expectedCount);

        // Act
        long actualCount = offreStageService.getTotalWaitingOffresStage();

        // Assert
        assertEquals(expectedCount, actualCount, "The count should be zero when there are no waiting OffreStages");
        verify(offreStageRepository, times(1)).countByStatus(OffreStage.Status.WAITING);
    }

    @Test
    void refuseOffreDeStage_Success() {
        // Arrange
        Long offreStageId = 1L;
        String refusalDescription = "Not suitable for the position";
        fichierOffreStage.setStatus(OffreStage.Status.WAITING);

        when(offreStageRepository.findById(offreStageId)).thenReturn(Optional.of(fichierOffreStage));
        when(offreStageRepository.save(any(OffreStage.class))).thenReturn(fichierOffreStage);

        when(utilisateurService.checkRole(Role.GESTIONNAIRE_STAGE)).thenReturn(true);


        // Act
        offreStageService.refuseOffreDeStage(offreStageId, refusalDescription);

        // Assert
        assertEquals(OffreStage.Status.REFUSED, fichierOffreStage.getStatus(), "OffreStage status should be REFUSED");
        assertEquals(refusalDescription, fichierOffreStage.getStatusDescription(), "Status description should be set correctly");
        verify(offreStageRepository, times(1)).findById(offreStageId);
        verify(offreStageRepository, times(1)).save(fichierOffreStage);
    }

    @Test
    void refuseOffreDeStage_OffreStageNotFound() {
        // Arrange
        Long nonExistentId = 99L;
        String refusalDescription = "Not suitable for the position";

        when(offreStageRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        when(utilisateurService.checkRole(Role.GESTIONNAIRE_STAGE)).thenReturn(true);

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            offreStageService.refuseOffreDeStage(nonExistentId, refusalDescription);
        }, "Expected EntityNotFoundException to be thrown");

        assertEquals("OffreStage not found with ID: " + nonExistentId, exception.getMessage(),
                "Exception message should match");
        verify(offreStageRepository, times(1)).findById(nonExistentId);
        verify(offreStageRepository, never()).save(any(OffreStage.class));
    }

    @Test
    void refuseOffreDeStage_InvalidStatus() {
        // Arrange
        Long offreStageId = 2L;
        String refusalDescription = "Already processed";
        fichierOffreStage.setStatus(OffreStage.Status.ACCEPTED);

        when(offreStageRepository.findById(offreStageId)).thenReturn(Optional.of(fichierOffreStage));

        when(utilisateurService.checkRole(Role.GESTIONNAIRE_STAGE)).thenReturn(true);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            offreStageService.refuseOffreDeStage(offreStageId, refusalDescription);
        }, "Expected IllegalArgumentException to be thrown");

        assertEquals("OffreStage is not waiting", exception.getMessage(),
                "Exception message should match");
        verify(offreStageRepository, times(1)).findById(offreStageId);
        verify(offreStageRepository, never()).save(any(OffreStage.class));
    }

    @Test
    void acceptOffreDeStage_Success_PrivateVisibility() {
        // Arrange
        fichierOffreStage.setStatus(OffreStage.Status.WAITING);
        when(offreStageRepository.findById(1L)).thenReturn(Optional.of(fichierOffreStage));
        when(offreStageRepository.save(any(OffreStage.class))).thenReturn(fichierOffreStage);
        when(etudiantRepository.findAllById(etudiantsPrivesIds)).thenReturn(etudiantsPrives);

        when(utilisateurService.checkRole(Role.GESTIONNAIRE_STAGE)).thenReturn(true);

        // Act
        offreStageService.acceptOffreDeStage(acceptDtoPrivate);

        // Assert
        assertEquals(OffreStage.Status.ACCEPTED, fichierOffreStage.getStatus(), "OffreStage status should be ACCEPTED");
        assertEquals("Approved for the position", fichierOffreStage.getStatusDescription(), "Status description should be set correctly");
        assertEquals(Programme.GENIE_LOGICIEL, fichierOffreStage.getProgramme(), "Programme should be set correctly");
        assertEquals(OffreStage.Visibility.PRIVATE, fichierOffreStage.getVisibility(), "Visibility should be PRIVATE");

        verify(offreStageRepository, times(1)).findById(1L);
        verify(offreStageRepository, times(1)).save(fichierOffreStage);
        verify(etudiantRepository, times(1)).findAllById(etudiantsPrivesIds);
        verify(etudiantOffreStagePriveeRepository, times(etudiantsPrives.size())).save(any());
    }

    @Test
    void acceptOffreDeStage_Success_PublicVisibility() {
        // Arrange
        fichierOffreStage.setStatus(OffreStage.Status.WAITING);
        when(offreStageRepository.findById(1L)).thenReturn(Optional.of(fichierOffreStage));
        when(offreStageRepository.save(any(OffreStage.class))).thenReturn(fichierOffreStage);

        when(utilisateurService.checkRole(Role.GESTIONNAIRE_STAGE)).thenReturn(true);


        // Act
        offreStageService.acceptOffreDeStage(acceptDtoPublic);

        // Assert
        assertEquals(OffreStage.Status.ACCEPTED, fichierOffreStage.getStatus(), "OffreStage status should be ACCEPTED");
        assertEquals("Approved for the position", fichierOffreStage.getStatusDescription(), "Status description should be set correctly");
        assertEquals(Programme.GENIE_LOGICIEL, fichierOffreStage.getProgramme(), "Programme should be set correctly");
        assertEquals(OffreStage.Visibility.PUBLIC, fichierOffreStage.getVisibility(), "Visibility should be PUBLIC");

        verify(offreStageRepository, times(1)).findById(1L);
        verify(offreStageRepository, times(1)).save(fichierOffreStage);
        verify(etudiantRepository, never()).findAllById(anyList());
        verify(etudiantOffreStagePriveeRepository, never()).save(any());
    }

    @Test
    void acceptOffreDeStage_OffreStageNotFound() {
        // Arrange
        when(offreStageRepository.findById(99L)).thenReturn(Optional.empty());

        when(utilisateurService.checkRole(Role.GESTIONNAIRE_STAGE)).thenReturn(true);


        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            offreStageService.acceptOffreDeStage(acceptDtoNonExistent);
        }, "Expected EntityNotFoundException to be thrown");

        assertEquals("OffreStage not found with ID: 99", exception.getMessage(), "Exception message should match");
        verify(offreStageRepository, times(1)).findById(99L);
        verify(offreStageRepository, never()).save(any(OffreStage.class));
        verify(etudiantRepository, never()).findAllById(anyList());
        verify(etudiantOffreStagePriveeRepository, never()).save(any());
    }

    @Test
    void acceptOffreDeStage_InvalidStatus() {
        // Arrange
        fichierOffreStage.setStatus(OffreStage.Status.ACCEPTED);
        when(offreStageRepository.findById(2L)).thenReturn(Optional.of(fichierOffreStage));

        when(utilisateurService.checkRole(Role.GESTIONNAIRE_STAGE)).thenReturn(true);


        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            offreStageService.acceptOffreDeStage(acceptDtoInvalidStatus);
        }, "Expected IllegalArgumentException to be thrown");

        assertEquals("OffreStage is not waiting", exception.getMessage(), "Exception message should match");
        verify(offreStageRepository, times(1)).findById(2L);
        verify(offreStageRepository, never()).save(any(OffreStage.class));
        verify(etudiantRepository, never()).findAllById(anyList());
        verify(etudiantOffreStagePriveeRepository, never()).save(any());
    }

    @Test
    void acceptOffreDeStage_ProgramNotSpecified() {
        // Arrange
        fichierOffreStage.setStatus(OffreStage.Status.WAITING);
        when(offreStageRepository.findById(1L)).thenReturn(Optional.of(fichierOffreStage));

        when(utilisateurService.checkRole(Role.GESTIONNAIRE_STAGE)).thenReturn(true);


        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            offreStageService.acceptOffreDeStage(acceptDtoProgramNotSpecified);
        }, "Expected IllegalArgumentException to be thrown");

        assertEquals("Programme or etudiantsPrives must be provided when uploaded by a gestionnaire de stage", exception.getMessage(),
                "Exception message should match");
        verify(offreStageRepository, times(1)).findById(1L);
        verify(offreStageRepository, never()).save(any(OffreStage.class));
        verify(etudiantRepository, never()).findAllById(anyList());
        verify(etudiantOffreStagePriveeRepository, never()).save(any());
    }

    @Test
    void acceptOffreDeStage_InvalidRole() {
        // Arrange
        when(utilisateurService.checkRole(Role.GESTIONNAIRE_STAGE)).thenReturn(false);

        // Act & Assert
        AuthenticationException exception = assertThrows(AuthenticationException.class, () -> {
            offreStageService.acceptOffreDeStage(acceptDtoPublic);
        }, "Expected AuthenticationException to be thrown");

        assertEquals("Vous n'avez pas les droits pour accepter une offre de stage", exception.getMessage(),
                "Exception message should match");
        verify(offreStageRepository, never()).findById(anyLong());
        verify(offreStageRepository, never()).save(any(OffreStage.class));
        verify(etudiantRepository, never()).findAllById(anyList());
        verify(etudiantOffreStagePriveeRepository, never()).save(any());
    }

    @Test
    void refuserOffreDeStage_InvalidRole() {
        // Arrange
        when(utilisateurService.checkRole(Role.GESTIONNAIRE_STAGE)).thenReturn(false);

        // Act & Assert
        AuthenticationException exception = assertThrows(AuthenticationException.class, () -> {
            offreStageService.refuseOffreDeStage(1L, "Not suitable for the position");
        }, "Expected AuthenticationException to be thrown");

        assertEquals("Vous n'avez pas les droits pour refuser une offre de stage", exception.getMessage(),
                "Exception message should match");
        verify(offreStageRepository, never()).findById(anyLong());
        verify(offreStageRepository, never()).save(any(OffreStage.class));
    }

    @Test
    void getOffreStageWithUtilisateurInfo_Success() {
        // Arrange
        Long offreStageId = 1L;

        fichierOffreStage.setId(offreStageId);
        fichierOffreStage.setStatus(OffreStage.Status.WAITING);
        fichierOffreStage.setStatusDescription("Waiting for approval");
        fichierOffreStage.setProgramme(Programme.GENIE_LOGICIEL);
        fichierOffreStage.setVisibility(OffreStage.Visibility.PUBLIC);
        fichierOffreStage.setCreateur(employeur);

        when(offreStageRepository.findById(offreStageId)).thenReturn(Optional.of(fichierOffreStage));

        // Act
        OffreStageAvecUtilisateurInfoDTO result = offreStageService.getOffreStageWithUtilisateurInfo(offreStageId);

        // Assert
        assertNotNull(result, "The returned DTO should not be null");
        assertEquals(fichierOffreStage.getId(), result.getId(), "DTO ID should match the entity ID");
        assertEquals(fichierOffreStage.getEntrepriseName(), result.getEntrepriseName(), "EntrepriseName should match");
        assertEquals(fichierOffreStage.getCreatedAt() , result.getCreatedAt(), "CreatedAt should match");
        assertEquals(fichierOffreStage.getTitle(), result.getTitle(), "Title should match");
        assertEquals(fichierOffreStage.getCreateur().getId(), result.getCreateur_id(), "Createur ID should match");
        assertEquals(fichierOffreStage.getCreateur().getPrenom(), result.getCreateur_prenom(), "Createur prenom should match");
        assertEquals(fichierOffreStage.getCreateur().getNom(), result.getCreateur_nom(), "Createur nom should match");
        assertEquals(fichierOffreStage.getCreateur().getNumeroDeTelephone(), result.getCreateur_telephone(), "Createur numeroDeTelephone should match");
        assertEquals(fichierOffreStage.getCreateur().getCourriel(), result.getCreateur_email(), "Createur courriel should match");
        assertEquals(fichierOffreStage.getCreateur().getRole(), result.getCreateur_role(), "Createur role should match");

        // Verify that findById was called once with the correct ID
        verify(offreStageRepository, times(1)).findById(offreStageId);
    }


    @Test
    void getOffreStageWithUtilisateurInfo_OffreStageNotFound() {
        // Arrange
        Long nonExistentId = 99L;

        when(offreStageRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            offreStageService.getOffreStageWithUtilisateurInfo(nonExistentId);
        }, "Expected EntityNotFoundException to be thrown");

        assertEquals("OffreStage not found with ID: " + nonExistentId, exception.getMessage(),
                "Exception message should match the expected message");

        // Verify that findById was called once with the correct ID
        verify(offreStageRepository, times(1)).findById(nonExistentId);
    }

    @Test
    public void testGetStages_Success() {
        // Arrange
        String token = "unTokenValide";
        Long createurId = 1L;
        int page = 0;
        PageRequest pageRequest = PageRequest.of(page, 10);
        Employeur createur = new Employeur(2L, "unPrenom", "unNom", "514-222-0385", "courriel@courriel.com", "123123123", "uneEntreprise");

        FormulaireOffreStage mockFormulaireOffreStage = new FormulaireOffreStage("unTitreForm", "uneEntreprise", "unEmployeur", "unEmail@mail.com", "unsite.com", "uneLocalisation", "1000", "uneDescription", createur, OffreStage.Visibility.PUBLIC, null, OffreStage.Status.ACCEPTED, OffreStage.SessionEcole.AUTOMNE, Year.of(2021), "09h00-17h00", "40");
        FichierOffreStage mockFichierOffreStage = new FichierOffreStage("unTitreFichier", "uneEntreprise", "nom.pdf", "data".getBytes(), createur, OffreStage.Visibility.PUBLIC, null, OffreStage.Status.ACCEPTED, OffreStage.SessionEcole.AUTOMNE, Year.of(2021));
        List<OffreStage> mockOffresListe = new ArrayList<>();
        mockOffresListe.add(mockFormulaireOffreStage);
        mockOffresListe.add(mockFichierOffreStage);

        Page<OffreStage> offresPage = new PageImpl<>(mockOffresListe, pageRequest, 2);

        when(utilisateurService.getMyUserId()).thenReturn(createurId);
        when(offreStageRepository.findOffreStageByCreateurIdFiltered(createurId, null, null, pageRequest)).thenReturn(offresPage);

        // Act
        List<OffreStageDTO> result = offreStageService.getStagesFiltered(page, null, null);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("unTitreForm", result.get(0).getTitle());
        assertEquals("unTitreFichier", result.get(1).getTitle());

        verify(utilisateurService, times(1)).getMyUserId();
        verify(offreStageRepository, times(1)).findOffreStageByCreateurIdFiltered(createurId, null, null, pageRequest);
    }

    @Test
    public void testGetStages_Null() {
        // Arrange
        String token = "unTokenValide";
        Long createurId = 1L;
        int page = 0;
        PageRequest pageRequest = PageRequest.of(page, 10);
        Page<OffreStage> offresPage = new PageImpl<>(List.of(), pageRequest, 0);

        when(utilisateurService.getMyUserId()).thenReturn(createurId);
        when(offreStageRepository.findOffreStageByCreateurIdFiltered(createurId, null, null, pageRequest)).thenReturn(offresPage);

        // Act
        List<OffreStageDTO> result = offreStageService.getStagesFiltered(page, null, null);

        // Assert
        assertNull(result);

        verify(utilisateurService, times(1)).getMyUserId();
        verify(offreStageRepository, times(1)).findOffreStageByCreateurIdFiltered(createurId, null, null, pageRequest);
    }

    @Test
    public void testGetAmountOfPageForCreateur_NumberEndWithZero() {
        //Arrange
        when(utilisateurService.getMyUserId()).thenReturn(1L);
        when(offreStageRepository.countOffreStageByCreateurIdFiltered(1L, null, null)).thenReturn(30L);

        //Act
        int nombrePage = offreStageService.getAmountOfPagesForCreateurFiltered(null, null);

        //Assert
        assertEquals(nombrePage, 3);
        verify(offreStageRepository, times(1)).countOffreStageByCreateurIdFiltered(1L, null, null);
    }

    @Test
    public void testGetAmountOfPageForCreateur_NumberNotEndWithZero() {
        //Arrange
        when(utilisateurService.getMyUserId()).thenReturn(1L);
        when(offreStageRepository.countOffreStageByCreateurIdFiltered(1L, null, null)).thenReturn(43L);

        //Act
        int nombrePage = offreStageService.getAmountOfPagesForCreateurFiltered(null, null);

        //Assert
        assertEquals(5, nombrePage);
        verify(offreStageRepository, times(1)).countOffreStageByCreateurIdFiltered(1L, null, null);
    }

    @Test
    public void testGetAmountOfPageForCreateur_NumberIsZero() {
        //Arrange
        when(utilisateurService.getMyUserId()).thenReturn(1L);
        when(offreStageRepository.countOffreStageByCreateurIdFiltered(1L, null, null)).thenReturn(0L);

        //Act
        int nombrePage = offreStageService.getAmountOfPagesForCreateurFiltered(null, null);

        //Assert
        assertEquals(nombrePage, 0);
        verify(offreStageRepository, times(1)).countOffreStageByCreateurIdFiltered(1L, null, null);
    }

    @Test
    void testGetAvailableOffreStagesForEtudiantFiltered_ValidInputs_ReturnsList() throws AccessDeniedException {
        // Arrange
        int page = 0;
        Integer annee = 2024;
        OffreStage.SessionEcole session = OffreStage.SessionEcole.AUTOMNE;
        String title = "Software Engineer";
        EtudiantDTO etudiantDTO = EtudiantDTO.empty();
        etudiantDTO.setId(1L);

        when(utilisateurService.getMe()).thenReturn(etudiantDTO);
        when(modelMapper.map(fichierOffreStage, FichierOffreStageDTO.class)).thenReturn(fichierOffreStageDTO);
        when(modelMapper.map(formulaireOffreStage, FormulaireOffreStageDTO.class)).thenReturn(formulaireOffreStageDTO);

        List<OffreStage> content = Arrays.asList(fichierOffreStage, formulaireOffreStage);
        Pageable pageable = PageRequest.of(0, 2);
        Page<OffreStage> offreStages = new PageImpl<>(content, pageable, content.size());

        PageRequest pageRequest = PageRequest.of(page, 10);
        when(offreStageRepository.findAllByEtudiantNotAppliedFilteredWithTitle(
                etudiantDTO.getId(),
                etudiantDTO.getProgramme(),
                Year.of(annee),
                session,
                title,
                pageRequest
        )).thenReturn(offreStages);

        // Act
        List<OffreStageDTO> result = offreStageService.getAvailableOffreStagesForEtudiantFiltered(page, annee, session, title);

        // Assert
        assertNotNull(result, "Result should not be null");
        assertEquals(2, result.size(), "Result size should be 2");

        verify(utilisateurService, times(1)).getMe();
        verify(offreStageRepository, times(1)).findAllByEtudiantNotAppliedFilteredWithTitle(
                etudiantDTO.getId(),
                etudiantDTO.getProgramme(),
                Year.of(annee),
                session,
                title,
                pageRequest
        );
    }

    @Test
    void testGetAvailableOffreStagesForEtudiantFiltered_NoData_ReturnsEmptyList() throws AccessDeniedException {
        // Arrange
        int page = 1;
        Integer annee = 2025;
        OffreStage.SessionEcole session = OffreStage.SessionEcole.AUTOMNE;
        String title = "Marketing Manager";
        EtudiantDTO etudiantDTO = EtudiantDTO.empty();
        etudiantDTO.setId(1L);
        Pageable pageable = PageRequest.of(0, 10);
        Page<OffreStage> offreStages = new PageImpl<>(Collections.emptyList(), pageable, 0);

        PageRequest pageRequest = PageRequest.of(page, 10);

        when(utilisateurService.getMe()).thenReturn(etudiantDTO);

        when(offreStageRepository.findAllByEtudiantNotAppliedFilteredWithTitle(
                etudiantDTO.getId(),
                etudiantDTO.getProgramme(),
                Year.of(annee),
                session,
                title,
                pageRequest
        )).thenReturn(offreStages);

        // Act
        List<OffreStageDTO> result = offreStageService.getAvailableOffreStagesForEtudiantFiltered(page, annee, session, title);

        // Assert
        assertNotNull(result, "Result should not be null");
        assertTrue(result.isEmpty(), "Result should be empty");

        verify(utilisateurService, times(1)).getMe();
        verify(offreStageRepository, times(1)).findAllByEtudiantNotAppliedFilteredWithTitle(
                etudiantDTO.getId(),
                etudiantDTO.getProgramme(),
                Year.of(annee),
                session,
                title,
                pageRequest
        );
    }

    @Test
    void testGetAvailableOffreStagesForEtudiantFiltered_NullTitle_TreatedAsEmpty() throws AccessDeniedException {
        // Arrange
        int page = 2;
        Integer annee = 2026;
        OffreStage.SessionEcole session = OffreStage.SessionEcole.AUTOMNE;

        EtudiantDTO etudiantDTO = EtudiantDTO.empty();
        etudiantDTO.setId(1L);

        when(utilisateurService.getMe()).thenReturn(etudiantDTO);
        when(modelMapper.map(fichierOffreStage, FichierOffreStageDTO.class)).thenReturn(fichierOffreStageDTO);

        List<OffreStage> content = Collections.singletonList(fichierOffreStage);
        Pageable pageable = PageRequest.of(0, 2);
        Page<OffreStage> offreStages = new PageImpl<>(content, pageable, content.size());

        PageRequest pageRequest = PageRequest.of(page, 10);
        when(offreStageRepository.findAllByEtudiantNotAppliedFilteredWithTitle(
                etudiantDTO.getId(),
                etudiantDTO.getProgramme(),
                Year.of(annee),
                session,
                "",
                pageRequest
        )).thenReturn(offreStages);

        // Act
        List<OffreStageDTO> result = offreStageService.getAvailableOffreStagesForEtudiantFiltered(page, annee, session, null);

        // Assert
        assertNotNull(result, "Result should not be null");
        assertEquals(1, result.size(), "Result size should be 1");

        verify(utilisateurService, times(1)).getMe();
        verify(offreStageRepository, times(1)).findAllByEtudiantNotAppliedFilteredWithTitle(
                etudiantDTO.getId(),
                etudiantDTO.getProgramme(),
                Year.of(annee),
                session,
                "",
                pageRequest
        );
    }

    @Test
    void testGetAvailableOffreStagesForEtudiantFiltered_AccessDeniedException() throws AccessDeniedException {
        // Arrange
        int page = 0;
        Integer annee = 2024;
        OffreStage.SessionEcole session = OffreStage.SessionEcole.AUTOMNE;
        String title = "Software Engineer";

        when(utilisateurService.getMe()).thenThrow(new AuthenticationException(HttpStatus.FORBIDDEN, "Authentication error"));

        // Act & Assert
        assertThrows(AuthenticationException.class, () -> {
            offreStageService.getAvailableOffreStagesForEtudiantFiltered(page, annee, session, title);
        }, "Should throw AuthenticationException");

        verify(utilisateurService, times(1)).getMe();
        verify(offreStageRepository, never()).findAllByEtudiantNotAppliedFilteredWithTitle(anyLong(), any(Programme.class), any(Year.class), any(OffreStage.SessionEcole.class), anyString(), any(PageRequest.class));
    }

    @Test
    public void testGetEmployeurByOffreStageId_Success() {
        // Arrange
        Long offreStageId = 1L;
        Credentials credentials = new Credentials("example@gmail.com", "passw0rd", Role.EMPLOYEUR);
        Employeur employeur = new Employeur();
        employeur.setId(1L);
        employeur.setNom("Dupont");
        employeur.setPrenom("Jean");
        employeur.setCredentials(credentials);
        EmployeurDTO employeurDTO = new EmployeurDTO();
        employeurDTO.setId(1L);
        employeurDTO.setNom("Dupont");
        employeurDTO.setPrenom("Jean");

        when(offreStageRepository.findEmployeurByOffreStageId(offreStageId)).thenReturn(employeur);

        // Act
        EmployeurDTO result = offreStageService.getEmployeurByOffreStageId(offreStageId);

        // Assert
        assertEquals(employeurDTO.getNom(), result.getNom());
        assertEquals(employeurDTO.getPrenom(), result.getPrenom());
        verify(offreStageRepository, times(1)).findEmployeurByOffreStageId(offreStageId);
    }

    @Test
    public void testGetEmployeurByOffreStageId_NotFound() {
        // Arrange
        Long offreStageId = 2L;

        when(offreStageRepository.findEmployeurByOffreStageId(offreStageId)).thenReturn(null);

        // Act & Assert
        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            offreStageService.getEmployeurByOffreStageId(offreStageId);
        });

        assertEquals("Aucun employeur associé à l'offre de stage id " + offreStageId + " n'existe.", exception.getMessage());
        verify(offreStageRepository, times(1)).findEmployeurByOffreStageId(offreStageId);
    }

    @Test
    void getNextSession_ReturnsSessionInfoDTO() {
        // Arrange
        SessionInfoDTO expectedSessionInfo = new SessionInfoDTO(OffreStage.SessionEcole.ETE, Year.of(2024));

        try (MockedStatic<SessionEcoleUtil> mockedStatic = mockStatic(SessionEcoleUtil.class)) {
            mockedStatic.when(() -> SessionEcoleUtil.getSessionInfo(any(LocalDateTime.class)))
                    .thenReturn(expectedSessionInfo);

            // Act
            SessionInfoDTO actualSessionInfo = offreStageService.getNextSession();

            // Assert
            mockedStatic.verify(() -> SessionEcoleUtil.getSessionInfo(any(LocalDateTime.class)), times(1));
            assertNotNull(actualSessionInfo, "Returned SessionInfoDTO should not be null.");
            assertEquals(expectedSessionInfo, actualSessionInfo, "Returned SessionInfoDTO should match the expected value.");
        }
    }

    @Test
    void getAllSessions_ReturnsListOfSessionInfoDTO() {
        // Arrange
        List<SessionInfoDTO> expectedSessions = Arrays.asList(
                new SessionInfoDTO(OffreStage.SessionEcole.AUTOMNE, Year.of(2024)),
                new SessionInfoDTO(OffreStage.SessionEcole.HIVER, Year.of(2025))
        );


        when(offreStageRepository.findDistinctSemesterAndYearAll()).thenReturn(expectedSessions);

        // Act
        List<SessionInfoDTO> actualSessions = offreStageService.getAllSessions();

        // Assert
        verify(offreStageRepository, times(1)).findDistinctSemesterAndYearAll();
        assertEquals(expectedSessions, actualSessions, "The returned list should match the expected sessions.");
    }

    @Test
    void getAllSessions_ReturnsEmptyList() {
        // Arrange
        List<SessionInfoDTO> expectedSessions = Collections.emptyList();

        when(offreStageRepository.findDistinctSemesterAndYearAll()).thenReturn(expectedSessions);

        // Act
        List<SessionInfoDTO> actualSessions = offreStageService.getAllSessions();

        // Assert
        verify(offreStageRepository, times(1)).findDistinctSemesterAndYearAll();
        assertTrue(actualSessions.isEmpty(), "The returned list should be empty.");
    }

    // --------------------- Tests for getSessionsForCreateur ---------------------

    @Test
    void getSessionsForCreateur_ReturnsListOfSessionInfoDTO() {
        // Arrange
        Long createurId = 1L;
        List<SessionInfoDTO> expectedSessions = Arrays.asList(
                new SessionInfoDTO(OffreStage.SessionEcole.AUTOMNE, Year.of(2024)),
                new SessionInfoDTO(OffreStage.SessionEcole.HIVER, Year.of(2025))
        );

        when(utilisateurService.getMyUserId()).thenReturn(createurId);
        when(offreStageRepository.findDistinctSemesterAndYearByCreateurId(createurId)).thenReturn(expectedSessions);

        // Act
        List<SessionInfoDTO> actualSessions = offreStageService.getSessionsForCreateur();

        // Assert
        verify(utilisateurService, times(1)).getMyUserId();
        verify(offreStageRepository, times(1)).findDistinctSemesterAndYearByCreateurId(createurId);
        assertEquals(expectedSessions, actualSessions, "The returned list should match the expected sessions.");
    }

    @Test
    void getSessionsForCreateur_NoSessionsFound_ReturnsEmptyList() {
        // Arrange
        Long createurId = 2L;
        List<SessionInfoDTO> expectedSessions = Collections.emptyList();

        when(utilisateurService.getMyUserId()).thenReturn(createurId);
        when(offreStageRepository.findDistinctSemesterAndYearByCreateurId(createurId)).thenReturn(expectedSessions);

        // Act
        List<SessionInfoDTO> actualSessions = offreStageService.getSessionsForCreateur();

        // Assert
        verify(utilisateurService, times(1)).getMyUserId();
        verify(offreStageRepository, times(1)).findDistinctSemesterAndYearByCreateurId(createurId);
        assertTrue(actualSessions.isEmpty(), "The returned list should be empty.");
    }

    // --------------------- Tests for getAllOffreStagesForEtudiantFiltered ---------------------

    @Test
    void getAllOffreStagesForEtudiantFiltered_WithValidInput_ReturnsPageOfOffreStageDTO() throws AccessDeniedException {
        // Arrange
        int pageNumber = 0;
        Integer annee = 2024;
        OffreStage.SessionEcole sessionEcole = OffreStage.SessionEcole.AUTOMNE;
        String title = "Software Engineer";

        EtudiantDTO etudiantDTO = new EtudiantDTO();
        etudiantDTO.setId(1L);
        etudiantDTO.setProgramme(Programme.GENIE_LOGICIEL);

        PageRequest pageRequest = PageRequest.of(pageNumber, OffreStageService.LIMIT_PER_PAGE);

        List<OffreStage> offreStages = Arrays.asList(fichierOffreStage, formulaireOffreStage);
        Page<OffreStage> offreStagePage = new PageImpl<>(offreStages, pageRequest, offreStages.size());

        when(utilisateurService.getMe()).thenReturn(etudiantDTO);
        when(offreStageRepository.findAllByEtudiantFilteredWithTitle(
                etudiantDTO.getId(),
                etudiantDTO.getProgramme(),
                Year.of(annee),
                sessionEcole,
                title,
                pageRequest
        )).thenReturn(offreStagePage);

        when(modelMapper.map(fichierOffreStage, FichierOffreStageDTO.class)).thenReturn(fichierOffreStageDTO);
        when(modelMapper.map(formulaireOffreStage, FormulaireOffreStageDTO.class)).thenReturn(formulaireOffreStageDTO);

        // Act
        Page<OffreStageDTO> actualPage = offreStageService.getAllOffreStagesForEtudiantFiltered(pageNumber, annee, sessionEcole, title);

        // Assert
        verify(utilisateurService, times(1)).getMe();
        verify(offreStageRepository, times(1)).findAllByEtudiantFilteredWithTitle(
                etudiantDTO.getId(),
                etudiantDTO.getProgramme(),
                Year.of(annee),
                sessionEcole,
                title,
                pageRequest
        );
        assertNotNull(actualPage, "Returned Page should not be null.");
        assertEquals(2, actualPage.getContent().size(), "Page should contain 2 OffreStageDTOs.");
        // Additional assertions can be made on the content
    }

    @Test
    void getAllOffreStagesForEtudiantFiltered_WithNullAnneeAndSession_ReturnsAllSessions() throws AccessDeniedException {
        // Arrange
        int pageNumber = 1;
        Integer annee = null;
        OffreStage.SessionEcole sessionEcole = null;
        String title = null; // Should default to empty string

        EtudiantDTO etudiantDTO = new EtudiantDTO();
        etudiantDTO.setId(2L);
        etudiantDTO.setProgramme(Programme.GENIE_LOGICIEL);

        PageRequest pageRequest = PageRequest.of(pageNumber, OffreStageService.LIMIT_PER_PAGE);

        List<OffreStage> offreStages = Collections.singletonList(fichierOffreStage);
        Page<OffreStage> offreStagePage = new PageImpl<>(offreStages, pageRequest, offreStages.size());

        when(utilisateurService.getMe()).thenReturn(etudiantDTO);
        when(offreStageRepository.findAllByEtudiantFilteredWithTitle(
                etudiantDTO.getId(),
                etudiantDTO.getProgramme(),
                null,
                null,
                "",
                pageRequest
        )).thenReturn(offreStagePage);

        when(modelMapper.map(fichierOffreStage, FichierOffreStageDTO.class)).thenReturn(fichierOffreStageDTO);

        // Act
        Page<OffreStageDTO> actualPage = offreStageService.getAllOffreStagesForEtudiantFiltered(pageNumber, annee, sessionEcole, title);

        // Assert
        verify(utilisateurService, times(1)).getMe();
        verify(offreStageRepository, times(1)).findAllByEtudiantFilteredWithTitle(
                etudiantDTO.getId(),
                etudiantDTO.getProgramme(),
                null,
                null,
                "",
                pageRequest
        );
        assertNotNull(actualPage, "Returned Page should not be null.");
        assertEquals(1, actualPage.getContent().size(), "Page should contain 1 OffreStageDTO.");
    }

    @Test
    void getAllOffreStagesForEtudiantFiltered_UserAccessDenied_ThrowsAuthenticationException() throws AccessDeniedException {
        // Arrange
        int pageNumber = 0;
        Integer annee = 2023;
        OffreStage.SessionEcole sessionEcole = OffreStage.SessionEcole.AUTOMNE;
        String title = "Data Analyst";

        when(utilisateurService.getMe()).thenThrow(new AccessDeniedException("Access denied"));

        // Act & Assert
        AuthenticationException exception = assertThrows(AuthenticationException.class, () ->
                        offreStageService.getAllOffreStagesForEtudiantFiltered(pageNumber, annee, sessionEcole, title),
                "Expected AuthenticationException to be thrown.");

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus(), "Exception status should be FORBIDDEN.");
        assertEquals("Authentication error", exception.getMessage(), "Exception message should match.");
    }

    @Test
    void updateOffreStage_WithValidData_ReturnsUpdatedFichierOffreStageDTO() throws IOException {
        // Arrange
        Long offreStageId = 1001L;

        // Create a valid UploadFicherOffreStageDTO
        UploadFicherOffreStageDTO uploadDTO = new UploadFicherOffreStageDTO();
        uploadDTO.setEntrepriseName("New Entreprise");
        uploadDTO.setTitle("Senior Developer");
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "resume.pdf",
                "application/pdf",
                "Dummy PDF Content".getBytes()
        );
        uploadDTO.setFile(file);

        // Mock UtilisateurService.getMe()
        UtilisateurDTO utilisateurDTO = new GestionnaireStageDTO();
        utilisateurDTO.setId(10L);
        when(utilisateurService.getMe()).thenReturn(utilisateurDTO);

        // Mock FicherOffreStageRepository.findById()
        FichierOffreStage existingFichier = new FichierOffreStage();
        existingFichier.setId(offreStageId);
        Enseignant createur = new Enseignant();
        createur.setId(utilisateurDTO.getId());
        existingFichier.setCreateur(createur);
        existingFichier.setEntrepriseName("Old Entreprise");
        existingFichier.setTitle("Developer");
        existingFichier.setFilename("old_resume.pdf");
        existingFichier.setData("Old PDF Content".getBytes());

        when(ficherOffreStageRepository.findById(offreStageId)).thenReturn(Optional.of(existingFichier));

        // Mock FicherOffreStageRepository.save()
        FichierOffreStage savedFichier = new FichierOffreStage();
        savedFichier.setId(offreStageId);
        savedFichier.setCreateur(createur);
        savedFichier.setEntrepriseName(uploadDTO.getEntrepriseName());
        savedFichier.setTitle(uploadDTO.getTitle());
        savedFichier.setFilename(file.getOriginalFilename());
        savedFichier.setData(file.getBytes());

        when(ficherOffreStageRepository.save(any(FichierOffreStage.class))).thenReturn(savedFichier);

        // Spy the service to mock convertToDTO
        OffreStageService spyService = Mockito.spy(offreStageService);
        FichierOffreStageDTO expectedDTO = new FichierOffreStageDTO();
        expectedDTO.setId(offreStageId);
        expectedDTO.setEntrepriseName(uploadDTO.getEntrepriseName());
        expectedDTO.setTitle(uploadDTO.getTitle());
        expectedDTO.setFilename(file.getOriginalFilename());
        expectedDTO.setFileData(Base64.getEncoder().encodeToString(file.getBytes()));

        doReturn(expectedDTO).when(spyService).convertToDTO(savedFichier);

        // Act
        FichierOffreStageDTO actualDTO = spyService.updateOffreStage(uploadDTO, offreStageId);

        // Assert
        verify(utilisateurService, times(1)).getMe();
        verify(ficherOffreStageRepository, times(1)).findById(offreStageId);
        verify(ficherOffreStageRepository, times(1)).save(existingFichier);
        verify(spyService, times(1)).convertToDTO(savedFichier);

        assertNotNull(actualDTO, "Returned FichierOffreStageDTO should not be null.");
        assertEquals(uploadDTO.getEntrepriseName(), actualDTO.getEntrepriseName(), "EntrepriseName should be updated.");
        assertEquals(uploadDTO.getTitle(), actualDTO.getTitle(), "Title should be updated.");
        assertEquals(file.getOriginalFilename(), actualDTO.getFilename(), "Filename should be updated.");
        assertEquals(Base64.getEncoder().encodeToString(file.getBytes()), actualDTO.getFileData(), "File data should be updated.");
    }

    @Test
    void updateOffreStage_FichierOffreStageNotFound_ThrowsResourceNotFoundException() throws IOException {
        // Arrange
        Long offreStageId = 2002L;

        UploadFicherOffreStageDTO uploadDTO = new UploadFicherOffreStageDTO();
        uploadDTO.setEntrepriseName("New Entreprise");
        uploadDTO.setTitle("Senior Developer");

        // Mock UtilisateurService.getMe()
        UtilisateurDTO utilisateurDTO = new GestionnaireStageDTO();
        utilisateurDTO.setId(20L);
        when(utilisateurService.getMe()).thenReturn(utilisateurDTO);

        // Mock FicherOffreStageRepository.findById() to return empty
        when(ficherOffreStageRepository.findById(offreStageId)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                        offreStageService.updateOffreStage(uploadDTO, offreStageId),
                "Expected ResourceNotFoundException to be thrown.");

        assertEquals("FichierOffreStage not found with ID: " + offreStageId, exception.getMessage(), "Exception message should match.");
        verify(utilisateurService, times(1)).getMe();
        verify(ficherOffreStageRepository, times(1)).findById(offreStageId);
        verify(ficherOffreStageRepository, never()).save(any(FichierOffreStage.class));
    }

    @Test
    void updateOffreStage_UserNotCreateur_ThrowsAccessDeniedException() throws IOException {
        // Arrange
        Long offreStageId = 3003L;

        UploadFicherOffreStageDTO uploadDTO = new UploadFicherOffreStageDTO();
        uploadDTO.setEntrepriseName("New Entreprise");

        // Mock UtilisateurService.getMe()
        UtilisateurDTO utilisateurDTO = new GestionnaireStageDTO();
        utilisateurDTO.setId(30L);
        when(utilisateurService.getMe()).thenReturn(utilisateurDTO);

        // Mock FicherOffreStageRepository.findById()
        FichierOffreStage existingFichier = new FichierOffreStage();
        existingFichier.setId(offreStageId);
        Enseignant createur = new Enseignant();
        createur.setId(999L); // Different from utilisateurDTO.getId()
        existingFichier.setCreateur(createur);
        existingFichier.setEntrepriseName("Old Entreprise");

        when(ficherOffreStageRepository.findById(offreStageId)).thenReturn(Optional.of(existingFichier));

        // Act & Assert
        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () ->
                        offreStageService.updateOffreStage(uploadDTO, offreStageId),
                "Expected AccessDeniedException to be thrown.");

        assertEquals("Vous n'avez pas les droits pour modifier cette offre de stage", exception.getMessage(), "Exception message should match.");
        verify(utilisateurService, times(1)).getMe();
        verify(ficherOffreStageRepository, times(1)).findById(offreStageId);
        verify(ficherOffreStageRepository, never()).save(any(FichierOffreStage.class));
    }

    @Test
    void updateOffreStage_WithNullFields_DoesNotUpdateNullFields() throws IOException {
        // Arrange
        Long offreStageId = 4004L;

        // Create UploadFicherOffreStageDTO with some null fields
        UploadFicherOffreStageDTO uploadDTO = new UploadFicherOffreStageDTO();
        uploadDTO.setEntrepriseName(null); // Should not update
        uploadDTO.setTitle("Lead Developer");
        uploadDTO.setFile(null); // Should not update

        // Mock UtilisateurService.getMe()
        UtilisateurDTO utilisateurDTO = new GestionnaireStageDTO();
        utilisateurDTO.setId(40L);
        when(utilisateurService.getMe()).thenReturn(utilisateurDTO);

        // Mock FicherOffreStageRepository.findById()
        FichierOffreStage existingFichier = new FichierOffreStage();
        existingFichier.setId(offreStageId);
        Enseignant createur = new Enseignant();
        createur.setId(utilisateurDTO.getId());
        existingFichier.setCreateur(createur);
        existingFichier.setEntrepriseName("Old Entreprise");
        existingFichier.setTitle("Developer");
        existingFichier.setFilename("old_resume.pdf");
        existingFichier.setData("Old PDF Content".getBytes());

        when(ficherOffreStageRepository.findById(offreStageId)).thenReturn(Optional.of(existingFichier));

        // Mock FicherOffreStageRepository.save()
        FichierOffreStage savedFichier = new FichierOffreStage();
        savedFichier.setId(offreStageId);
        savedFichier.setCreateur(createur);
        savedFichier.setEntrepriseName(existingFichier.getEntrepriseName()); // Should remain unchanged
        savedFichier.setTitle(uploadDTO.getTitle()); // Updated
        savedFichier.setFilename(existingFichier.getFilename()); // Should remain unchanged
        savedFichier.setData(existingFichier.getData()); // Should remain unchanged

        when(ficherOffreStageRepository.save(existingFichier)).thenReturn(savedFichier);

        // Spy the service to mock convertToDTO
        OffreStageService spyService = Mockito.spy(offreStageService);
        FichierOffreStageDTO expectedDTO = new FichierOffreStageDTO();
        expectedDTO.setId(offreStageId);
        expectedDTO.setEntrepriseName(existingFichier.getEntrepriseName());
        expectedDTO.setTitle(uploadDTO.getTitle());
        expectedDTO.setFilename(existingFichier.getFilename());
        expectedDTO.setFileData(Base64.getEncoder().encodeToString(existingFichier.getData()));

        doReturn(expectedDTO).when(spyService).convertToDTO(savedFichier);

        // Act
        FichierOffreStageDTO actualDTO = spyService.updateOffreStage(uploadDTO, offreStageId);

        // Assert
        verify(utilisateurService, times(1)).getMe();
        verify(ficherOffreStageRepository, times(1)).findById(offreStageId);
        verify(ficherOffreStageRepository, times(1)).save(existingFichier);
        verify(spyService, times(1)).convertToDTO(savedFichier);

        assertNotNull(actualDTO, "Returned FichierOffreStageDTO should not be null.");
        assertEquals(existingFichier.getEntrepriseName(), actualDTO.getEntrepriseName(), "EntrepriseName should remain unchanged.");
        assertEquals(uploadDTO.getTitle(), actualDTO.getTitle(), "Title should be updated.");
        assertEquals(existingFichier.getFilename(), actualDTO.getFilename(), "Filename should remain unchanged.");
        assertEquals(Base64.getEncoder().encodeToString(existingFichier.getData()), actualDTO.getFileData(), "File data should remain unchanged.");
    }

    @Test
    void updateOffreStage_WithEmptyFile_DoesNotUpdateFile() throws IOException {
        // Arrange
        Long offreStageId = 5005L;

        // Create UploadFicherOffreStageDTO with empty file
        UploadFicherOffreStageDTO uploadDTO = new UploadFicherOffreStageDTO();
        uploadDTO.setEntrepriseName("Updated Entreprise");
        uploadDTO.setTitle("Project Manager");
        MockMultipartFile emptyFile = new MockMultipartFile(
                "file",
                "",
                "application/pdf",
                new byte[0]
        );
        uploadDTO.setFile(emptyFile);

        // Mock UtilisateurService.getMe()
        UtilisateurDTO utilisateurDTO = new GestionnaireStageDTO();
        utilisateurDTO.setId(50L);
        when(utilisateurService.getMe()).thenReturn(utilisateurDTO);

        // Mock FicherOffreStageRepository.findById()
        FichierOffreStage existingFichier = new FichierOffreStage();
        existingFichier.setId(offreStageId);
        Enseignant createur = new Enseignant();
        createur.setId(utilisateurDTO.getId());
        existingFichier.setCreateur(createur);
        existingFichier.setEntrepriseName("Old Entreprise");
        existingFichier.setTitle("Developer");
        existingFichier.setFilename("resume.pdf");
        existingFichier.setData("Existing PDF Content".getBytes());

        when(ficherOffreStageRepository.findById(offreStageId)).thenReturn(Optional.of(existingFichier));

        // Mock FicherOffreStageRepository.save()
        FichierOffreStage savedFichier = new FichierOffreStage();
        savedFichier.setId(offreStageId);
        savedFichier.setCreateur(createur);
        savedFichier.setEntrepriseName(uploadDTO.getEntrepriseName());
        savedFichier.setTitle(uploadDTO.getTitle());
        savedFichier.setFilename(existingFichier.getFilename()); // Should remain unchanged
        savedFichier.setData(existingFichier.getData()); // Should remain unchanged

        when(ficherOffreStageRepository.save(existingFichier)).thenReturn(savedFichier);

        // Spy the service to mock convertToDTO
        OffreStageService spyService = Mockito.spy(offreStageService);
        FichierOffreStageDTO expectedDTO = new FichierOffreStageDTO();
        expectedDTO.setId(offreStageId);
        expectedDTO.setEntrepriseName(uploadDTO.getEntrepriseName());
        expectedDTO.setTitle(uploadDTO.getTitle());
        expectedDTO.setFilename(existingFichier.getFilename());
        expectedDTO.setFileData(Base64.getEncoder().encodeToString(existingFichier.getData()));

        doReturn(expectedDTO).when(spyService).convertToDTO(savedFichier);

        // Act
        FichierOffreStageDTO actualDTO = spyService.updateOffreStage(uploadDTO, offreStageId);

        // Assert
        verify(utilisateurService, times(1)).getMe();
        verify(ficherOffreStageRepository, times(1)).findById(offreStageId);
        verify(ficherOffreStageRepository, times(1)).save(existingFichier);
        verify(spyService, times(1)).convertToDTO(savedFichier);

        assertNotNull(actualDTO, "Returned FichierOffreStageDTO should not be null.");
        assertEquals(uploadDTO.getEntrepriseName(), actualDTO.getEntrepriseName(), "EntrepriseName should be updated.");
        assertEquals(uploadDTO.getTitle(), actualDTO.getTitle(), "Title should be updated.");
        assertEquals(existingFichier.getFilename(), actualDTO.getFilename(), "Filename should remain unchanged.");
        assertEquals(Base64.getEncoder().encodeToString(existingFichier.getData()), actualDTO.getFileData(), "File data should remain unchanged.");
    }

    @Test
    void checkAnneeSession_WithValidData_ThrowsArgument() {
        // Arrange
        Integer annee = 2024;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                        offreStageService.checkAnneeAndSessionTogether(annee, null),
                "Expected IllegalArgumentException to be thrown.");
    }

    @Test
    void getAmountOfPagesForEtudiantFiltered_WithValidInput_ReturnsCorrectPageCount() throws AccessDeniedException {
        // Arrange
        Integer annee = 2024;
        OffreStage.SessionEcole sessionEcole = OffreStage.SessionEcole.AUTOMNE;
        String title = "Software Engineer";

        EtudiantDTO etudiantDTO = new EtudiantDTO();
        etudiantDTO.setId(1L);
        etudiantDTO.setProgramme(Programme.GENIE_LOGICIEL);

        when(utilisateurService.getMe()).thenReturn(etudiantDTO);

        // Assume LIMIT_PER_PAGE is 10
        int limitPerPage = OffreStageService.LIMIT_PER_PAGE; // 10

        // Mock countByEtudiantIdNotAppliedFilteredWithTitle
        when(offreStageRepository.countByEtudiantIdNotAppliedFilteredWithTitle(
                etudiantDTO.getId(),
                etudiantDTO.getProgramme(),
                Year.of(annee),
                sessionEcole,
                title
        )).thenReturn(25L); // Expecting 3 pages (25 / 10 = 2.5 => 3)

        // Act
        int actualPageCount = offreStageService.getAmountOfPagesForEtudiantFiltered(annee, sessionEcole, title);

        // Assert
        verify(utilisateurService, times(1)).getMe();
        verify(offreStageRepository, times(1)).countByEtudiantIdNotAppliedFilteredWithTitle(
                etudiantDTO.getId(),
                etudiantDTO.getProgramme(),
                Year.of(annee),
                sessionEcole,
                title
        );
        assertEquals(3, actualPageCount, "Page count should be 3.");
    }

    @Test
    void getAmountOfPagesForEtudiantFiltered_WithExactMultiple_ReturnsExactPageCount() throws AccessDeniedException {
        // Arrange
        Integer annee = 2023;
        OffreStage.SessionEcole sessionEcole = OffreStage.SessionEcole.AUTOMNE;
        String title = "Data Analyst";

        EtudiantDTO etudiantDTO = new EtudiantDTO();
        etudiantDTO.setId(2L);
        etudiantDTO.setProgramme(Programme.GENIE_LOGICIEL);

        when(utilisateurService.getMe()).thenReturn(etudiantDTO);

        // Assume LIMIT_PER_PAGE is 10
        int limitPerPage = OffreStageService.LIMIT_PER_PAGE; // 10

        // Mock countByEtudiantIdNotAppliedFilteredWithTitle
        when(offreStageRepository.countByEtudiantIdNotAppliedFilteredWithTitle(
                etudiantDTO.getId(),
                etudiantDTO.getProgramme(),
                Year.of(annee),
                sessionEcole,
                title
        )).thenReturn(30L); // Expecting 3 pages (30 / 10 = 3)

        // Act
        int actualPageCount = offreStageService.getAmountOfPagesForEtudiantFiltered(annee, sessionEcole, title);

        // Assert
        verify(utilisateurService, times(1)).getMe();
        verify(offreStageRepository, times(1)).countByEtudiantIdNotAppliedFilteredWithTitle(
                etudiantDTO.getId(),
                etudiantDTO.getProgramme(),
                Year.of(annee),
                sessionEcole,
                title
        );
        assertEquals(3, actualPageCount, "Page count should be 3.");
    }

    @Test
    void getAmountOfPagesForEtudiantFiltered_WithZeroRows_ReturnsZeroPages() throws AccessDeniedException {
        // Arrange
        Integer annee = 2025;
        OffreStage.SessionEcole sessionEcole = OffreStage.SessionEcole.AUTOMNE;
        String title = "Project Manager";

        EtudiantDTO etudiantDTO = new EtudiantDTO();
        etudiantDTO.setId(3L);
        etudiantDTO.setProgramme(Programme.GENIE_LOGICIEL);

        when(utilisateurService.getMe()).thenReturn(etudiantDTO);

        // Mock countByEtudiantIdNotAppliedFilteredWithTitle
        when(offreStageRepository.countByEtudiantIdNotAppliedFilteredWithTitle(
                etudiantDTO.getId(),
                etudiantDTO.getProgramme(),
                Year.of(annee),
                sessionEcole,
                title
        )).thenReturn(0L); // Expecting 0 pages

        // Act
        int actualPageCount = offreStageService.getAmountOfPagesForEtudiantFiltered(annee, sessionEcole, title);

        // Assert
        verify(utilisateurService, times(1)).getMe();
        verify(offreStageRepository, times(1)).countByEtudiantIdNotAppliedFilteredWithTitle(
                etudiantDTO.getId(),
                etudiantDTO.getProgramme(),
                Year.of(annee),
                sessionEcole,
                title
        );
        assertEquals(0, actualPageCount, "Page count should be 0.");
    }

    @Test
    void getAmountOfPagesForEtudiantFiltered_UserAccessDenied_ThrowsAuthenticationException() throws AccessDeniedException {
        // Arrange
        Integer annee = 2024;
        OffreStage.SessionEcole sessionEcole = OffreStage.SessionEcole.AUTOMNE;
        String title = "Software Engineer";

        when(utilisateurService.getMe()).thenThrow(new AccessDeniedException("Access denied"));

        // Act & Assert
        AuthenticationException exception = assertThrows(AuthenticationException.class, () ->
                        offreStageService.getAmountOfPagesForEtudiantFiltered(annee, sessionEcole, title),
                "Expected AuthenticationException to be thrown.");

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus(), "Exception status should be FORBIDDEN.");
        assertEquals("Authentication error", exception.getMessage(), "Exception message should match.");

        verify(utilisateurService, times(1)).getMe();
        verifyNoMoreInteractions(offreStageRepository);
    }

    // --------------------- Tests for getWaitingOffreStage ---------------------

    @Test
    void getWaitingOffreStage_WithExistingWaitingOffres_ReturnsListOfOffreStageDTO() {
        fichierOffreStage.setTitle("Developer");

        // Arrange
        List<OffreStage> waitingOffres = Arrays.asList(
                fichierOffreStage,
                formulaireOffreStage
        );

        when(offreStageRepository.getOffreStagesByStatusEquals(OffreStage.Status.WAITING))
                .thenReturn(Optional.of(waitingOffres));

        // Act
        List<OffreStageDTO> result = offreStageService.getWaitingOffreStage();

        // Assert
        verify(offreStageRepository, times(1)).getOffreStagesByStatusEquals(OffreStage.Status.WAITING);
        assertNotNull(result, "Resulting list should not be null.");
        assertEquals(2, result.size(), "List should contain 2 OffreStageDTOs.");

        // Additional assertions can be added to verify the content of each DTO
        assertEquals("Sample Entreprise", result.get(0).getEntrepriseName(), "First OffreStageDTO should have EntrepriseName 'Company A'.");
        assertEquals("Developer", result.get(0).getTitle(), "First OffreStageDTO should have title 'Developer'.");
    }

    @Test
    void getWaitingOffreStage_WithNoWaitingOffres_ReturnsEmptyList() {
        // Arrange
        when(offreStageRepository.getOffreStagesByStatusEquals(OffreStage.Status.WAITING))
                .thenReturn(Optional.empty());

        // Act
        List<OffreStageDTO> result = offreStageService.getWaitingOffreStage();

        // Assert
        verify(offreStageRepository, times(1)).getOffreStagesByStatusEquals(OffreStage.Status.WAITING);
        assertNotNull(result, "Resulting list should not be null.");
        assertTrue(result.isEmpty(), "List should be empty.");
    }

    // --------------------- Tests for getAcceptedOffreStage ---------------------

    @Test
    void getAcceptedOffreStage_WithExistingAcceptedOffres_ReturnsListOfOffreStageDTO() {
        // Arrange
        List<OffreStage> acceptedOffres = Arrays.asList(
                formulaireOffreStage,
                fichierOffreStage
        );

        when(offreStageRepository.getOffreStagesByStatusEquals(OffreStage.Status.ACCEPTED))
                .thenReturn(Optional.of(acceptedOffres));

        // Act
        List<OffreStageDTO> result = offreStageService.getAcceptedOffreStage();

        // Assert
        verify(offreStageRepository, times(1)).getOffreStagesByStatusEquals(OffreStage.Status.ACCEPTED);
        assertNotNull(result, "Resulting list should not be null.");
        assertEquals(2, result.size(), "List should contain 2 OffreStageDTOs.");
    }

    @Test
    void getAcceptedOffreStage_WithNoAcceptedOffres_ReturnsEmptyList() {
        // Arrange
        when(offreStageRepository.getOffreStagesByStatusEquals(OffreStage.Status.ACCEPTED))
                .thenReturn(Optional.empty());

        // Act
        List<OffreStageDTO> result = offreStageService.getAcceptedOffreStage();

        // Assert
        verify(offreStageRepository, times(1)).getOffreStagesByStatusEquals(OffreStage.Status.ACCEPTED);
        assertNotNull(result, "Resulting list should not be null.");
        assertTrue(result.isEmpty(), "List should be empty.");
    }
}
