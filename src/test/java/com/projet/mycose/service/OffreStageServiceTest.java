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
    private FormulaireOffreStageRepository formulaireOffreStageRepository;

    @Mock
    private FichierOffreStageRepository ficherOffreStageRepository;

    @Mock
    private MultipartFile file;

    @InjectMocks
    private OffreStageService offreStageService;

    // Sample data for tests
    private UtilisateurDTO employeurDTO;
    private UtilisateurDTO gestionnaireDTO;
    private UploadFicherOffreStageDTO uploadFicherOffreStageDTO;
    private FichierOffreStageDTO fichierOffreStageDTO;
    private FichierOffreStage fichierOffreStage;
    private FormulaireOffreStageDTO formulaireOffreStageDTO;
    private FormulaireOffreStage formulaireOffreStage;
    private Employeur employeur;

    private static final String SAMPLE_DATA = "sampleData";
    private static final String BASE64_SAMPLE_DATA = "c2FtcGxlRGF0YQ==";
    private static final String VALID_TOKEN = "validToken";
    private static final String INVALID_TOKEN = "invalidRoleToken";

    @BeforeEach
    public void setUp() {
        // Initialize sample data
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

        fichierOffreStage = new FichierOffreStage();
        fichierOffreStage.setId(1L);
        fichierOffreStage.setData(SAMPLE_DATA.getBytes());
        fichierOffreStage.setEntrepriseName("Sample Entreprise");

        formulaireOffreStageDTO = new FormulaireOffreStageDTO();
        formulaireOffreStageDTO.setCreateur_id(1L);

        formulaireOffreStage = new FormulaireOffreStage();
        formulaireOffreStage.setId(2L);
        formulaireOffreStage.setCreateur(new Employeur());

        employeur = Employeur.builder()
                .id(1L)
                .prenom("Jane")
                .nom("Smith")
                .numeroDeTelephone("098-765-4321")
                .courriel("jane.smith@techcorp.com")
                .motDePasse("securePass!")
                .entrepriseName("TechCorp")
                .build();
    }

    // --------------------- Test Cases for saveFile ---------------------

    @Test
    public void shouldSaveFileSuccessfully_WhenUserIsEmployeur() throws IOException {
        // Arrange
        String token = VALID_TOKEN;
        when(utilisateurService.getMe(token)).thenReturn(employeurDTO);

        // Specific stubbing for this test
        when(modelMapper.map(any(FichierOffreStageDTO.class), eq(FichierOffreStage.class))).thenReturn(fichierOffreStage);
        when(ficherOffreStageRepository.save(any(FichierOffreStage.class))).thenReturn(fichierOffreStage);
        when(modelMapper.map(fichierOffreStage, FichierOffreStageDTO.class)).thenReturn(fichierOffreStageDTO);
        when(validator.validate(any(FichierOffreStageDTO.class))).thenReturn(Collections.emptySet());
        when(file.getBytes()).thenReturn(SAMPLE_DATA.getBytes());
        when(file.getOriginalFilename()).thenReturn("sampleFile.txt");
        when(utilisateurRepository.findById(1L)).thenReturn(Optional.of(employeur));

        // Act
        FichierOffreStageDTO result = offreStageService.saveFile(uploadFicherOffreStageDTO, token);

        // Assert
        assertNotNull(result);
        assertEquals(fichierOffreStageDTO.getCreateur_id(), result.getCreateur_id());
        assertEquals(fichierOffreStageDTO.getEntrepriseName(), result.getEntrepriseName());
        verify(ficherOffreStageRepository, times(1)).save(any(FichierOffreStage.class));
    }

    @Test
    public void shouldSaveFileSuccessfully_WhenUserIsGestionnaireStage() throws IOException {
        // Arrange
        String token = VALID_TOKEN;
        uploadFicherOffreStageDTO.setProgramme(Programme.GENIE_LOGICIEL);
        when(utilisateurService.getMe(token)).thenReturn(gestionnaireDTO);

        // Specific stubbing for this test
        when(modelMapper.map(any(FichierOffreStageDTO.class), eq(FichierOffreStage.class))).thenReturn(fichierOffreStage);
        when(ficherOffreStageRepository.save(any(FichierOffreStage.class))).thenReturn(fichierOffreStage);
        when(modelMapper.map(fichierOffreStage, FichierOffreStageDTO.class)).thenReturn(fichierOffreStageDTO);
        when(validator.validate(any(FichierOffreStageDTO.class))).thenReturn(Collections.emptySet());
        when(file.getBytes()).thenReturn(SAMPLE_DATA.getBytes());
        when(file.getOriginalFilename()).thenReturn("sampleFile.txt");
        when(utilisateurRepository.findById(2L)).thenReturn(Optional.of(new GestionnaireStage()));

        // Act
        FichierOffreStageDTO result = offreStageService.saveFile(uploadFicherOffreStageDTO, token);

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
        when(utilisateurService.getMe(token)).thenReturn(invalidUser);
        when(file.getBytes()).thenReturn(SAMPLE_DATA.getBytes());
        when(file.getOriginalFilename()).thenReturn("sampleFile.txt");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            offreStageService.saveFile(uploadFicherOffreStageDTO, token);
        });

        assertEquals("Utilisateur n'est pas un employeur ou un gestionnaire de stage", exception.getMessage());
        verify(ficherOffreStageRepository, never()).save(any(FichierOffreStage.class));
    }

    @Test
    public void shouldThrowConstraintViolationException_WhenValidationFails() throws AccessDeniedException, IOException {
        // Arrange
        String token = VALID_TOKEN;
        when(utilisateurService.getMe(token)).thenReturn(employeurDTO);

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
            offreStageService.saveFile(uploadFicherOffreStageDTO, token);
        });

        assertFalse(exception.getConstraintViolations().isEmpty());
        verify(ficherOffreStageRepository, never()).save(any(FichierOffreStage.class));
    }

    @Test
    public void shouldThrowEntityNotFoundException_WhenCreateurNotFound() throws AccessDeniedException, IOException {
        // Arrange
        String token = VALID_TOKEN;
        when(utilisateurService.getMe(token)).thenReturn(employeurDTO);

        // Specific stubbing for this test
        when(modelMapper.map(any(FichierOffreStageDTO.class), eq(FichierOffreStage.class)))
                .thenThrow(new EntityNotFoundException("Utilisateur not found with ID: 1"));
        when(file.getBytes()).thenReturn(SAMPLE_DATA.getBytes());
        when(file.getOriginalFilename()).thenReturn("sampleFile.txt");

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            offreStageService.saveFile(uploadFicherOffreStageDTO, token);
        });

        assertEquals("Utilisateur not found with ID: 1", exception.getMessage());
        verify(ficherOffreStageRepository, never()).save(any(FichierOffreStage.class));
    }

    // --------------------- Test Cases for saveForm ---------------------

    @Test
    public void shouldSaveFormSuccessfully_WhenUserIsEmployeur() throws AccessDeniedException {
        // Arrange
        String token = VALID_TOKEN;
        when(utilisateurService.getMe(token)).thenReturn(employeurDTO);

        // Specific stubbing for this test
        // Mapping DTO to Entity
        when(modelMapper.map(formulaireOffreStageDTO, FormulaireOffreStage.class)).thenReturn(formulaireOffreStage);
        // Mapping Entity back to DTO
        when(modelMapper.map(formulaireOffreStage, FormulaireOffreStageDTO.class)).thenReturn(formulaireOffreStageDTO);

        when(formulaireOffreStageRepository.save(any(FormulaireOffreStage.class))).thenReturn(formulaireOffreStage);

        when(utilisateurRepository.findById(1L)).thenReturn(Optional.of(employeur));

        // Act
        FormulaireOffreStageDTO result = offreStageService.saveForm(formulaireOffreStageDTO, token);

        // Assert
        assertNotNull(result);
        assertEquals(formulaireOffreStageDTO.getCreateur_id(), result.getCreateur_id());
        verify(formulaireOffreStageRepository, times(1)).save(any(FormulaireOffreStage.class));
        verify(modelMapper, times(1)).map(formulaireOffreStageDTO, FormulaireOffreStage.class);
        verify(modelMapper, times(1)).map(formulaireOffreStage, FormulaireOffreStageDTO.class);
    }


    @Test
    public void shouldThrowAccessDeniedException_WhenUserIsNotEmployeur() throws AccessDeniedException {
        // Arrange
        String token = INVALID_TOKEN;
        UtilisateurDTO invalidUser = new EtudiantDTO();
        invalidUser.setId(3L);
        invalidUser.setRole(Role.ETUDIANT); // Assuming ETUDIANT is another role
        when(utilisateurService.getMe(token)).thenReturn(invalidUser);

        // Act & Assert
        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> {
            offreStageService.saveForm(formulaireOffreStageDTO, token);
        });

        assertEquals("Utilisateur n'est pas un employeur", exception.getMessage());
        verify(formulaireOffreStageRepository, never()).save(any(FormulaireOffreStage.class));
    }

    // --------------------- Test Cases for changeStatus ---------------------

    @Test
    public void shouldChangeStatusSuccessfully_WhenOffreStageExists() throws ChangeSetPersister.NotFoundException {
        // Arrange
        Long offreStageId = 1L;
        OffreStage.Status newStatus = OffreStage.Status.ACCEPTED;
        String description = "Approved by admin";
        when(offreStageRepository.findById(offreStageId)).thenReturn(Optional.of(fichierOffreStage));
        when(offreStageRepository.save(any(OffreStage.class))).thenReturn(fichierOffreStage);

        // Act
        offreStageService.changeStatus(offreStageId, newStatus, description);

        // Assert
        assertEquals(newStatus, fichierOffreStage.getStatus());
        assertEquals(description, fichierOffreStage.getStatusDescription());
        verify(offreStageRepository, times(1)).findById(offreStageId);
        verify(offreStageRepository, times(1)).save(fichierOffreStage);
    }

    @Test
    public void shouldThrowNotFoundException_WhenOffreStageDoesNotExist() {
        // Arrange
        Long offreStageId = 999L;
        OffreStage.Status newStatus = OffreStage.Status.ACCEPTED;
        String description = "Approved by admin";
        when(offreStageRepository.findById(offreStageId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ChangeSetPersister.NotFoundException.class, () -> {
            offreStageService.changeStatus(offreStageId, newStatus, description);
        });

        verify(offreStageRepository, times(1)).findById(offreStageId);
        verify(offreStageRepository, never()).save(any(OffreStage.class));
    }

    // --------------------- Test Cases for getWaitingOffreStage ---------------------

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
                        // Set other fields as necessary
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

    // --------------------- Test Cases for getAmountOfPages ---------------------

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

    // --------------------- Test Cases for getAvailableOffreStagesForEtudiant ---------------------


    @Test
    public void shouldReturnAvailableOffreStages_ForEtudiant() throws AccessDeniedException {
        // Arrange
        String token = VALID_TOKEN;
        Long etudiantId = 1L;
        Programme programme = Programme.NOT_SPECIFIED;
        EtudiantDTO etudiantDTO = EtudiantDTO.empty();
        etudiantDTO.setId(etudiantId);
        etudiantDTO.setProgramme(programme);
        when(utilisateurService.getMe(token)).thenReturn(etudiantDTO); // Changed to getMe(token)

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
        List<OffreStageDTO> result = offreStageService.getAvailableOffreStagesForEtudiant(token);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(utilisateurService, times(1)).getMe(token); // Changed to getMe(token)
        verify(offreStageRepository, times(1)).findAllByEtudiantNotApplied(etudiantId, programme);
        verify(modelMapper, times(1)).map(fichierOffreStage, FichierOffreStageDTO.class);
        verify(modelMapper, times(1)).map(formulaireOffreStage, FormulaireOffreStageDTO.class);
    }





    // --------------------- Test Cases for convertToDTO and convertToEntity ---------------------

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
}
