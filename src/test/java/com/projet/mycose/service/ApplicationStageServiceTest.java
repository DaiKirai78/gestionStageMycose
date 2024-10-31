package com.projet.mycose.service;

import com.projet.mycose.dto.*;

import com.projet.mycose.modele.*;
import com.projet.mycose.modele.auth.Credentials;
import com.projet.mycose.modele.auth.Role;
import com.projet.mycose.repository.ApplicationStageRepository;
import com.projet.mycose.repository.EtudiantOffreStagePriveeRepository;
import com.projet.mycose.repository.EtudiantRepository;
import com.projet.mycose.repository.OffreStageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ApplicationStageServiceTest {

    @Mock
    private ApplicationStageRepository applicationStageRepository;

    @Mock
    private UtilisateurService utilisateurService;

    @Mock
    private OffreStageRepository offreStageRepository;

    @Mock
    private EtudiantRepository etudiantRepository;

    @Mock
    private EtudiantOffreStagePriveeRepository etudiantOffreStagePriveeRepository;

    @InjectMocks
    private ApplicationStageService applicationStageService;


    private Long offreStageId;
    private Etudiant etudiant;
    private Etudiant etudiant2;
    private FichierOffreStage fichierOffreStage;
    private ApplicationStage applicationStage;
    private ApplicationStageDTO applicationStageDTO;
    private ApplicationStageAvecInfosDTO applicationStageAvecInfosDTO;

    private SummonEtudiantDTO summonEtudiantDTO;

    @BeforeEach
    void setup() {
        offreStageId = 1L;

        etudiant = new Etudiant();
        etudiant.setId(1L);
        etudiant.setPrenom("Roberto");
        etudiant.setNom("Berrios");
        Credentials credentials = new Credentials("example@gmail.com", "passw0rd", Role.ETUDIANT);
        etudiant.setCredentials(credentials);
        etudiant.setProgramme(Programme.GENIE_LOGICIEL);

        etudiant2 = new Etudiant();
        etudiant2.setId(2L);

        fichierOffreStage = new FichierOffreStage();
        fichierOffreStage.setId(offreStageId);
        fichierOffreStage.setTitle("Software Engineering Internship");
        fichierOffreStage.setEntrepriseName("Tech Corp");
        fichierOffreStage.setStatus(OffreStage.Status.ACCEPTED);
        fichierOffreStage.setStatusDescription("Approved for applications.");
        fichierOffreStage.setCreateur(new Etudiant());
        fichierOffreStage.getCreateur().setId(1L);
        fichierOffreStage.setFilename("internship_offer.pdf");
        fichierOffreStage.setData(new byte[]{1, 2, 3});
        fichierOffreStage.setProgramme(Programme.GENIE_LOGICIEL);

        applicationStage = new ApplicationStage();
        applicationStage.setId(1L);
        applicationStage.setOffreStage(fichierOffreStage);
        applicationStage.setEtudiant(etudiant);
        applicationStage.setStatus(ApplicationStage.ApplicationStatus.PENDING);

        applicationStageDTO = new ApplicationStageDTO();
        applicationStageDTO.setId(applicationStage.getId());
        applicationStageDTO.setOffreStage_id(fichierOffreStage.getId());
        applicationStageDTO.setEtudiant_id(etudiant.getId());

        applicationStageAvecInfosDTO = new ApplicationStageAvecInfosDTO();
        applicationStageAvecInfosDTO.setId(applicationStage.getId());
        applicationStageAvecInfosDTO.setTitle(fichierOffreStage.getTitle());
        applicationStageAvecInfosDTO.setEntrepriseName(fichierOffreStage.getEntrepriseName());
        applicationStageAvecInfosDTO.setStatus(applicationStage.getStatus());

        summonEtudiantDTO = new SummonEtudiantDTO();
        summonEtudiantDTO.setScheduledAt(LocalDateTime.now().plusDays(1));
        summonEtudiantDTO.setLocation("Tech Corp");
        summonEtudiantDTO.setMessageConvocation("You have been summoned for an interview.");
    }

    @Test
    void applyToOffreStage_Success() throws Exception {
        when(utilisateurService.getMeUtilisateur()).thenReturn(etudiant);
        when(offreStageRepository.findById(offreStageId)).thenReturn(Optional.of(fichierOffreStage));
        when(applicationStageRepository.findByEtudiantAndOffreStage(etudiant, fichierOffreStage)).thenReturn(Optional.empty());
        when(applicationStageRepository.save(any(ApplicationStage.class))).thenReturn(applicationStage);

        ApplicationStageDTO result = applicationStageService.applyToOffreStage(offreStageId);

        assertNotNull(result, "The returned ApplicationStageDTO should not be null.");
        assertEquals(applicationStageDTO.getId(), result.getId(), "The ID should match.");
        assertEquals(applicationStageDTO.getOffreStage_id(), result.getOffreStage_id(), "The OffreStage ID should match.");
        assertEquals(applicationStageDTO.getEtudiant_id(), result.getEtudiant_id(), "The Etudiant ID should match.");

        verify(utilisateurService, times(1)).getMeUtilisateur();
        verify(offreStageRepository, times(1)).findById(offreStageId);
        verify(applicationStageRepository, times(1)).findByEtudiantAndOffreStage(etudiant, fichierOffreStage);
        verify(applicationStageRepository, times(1)).save(any(ApplicationStage.class));
    }

    @Test
    void applyToOffreStage_NotFoundException() throws Exception {
        when(utilisateurService.getMeUtilisateur()).thenReturn(etudiant);
        when(offreStageRepository.findById(offreStageId)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            applicationStageService.applyToOffreStage(offreStageId);
        }, "Expected ResponseStatusException to be thrown.");

        assertNotNull(exception, "Exception should not be null.");

        verify(utilisateurService, times(1)).getMeUtilisateur();
        verify(offreStageRepository, times(1)).findById(offreStageId);
        verify(applicationStageRepository, never()).findByEtudiantAndOffreStage(any(), any());
        verify(applicationStageRepository, never()).save(any());
    }

    @Test
    void applyToOffreStage_DuplicateRequestException() throws Exception {
        // Mock behaviors
        when(utilisateurService.getMeUtilisateur()).thenReturn(etudiant);
        when(offreStageRepository.findById(offreStageId)).thenReturn(Optional.of(fichierOffreStage));
        when(applicationStageRepository.findByEtudiantAndOffreStage(etudiant, fichierOffreStage)).thenReturn(Optional.of(applicationStage));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            applicationStageService.applyToOffreStage(offreStageId);
        }, "Etudiant has already applied to this OffreStage.");

        assertEquals("409 CONFLICT \"Etudiant has already applied to this OffreStage.\"", exception.getMessage(), "Exception message should match.");

        verify(utilisateurService, times(1)).getMeUtilisateur();
        verify(offreStageRepository, times(1)).findById(offreStageId);
        verify(applicationStageRepository, times(1)).findByEtudiantAndOffreStage(etudiant, fichierOffreStage);
        verify(applicationStageRepository, never()).save(any());
    }

    @Test
    void applyToOffreStage_OffreStageNotAccepted() throws Exception {
        fichierOffreStage.setStatus(OffreStage.Status.WAITING);

        when(utilisateurService.getMeUtilisateur()).thenReturn(etudiant);
        when(offreStageRepository.findById(offreStageId)).thenReturn(Optional.of(fichierOffreStage));
        when(applicationStageRepository.findByEtudiantAndOffreStage(etudiant, fichierOffreStage)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            applicationStageService.applyToOffreStage(offreStageId);
        }, "Expected ResponseStatusException to be thrown.");

        assertEquals("400 BAD_REQUEST \"Offre de stage non disponible\"", exception.getMessage(), "Exception message should match.");

        verify(utilisateurService, times(1)).getMeUtilisateur();
        verify(offreStageRepository, times(1)).findById(offreStageId);
        verify(applicationStageRepository, times(1)).findByEtudiantAndOffreStage(etudiant, fichierOffreStage);
        verify(applicationStageRepository, never()).save(any());
    }

    @Test
    void applyToOffreStage_UserIsNotAnEtudiant() throws Exception {
        when(utilisateurService.getMeUtilisateur()).thenReturn(new Enseignant());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            applicationStageService.applyToOffreStage(offreStageId);
        }, "Expected ResponseStatusException to be thrown.");

        assertNotNull(exception, "Exception should not be null.");

        verify(utilisateurService, times(1)).getMeUtilisateur();
        verify(offreStageRepository, never()).findById(any());
        verify(applicationStageRepository, never()).findByEtudiantAndOffreStage(any(), any());
        verify(applicationStageRepository, never()).save(any());
    }

    @Test
    void applyToOffreStage_PrivateOffreStage_AccessDeniedException() throws Exception {
        fichierOffreStage.setVisibility(OffreStage.Visibility.PRIVATE);

        when(utilisateurService.getMeUtilisateur()).thenReturn(etudiant);
        when(offreStageRepository.findById(offreStageId)).thenReturn(Optional.of(fichierOffreStage));
        when(applicationStageRepository.findByEtudiantAndOffreStage(etudiant, fichierOffreStage)).thenReturn(Optional.empty());
        when(etudiantOffreStagePriveeRepository.existsByOffreStageAndEtudiant(fichierOffreStage, etudiant)).thenReturn(false);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            applicationStageService.applyToOffreStage(offreStageId);
        }, "Expected ResponseStatusException to be thrown.");

        assertEquals("400 BAD_REQUEST \"Offre de stage non disponible\"", exception.getMessage(), "Exception message should match.");

        verify(utilisateurService, times(1)).getMeUtilisateur();
        verify(offreStageRepository, times(1)).findById(offreStageId);
        verify(applicationStageRepository, times(1)).findByEtudiantAndOffreStage(etudiant, fichierOffreStage);
        verify(applicationStageRepository, never()).save(any());
    }

    @Test
    void applyToOffreStage_PrivateOffreStage_Success() throws Exception {
        fichierOffreStage.setVisibility(OffreStage.Visibility.PRIVATE);

        when(utilisateurService.getMeUtilisateur()).thenReturn(etudiant);
        when(offreStageRepository.findById(offreStageId)).thenReturn(Optional.of(fichierOffreStage));
        when(applicationStageRepository.findByEtudiantAndOffreStage(etudiant, fichierOffreStage)).thenReturn(Optional.empty());
        when(etudiantOffreStagePriveeRepository.existsByOffreStageAndEtudiant(fichierOffreStage, etudiant)).thenReturn(true);
        when(applicationStageRepository.save(any(ApplicationStage.class))).thenReturn(applicationStage);

        ApplicationStageDTO result = applicationStageService.applyToOffreStage(offreStageId);

        assertNotNull(result, "The returned ApplicationStageDTO should not be null.");
        assertEquals(applicationStageDTO.getId(), result.getId(), "The ID should match.");
        assertEquals(applicationStageDTO.getOffreStage_id(), result.getOffreStage_id(), "The OffreStage ID should match.");
        assertEquals(applicationStageDTO.getEtudiant_id(), result.getEtudiant_id(), "The Etudiant ID should match.");

        verify(utilisateurService, times(1)).getMeUtilisateur();
        verify(offreStageRepository, times(1)).findById(offreStageId);
        verify(applicationStageRepository, times(1)).findByEtudiantAndOffreStage(etudiant, fichierOffreStage);
        verify(applicationStageRepository, times(1)).save(any(ApplicationStage.class));
    }

    @Test
    void applyToOffreStageWrongProgramme() throws Exception {
        // Arrange
        Etudiant etudiant = new Etudiant();
        //FichierOffreStage has GENIE_LOGICIEL programme
        etudiant.setProgramme(Programme.TECHNIQUE_INFORMATIQUE);
        when(utilisateurService.getMeUtilisateur()).thenReturn(etudiant);
        when(offreStageRepository.findById(offreStageId)).thenReturn(Optional.of(fichierOffreStage));

        // Act
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            applicationStageService.applyToOffreStage(offreStageId);
        });

        // Assert
        assertNotNull(exception);
        assertEquals("400 BAD_REQUEST \"Offre de stage non disponible car vous ne faites pas partie du programme associé à l'offre de stage\"", exception.getMessage());

        verify(utilisateurService, times(1)).getMeUtilisateur();
        verify(offreStageRepository, times(1)).findById(offreStageId);
        verify(applicationStageRepository, never()).save(any());
    }

    @Test
    void getApplicationsByEtudiant_Success() {
        Long etudiantId = etudiant.getId();
        List<ApplicationStage> applications = Collections.singletonList(applicationStage);

        when(utilisateurService.getMyUserId()).thenReturn(etudiantId);
        when(applicationStageRepository.findByEtudiantId(etudiantId)).thenReturn(applications);

        List<ApplicationStageAvecInfosDTO> result = applicationStageService.getApplicationsByEtudiant();

        assertNotNull(result, "The result list should not be null.");
        assertEquals(1, result.size(), "The result list should contain exactly one element.");
        ApplicationStageAvecInfosDTO dto = result.getFirst();
        assertEquals(applicationStageAvecInfosDTO.getId(), dto.getId(), "DTO ID should match.");
        assertEquals(applicationStageAvecInfosDTO.getTitle(), dto.getTitle(), "OffreStage title should match.");
        assertEquals(applicationStageAvecInfosDTO.getEntrepriseName(), dto.getEntrepriseName(), "Entreprise name should match.");
        assertEquals(applicationStageAvecInfosDTO.getStatus(), dto.getStatus(), "Status should match.");

        verify(utilisateurService, times(1)).getMyUserId();
        verify(applicationStageRepository, times(1)).findByEtudiantId(etudiantId);
    }

    @Test
    void getApplicationsByEtudiantWithStatus_Success() {
        Long etudiantId = etudiant.getId();
        ApplicationStage.ApplicationStatus status = ApplicationStage.ApplicationStatus.PENDING;
        List<ApplicationStage> applications = Collections.singletonList(applicationStage);

        when(utilisateurService.getMyUserId()).thenReturn(etudiantId);
        when(applicationStageRepository.findByEtudiantIdAndStatusEquals(etudiantId, status)).thenReturn(applications);

        List<ApplicationStageAvecInfosDTO> result = applicationStageService.getApplicationsByEtudiantWithStatus(status);

        assertNotNull(result, "The result list should not be null.");
        assertEquals(1, result.size(), "The result list should contain exactly one element.");
        ApplicationStageAvecInfosDTO dto = result.getFirst();
        assertEquals(applicationStageAvecInfosDTO.getId(), dto.getId(), "DTO ID should match.");
        assertEquals(applicationStageAvecInfosDTO.getTitle(), dto.getTitle(), "OffreStage title should match.");
        assertEquals(applicationStageAvecInfosDTO.getEntrepriseName(), dto.getEntrepriseName(), "Entreprise name should match.");
        assertEquals(applicationStageAvecInfosDTO.getStatus(), dto.getStatus(), "Status should match.");

        verify(utilisateurService, times(1)).getMyUserId();
        verify(applicationStageRepository, times(1)).findByEtudiantIdAndStatusEquals(etudiantId, status);
    }

    @Test
    void getApplicationById_Success() {
        Long etudiantId = etudiant.getId();
        Long applicationId = fichierOffreStage.getId(); // Assuming applicationId refers to OffreStage ID

        when(utilisateurService.getMyUserId()).thenReturn(etudiantId);
        when(applicationStageRepository.findByEtudiantIdAndOffreStageId(etudiantId, applicationId)).thenReturn(Optional.of(applicationStage));

        ApplicationStageAvecInfosDTO result = applicationStageService.getApplicationById(applicationId);

        assertNotNull(result, "The returned ApplicationStageAvecInfosDTO should not be null.");
        assertEquals(applicationStageAvecInfosDTO.getId(), result.getId(), "DTO ID should match.");
        assertEquals(applicationStageAvecInfosDTO.getTitle(), result.getTitle(), "OffreStage title should match.");
        assertEquals(applicationStageAvecInfosDTO.getEntrepriseName(), result.getEntrepriseName(), "Entreprise name should match.");
        assertEquals(applicationStageAvecInfosDTO.getStatus(), result.getStatus(), "Status should match.");

        verify(utilisateurService, times(1)).getMyUserId();
        verify(applicationStageRepository, times(1)).findByEtudiantIdAndOffreStageId(etudiantId, applicationId);
    }

    @Test
    void getApplicationById_NotFoundException() {
        Long etudiantId = etudiant.getId();
        Long applicationId = fichierOffreStage.getId();

        when(utilisateurService.getMyUserId()).thenReturn(etudiantId);
        when(applicationStageRepository.findByEtudiantIdAndOffreStageId(etudiantId, applicationId)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            applicationStageService.getApplicationById(applicationId);
        }, "Expected NotFoundException to be thrown.");

        assertNotNull(exception, "Exception should not be null.");

        verify(utilisateurService, times(1)).getMyUserId();
        verify(applicationStageRepository, times(1)).findByEtudiantIdAndOffreStageId(etudiantId, applicationId);
    }

    @Test
    void getAllApplicationsPourUneOffreByIdTest() {
        // Arrange
        ApplicationStage applicationStage1 = new ApplicationStage(etudiant, fichierOffreStage);
        applicationStage1.setStatus(ApplicationStage.ApplicationStatus.PENDING);
        ApplicationStage applicationStage2 = new ApplicationStage(etudiant2, fichierOffreStage);
        applicationStage2.setStatus(ApplicationStage.ApplicationStatus.SUMMONED);

        List<ApplicationStage> applicationStageList = new ArrayList<>();
        applicationStageList.add(applicationStage1);
        applicationStageList.add(applicationStage2);

        when(applicationStageRepository
                .findAllByOffreStageIdAndStatusIn(1L, List.of(ApplicationStage.ApplicationStatus.PENDING, ApplicationStage.ApplicationStatus.SUMMONED))).thenReturn(applicationStageList);

        // Act
        List<ApplicationStageAvecInfosDTO> applicationStageAvecInfosDTOList = applicationStageService.getAllApplicationsPourUneOffreByIdPendingOrSummoned(1L);

        // Assert
        assertEquals(2, applicationStageAvecInfosDTOList.size());
    }

    @Test
    void accepterOuRefuserApplication_SuccessAccept() {
        // Arrange
        Long applicationId = 1L;
        ApplicationStage.ApplicationStatus newStatus = ApplicationStage.ApplicationStatus.ACCEPTED;
        applicationStage.setStatus(newStatus);
        etudiant.setContractStatus(Etudiant.ContractStatus.NO_CONTRACT);

        ApplicationStage applicationStage1 = new ApplicationStage();
        applicationStage1.setId(applicationId);
        applicationStage1.setEtudiant(etudiant);
        applicationStage1.setOffreStage(fichierOffreStage);
        applicationStage1.setStatus(ApplicationStage.ApplicationStatus.PENDING);

        when(applicationStageRepository.findById(applicationId)).thenReturn(Optional.of(applicationStage1));
        when(utilisateurService.getEtudiantDTO(any())).thenReturn(EtudiantDTO.toDTO(etudiant));
        when(offreStageRepository.findById(fichierOffreStage.getId())).thenReturn(Optional.of(fichierOffreStage));
        when(applicationStageRepository.save(any(ApplicationStage.class))).thenReturn(applicationStage);
        when(etudiantRepository.save(any(Etudiant.class))).thenReturn(etudiant);
        when(etudiantRepository.findEtudiantById(applicationStage1.getEtudiant().getId())).thenReturn(etudiant);


        // Act
        ApplicationStageAvecInfosDTO response = applicationStageService.accepterOuRefuserApplication(applicationId, newStatus);

        // Assert
        assertEquals(newStatus, response.getStatus(), "The status should be updated to ACCEPTED.");
        verify(applicationStageRepository, times(1)).save(any(ApplicationStage.class));
    }

    @Test
    void accepterOuRefuserApplication_AlreadyAccepted() {
        // Arrange
        Long applicationId = 1L;
        ApplicationStage.ApplicationStatus newStatus = ApplicationStage.ApplicationStatus.ACCEPTED;
        applicationStage.setStatus(newStatus);

        when(applicationStageRepository.findById(applicationId)).thenReturn(Optional.of(applicationStage));

        // Act et Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                applicationStageService.accepterOuRefuserApplication(1L, ApplicationStage.ApplicationStatus.ACCEPTED)
        );
        assertEquals("409 CONFLICT \"La candidature a déjà été acceptée ou refusée et ne peut pas être modifiée.\"", exception.getMessage());
    }

    @Test
    public void testAccepterOuRefuserApplicationNotFound() {
        // Act et Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                applicationStageService.accepterOuRefuserApplication(1L, ApplicationStage.ApplicationStatus.ACCEPTED)
        );
        assertEquals("404 NOT_FOUND \"Application not found\"", exception.getMessage());
    }

    @Test
    public void testMettreAJourApplicationSuccess() {
        // Arrange
        ApplicationStage updatedApplicationStage = new ApplicationStage();
        updatedApplicationStage.setId(applicationStage.getId());
        updatedApplicationStage.setOffreStage(fichierOffreStage);
        updatedApplicationStage.setEtudiant(etudiant);
        updatedApplicationStage.setStatus(ApplicationStage.ApplicationStatus.ACCEPTED);

        // Act
        ApplicationStage result = applicationStageService.mettreAJourApplication(applicationStageAvecInfosDTO, etudiant, fichierOffreStage, ApplicationStage.ApplicationStatus.ACCEPTED);

        // Assert
        assertEquals(updatedApplicationStage.getId(), result.getId());
        assertEquals(updatedApplicationStage.getEtudiant(), result.getEtudiant());
        assertEquals(updatedApplicationStage.getOffreStage(), result.getOffreStage());
        assertEquals(updatedApplicationStage.getStatus(), result.getStatus());
    }


    @Test
    void summonEtudiant_Success() {
        // Arrange
        when(applicationStageRepository.findById(1L)).thenReturn(Optional.of(applicationStage));
        when(utilisateurService.getMyUserId()).thenReturn(1L);
        when(applicationStageRepository.save(any(ApplicationStage.class))).thenReturn(applicationStage);
        applicationStageAvecInfosDTO.setStatus(ApplicationStage.ApplicationStatus.SUMMONED);

        // Act
        ApplicationStageAvecInfosDTO result = applicationStageService.summonEtudiant(1L, summonEtudiantDTO);

        // Assert
        assertNotNull(result);
        assertEquals(applicationStageAvecInfosDTO.getId(), result.getId());
        assertEquals(applicationStageAvecInfosDTO.getTitle(), result.getTitle());
        assertEquals(applicationStageAvecInfosDTO.getEntrepriseName(), result.getEntrepriseName());
        assertEquals(applicationStageAvecInfosDTO.getStatus(), result.getStatus());

        verify(applicationStageRepository, times(1)).findById(1L);
        verify(utilisateurService, times(1)).getMyUserId();
        verify(applicationStageRepository, times(1)).save(any(ApplicationStage.class));
    }

    @Test
    void summonEtudiant_NotFoundException() {
        // Arrange
        when(applicationStageRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            applicationStageService.summonEtudiant(1L, summonEtudiantDTO);
        });

        // Assert
        assertNotNull(exception);
        assertEquals("404 NOT_FOUND \"Application not found\"", exception.getMessage());

        verify(applicationStageRepository, times(1)).findById(1L);
        verify(utilisateurService, never()).getMyUserId();
        verify(applicationStageRepository, never()).save(any(ApplicationStage.class));
    }

    @Test
    void summonEtudiant_ForbiddenException() {
        // Arrange
        when(applicationStageRepository.findById(1L)).thenReturn(Optional.of(applicationStage));
        when(utilisateurService.getMyUserId()).thenReturn(2L);

        // Act
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            applicationStageService.summonEtudiant(1L, summonEtudiantDTO);
        });

        // Assert
        assertNotNull(exception);
        assertEquals("403 FORBIDDEN \"You are not allowed to summon this student\"", exception.getMessage());

        verify(applicationStageRepository, times(1)).findById(1L);
        verify(utilisateurService, times(1)).getMyUserId();
        verify(applicationStageRepository, never()).save(any(ApplicationStage.class));
    }

    @Test
    void summonEtudiant_BadRequestException() {
        // Arrange
        applicationStage.setStatus(ApplicationStage.ApplicationStatus.ACCEPTED);
        when(applicationStageRepository.findById(1L)).thenReturn(Optional.of(applicationStage));
        when(utilisateurService.getMyUserId()).thenReturn(1L);

        // Act
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            applicationStageService.summonEtudiant(1L, summonEtudiantDTO);
        });

        // Assert
        assertNotNull(exception);
        assertEquals("400 BAD_REQUEST \"Application is not pending\"", exception.getMessage());

        verify(applicationStageRepository, times(1)).findById(1L);
        verify(utilisateurService, times(1)).getMyUserId();
        verify(applicationStageRepository, never()).save(any(ApplicationStage.class));
    }

    @Test
    void testChangeContractStatusToPending_Success() {
        // Arrange
        Etudiant.ContractStatus newContractStatus = Etudiant.ContractStatus.PENDING;
        etudiant.setContractStatus(Etudiant.ContractStatus.NO_CONTRACT);
        when(utilisateurService.getEtudiantDTO(any())).thenReturn(EtudiantDTO.toDTO(etudiant));
        when(etudiantRepository.findEtudiantById(anyLong())).thenReturn(etudiant);
        when(etudiantRepository.save(any(Etudiant.class))).thenAnswer(invocation -> {
            Etudiant savedEtudiant = invocation.getArgument(0);
            savedEtudiant.setContractStatus(Etudiant.ContractStatus.PENDING);
            return savedEtudiant;
        });

        // Act
        EtudiantDTO etudiantDTO = applicationStageService.changeContractStatusToPending(etudiant.getId());

        // Assert
        assertEquals(newContractStatus, etudiantDTO.getContractStatus());
        verify(etudiantRepository, times(1)).save(any(Etudiant.class));
    }

    @Test
    void testChangeContractStatusToPending_etudiantEmpty() {
        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            applicationStageService.changeContractStatusToPending(etudiant.getId());
        });

        assertEquals("404 NOT_FOUND \"L'étudiant avec l'ID 1 est innexistant\"", exception.getMessage());
    }

    @Test
    void testChangeContractStatusToPending_contractStatusIsAlreadyPending() {
        // Arrange
        etudiant.setContractStatus(Etudiant.ContractStatus.PENDING);
        when(etudiantRepository.findEtudiantById(anyLong())).thenReturn(etudiant);
        when(utilisateurService.getEtudiantDTO(1L)).thenReturn(EtudiantDTO.toDTO(etudiant));

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            applicationStageService.changeContractStatusToPending(etudiant.getId());
        });

        assertEquals("409 CONFLICT \"L'étudiant a déjà un stage actif ou une demande de stage active\"", exception.getMessage());
    }

    @Test
    void answerSummon_Success() {
        // Arrange
        Long applicationStageId = 1L;
        AnswerSummonDTO answerSummonDTO = new AnswerSummonDTO("I accept the summon.", Convocation.ConvocationStatus.ACCEPTED);

        when(applicationStageRepository.findById(applicationStageId)).thenReturn(Optional.of(applicationStage));
        when(utilisateurService.getMyUserId()).thenReturn(etudiant.getId());
        when(applicationStageRepository.save(any(ApplicationStage.class))).thenReturn(applicationStage);

        applicationStage.setStatus(ApplicationStage.ApplicationStatus.SUMMONED);
        applicationStage.setConvocation(new Convocation(applicationStage, summonEtudiantDTO));
        System.out.println(applicationStage.getConvocation().getStatus());

        // Act
        ApplicationStageAvecInfosDTO result = applicationStageService.answerSummon(applicationStageId, answerSummonDTO);

        // Assert
        assertNotNull(result, "The result should not be null.");
        verify(applicationStageRepository, times(1)).findById(applicationStageId);
        verify(utilisateurService, times(1)).getMyUserId();
        verify(applicationStageRepository, times(1)).save(applicationStage);
    }

    @Test
    void answerSummon_ApplicationStageNotFound() {
        // Arrange
        Long applicationStageId = 1L;
        AnswerSummonDTO answerSummonDTO = new AnswerSummonDTO("I accept the summon.", Convocation.ConvocationStatus.ACCEPTED);

        when(applicationStageRepository.findById(applicationStageId)).thenReturn(Optional.empty());

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            applicationStageService.answerSummon(applicationStageId, answerSummonDTO);
        });

        assertEquals("404 NOT_FOUND \"Application not found\"", exception.getMessage());
        verify(applicationStageRepository, times(1)).findById(applicationStageId);
        verify(utilisateurService, times(0)).getMyUserId();
        verify(applicationStageRepository, times(0)).save(any(ApplicationStage.class));
    }

    @Test
    void answerSummon_UserNotAuthorized() {
        // Arrange
        Long applicationStageId = 1L;
        AnswerSummonDTO answerSummonDTO = new AnswerSummonDTO("I accept the summon.", Convocation.ConvocationStatus.ACCEPTED);

        when(applicationStageRepository.findById(applicationStageId)).thenReturn(Optional.of(applicationStage));
        when(utilisateurService.getMyUserId()).thenReturn(etudiant2.getId()); // Different user

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            applicationStageService.answerSummon(applicationStageId, answerSummonDTO);
        });

        assertEquals("403 FORBIDDEN \"You are not allowed to answer this summon\"", exception.getMessage());
        verify(applicationStageRepository, times(1)).findById(applicationStageId);
        verify(utilisateurService, times(1)).getMyUserId();
        verify(applicationStageRepository, times(0)).save(any(ApplicationStage.class));
    }

    @Test
    void answerSummon_ApplicationNotSummoned() {
        // Arrange
        Long applicationStageId = 1L;
        AnswerSummonDTO answerSummonDTO = new AnswerSummonDTO("I accept the summon.", Convocation.ConvocationStatus.ACCEPTED);


        // Set status to PENDING instead of SUMMONED
        applicationStage.setStatus(ApplicationStage.ApplicationStatus.PENDING);

        when(applicationStageRepository.findById(applicationStageId)).thenReturn(Optional.of(applicationStage));
        when(utilisateurService.getMyUserId()).thenReturn(etudiant.getId());

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            applicationStageService.answerSummon(applicationStageId, answerSummonDTO);
        });

        assertEquals("400 BAD_REQUEST \"Application is not summoned\"", exception.getMessage());
        verify(applicationStageRepository, times(1)).findById(applicationStageId);
        verify(utilisateurService, times(1)).getMyUserId();
        verify(applicationStageRepository, times(0)).save(any(ApplicationStage.class));
    }

    @Test
    void answerSummon_ConvocationNotPending() {
        // Arrange
        Long applicationStageId = 1L;
        AnswerSummonDTO answerSummonDTO = new AnswerSummonDTO("I accept the summon.", Convocation.ConvocationStatus.ACCEPTED);

        // Set convocation status to ACCEPTED instead of PENDING
        applicationStage.setStatus(ApplicationStage.ApplicationStatus.SUMMONED);
        applicationStage.setConvocation(new Convocation(applicationStage, summonEtudiantDTO));
        applicationStage.getConvocation().setStatus(Convocation.ConvocationStatus.ACCEPTED);

        when(applicationStageRepository.findById(applicationStageId)).thenReturn(Optional.of(applicationStage));
        when(utilisateurService.getMyUserId()).thenReturn(etudiant.getId());

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            applicationStageService.answerSummon(applicationStageId, answerSummonDTO);
        });

        assertEquals("400 BAD_REQUEST \"Convocation is not pending\"", exception.getMessage());
        verify(applicationStageRepository, times(1)).findById(applicationStageId);
        verify(utilisateurService, times(1)).getMyUserId();
        verify(applicationStageRepository, times(0)).save(any(ApplicationStage.class));
    }

    @Test
    void answerSummon_InvalidConvocationStatus() {
        // Arrange
        Long applicationStageId = 1L;
        AnswerSummonDTO answerSummonDTO = new AnswerSummonDTO("Invalid status.", Convocation.ConvocationStatus.PENDING);

        applicationStage.setStatus(ApplicationStage.ApplicationStatus.SUMMONED);
        applicationStage.setConvocation(new Convocation(applicationStage, summonEtudiantDTO));

        when(applicationStageRepository.findById(applicationStageId)).thenReturn(Optional.of(applicationStage));
        when(utilisateurService.getMyUserId()).thenReturn(etudiant.getId());

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            applicationStageService.answerSummon(applicationStageId, answerSummonDTO);
        });

        assertEquals("400 BAD_REQUEST \"Invalid status\"", exception.getMessage());
        verify(applicationStageRepository, times(1)).findById(applicationStageId);
        verify(utilisateurService, times(1)).getMyUserId();
        verify(applicationStageRepository, times(0)).save(any(ApplicationStage.class));
    }

    @Test
    void testGetEtudiantFromApplicationId_Success() {
        // Arrange
        when(applicationStageRepository.findById(anyLong())).thenReturn(Optional.ofNullable(applicationStage));

        // Act
        EtudiantDTO etudiantDTO = applicationStageService.getEtudiantFromApplicationId(applicationStage.getId());

        // Assert
        assertEquals(1L, etudiantDTO.getId());
        assertEquals("Roberto", etudiantDTO.getPrenom());
        assertEquals("Berrios", etudiantDTO.getNom());
        verify(applicationStageRepository, times(1)).findById(anyLong());
    }

    @Test
    void testGetOffreStageFromApplicationId_Success() {
        // Arrange
        when(applicationStageRepository.findById(anyLong())).thenReturn(Optional.ofNullable(applicationStage));

        // Act
        OffreStageDTO offreStageDTO = applicationStageService.getOffreStageFromApplicationId(applicationStage.getId());

        // Assert
        assertEquals(1L, offreStageDTO.getId());
        assertEquals("Software Engineering Internship", offreStageDTO.getTitle());
        assertEquals("Tech Corp", offreStageDTO.getEntrepriseName());
        verify(applicationStageRepository, times(1)).findById(anyLong());
    }
}
