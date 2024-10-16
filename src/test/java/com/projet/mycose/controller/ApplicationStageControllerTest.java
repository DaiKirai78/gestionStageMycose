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

import java.nio.file.AccessDeniedException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ApplicationStageControllerTest {

    @Mock
    private ApplicationStageService applicationStageService;

    @InjectMocks
    private ApplicationStageController applicationStageController;

    private String token;
    private Long id;
    private ApplicationStageDTO applicationStageDTO;
    private ApplicationStageAvecInfosDTO applicationStageAvecInfosDTO;
    private List<ApplicationStageAvecInfosDTO> applicationStageAvecInfosDTOList;

    @BeforeEach
    void setup() {
        token = "Bearer sampleToken";
        id = 1L;

        applicationStageDTO = new ApplicationStageDTO();
        // Initialize fields of applicationStageDTO as needed

        applicationStageAvecInfosDTO = new ApplicationStageAvecInfosDTO();
        // Initialize fields of applicationStageAvecInfosDTO as needed

        applicationStageAvecInfosDTOList = List.of(applicationStageAvecInfosDTO);
    }

    @Test
    void applyForStage_Success() throws Exception {
        when(applicationStageService.applyToOffreStage(token, id)).thenReturn(applicationStageDTO);

        ResponseEntity<ApplicationStageDTO> response = applicationStageController.applyForStage(id, token);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(applicationStageDTO, response.getBody());
        verify(applicationStageService, times(1)).applyToOffreStage(token, id);
    }

    @Test
    void applyForStage_AccessDeniedException() throws Exception {
        when(applicationStageService.applyToOffreStage(token, id)).thenThrow(new AccessDeniedException("Access Denied"));

        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> {
            applicationStageController.applyForStage(id, token);
        });

        assertEquals("Access Denied", exception.getMessage());
        verify(applicationStageService, times(1)).applyToOffreStage(token, id);
    }

    @Test
    void applyForStage_NotFoundException() throws Exception {
        when(applicationStageService.applyToOffreStage(token, id)).thenThrow(new ChangeSetPersister.NotFoundException());

        assertThrows(ChangeSetPersister.NotFoundException.class, () -> {
            applicationStageController.applyForStage(id, token);
        });

        verify(applicationStageService, times(1)).applyToOffreStage(token, id);
    }

    @Test
    void getMyApplications_Success() {
        when(applicationStageService.getApplicationsByEtudiant(token)).thenReturn(applicationStageAvecInfosDTOList);

        ResponseEntity<List<ApplicationStageAvecInfosDTO>> response = applicationStageController.getMyApplications(token);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(applicationStageAvecInfosDTOList, response.getBody());
        verify(applicationStageService, times(1)).getApplicationsByEtudiant(token);
    }

    @Test
    void getMyApplicationsWithStatus_Success() {
        ApplicationStage.ApplicationStatus status = ApplicationStage.ApplicationStatus.PENDING;
        when(applicationStageService.getApplicationsByEtudiantWithStatus(token, status)).thenReturn(applicationStageAvecInfosDTOList);

        ResponseEntity<List<ApplicationStageAvecInfosDTO>> response = applicationStageController.getMyApplicationsWithStatus(token, status);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(applicationStageAvecInfosDTOList, response.getBody());
        verify(applicationStageService, times(1)).getApplicationsByEtudiantWithStatus(token, status);
    }

    @Test
    void getMyApplication_Success() throws Exception {
        when(applicationStageService.getApplicationById(token, id)).thenReturn(applicationStageAvecInfosDTO);

        ResponseEntity<ApplicationStageAvecInfosDTO> response = applicationStageController.getMyApplication(token, id);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(applicationStageAvecInfosDTO, response.getBody());
        verify(applicationStageService, times(1)).getApplicationById(token, id);
    }

    @Test
    void getMyApplication_NotFoundException() throws Exception {
        when(applicationStageService.getApplicationById(token, id)).thenThrow(new ChangeSetPersister.NotFoundException());

        assertThrows(ChangeSetPersister.NotFoundException.class, () -> {
            applicationStageController.getMyApplication(token, id);
        });

        verify(applicationStageService, times(1)).getApplicationById(token, id);
    }
}
