package com.projet.mycose.service;

import com.projet.mycose.dto.EtudiantDTO;

import com.projet.mycose.modele.*;
import com.projet.mycose.modele.auth.Credentials;
import com.projet.mycose.modele.auth.Role;
import com.projet.mycose.repository.ApplicationStageRepository;
import com.projet.mycose.repository.EtudiantOffreStagePriveeRepository;
import com.projet.mycose.repository.OffreStageRepository;
import com.projet.mycose.dto.ApplicationStageAvecInfosDTO;
import com.projet.mycose.dto.ApplicationStageDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

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

    @BeforeEach
    void setup() {
        offreStageId = 1L;

        etudiant = new Etudiant();
        etudiant.setId(1L);
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

        applicationStageAvecInfosDTO.setId(applicationId);
        applicationStageAvecInfosDTO.setEtudiant_id(etudiant.getId());
        applicationStageAvecInfosDTO.setOffreStage_id(fichierOffreStage.getId());
        applicationStageAvecInfosDTO.setStatus(ApplicationStage.ApplicationStatus.PENDING);

        when(utilisateurService.getEtudiantDTO(any())).thenReturn(EtudiantDTO.toDTO(etudiant));
        when(offreStageRepository.findById(fichierOffreStage.getId())).thenReturn(Optional.of(fichierOffreStage));
        when(applicationStageRepository.save(any(ApplicationStage.class))).thenReturn(applicationStage);
        when(applicationStageRepository.findById(applicationId)).thenReturn(Optional.of(applicationStage));

        // Act
        ApplicationStageAvecInfosDTO response = applicationStageService.accepterOuRefuserApplication(applicationId, newStatus);

        // Assert
        assertEquals(newStatus, response.getStatus(), "The status should be updated to ACCEPTED.");
        verify(applicationStageRepository, times(1)).save(any(ApplicationStage.class));
    }

    @Test
    public void testAccepterOuRefuserApplicationNotFound() {
        // Act et Assert
        assertThrows(ResponseStatusException.class, () ->
                applicationStageService.accepterOuRefuserApplication(1L, ApplicationStage.ApplicationStatus.ACCEPTED)
        );
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
        ApplicationStageAvecInfosDTO result = applicationStageService.summonEtudiant(1L);

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
            applicationStageService.summonEtudiant(1L);
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
            applicationStageService.summonEtudiant(1L);
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
            applicationStageService.summonEtudiant(1L);
        });

        // Assert
        assertNotNull(exception);
        assertEquals("400 BAD_REQUEST \"Application is not pending\"", exception.getMessage());

        verify(applicationStageRepository, times(1)).findById(1L);
        verify(utilisateurService, times(1)).getMyUserId();
        verify(applicationStageRepository, never()).save(any(ApplicationStage.class));
    }
}
