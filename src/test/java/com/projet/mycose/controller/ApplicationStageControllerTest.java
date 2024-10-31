package com.projet.mycose.controller;

import com.projet.mycose.dto.SummonEtudiantDTO;
import com.projet.mycose.modele.*;
import com.projet.mycose.modele.auth.Credentials;
import com.projet.mycose.modele.auth.Role;
import com.projet.mycose.service.ApplicationStageService;
import com.projet.mycose.dto.ApplicationStageAvecInfosDTO;
import com.projet.mycose.dto.ApplicationStageDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ApplicationStageControllerTest {

    @Mock
    private ApplicationStageService applicationStageService;

    @InjectMocks
    private ApplicationStageController applicationStageController;

    private Long id;
    private Long offreStageId;
    private Etudiant etudiant;
    private FichierOffreStage fichierOffreStage;
    private ApplicationStageDTO applicationStageDTO;
    private ApplicationStage applicationStage;
    private ApplicationStageAvecInfosDTO applicationStageAvecInfosDTO;
    private List<ApplicationStageAvecInfosDTO> applicationStageAvecInfosDTOList;

    @BeforeEach
    void setup() {
        id = 1L;

        offreStageId = 1L;

        applicationStageDTO = new ApplicationStageDTO();
        // Initialize fields of applicationStageDTO as needed

        applicationStageAvecInfosDTO = new ApplicationStageAvecInfosDTO();
        // Initialize fields of applicationStageAvecInfosDTO as needed

        etudiant = new Etudiant();
        etudiant.setId(1L);
        Credentials credentials = new Credentials("example@gmail.com", "passw0rd", Role.ETUDIANT);
        etudiant.setCredentials(credentials);
        etudiant.setProgramme(Programme.GENIE_LOGICIEL);

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

        applicationStageAvecInfosDTOList = List.of(applicationStageAvecInfosDTO);
    }

    @Test
    void applyForStage_Success() throws Exception {
        when(applicationStageService.applyToOffreStage(id)).thenReturn(applicationStageDTO);

        ResponseEntity<ApplicationStageDTO> response = applicationStageController.applyForStage(id);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(applicationStageDTO, response.getBody());
        verify(applicationStageService, times(1)).applyToOffreStage(id);
    }

    @Test
    void applyForStage_AccessDeniedException() throws Exception {
        when(applicationStageService.applyToOffreStage(id)).thenThrow(new AccessDeniedException("Access Denied"));

        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> {
            applicationStageController.applyForStage(id);
        });

        assertEquals("Access Denied", exception.getMessage());
        verify(applicationStageService, times(1)).applyToOffreStage(id);
    }

    @Test
    void applyForStage_NotFoundException() throws Exception {
        when(applicationStageService.applyToOffreStage(id)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "OffreStage not found"));

        assertThrows(ResponseStatusException.class, () -> {
            applicationStageController.applyForStage(id);
        });

        verify(applicationStageService, times(1)).applyToOffreStage(id);
    }

    @Test
    void getMyApplications_Success() {
        when(applicationStageService.getApplicationsByEtudiant()).thenReturn(applicationStageAvecInfosDTOList);

        ResponseEntity<List<ApplicationStageAvecInfosDTO>> response = applicationStageController.getMyApplications();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(applicationStageAvecInfosDTOList, response.getBody());
        verify(applicationStageService, times(1)).getApplicationsByEtudiant();
    }

    @Test
    void getMyApplicationsWithStatus_Success() {
        ApplicationStage.ApplicationStatus status = ApplicationStage.ApplicationStatus.PENDING;
        when(applicationStageService.getApplicationsByEtudiantWithStatus(status)).thenReturn(applicationStageAvecInfosDTOList);

        ResponseEntity<List<ApplicationStageAvecInfosDTO>> response = applicationStageController.getMyApplicationsWithStatus(status);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(applicationStageAvecInfosDTOList, response.getBody());
        verify(applicationStageService, times(1)).getApplicationsByEtudiantWithStatus(status);
    }

    @Test
    void getMyApplication_Success() {
        when(applicationStageService.getApplicationById(id)).thenReturn(applicationStageAvecInfosDTO);

        ResponseEntity<ApplicationStageAvecInfosDTO> response = applicationStageController.getMyApplication(id);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(applicationStageAvecInfosDTO, response.getBody());
        verify(applicationStageService, times(1)).getApplicationById(id);
    }

    @Test
    void getMyApplication_NotFoundException() {
        when(applicationStageService.getApplicationById(id)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Application not found"));

        assertThrows(ResponseStatusException.class, () -> {
            applicationStageController.getMyApplication(id);
        });

        verify(applicationStageService, times(1)).getApplicationById(id);
    }

    @Test
    void summonEtudiant_Success() {
        SummonEtudiantDTO summonEtudiantDTO = new SummonEtudiantDTO();
        summonEtudiantDTO.setScheduledAt(LocalDateTime.now().plusDays(1));
        summonEtudiantDTO.setLocation("Tech Corp");
        summonEtudiantDTO.setMessageConvocation("You have been summoned for an interview.");
        when(applicationStageService.summonEtudiant(id, summonEtudiantDTO)).thenReturn(applicationStageAvecInfosDTO);

        ResponseEntity<ApplicationStageAvecInfosDTO> response = applicationStageController.summonEtudiant(id, summonEtudiantDTO);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(applicationStageAvecInfosDTO, response.getBody());
        verify(applicationStageService, times(1)).summonEtudiant(id, summonEtudiantDTO);
    }

    @Test
    void testAccepterApplication_Success() {
        // Arrange
        when(applicationStageService.accepterOuRefuserApplication(id, ApplicationStage.ApplicationStatus.ACCEPTED)).thenReturn(ApplicationStageAvecInfosDTO.toDTO(applicationStage));

        // Act
        ResponseEntity<?> response = applicationStageController.accepterApplication(id);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(response.getBody(), "Application acceptée");
        verify(applicationStageService, times(1)).accepterOuRefuserApplication(id, ApplicationStage.ApplicationStatus.ACCEPTED);
    }

    @Test
    void testAccepterApplication_Echec() {
        // Arrange
        when(applicationStageService.accepterOuRefuserApplication(20L, ApplicationStage.ApplicationStatus.ACCEPTED))
                .thenThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        // Act
        ResponseEntity<?> response = applicationStageController.accepterApplication(20L);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void testRefuserApplication_Success() {
        // Arrange
        when(applicationStageService.accepterOuRefuserApplication(id, ApplicationStage.ApplicationStatus.REJECTED)).thenReturn(ApplicationStageAvecInfosDTO.toDTO(applicationStage));

        // Act
        ResponseEntity<?> response = applicationStageController.refuserApplication(id);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(response.getBody(), "Application refusée");
        verify(applicationStageService, times(1)).accepterOuRefuserApplication(id, ApplicationStage.ApplicationStatus.REJECTED);
    }

    @Test
    void testRefuserApplication_Echec() {
        // Arrange
        when(applicationStageService.accepterOuRefuserApplication(20L, ApplicationStage.ApplicationStatus.ACCEPTED))
                .thenThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        // Act
        ResponseEntity<?> response = applicationStageController.refuserApplication(20L);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}
