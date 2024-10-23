package com.projet.mycose.controller;

import com.projet.mycose.modele.FichierCV;
import com.projet.mycose.service.FichierCVService;
import com.projet.mycose.dto.FichierCVDTO;
import com.projet.mycose.dto.FichierCVStudInfoDTO;
import com.projet.mycose.service.UtilisateurService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class FichierCVControllerTest {

    @Mock
    private FichierCVService fichierCVService;

    @Mock
    private UtilisateurService utilisateurService;

    @InjectMocks
    private FichierCVController fichierCVController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(fichierCVController)
                .setControllerAdvice(new GlobalExceptionHandler()) // Register GlobalExceptionHandler
                .build();
    }

    @Test
    void testUploadFile_Success() throws Exception {
        // Arrange
        MockMultipartFile mockFile = new MockMultipartFile("file", "validFile.pdf",
                MediaType.APPLICATION_PDF_VALUE, "Some PDF content".getBytes());

        FichierCVDTO validFichierCVDTO = new FichierCVDTO();
        validFichierCVDTO.setId(1L);
        validFichierCVDTO.setFilename("validFile.pdf");
        validFichierCVDTO.setFileData("Base64FileData"); // Example Base64 data
        validFichierCVDTO.setEtudiant_id(1L);

        when(fichierCVService.saveFile(any(MultipartFile.class)))
                .thenReturn(validFichierCVDTO);

        when(utilisateurService.getMyUserId()).thenReturn(1L);

        when(fichierCVService.getCurrentCV_returnNullIfEmpty(1L)).thenReturn(null);

        // Act & Assert
        mockMvc.perform(multipart("/api/cv/upload")
                        .file(mockFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.filename").value("validFile.pdf"))
                .andExpect(jsonPath("$.fileData").value("Base64FileData"))
                .andExpect(jsonPath("$.etudiant_id").value(1));
    }

    @Test
    void testUploadFile_ValidationFailure_ReturnsBadRequest() throws Exception {
        // Arrange: Create a MockMultipartFile
        MockMultipartFile mockFile = new MockMultipartFile("file", "invalidFile.pdf",
                MediaType.APPLICATION_PDF_VALUE, "Some content".getBytes());

        // Mocking the ConstraintViolation for the "filename" field
        ConstraintViolation<FichierCVDTO> mockViolation = mock(ConstraintViolation.class);

        // Mock the Path object and ensure getPropertyPath() does not return null
        jakarta.validation.Path mockPath = mock(jakarta.validation.Path.class);
        when(mockViolation.getPropertyPath()).thenReturn(mockPath);
        when(mockPath.toString()).thenReturn("filename");

        // Stubbing the violation message
        when(mockViolation.getMessage()).thenReturn("Invalid filename format. Only PDF files are allowed.");

        // Create a set of violations to mock the validator behavior
        Set<ConstraintViolation<FichierCVDTO>> violations = new HashSet<>();
        violations.add(mockViolation);

        // Mock the ConstraintViolationException to return the violations
        ConstraintViolationException mockConstraintViolationException = new ConstraintViolationException(violations);

        // Simulate the service throwing the mocked ConstraintViolationException
        when(fichierCVService.saveFile(any(MultipartFile.class)))
                .thenThrow(mockConstraintViolationException);

        // Act & Assert: Perform the request and check for BadRequest (400) response and validate error messages
        mockMvc.perform(multipart("/api/cv/upload")
                        .file(mockFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.filename").value("Invalid filename format. Only PDF files are allowed."));
    }

    @Test
    void testUploadFile_IOException_ReturnsInternalServerError() throws Exception {
        // Arrange
        MockMultipartFile mockFile = new MockMultipartFile("file", "validFile.pdf",
                MediaType.APPLICATION_PDF_VALUE, "Some content".getBytes());

        // Simulate an IOException when the service tries to save the file
        when(fichierCVService.saveFile(any(MultipartFile.class)))
                .thenThrow(new IOException("File error"));

        // Act & Assert: Perform the request and expect InternalServerError (500)
        mockMvc.perform(multipart("/api/cv/upload")
                        .file(mockFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getWaitingCv_NothingFound() throws Exception {
        // Act
        when(fichierCVService.getWaitingCv(1)).thenReturn(new ArrayList<>());

        // Assert
        mockMvc.perform(get("/api/cv/waitingcv?page=1"))
                .andExpect(content().json("[]"))
                .andExpect(status().isOk());
    }

    @Test
    void getWaitingCv_FoundCvs() throws Exception {
        // Arrange
        FichierCVStudInfoDTO fichierCVDTO = new FichierCVStudInfoDTO();
        fichierCVDTO.setId(1L);
        fichierCVDTO.setFilename("validFile.pdf");
        fichierCVDTO.setFileData("Base64FileData"); // Example Base64 data

        FichierCVStudInfoDTO fichierCVDTO2 = new FichierCVStudInfoDTO();
        fichierCVDTO2.setId(2L);

        List<FichierCVStudInfoDTO> fichierCVDTOS = new ArrayList<>();

        fichierCVDTOS.add(fichierCVDTO);
        fichierCVDTOS.add(fichierCVDTO2);

        // Act
        when(fichierCVService.getWaitingCv(0)).thenReturn(fichierCVDTOS);

        // Assert
        mockMvc.perform(get("/api/cv/waitingcv?page=0"))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(status().isOk());
    }

    @Test
    void testGetCurrentCV_Success() throws Exception {
        // Arrange
        FichierCVDTO mockFichierCVDTO = new FichierCVDTO();
        mockFichierCVDTO.setId(1L);
        mockFichierCVDTO.setFilename("test.pdf");
        mockFichierCVDTO.setFileData("Base64EncodedData");
        mockFichierCVDTO.setEtudiant_id(1L);
        when(fichierCVService.getCurrentCVDTO()).thenReturn(mockFichierCVDTO);

        // Act & Assert
        mockMvc.perform(post("/api/cv/current")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.filename").value("test.pdf"))
                .andExpect(jsonPath("$.fileData").value("Base64EncodedData"))
                .andExpect(jsonPath("$.etudiant_id").value(1));
    }

    @Test
    void test_acceptCv_OK() throws Exception {
        // Act
        doNothing().when(fichierCVService).changeStatus(1L, FichierCV.Status.ACCEPTED, "asd");
        // Assert
        mockMvc.perform(patch("/api/cv/accept?id=1")
                        .content("{\"commentaire\": \"asd\"}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void test_acceptCv_UserNotFound() throws Exception {
        // Act
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Fichier non trouvé"))
                .when(fichierCVService)
                .changeStatus(1L, FichierCV.Status.ACCEPTED, "asd");
        // Assert
        mockMvc.perform(patch("/api/cv/accept?id=1")
                        .content("{\"commentaire\": \"asd\"}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void test_refuseCv_OK() throws Exception {
        // Act
        doNothing().when(fichierCVService).changeStatus(1L, FichierCV.Status.REFUSED, "asd");
        // Assert
        mockMvc.perform(patch("/api/cv/refuse?id=1")
                        .content("{\"commentaire\": \"asd\"}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteCurrentCV_Success() throws Exception {
        // Arrange
        when(fichierCVService.deleteCurrentCV()).thenReturn(new FichierCVDTO());

        // Act & Assert
        mockMvc.perform(patch("/api/cv/delete_current"))
                .andExpect(status().isOk())
                .andExpect(content().string("CV supprimé avec succès"));

        // Verify interactions
        verify(fichierCVService, times(1)).deleteCurrentCV();
    }

    @Test
    void testDeleteCurrentCV_FileNotFound() throws Exception {
        // Arrange
        doThrow(new RuntimeException("Fichier non trouvé"))
                .when(fichierCVService).deleteCurrentCV();

        // Act & Assert
        mockMvc.perform(patch("/api/cv/delete_current"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Fichier non trouvé"));

        // Verify interactions
        verify(fichierCVService, times(1)).deleteCurrentCV();
    }

    @Test
    void test_getAmountOfPages_OK() throws Exception {
        // Act
        when(fichierCVService.getAmountOfPages()).thenReturn(4);

        // Assert
        mockMvc.perform(get("/api/cv/pages"))
                .andExpect(content().string("4"))
                .andExpect(status().isOk());
    }
}
