package com.projet.mycose.controller;

import com.projet.mycose.dto.*;
import com.projet.mycose.exceptions.GlobalExceptionHandler;
import com.projet.mycose.exceptions.ResourceNotFoundException;
import com.projet.mycose.modele.*;
import com.projet.mycose.modele.auth.Credentials;
import com.projet.mycose.modele.auth.Role;
import com.projet.mycose.service.ApplicationStageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;


import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class ApplicationStageControllerTest {
    @Mock
    private ApplicationStageService applicationStageService;

    @InjectMocks
    private ApplicationStageController applicationStageController;

    private MockMvc mockMvc;

    private Long id;
    private Long offreStageId;
    private Etudiant etudiant;
    private FichierOffreStage fichierOffreStage;
    private ApplicationStageDTO applicationStageDTO;
    private ApplicationStage applicationStage;
    private ApplicationStageAvecInfosDTO applicationStageAvecInfosDTO;
    private ApplicationStageAvecInfosDTO applicationStageAvecInfosDTO2;
    private List<ApplicationStageAvecInfosDTO> applicationStageAvecInfosDTOList;
    private AnswerSummonDTO answerSummonDTO;


    @BeforeEach
    void setup() {
        id = 1L;

        offreStageId = 1L;

        applicationStageDTO = new ApplicationStageDTO();
        // Initialize fields of applicationStageDTO as needed

        applicationStageAvecInfosDTO = new ApplicationStageAvecInfosDTO();
        applicationStageAvecInfosDTO2 = new ApplicationStageAvecInfosDTO();
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

        answerSummonDTO = new AnswerSummonDTO("I accept the summon.", Convocation.ConvocationStatus.ACCEPTED);

        mockMvc = MockMvcBuilders.standaloneSetup(applicationStageController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
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
        when(applicationStageService.getMyApplicationByOffreStageID(id)).thenReturn(applicationStageAvecInfosDTO);

        ResponseEntity<ApplicationStageAvecInfosDTO> response = applicationStageController.getMyApplicationByOffreStageID(id);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(applicationStageAvecInfosDTO, response.getBody());
        verify(applicationStageService, times(1)).getMyApplicationByOffreStageID(id);
    }

    @Test
    void getMyApplication_NotFoundException() {
        when(applicationStageService.getMyApplicationByOffreStageID(id)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Application not found"));

        assertThrows(ResponseStatusException.class, () -> {
            applicationStageController.getMyApplicationByOffreStageID(id);
        });

        verify(applicationStageService, times(1)).getMyApplicationByOffreStageID(id);
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
    void answerSummon_Success() {
        // Arrange
        when(applicationStageService.answerSummon(id, answerSummonDTO)).thenReturn(applicationStageAvecInfosDTO);

        // Act
        ResponseEntity<ApplicationStageAvecInfosDTO> response = applicationStageController.answerSummon(id, answerSummonDTO);

        // Assert
        assertNotNull(response, "Response should not be null.");
        assertEquals(HttpStatus.OK, response.getStatusCode(), "HTTP Status should be OK.");
        assertEquals(applicationStageAvecInfosDTO, response.getBody(), "Response body should match the expected DTO.");
        verify(applicationStageService, times(1)).answerSummon(id, answerSummonDTO);
    }

    @Test
    void answerSummon_ApplicationStageNotFound() {
        // Arrange
        when(applicationStageService.answerSummon(id, answerSummonDTO))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Application not found"));

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            applicationStageController.answerSummon(id, answerSummonDTO);
        }, "Expected a ResponseStatusException to be thrown.");

        assertEquals("404 NOT_FOUND \"Application not found\"", exception.getMessage());
        verify(applicationStageService, times(1)).answerSummon(id, answerSummonDTO);
    }

    @Test
    void answerSummon_UserNotAuthorized() {
        // Arrange
        when(applicationStageService.answerSummon(id, answerSummonDTO))
                .thenThrow(new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to answer this summon"));

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            applicationStageController.answerSummon(id, answerSummonDTO);
        }, "Expected a ResponseStatusException to be thrown.");

        assertEquals("403 FORBIDDEN \"You are not allowed to answer this summon\"", exception.getMessage());
        verify(applicationStageService, times(1)).answerSummon(id, answerSummonDTO);
    }

    @Test
    void answerSummon_ApplicationNotSummoned() {
        // Arrange
        when(applicationStageService.answerSummon(id, answerSummonDTO))
                .thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Application is not summoned"));

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            applicationStageController.answerSummon(id, answerSummonDTO);
        }, "Expected a ResponseStatusException to be thrown.");

        assertEquals("400 BAD_REQUEST \"Application is not summoned\"", exception.getMessage());
        verify(applicationStageService, times(1)).answerSummon(id, answerSummonDTO);
    }

    @Test
    void answerSummon_ConvocationNotPending() {
        // Arrange
        when(applicationStageService.answerSummon(id, answerSummonDTO))
                .thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Convocation is not pending"));

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            applicationStageController.answerSummon(id, answerSummonDTO);
        }, "Expected a ResponseStatusException to be thrown.");

        assertEquals("400 BAD_REQUEST \"Convocation is not pending\"", exception.getMessage());
        verify(applicationStageService, times(1)).answerSummon(id, answerSummonDTO);
    }

    @Test
    void answerSummon_InvalidConvocationStatus() {
        // Arrange
        // Assuming ConvocationStatus.PENDING is invalid for answering a summon
        answerSummonDTO = new AnswerSummonDTO("Invalid status.", Convocation.ConvocationStatus.PENDING);

        when(applicationStageService.answerSummon(id, answerSummonDTO))
                .thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid status"));

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            applicationStageController.answerSummon(id, answerSummonDTO);
        }, "Expected a ResponseStatusException to be thrown.");

        assertEquals("400 BAD_REQUEST \"Invalid status\"", exception.getMessage());
        verify(applicationStageService, times(1)).answerSummon(id, answerSummonDTO);
    }

    @Test
    public void testGetApplicationsByEtudiantId_Success() throws Exception {
        applicationStageAvecInfosDTO.setId(101L);
        applicationStageAvecInfosDTO.setEtudiant_id(1L);
        applicationStageAvecInfosDTO.setEntrepriseName("Entreprise 1");
        applicationStageAvecInfosDTO.setStatus(ApplicationStage.ApplicationStatus.PENDING);

        applicationStageAvecInfosDTO2.setId(102L);
        applicationStageAvecInfosDTO2.setEtudiant_id(1L);
        applicationStageAvecInfosDTO2.setEntrepriseName("Entreprise 2");
        applicationStageAvecInfosDTO2.setStatus(ApplicationStage.ApplicationStatus.ACCEPTED);



        Long etudiantId = 1L;
        List<ApplicationStageAvecInfosDTO> applications = Arrays.asList(
                applicationStageAvecInfosDTO,
                applicationStageAvecInfosDTO2
        );

        when(applicationStageService.getApplicationsByEtudiant(etudiantId)).thenReturn(applications);

        mockMvc.perform(get("/api/application-stage/get/etudiant/{id}", etudiantId)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(101)))
                .andExpect(jsonPath("$[0].etudiant_id", is(1)))
                .andExpect(jsonPath("$[0].entrepriseName", is("Entreprise 1")))
                .andExpect(jsonPath("$[0].status", is("PENDING")))
                .andExpect(jsonPath("$[1].id", is(102)))
                .andExpect(jsonPath("$[1].etudiant_id", is(1)))
                .andExpect(jsonPath("$[1].entrepriseName", is("Entreprise 2")))
                .andExpect(jsonPath("$[1].status", is("ACCEPTED")));
    }

    @Test
    public void testGetApplicationsByEtudiantId_EmptyList() throws Exception {
        Long etudiantId = 2L;
        List<ApplicationStageAvecInfosDTO> applications = Collections.emptyList();

        when(applicationStageService.getApplicationsByEtudiant(etudiantId)).thenReturn(applications);

        mockMvc.perform(get("/api/application-stage/get/etudiant/{id}", etudiantId)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void testGetApplicationsByEtudiantId_ServiceException() throws Exception {
        Long etudiantId = 3L;

        when(applicationStageService.getApplicationsByEtudiant(etudiantId))
                .thenThrow(new RuntimeException("Service unavailable"));

        mockMvc.perform(get("/api/application-stage/get/etudiant/{id}", etudiantId)
                        .with(csrf()))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is("Service unavailable")))
                .andExpect(jsonPath("$.status", is(500)))
                .andExpect(jsonPath("$.timestamp").isNumber());
    }

    // Test for getApplicationsWithStatus
    @Test
    public void testGetApplicationsWithStatus_Success() throws Exception {
        applicationStageAvecInfosDTO.setId(201L);
        applicationStageAvecInfosDTO.setEtudiant_id(1L);
        applicationStageAvecInfosDTO.setEntrepriseName("Entreprise 1");
        applicationStageAvecInfosDTO.setStatus(ApplicationStage.ApplicationStatus.ACCEPTED);

        applicationStageAvecInfosDTO2.setId(202L);
        applicationStageAvecInfosDTO2.setEtudiant_id(2L);
        applicationStageAvecInfosDTO2.setEntrepriseName("Entreprise 2");
        applicationStageAvecInfosDTO2.setStatus(ApplicationStage.ApplicationStatus.ACCEPTED);

        ApplicationStage.ApplicationStatus status = ApplicationStage.ApplicationStatus.ACCEPTED;
        List<ApplicationStageAvecInfosDTO> applications = Arrays.asList(
                applicationStageAvecInfosDTO,
                applicationStageAvecInfosDTO2
        );

        when(applicationStageService.getApplicationsWithStatus(status)).thenReturn(applications);

        mockMvc.perform(get("/api/application-stage/status/{status}", status)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(201)))
                .andExpect(jsonPath("$[0].etudiant_id", is(1)))
                .andExpect(jsonPath("$[0].entrepriseName", is("Entreprise 1")))
                .andExpect(jsonPath("$[0].status", is("ACCEPTED")))
                .andExpect(jsonPath("$[1].id", is(202)))
                .andExpect(jsonPath("$[1].etudiant_id", is(2)))
                .andExpect(jsonPath("$[1].entrepriseName", is("Entreprise 2")))
                .andExpect(jsonPath("$[1].status", is("ACCEPTED")));
    }

    @Test
    public void testGetApplicationsWithStatus_EmptyList() throws Exception {
        ApplicationStage.ApplicationStatus status = ApplicationStage.ApplicationStatus.REJECTED;
        List<ApplicationStageAvecInfosDTO> applications = Collections.emptyList();

        when(applicationStageService.getApplicationsWithStatus(status)).thenReturn(applications);

        mockMvc.perform(get("/api/application-stage/status/{status}", status)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void testGetApplicationsWithStatus_ServiceException() throws Exception {
        ApplicationStage.ApplicationStatus status = ApplicationStage.ApplicationStatus.PENDING;

        when(applicationStageService.getApplicationsWithStatus(status))
                .thenThrow(new RuntimeException("Service failure"));

        mockMvc.perform(get("/api/application-stage/status/{status}", status)
                        .with(csrf()))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is("Service failure")))
                .andExpect(jsonPath("$.status", is(500)))
                .andExpect(jsonPath("$.timestamp").isNumber());
    }

    // Test for getEtudiantFromApplication
    @Test
    public void testGetEtudiantFromApplication_Success() throws Exception {
        Long applicationId = 301L;
        EtudiantDTO etudiantDTO1 = new EtudiantDTO();
        etudiantDTO1.setId(1L);
        etudiantDTO1.setNom("John");
        etudiantDTO1.setPrenom("Doe");
        etudiantDTO1.setCourriel("eliescrummaster@gmail.com");

        when(applicationStageService.getEtudiantFromApplicationId(applicationId)).thenReturn(etudiantDTO1);

        mockMvc.perform(get("/api/application-stage/getEtudiant/{applicationId}", applicationId)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nom", is("John")))
                .andExpect(jsonPath("$.prenom", is("Doe")))
                .andExpect(jsonPath("$.courriel", is("eliescrummaster@gmail.com")));
    }

    @Test
    public void testGetEtudiantFromApplication_NotFound() throws Exception {
        Long applicationId = 302L;

        when(applicationStageService.getEtudiantFromApplicationId(applicationId))
                .thenThrow(new ResourceNotFoundException("Application not found"));

        mockMvc.perform(get("/api/application-stage/getEtudiant/{applicationId}", applicationId)
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is("Application not found")))
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.timestamp").isNumber());
    }

    @Test
    public void testGetEtudiantFromApplication_ServiceException() throws Exception {
        Long applicationId = 303L;

        when(applicationStageService.getEtudiantFromApplicationId(applicationId))
                .thenThrow(new RuntimeException("Service error"));

        mockMvc.perform(get("/api/application-stage/getEtudiant/{applicationId}", applicationId)
                        .with(csrf()))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is("Service error")))
                .andExpect(jsonPath("$.status", is(500)))
                .andExpect(jsonPath("$.timestamp").isNumber());
    }

    // Test for getOffreStageFromApplication
    @Test
    public void testGetOffreStageFromApplication_Success() throws Exception {
        Long applicationId = 401L;

        OffreStageDTO offreStageDTO1 = new OffreStageDTO();
        offreStageDTO1.setId(10L);
        offreStageDTO1.setTitle("Offre 1");
        offreStageDTO1.setEntrepriseName("Entreprise 1");

        when(applicationStageService.getOffreStageFromApplicationId(applicationId)).thenReturn(offreStageDTO1);

        mockMvc.perform(get("/api/application-stage/getOffreStage/{applicationId}", applicationId)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(10)))
                .andExpect(jsonPath("$.title", is("Offre 1")))
                .andExpect(jsonPath("$.entrepriseName", is("Entreprise 1")));
    }

    @Test
    public void testGetOffreStageFromApplication_NotFound() throws Exception {
        Long applicationId = 402L;

        when(applicationStageService.getOffreStageFromApplicationId(applicationId))
                .thenThrow(new ResourceNotFoundException("Application not found"));

        mockMvc.perform(get("/api/application-stage/getOffreStage/{applicationId}", applicationId)
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is("Application not found")))
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.timestamp").isNumber());
    }

    @Test
    public void testGetOffreStageFromApplication_ServiceException() throws Exception {
        Long applicationId = 403L;

        when(applicationStageService.getOffreStageFromApplicationId(applicationId))
                .thenThrow(new RuntimeException("Service error"));

        mockMvc.perform(get("/api/application-stage/getOffreStage/{applicationId}", applicationId)
                        .with(csrf()))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is("Service error")))
                .andExpect(jsonPath("$.status", is(500)))
                .andExpect(jsonPath("$.timestamp").isNumber());
    }
}
