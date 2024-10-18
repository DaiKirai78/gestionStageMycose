package com.projet.mycose.controller;

import com.projet.mycose.dto.*;
import com.projet.mycose.service.OffreStageService;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OffreStageControllerTest {

    @Mock
    private OffreStageService offreStageService;

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

//    @Test
//    void acceptOffreStage_Success() throws Exception {
//        String description = "Accepted description";
//
//        doNothing().when(offreStageService).changeStatus(id, OffreStage.Status.ACCEPTED, description);
//
//        ResponseEntity<?> response = offreStageController.acceptOffreStage(id, description);
//
//        assertNotNull(response);
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertNull(response.getBody());
//        verify(offreStageService, times(1)).changeStatus(id, OffreStage.Status.ACCEPTED, description);
//    }
//
//    @Test
//    void acceptOffreStage_NotFoundException() throws Exception {
//        String description = "Accepted description";
//        doThrow(new ChangeSetPersister.NotFoundException()).when(offreStageService).changeStatus(id, OffreStage.Status.ACCEPTED, description);
//
//        ResponseEntity<?> response = offreStageController.acceptOffreStage(id, description);
//
//        assertNotNull(response);
//        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
//        verify(offreStageService, times(1)).changeStatus(id, OffreStage.Status.ACCEPTED, description);
//    }

//    @Test
//    void refuseOffreStage_Success() throws Exception {
//        String description = "Refused description";
//
//        doNothing().when(offreStageService).changeStatus(id, OffreStage.Status.REFUSED, "description");
//
//        ResponseEntity<?> response = offreStageController.refuseOffreStage(id, "description");
//
//        assertNotNull(response);
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertNull(response.getBody());
//        verify(offreStageService, times(1)).changeStatus(id, OffreStage.Status.REFUSED, "description");
//    }
//
//    @Test
//    void refuseOffreStage_NotFoundException() throws Exception {
//        String description = "Refused description";
//        doThrow(new ChangeSetPersister.NotFoundException()).when(offreStageService).changeStatus(id, OffreStage.Status.REFUSED, "description");
//
//        ResponseEntity<?> response = offreStageController.refuseOffreStage(id, "description");
//
//        assertNotNull(response);
//        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
//        verify(offreStageService, times(1)).changeStatus(id, OffreStage.Status.REFUSED, "description");
//    }

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
}
