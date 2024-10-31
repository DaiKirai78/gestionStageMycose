package com.projet.mycose.service;

import com.projet.mycose.dto.*;
import com.projet.mycose.modele.*;
import com.projet.mycose.modele.auth.Role;
import com.projet.mycose.repository.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OffreStageServiceTest {

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
        uploadFicherOffreStageDTO.setSession(OffreStage.SessionEcole.AUTOMNE);
        uploadFicherOffreStageDTO.setAnnee(2024);

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

        formulaireOffreStageDTO = new FormulaireOffreStageDTO();
        formulaireOffreStageDTO.setCreateur_id(1L);
        formulaireOffreStageDTO.setSession(OffreStage.SessionEcole.AUTOMNE);
        formulaireOffreStageDTO.setAnnee(2024);

        formulaireOffreStage = new FormulaireOffreStage();
        formulaireOffreStage.setId(2L);
        formulaireOffreStage.setCreateur(new Employeur());

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
        when(offreStageRepository.getOffreStageByStatusEquals(eq(OffreStage.Status.WAITING), any()))
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
                    .getOffreStageByStatusEquals(eq(OffreStage.Status.WAITING), eq(PageRequest.of(page - 1, 10)));

            // Verify static method invocations
            mockedStatic.verify(() -> OffreStageAvecUtilisateurInfoDTO.toDto(fichierOffreStage), times(1));
            mockedStatic.verify(() -> OffreStageAvecUtilisateurInfoDTO.toDto(formulaireOffreStage), times(1));
        }
    }

    @Test
    public void shouldReturnEmptyList_WhenThereAreNoWaitingOffreStages() {
        // Arrange
        int page = 1;
        when(offreStageRepository.getOffreStageByStatusEquals(eq(OffreStage.Status.WAITING), any()))
                .thenReturn(Optional.empty());

        // Act
        List<OffreStageAvecUtilisateurInfoDTO> result = offreStageService.getWaitingOffreStage(page);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(offreStageRepository, times(1)).getOffreStageByStatusEquals(OffreStage.Status.WAITING, PageRequest.of(page - 1, 10));
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

        FormulaireOffreStageDTO formulaireOffreStageDTO = new FormulaireOffreStageDTO();
        formulaireOffreStageDTO.setCreateur_id(2L);
        formulaireOffreStageDTO.setEntrepriseName("Sample Entreprise");

        // Initialize Entities
        FichierOffreStage fichierOffreStage = new FichierOffreStage();
        fichierOffreStage.setId(1L);
        fichierOffreStage.setData("sampleData".getBytes());
        fichierOffreStage.setEntrepriseName("Sample Entreprise");

        FormulaireOffreStage formulaireOffreStage = new FormulaireOffreStage();
        formulaireOffreStage.setId(2L);
        formulaireOffreStage.setCreateur(new GestionnaireStage());
        formulaireOffreStage.setEntrepriseName("Sample Entreprise");

        // Set OffreStages
        List<OffreStage> availableOffres = Arrays.asList(fichierOffreStage, formulaireOffreStage);
        when(offreStageRepository.findAllByEtudiantNotApplied(etudiantId, programme)).thenReturn(availableOffres);

        // Correct stubbing: map to specific DTO classes
        when(modelMapper.map(fichierOffreStage, FichierOffreStageDTO.class)).thenReturn(fichierOffreStageDTO);
        when(modelMapper.map(formulaireOffreStage, FormulaireOffreStageDTO.class)).thenReturn(formulaireOffreStageDTO);

        // Act
        List<OffreStageDTO> result = offreStageService.getAvailableOffreStagesForEtudiant();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(utilisateurService, times(1)).getMe(); // Changed to getMe(token)
        verify(offreStageRepository, times(1)).findAllByEtudiantNotApplied(etudiantId, programme);
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
    void getTotalWaitingOffreStages_WithWaitingOffreStages() {
        // Arrange
        long expectedCount = 5L;
        when(offreStageRepository.countByStatus(OffreStage.Status.WAITING)).thenReturn(expectedCount);

        // Act
        long actualCount = offreStageService.getTotalWaitingOffreStages();

        // Assert
        assertEquals(expectedCount, actualCount, "The count of waiting OffreStages should match the expected value");
        verify(offreStageRepository, times(1)).countByStatus(OffreStage.Status.WAITING);
    }

    @Test
    void getTotalWaitingOffreStages_NoWaitingOffreStages() {
        // Arrange
        long expectedCount = 0L;
        when(offreStageRepository.countByStatus(OffreStage.Status.WAITING)).thenReturn(expectedCount);

        // Act
        long actualCount = offreStageService.getTotalWaitingOffreStages();

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
}
