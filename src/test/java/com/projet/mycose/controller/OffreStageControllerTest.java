package com.projet.mycose.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.projet.mycose.dto.*;
import com.projet.mycose.exceptions.GlobalExceptionHandler;
import com.projet.mycose.modele.ApplicationStage;
import com.projet.mycose.modele.Etudiant;
import com.projet.mycose.modele.OffreStage;
import com.projet.mycose.modele.Programme;
import com.projet.mycose.service.ApplicationStageService;
import com.projet.mycose.service.OffreStageService;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.*;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class OffreStageControllerTest {
    private MockMvc mockMvc;

    @Mock
    private OffreStageService offreStageService;
    @Mock
    private ApplicationStageService applicationStageService;

    @InjectMocks
    private OffreStageController offreStageController;

    private ObjectMapper objectMapper;

    private Long id;
    private UploadFicherOffreStageDTO uploadFicherOffreStageDTO;
    private FichierOffreStageDTO fichierOffreStageDTO;
    private FormulaireOffreStageDTO formulaireOffreStageDTO;
    private OffreStageAvecUtilisateurInfoDTO offreStageAvecUtilisateurInfoDTO;
    private OffreStageDTO offreStageDTO;
    private List<OffreStageAvecUtilisateurInfoDTO> offreStageAvecUtilisateurInfoDTOList;
    private List<OffreStageDTO> offreStageDTOList;
    private Etudiant etudiant;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(offreStageController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        id = 1L;

        uploadFicherOffreStageDTO = new UploadFicherOffreStageDTO();

        fichierOffreStageDTO = new FichierOffreStageDTO();

        formulaireOffreStageDTO = new FormulaireOffreStageDTO();

        offreStageAvecUtilisateurInfoDTO = new OffreStageAvecUtilisateurInfoDTO();

        offreStageDTO = new OffreStageDTO();

        offreStageAvecUtilisateurInfoDTOList = Collections.singletonList(offreStageAvecUtilisateurInfoDTO);
        offreStageDTOList = List.of(offreStageDTO);

        etudiant = Etudiant.builder()
                .id(1L)
                .prenom("Roberto")
                .nom("Berrios")
                .numeroDeTelephone("438-508-3245")
                .courriel("roby@gmail.com")
                .motDePasse("Roby123$")
                .programme(Programme.TECHNIQUE_INFORMATIQUE)
                .build();
    }

    @Test
    void uploadFile_Success() throws Exception {
        when(offreStageService.saveFile(uploadFicherOffreStageDTO)).thenReturn(fichierOffreStageDTO);

        ResponseEntity<?> response = offreStageController.uploadFile(uploadFicherOffreStageDTO);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(fichierOffreStageDTO, response.getBody());
        verify(offreStageService, times(1)).saveFile(uploadFicherOffreStageDTO);
    }

    @Test
    void uploadFile_ConstraintViolationException() throws Exception {
        MockMultipartFile mockFile = new MockMultipartFile("file", "validFile.pdf",
                MediaType.APPLICATION_PDF_VALUE, "Some content".getBytes());
        ConstraintViolationException exception = mock(ConstraintViolationException.class);
        when(offreStageService.saveFile(any(UploadFicherOffreStageDTO.class))).thenThrow(exception);

        MvcResult result = mockMvc.perform(multipart("/api/offres-stages/upload-file")
                        .file(mockFile)
                        .param("title", "Title")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        Exception resolvedException = result.getResolvedException();

        // Assert that the resolved exception is not null
        assertNotNull(resolvedException, "Resolved exception should not be null");

        // Assert that the resolved exception is of type ConstraintViolationException
        assertTrue(resolvedException instanceof ConstraintViolationException,
                "Resolved exception should be an instance of ConstraintViolationException");
    }

    @Test
    void uploadFile_IOException() throws Exception {
        when(offreStageService.saveFile(uploadFicherOffreStageDTO)).thenThrow(new IOException("File error"));

        ResponseEntity<?> response = offreStageController.uploadFile(uploadFicherOffreStageDTO);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
        verify(offreStageService, times(1)).saveFile(uploadFicherOffreStageDTO);
    }

    @Test
    void uploadForm_Success() throws Exception {
        when(offreStageService.saveForm(formulaireOffreStageDTO)).thenReturn(formulaireOffreStageDTO);

        ResponseEntity<FormulaireOffreStageDTO> response = offreStageController.uploadForm(formulaireOffreStageDTO);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(formulaireOffreStageDTO, response.getBody());
        verify(offreStageService, times(1)).saveForm(formulaireOffreStageDTO);
    }

    @Test
    void uploadForm_AccessDeniedException() throws Exception {
        when(offreStageService.saveForm(formulaireOffreStageDTO)).thenThrow(new AccessDeniedException("Access Denied"));

        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> {
            offreStageController.uploadForm(formulaireOffreStageDTO);
        });

        assertEquals("Access Denied", exception.getMessage());
        verify(offreStageService, times(1)).saveForm(formulaireOffreStageDTO);
    }

    @Test
    void getWaitingOffreStage_Success() {
        int page = 0;
        when(offreStageService.getWaitingOffreStage(page)).thenReturn(offreStageAvecUtilisateurInfoDTOList);

        ResponseEntity<List<OffreStageAvecUtilisateurInfoDTO>> response = offreStageController.getWaitingOffreStage(page);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(offreStageAvecUtilisateurInfoDTOList, response.getBody());
        verify(offreStageService, times(1)).getWaitingOffreStage(page);
    }

    @Test
    void getAmountOfPages_Success() {
        int amountOfPages = 5;
        when(offreStageService.getAmountOfPages()).thenReturn(amountOfPages);

        ResponseEntity<Integer> response = offreStageController.getAmountOfPages();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(amountOfPages, response.getBody());
        verify(offreStageService, times(1)).getAmountOfPages();
    }
    @Test
    void getOffreStage_Success() {
        when(offreStageService.getOffreStageWithUtilisateurInfo(id)).thenReturn(offreStageAvecUtilisateurInfoDTO);

        ResponseEntity<OffreStageAvecUtilisateurInfoDTO> response = offreStageController.getOffreStage(id);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(offreStageAvecUtilisateurInfoDTO, response.getBody());
        verify(offreStageService, times(1)).getOffreStageWithUtilisateurInfo(id);
    }

    @Test
    void getFormOffreStage_Success() {
        when(offreStageService.getOffreStageFormulaire(id)).thenReturn(formulaireOffreStageDTO);

        ResponseEntity<FormulaireOffreStageDTO> response = offreStageController.getOffreStageForm(id);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(formulaireOffreStageDTO, response.getBody());
        verify(offreStageService, times(1)).getOffreStageFormulaire(id);
    }

    @Test
    void getMyOffres_Success() throws AccessDeniedException {
        when(offreStageService.getAvailableOffreStagesForEtudiantFiltered(1, null, null, null)).thenReturn(offreStageDTOList);

        ResponseEntity<List<OffreStageDTO>> response = offreStageController.getMyOffresByYearAndSessionEcole(null, null, 1, null);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(offreStageDTOList, response.getBody());
        verify(offreStageService, times(1)).getAvailableOffreStagesForEtudiantFiltered(1, null, null, null);
    }

    @Test
    void getAllEtudiantQuiOntAppliquesAUneOffreTest() {
        ApplicationStageAvecInfosDTO applicationStageDTO = new ApplicationStageAvecInfosDTO();
        applicationStageDTO.setEtudiant_id(1L);
        applicationStageDTO.setId(1L);
        applicationStageDTO.setOffreStage_id(1L);
        applicationStageDTO.setStatus(ApplicationStage.ApplicationStatus.ACCEPTED);
        List<ApplicationStageAvecInfosDTO> applicationStageDTOList = new ArrayList<>();
        applicationStageDTOList.add(applicationStageDTO);
        List<EtudiantDTO> etudiantsList = new ArrayList<>();
        etudiantsList.add(EtudiantDTO.toDTO(etudiant));
        when(applicationStageService.getAllApplicationsPourUneOffreByIdPendingOrSummoned(1L)).thenReturn(applicationStageDTOList);
        when(offreStageService.getEtudiantsQuiOntAppliquesAUneOffre(any())).thenReturn(etudiantsList);

        ResponseEntity<List<EtudiantDTO>> response = offreStageController.getAllEtudiantQuiOntAppliquesAUneOffre(1L);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(etudiantsList, response.getBody());
        verify(offreStageService, times(1)).getEtudiantsQuiOntAppliquesAUneOffre(applicationStageDTOList);
    }

    @Test
    void getTotalWaitingOffres_Success() {
        // Arrange
        Long totalWaitingOffres = 10L;
        when(offreStageService.getTotalWaitingOffresStage()).thenReturn(totalWaitingOffres);

        // Act
        ResponseEntity<Long> response = offreStageController.getTotalWaitingOffres();

        // Assert
        assertNotNull(response, "Response should not be null");
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Status code should be OK");
        assertEquals(totalWaitingOffres, response.getBody(), "Body should contain the correct total waiting offers count");
        verify(offreStageService, times(1)).getTotalWaitingOffresStage();
    }


    @Test
    void acceptOffreStage_Success() {
        // Arrange
        AcceptOffreDeStageDTO dto = new AcceptOffreDeStageDTO();
        dto.setId(1L);
        dto.setProgramme(Programme.TECHNIQUE_INFORMATIQUE);
        dto.setStatusDescription("Good job!");
        dto.setEtudiantsPrives(Arrays.asList(1001L, 1002L));

        doNothing().when(offreStageService).acceptOffreDeStage(dto);

        // Act
        ResponseEntity<?> response = offreStageController.acceptOffreStage(dto);

        // Assert
        assertNotNull(response, "Response should not be null");
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Status code should be OK");
        assertNull(response.getBody(), "Body should be null for OK response");
        verify(offreStageService, times(1)).acceptOffreDeStage(dto);
    }

    @Test
    void acceptOffreStage_ServiceException() {
        // Arrange
        AcceptOffreDeStageDTO dto = new AcceptOffreDeStageDTO();
        dto.setId(1L);
        dto.setProgramme(Programme.TECHNIQUE_INFORMATIQUE);
        dto.setStatusDescription("Good job!");
        dto.setEtudiantsPrives(Arrays.asList(1001L, 1002L));

        doThrow(new RuntimeException("Service exception")).when(offreStageService).acceptOffreDeStage(dto);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            offreStageController.acceptOffreStage(dto);
        });

        assertEquals("Service exception", exception.getMessage(), "Exception message should match the service exception");
        verify(offreStageService, times(1)).acceptOffreDeStage(dto);
    }

    @Test
    void refuseOffreStage_Success() {
        // Arrange
        Long offreStageId = 1L;
        String commentaire = "Refusing the internship offer due to personal reasons.";
        JsonNode jsonNode = mock(JsonNode.class);
        when(jsonNode.get("commentaire")).thenReturn(mock(JsonNode.class));
        when(jsonNode.get("commentaire").isNull()).thenReturn(false);
        when(jsonNode.get("commentaire").asText()).thenReturn(commentaire);

        doNothing().when(offreStageService).refuseOffreDeStage(offreStageId, commentaire);

        // Act
        ResponseEntity<?> response = offreStageController.refuseOffreStage(offreStageId, jsonNode);

        // Assert
        assertNotNull(response, "Response should not be null");
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Status code should be OK");
        assertNull(response.getBody(), "Body should be null for OK response");
        verify(offreStageService, times(1)).refuseOffreDeStage(offreStageId, commentaire);
    }

    @Test
    void refuseOffreStage_MissingCommentaire() {
        // Arrange
        Long offreStageId = 1L;
        JsonNode jsonNode = mock(JsonNode.class);
        when(jsonNode.get("commentaire")).thenReturn(null);

        // Act
        ResponseEntity<?> response = offreStageController.refuseOffreStage(offreStageId, jsonNode);

        // Assert
        assertNotNull(response, "Response should not be null");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "Status code should be BAD_REQUEST");
        assertEquals("Description field is missing", response.getBody(), "Should return missing description message");
        verify(offreStageService, times(0)).refuseOffreDeStage(anyLong(), anyString());
    }

    @Test
    void refuseOffreStage_ServiceException() {
        // Arrange
        Long offreStageId = 1L;
        String commentaire = "Refusing the internship offer due to personal reasons.";
        JsonNode jsonNode = mock(JsonNode.class);
        when(jsonNode.get("commentaire")).thenReturn(mock(JsonNode.class));
        when(jsonNode.get("commentaire").isNull()).thenReturn(false);
        when(jsonNode.get("commentaire").asText()).thenReturn(commentaire);

        doThrow(new RuntimeException("Service exception")).when(offreStageService).refuseOffreDeStage(offreStageId, commentaire);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            offreStageController.refuseOffreStage(offreStageId, jsonNode);
        });

        assertEquals("Service exception", exception.getMessage(), "Exception message should match the service exception");
        verify(offreStageService, times(1)).refuseOffreDeStage(offreStageId, commentaire);
    }


    @Test
    public void testGetStages_Success() throws Exception{
        //nouveau contructeur

        FormulaireOffreStageDTO mockFormulaire = new FormulaireOffreStageDTO(
                1L,
                "unNomEntreprise",
                "unNomEmployeur",
                "unEmail@mail.com",
                "unSite.com",
                "unTitreStage",
                "uneLcalisation",
                "1000",
                "uneDescription",
                LocalDateTime.now(),
                LocalDateTime.now(),
                1L,
                OffreStage.Status.WAITING,
                Programme.TECHNIQUE_INFORMATIQUE,
                OffreStage.Visibility.PUBLIC,
                null,
                OffreStage.SessionEcole.AUTOMNE,
                2021,
                "09h00-17h00",
                "40"
        );

        List<OffreStageDTO> mockListeOffres = new ArrayList<>();
        mockListeOffres.add(mockFormulaire);
        when(offreStageService.getStagesFiltered(0, null, null)).thenReturn(mockListeOffres);

        // Act & Assert
        mockMvc.perform(get("/api/offres-stages/getOffresPosted")
                        .param("pageNumber", String.valueOf(0))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isAccepted())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].status").value("WAITING"));

    }

    @Test
    public void testGetAmountOfPagesForCreateur_Success() throws Exception {
        //Arrange
        when(offreStageService.getAmountOfPagesForCreateurFiltered(null, null)).thenReturn(2);

        //Act & Assert
        mockMvc.perform(get("/api/offres-stages/pagesForCreateur"))
                .andExpect(status().isAccepted())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("2"));
    }

    @Test
    public void testGetEmployeurFromOffreStage_Success() throws Exception {
        Long offreStageId = 1L;
        EmployeurDTO employeurDTO = new EmployeurDTO();
        employeurDTO.setId(10L);
        employeurDTO.setPrenom("Jean");
        employeurDTO.setNom("Dupont");

        when(offreStageService.getEmployeurByOffreStageId(offreStageId)).thenReturn(employeurDTO);

        mockMvc.perform(get("/api/offres-stages/getEmployeur/{offreStageId}", offreStageId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10L))
                .andExpect(jsonPath("$.nom").value("Dupont"))
                .andExpect(jsonPath("$.prenom").value("Jean"));
    }

    @Test
    public void testGetNextSession_Success() throws Exception {
        SessionInfoDTO nextSession = new SessionInfoDTO(OffreStage.SessionEcole.AUTOMNE, Year.of(2024));

        when(offreStageService.getNextSession()).thenReturn(nextSession);

        mockMvc.perform(get("/api/offres-stages/get-next-session")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.session", is("AUTOMNE")))
                .andExpect(jsonPath("$.annee", is(2024)));
    }

    @Test
    public void testGetNextSession_ServiceException() throws Exception {
        when(offreStageService.getNextSession()).thenThrow(new RuntimeException("Service unavailable"));

        mockMvc.perform(get("/api/offres-stages/get-next-session")
                        .with(csrf()))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is("Service unavailable")))
                .andExpect(jsonPath("$.status", is(500)))
                .andExpect(jsonPath("$.timestamp").isNumber());
    }

    @Test
    public void testGetAllSessions_Success() throws Exception {
        List<SessionInfoDTO> sessions = Arrays.asList(
                new SessionInfoDTO(OffreStage.SessionEcole.ETE, Year.of(2023)),
                new SessionInfoDTO(OffreStage.SessionEcole.AUTOMNE, Year.of(2024))
        );

        when(offreStageService.getAllSessions()).thenReturn(sessions);

        mockMvc.perform(get("/api/offres-stages/get-all-sessions")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].session", is("ETE")))
                .andExpect(jsonPath("$[0].annee", is(2023)))
                .andExpect(jsonPath("$[1].session", is("AUTOMNE")))
                .andExpect(jsonPath("$[1].annee", is(2024)));
    }

    @Test
    public void testGetAllSessions_EmptyList() throws Exception {
        List<SessionInfoDTO> sessions = Collections.emptyList();

        when(offreStageService.getAllSessions()).thenReturn(sessions);

        mockMvc.perform(get("/api/offres-stages/get-all-sessions")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void testGetAllSessions_ServiceException() throws Exception {
        when(offreStageService.getAllSessions()).thenThrow(new RuntimeException("Service error"));

        mockMvc.perform(get("/api/offres-stages/get-all-sessions")
                        .with(csrf()))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is("Service error")))
                .andExpect(jsonPath("$.status", is(500)))
                .andExpect(jsonPath("$.timestamp").isNumber());
    }

    @Test
    public void testGetSessionsForCreateur_Success() throws Exception {
        List<SessionInfoDTO> sessions = List.of(
                new SessionInfoDTO(OffreStage.SessionEcole.HIVER, Year.of(2025))
        );

        when(offreStageService.getSessionsForCreateur()).thenReturn(sessions);

        mockMvc.perform(get("/api/offres-stages/get-sessions-for-createur")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].session", is("HIVER")))
                .andExpect(jsonPath("$[0].annee", is(2025)));
    }

    @Test
    public void testGetSessionsForCreateur_EmptyList() throws Exception {
        List<SessionInfoDTO> sessions = Collections.emptyList();

        when(offreStageService.getSessionsForCreateur()).thenReturn(sessions);

        mockMvc.perform(get("/api/offres-stages/get-sessions-for-createur")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void testGetSessionsForCreateur_ServiceException() throws Exception {
        when(offreStageService.getSessionsForCreateur()).thenThrow(new RuntimeException("Service failure"));

        mockMvc.perform(get("/api/offres-stages/get-sessions-for-createur")
                        .with(csrf()))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is("Service failure")))
                .andExpect(jsonPath("$.status", is(500)))
                .andExpect(jsonPath("$.timestamp").isNumber());
    }

    @Test
    public void testGetAmountOfPagesForMyOffres_Success_AllParams() throws Exception {
        Integer year = 2024;
        OffreStage.SessionEcole sessionEcole = OffreStage.SessionEcole.AUTOMNE;
        String title = "Java Developer";
        Integer expectedPages = 10;

        when(offreStageService.getAmountOfPagesForEtudiantFiltered(year, sessionEcole, title))
                .thenReturn(expectedPages);

        mockMvc.perform(get("/api/offres-stages/my-offres-pages")
                        .param("year", year.toString())
                        .param("sessionEcole", sessionEcole.name())
                        .param("title", title)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(expectedPages.toString()));
    }

    @Test
    public void testGetAmountOfPagesForMyOffres_Success_SomeParamsMissing() throws Exception {
        // Only 'title' is provided
        String title = "Python Developer";
        Integer expectedPages = 5;

        when(offreStageService.getAmountOfPagesForEtudiantFiltered(null, null, title))
                .thenReturn(expectedPages);

        mockMvc.perform(get("/api/offres-stages/my-offres-pages")
                        .param("title", title)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(expectedPages.toString()));
    }

    @Test
    public void testGetAmountOfPagesForMyOffres_Success_NoParams() throws Exception {
        Integer expectedPages = 8;

        when(offreStageService.getAmountOfPagesForEtudiantFiltered(null, null, ""))
                .thenReturn(expectedPages);

        mockMvc.perform(get("/api/offres-stages/my-offres-pages")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(expectedPages.toString()));
    }

    @Test
    public void testGetAmountOfPagesForMyOffres_ServiceException() throws Exception {
        Integer year = 2023;
        OffreStage.SessionEcole sessionEcole = OffreStage.SessionEcole.HIVER;
        String title = "Backend Engineer";

        when(offreStageService.getAmountOfPagesForEtudiantFiltered(year, sessionEcole, title))
                .thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(get("/api/offres-stages/my-offres-pages")
                        .param("year", year.toString())
                        .param("sessionEcole", sessionEcole.name())
                        .param("title", title)
                        .with(csrf()))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is("Database error")))
                .andExpect(jsonPath("$.status", is(500)))
                .andExpect(jsonPath("$.timestamp").isNumber());
    }

    @Test
    public void testGetMyOffresByYearAndSessionEcoleAll_Success_AllParams() throws Exception {
        Integer year = 2024;
        OffreStage.SessionEcole sessionEcole = OffreStage.SessionEcole.AUTOMNE;
        String title = "Java Developer";
        int pageNumber = 0;
        int pageSize = 10;

        OffreStageDTO offreStageDTO1 = new OffreStageDTO();
        offreStageDTO1.setId(1L);
        offreStageDTO1.setTitle("Java Developer");
        offreStageDTO1.setEntrepriseName("Oracle");

        OffreStageDTO offreStageDTO2 = new OffreStageDTO();
        offreStageDTO2.setId(2L);
        offreStageDTO2.setTitle("Senior Flutter (trash language) Developer");
        offreStageDTO2.setEntrepriseName("Google");

        List<OffreStageDTO> offreStages = Arrays.asList(
                offreStageDTO1,
                offreStageDTO2
        );

        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<OffreStageDTO> page = new PageImpl<>(offreStages, pageable, offreStages.size());

        when(offreStageService.getAllOffreStagesForEtudiantFiltered(pageNumber, year, sessionEcole, title))
                .thenReturn(page);

        mockMvc.perform(get("/api/offres-stages/my-offres-all")
                        .param("year", year.toString())
                        .param("sessionEcole", sessionEcole.name())
                        .param("title", title)
                        .param("pageNumber", String.valueOf(pageNumber))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                // Validate page content
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].id", is(1)))
                .andExpect(jsonPath("$.content[0].title", is("Java Developer")))
                .andExpect(jsonPath("$.content[0].entrepriseName", is("Oracle")))
                .andExpect(jsonPath("$.content[1].id", is(2)))
                .andExpect(jsonPath("$.content[1].title", is("Senior Flutter (trash language) Developer")))
                .andExpect(jsonPath("$.content[1].entrepriseName", is("Google")))
                // Validate page metadata
                .andExpect(jsonPath("$.number", is(pageNumber)))
                .andExpect(jsonPath("$.size", is(pageSize)))
                .andExpect(jsonPath("$.totalElements", is(offreStages.size())))
                .andExpect(jsonPath("$.totalPages", is(1)));
    }

    @Test
    public void testGetMyOffresByYearAndSessionEcoleAll_Success_SomeParamsMissing() throws Exception {
        int pageSize = 10;
        int pageNumber = 0;

        OffreStageDTO offreStageDTO1 = new OffreStageDTO();
        offreStageDTO1.setId(3L);
        offreStageDTO1.setTitle("Senior Flutter (trash language) Developer");
        offreStageDTO1.setEntrepriseName("Google");

        List<OffreStageDTO> offreStages = List.of(
                offreStageDTO1
        );

        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<OffreStageDTO> page = new PageImpl<>(offreStages, pageable, offreStages.size());

        when(offreStageService.getAllOffreStagesForEtudiantFiltered(pageNumber, null, null, ""))
                .thenReturn(page);

        mockMvc.perform(get("/api/offres-stages/my-offres-all")
                        .param("pageNumber", String.valueOf(pageNumber))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                // Validate page content
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].id", is(3)))
                .andExpect(jsonPath("$.content[0].title", is("Senior Flutter (trash language) Developer")))
                .andExpect(jsonPath("$.content[0].entrepriseName", is("Google")))
                // Validate page metadata
                .andExpect(jsonPath("$.number", is(pageNumber)))
                .andExpect(jsonPath("$.size", is(pageSize)))
                .andExpect(jsonPath("$.totalElements", is(offreStages.size())))
                .andExpect(jsonPath("$.totalPages", is(1)));
    }

    @Test
    public void testGetMyOffresByYearAndSessionEcoleAll_Success_NoParams() throws Exception {
        int pageNumber = 2;
        int pageSize = 1;

        List<OffreStageDTO> offreStages = Collections.emptyList();

        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<OffreStageDTO> page = new PageImpl<>(offreStages, pageable, 0);

        when(offreStageService.getAllOffreStagesForEtudiantFiltered(pageNumber, null, null, ""))
                .thenReturn(page);

        mockMvc.perform(get("/api/offres-stages/my-offres-all")
                        .param("pageNumber", String.valueOf(pageNumber))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                // Validate page content
                .andExpect(jsonPath("$.content", hasSize(0)))
                // Validate page metadata
                .andExpect(jsonPath("$.number", is(pageNumber)))
                .andExpect(jsonPath("$.size", is(pageSize)))
                .andExpect(jsonPath("$.totalElements", is(0)))
                .andExpect(jsonPath("$.totalPages", is(0)));
    }

    @Test
    public void testGetMyOffresByYearAndSessionEcoleAll_ServiceException() throws Exception {
        Integer year = 2023;
        OffreStage.SessionEcole sessionEcole = OffreStage.SessionEcole.AUTOMNE;
        String title = "Frontend Developer";
        int pageNumber = 0;

        when(offreStageService.getAllOffreStagesForEtudiantFiltered(pageNumber, year, sessionEcole, title))
                .thenThrow(new RuntimeException("Database connection failed"));

        mockMvc.perform(get("/api/offres-stages/my-offres-all")
                        .param("year", year.toString())
                        .param("sessionEcole", sessionEcole.name())
                        .param("title", title)
                        .param("pageNumber", String.valueOf(pageNumber))
                        .with(csrf()))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is("Database connection failed")))
                .andExpect(jsonPath("$.status", is(500)))
                .andExpect(jsonPath("$.timestamp").isNumber());
    }

    @Test
    public void testGetAmountOfPagesForMyOffres_InvalidParameters() throws Exception {
        // 'year' is invalid (non-numeric)
        String invalidYear = "two thousand twenty-four";
        OffreStage.SessionEcole sessionEcole = OffreStage.SessionEcole.AUTOMNE;
        String title = "Java Developer";

        mockMvc.perform(get("/api/offres-stages/my-offres-pages")
                        .param("year", invalidYear)
                        .param("sessionEcole", sessionEcole.name())
                        .param("title", title)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", containsString("Failed to convert value of type 'java.lang.String' to required type 'java.lang.Integer';")))
                .andExpect(jsonPath("$.status", is(500)))
                .andExpect(jsonPath("$.timestamp").isNumber());
    }

    @Test
    public void testUpdateFichierOffreStage_Success() throws Exception {
        // Arrange
        Long offreStageId = 1L;

        // Create MockMultipartFile for 'fichier'
        MockMultipartFile fichierFile = new MockMultipartFile(
                "fichier",
                "fichier.pdf",
                MediaType.APPLICATION_PDF_VALUE,
                "Dummy PDF Content".getBytes()
        );

        // Create MockMultipartFile for 'uploadFicherOffreStageDTO'
        UploadFicherOffreStageDTO uploadFicherDTO = new UploadFicherOffreStageDTO();
        uploadFicherDTO.setTitle("value");
        String uploadFicherJson = objectMapper.writeValueAsString(uploadFicherDTO);
        MockMultipartFile uploadFicherData = new MockMultipartFile(
                "uploadFicherOffreStageDTO",
                "uploadFicherOffreStageDTO.json",
                MediaType.APPLICATION_JSON_VALUE,
                uploadFicherJson.getBytes()
        );

        // Create a sample FichierOffreStageDTO to be returned by the service
        FichierOffreStageDTO savedFichierDTO = new FichierOffreStageDTO();
        savedFichierDTO.setId(offreStageId);
        savedFichierDTO.setFilename("fichier.pdf");
        savedFichierDTO.setFileData(Base64.getEncoder().encodeToString("Dummy PDF Content".getBytes()));
        // Set other necessary fields

        // Mock the service method
        when(offreStageService.updateOffreStage(any(UploadFicherOffreStageDTO.class), eq(offreStageId)))
                .thenReturn(savedFichierDTO);

        // Act & Assert
        mockMvc.perform(multipart("/api/offres-stages/update-fichier")
                        .file(fichierFile)
                        .file(uploadFicherData)
                        .param("offreStageId", offreStageId.toString())
                        .with(SecurityMockMvcRequestPostProcessors.user("user").password("password").roles("USER"))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(request -> { // Override the HTTP method to PATCH
                            request.setMethod("PATCH");
                            return request;
                        }))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(offreStageId))
                .andExpect(jsonPath("$.filename").value("fichier.pdf"));

        // Verify that the service method was called once
        verify(offreStageService, times(1)).updateOffreStage(any(UploadFicherOffreStageDTO.class), eq(offreStageId));
    }

    @Test
    public void testUpdateFichierOffreStage_ServiceException() throws Exception {
        // Arrange
        Long offreStageId = 1L;

        MockMultipartFile fichierFile = new MockMultipartFile(
                "fichier",
                "fichier.pdf",
                MediaType.APPLICATION_PDF_VALUE,
                "Dummy PDF Content".getBytes()
        );

        UploadFicherOffreStageDTO uploadFicherDTO = new UploadFicherOffreStageDTO();
        uploadFicherDTO.setTitle("value");
        String uploadFicherJson = objectMapper.writeValueAsString(uploadFicherDTO);
        MockMultipartFile uploadFicherData = new MockMultipartFile(
                "uploadFicherOffreStageDTO",
                "uploadFicherOffreStageDTO.json",
                MediaType.APPLICATION_JSON_VALUE,
                uploadFicherJson.getBytes()
        );

        // Mock the service method to throw an exception
        when(offreStageService.updateOffreStage(any(UploadFicherOffreStageDTO.class), eq(offreStageId)))
                .thenThrow(new IOException("Failed to save file"));

        // Act & Assert
        mockMvc.perform(multipart("/api/offres-stages/update-fichier")
                        .file(fichierFile)
                        .file(uploadFicherData)
                        .param("offreStageId", offreStageId.toString())
                        .with(SecurityMockMvcRequestPostProcessors.user("user").password("password").roles("USER"))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(request -> { // Override the HTTP method to PATCH
                            request.setMethod("PATCH");
                            return request;
                        }))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("An unexpected error occurred: Failed to save file"))
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.timestamp").isNumber());

        // Verify that the service method was called once
        verify(offreStageService, times(1)).updateOffreStage(any(UploadFicherOffreStageDTO.class), eq(offreStageId));
    }

    @Test
    public void testUpdateFichierOffreStage_InvalidOffreStageId() throws Exception {
        // Arrange
        String invalidOffreStageId = "invalid-id"; // Non-numeric

        MockMultipartFile fichierFile = new MockMultipartFile(
                "fichier",
                "fichier.pdf",
                MediaType.APPLICATION_PDF_VALUE,
                "Dummy PDF Content".getBytes()
        );

        UploadFicherOffreStageDTO uploadFicherDTO = new UploadFicherOffreStageDTO();
        uploadFicherDTO.setTitle("value");
        String uploadFicherJson = objectMapper.writeValueAsString(uploadFicherDTO);
        MockMultipartFile uploadFicherData = new MockMultipartFile(
                "uploadFicherOffreStageDTO",
                "uploadFicherOffreStageDTO.json",
                MediaType.APPLICATION_JSON_VALUE,
                uploadFicherJson.getBytes()
        );

        // Act & Assert
        mockMvc.perform(multipart("/api/offres-stages/update-fichier")
                        .file(fichierFile)
                        .file(uploadFicherData)
                        .param("offreStageId", invalidOffreStageId)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .with(request -> { // Override the HTTP method to PATCH
                            request.setMethod("PATCH");
                            return request;
                        }))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", containsString("Failed to convert value of type 'java.lang.String' to required type 'java.lang.Long';")))
                .andExpect(jsonPath("$.status", is(500)))
                .andExpect(jsonPath("$.timestamp").isNumber());

        // Verify that the service method was never called
        verify(offreStageService, never()).updateOffreStage((UploadFicherOffreStageDTO) any(), anyLong());
    }

    @Test
    public void testUpdateFormulaireOffreStage_Success() throws Exception {
        // Arrange
        Long offreStageId = 1L;

        // Create FormulaireOffreStageDTO object with necessary fields
        FormulaireOffreStageDTO formulaireDTO = new FormulaireOffreStageDTO();
        formulaireDTO.setTitle("value1");
        formulaireDTO.setEmployerName("value2");
        // Set other required fields

        // Mock the service method to return a saved DTO
        FormulaireOffreStageDTO savedFormulaireDTO = new FormulaireOffreStageDTO();
        savedFormulaireDTO.setId(offreStageId);
        savedFormulaireDTO.setTitle("value1");
        savedFormulaireDTO.setEmployerName("value2");
        // Set other fields as necessary

        when(offreStageService.updateOffreStage(any(FormulaireOffreStageDTO.class), eq(offreStageId)))
                .thenReturn(savedFormulaireDTO);

        // Act & Assert
        mockMvc.perform(patch("/api/offres-stages/update-formulaire")
                        .param("offreStageId", offreStageId.toString())
                        .param("field1", "value1")
                        .param("field2", "value2")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(offreStageId))
                .andExpect(jsonPath("$.title").value("value1"))
                .andExpect(jsonPath("$.employerName").value("value2"));

        // Verify that the service method was called once
        verify(offreStageService, times(1)).updateOffreStage(any(FormulaireOffreStageDTO.class), eq(offreStageId));
    }

    @Test
    public void testUpdateFormulaireOffreStage_ServiceException() throws Exception {
        // Arrange
        Long offreStageId = 1L;

        // Create FormulaireOffreStageDTO object with necessary fields
        FormulaireOffreStageDTO formulaireDTO = new FormulaireOffreStageDTO();
        formulaireDTO.setTitle("value1");
        formulaireDTO.setEntrepriseName("value2");
        // Set other required fields

        // Mock the service method to throw an exception
        when(offreStageService.updateOffreStage(any(FormulaireOffreStageDTO.class), eq(offreStageId)))
                .thenThrow(new AccessDeniedException("Access denied"));

        // Act & Assert
        mockMvc.perform(patch("/api/offres-stages/update-formulaire")
                        .param("offreStageId", offreStageId.toString())
                        .param("field1", "value1")
                        .param("field2", "value2")
                        // Add other form fields as needed
                        .with(SecurityMockMvcRequestPostProcessors.user("user").password("password").roles("USER"))
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("An unexpected error occurred: Access denied"))
                .andExpect(jsonPath("$.status").value(500));

        // Verify that the service method was called once
        verify(offreStageService, times(1)).updateOffreStage(any(FormulaireOffreStageDTO.class), eq(offreStageId));
    }

    @Test
    public void testUpdateFormulaireOffreStage_InvalidOffreStageId() throws Exception {
        // Arrange
        String invalidOffreStageId = "invalid-id"; // Non-numeric

        // Act & Assert
        mockMvc.perform(patch("/api/offres-stages/update-formulaire")
                        .param("offreStageId", invalidOffreStageId)
                        .param("field1", "value1")
                        .param("field2", "value2")
                        // Add other form fields as needed
                        .with(SecurityMockMvcRequestPostProcessors.user("user").password("password").roles("USER"))
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", containsString("Failed to convert value of type 'java.lang.String' to required type 'java.lang.Long';")))
                .andExpect(jsonPath("$.status", is(500)));

        // Verify that the service method was never called
        verify(offreStageService, never()).updateOffreStage((FormulaireOffreStageDTO) any(), anyLong());
    }

}
