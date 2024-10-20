package com.projet.mycose.service;

import com.projet.mycose.modele.*;
import com.projet.mycose.repository.ApplicationStageRepository;
import com.projet.mycose.repository.OffreStageRepository;
import com.projet.mycose.dto.ApplicationStageAvecInfosDTO;
import com.projet.mycose.dto.ApplicationStageDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.web.server.ResponseStatusException;

import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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

        etudiant2 = new Etudiant();
        etudiant2.setId(2L);

        fichierOffreStage = new FichierOffreStage();
        fichierOffreStage.setId(offreStageId);
        fichierOffreStage.setTitle("Software Engineering Internship");
        fichierOffreStage.setEntrepriseName("Tech Corp");
        fichierOffreStage.setStatus(OffreStage.Status.ACCEPTED);
        fichierOffreStage.setStatusDescription("Approved for applications.");
        fichierOffreStage.setCreateur(new Etudiant());
        fichierOffreStage.setFilename("internship_offer.pdf");
        fichierOffreStage.setData(new byte[]{1, 2, 3});

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
    void applyToOffreStage_AccessDeniedException() throws Exception {
        fichierOffreStage.setStatus(OffreStage.Status.WAITING);

        when(utilisateurService.getMeUtilisateur()).thenReturn(etudiant);
        when(offreStageRepository.findById(offreStageId)).thenReturn(Optional.of(fichierOffreStage));
        when(applicationStageRepository.findByEtudiantAndOffreStage(etudiant, fichierOffreStage)).thenReturn(Optional.empty());

        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> {
            applicationStageService.applyToOffreStage(offreStageId);
        }, "Expected AccessDeniedException to be thrown.");

        assertEquals("Offre de stage non disponible", exception.getMessage(), "Exception message should match.");

        verify(utilisateurService, times(1)).getMeUtilisateur();
        verify(offreStageRepository, times(1)).findById(offreStageId);
        verify(applicationStageRepository, times(1)).findByEtudiantAndOffreStage(etudiant, fichierOffreStage);
        verify(applicationStageRepository, never()).save(any());
    }

    @Test
    void getApplicationsByEtudiant_Success() {
        Long etudiantId = etudiant.getId();
        List<ApplicationStage> applications = Arrays.asList(applicationStage);

        when(utilisateurService.getMyUserId()).thenReturn(etudiantId);
        when(applicationStageRepository.findByEtudiantId(etudiantId)).thenReturn(applications);

        List<ApplicationStageAvecInfosDTO> result = applicationStageService.getApplicationsByEtudiant();

        assertNotNull(result, "The result list should not be null.");
        assertEquals(1, result.size(), "The result list should contain exactly one element.");
        ApplicationStageAvecInfosDTO dto = result.get(0);
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
        List<ApplicationStage> applications = Arrays.asList(applicationStage);

        when(utilisateurService.getMyUserId()).thenReturn(etudiantId);
        when(applicationStageRepository.findByEtudiantIdAndStatusEquals(etudiantId, status)).thenReturn(applications);

        List<ApplicationStageAvecInfosDTO> result = applicationStageService.getApplicationsByEtudiantWithStatus(status);

        assertNotNull(result, "The result list should not be null.");
        assertEquals(1, result.size(), "The result list should contain exactly one element.");
        ApplicationStageAvecInfosDTO dto = result.get(0);
        assertEquals(applicationStageAvecInfosDTO.getId(), dto.getId(), "DTO ID should match.");
        assertEquals(applicationStageAvecInfosDTO.getTitle(), dto.getTitle(), "OffreStage title should match.");
        assertEquals(applicationStageAvecInfosDTO.getEntrepriseName(), dto.getEntrepriseName(), "Entreprise name should match.");
        assertEquals(applicationStageAvecInfosDTO.getStatus(), dto.getStatus(), "Status should match.");

        verify(utilisateurService, times(1)).getMyUserId();
        verify(applicationStageRepository, times(1)).findByEtudiantIdAndStatusEquals(etudiantId, status);
    }

    @Test
    void getApplicationById_Success() throws Exception {
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
    void getApplicationById_NotFoundException() throws Exception {
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
        applicationStage1.setStatus(ApplicationStage.ApplicationStatus.ACCEPTED);
        ApplicationStage applicationStage2 = new ApplicationStage(etudiant2, fichierOffreStage);
        applicationStage2.setStatus(ApplicationStage.ApplicationStatus.ACCEPTED);

        List<ApplicationStage> applicationStageList = new ArrayList<>();
        applicationStageList.add(applicationStage1);
        applicationStageList.add(applicationStage2);

        when(applicationStageRepository
                .findAllByOffreStageIdAndStatusEquals(1L, ApplicationStage.ApplicationStatus.ACCEPTED)).thenReturn(applicationStageList);

        // Act
        List<ApplicationStageAvecInfosDTO> applicationStageAvecInfosDTOList = applicationStageService.getAllApplicationsPourUneOffreById(1L);

        // Assert
        assertEquals(2, applicationStageAvecInfosDTOList.size());
    }
}
