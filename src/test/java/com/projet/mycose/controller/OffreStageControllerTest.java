package com.projet.mycose.controller;

import com.projet.mycose.dto.*;
import com.projet.mycose.modele.ApplicationStage;
import com.projet.mycose.modele.Etudiant;
import com.projet.mycose.modele.Programme;
import com.projet.mycose.service.ApplicationStageService;
import com.projet.mycose.service.OffreStageService;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OffreStageControllerTest {

    @Mock
    private OffreStageService offreStageService;
    @Mock
    private ApplicationStageService applicationStageService;

    @InjectMocks
    private OffreStageController offreStageController;

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
        ConstraintViolationException exception = mock(ConstraintViolationException.class);
        when(exception.getConstraintViolations()).thenReturn(new HashSet<>());
        when(offreStageService.saveFile(uploadFicherOffreStageDTO)).thenThrow(exception);

        ResponseEntity<?> response = offreStageController.uploadFile(uploadFicherOffreStageDTO);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(offreStageService, times(1)).saveFile(uploadFicherOffreStageDTO);
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
    void getMyOffres_Success() throws AccessDeniedException {
        when(offreStageService.getAvailableOffreStagesForEtudiant()).thenReturn(offreStageDTOList);

        ResponseEntity<List<OffreStageDTO>> response = offreStageController.getMyOffres();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(offreStageDTOList, response.getBody());
        verify(offreStageService, times(1)).getAvailableOffreStagesForEtudiant();
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
        when(applicationStageService.getAllApplicationsPourUneOffreById(1L)).thenReturn(applicationStageDTOList);
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
        when(offreStageService.getTotalWaitingOffreStages()).thenReturn(totalWaitingOffres);

        // Act
        ResponseEntity<Long> response = offreStageController.getTotalWaitingOffres();

        // Assert
        assertNotNull(response, "Response should not be null");
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Status code should be OK");
        assertEquals(totalWaitingOffres, response.getBody(), "Body should contain the correct total waiting offers count");
        verify(offreStageService, times(1)).getTotalWaitingOffreStages();
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

}
