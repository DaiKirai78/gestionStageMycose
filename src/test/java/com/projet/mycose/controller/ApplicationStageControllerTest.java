package com.projet.mycose.controller;

import com.projet.mycose.modele.ApplicationStage;
import com.projet.mycose.service.ApplicationStageService;
import com.projet.mycose.dto.ApplicationStageAvecInfosDTO;
import com.projet.mycose.dto.ApplicationStageDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.web.server.ResponseStatusException;

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
    private ApplicationStageDTO applicationStageDTO;
    private ApplicationStageAvecInfosDTO applicationStageAvecInfosDTO;
    private List<ApplicationStageAvecInfosDTO> applicationStageAvecInfosDTOList;

    @BeforeEach
    void setup() {
        id = 1L;

        applicationStageDTO = new ApplicationStageDTO();
        // Initialize fields of applicationStageDTO as needed

        applicationStageAvecInfosDTO = new ApplicationStageAvecInfosDTO();
        // Initialize fields of applicationStageAvecInfosDTO as needed

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
        when(applicationStageService.summonEtudiant(id)).thenReturn(applicationStageAvecInfosDTO);

        ResponseEntity<ApplicationStageAvecInfosDTO> response = applicationStageController.summonEtudiant(id);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(applicationStageAvecInfosDTO, response.getBody());
        verify(applicationStageService, times(1)).summonEtudiant(id);
    }
}
