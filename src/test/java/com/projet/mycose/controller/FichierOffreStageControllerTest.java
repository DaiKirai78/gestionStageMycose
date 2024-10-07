package com.projet.mycose.controller;

import com.projet.mycose.service.FichierOffreStageService;
import com.projet.mycose.service.UtilisateurService;
import com.projet.mycose.service.dto.FichierOffreStageDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class FichierOffreStageControllerTest {

    @Mock
    private FichierOffreStageService fichierOffreStageService;

    @Mock
    private UtilisateurService utilisateurService;

    @InjectMocks
    private FichierOffreStageController fichierOffreStageController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(fichierOffreStageController)
                .setControllerAdvice(new GlobalExceptionHandler()) // Register GlobalExceptionHandler
                .build();

    }

    @Test
    void testUploadFile_Success() throws Exception {
        // Arrange
        MockMultipartFile mockFile = new MockMultipartFile("file", "validFile.pdf",
                MediaType.APPLICATION_PDF_VALUE, "Some PDF content".getBytes());

        FichierOffreStageDTO validFichierOffreStageDTO = new FichierOffreStageDTO();
        validFichierOffreStageDTO.setId(1L);
        validFichierOffreStageDTO.setFilename("validFile.pdf");
        validFichierOffreStageDTO.setFileData("Base64FileData"); // Example Base64 data

        when(fichierOffreStageService.saveFile(any(MultipartFile.class), any(String.class)))
                .thenReturn(validFichierOffreStageDTO);

        // Act & Assert
        mockMvc.perform(multipart("/api/offres-stage/upload")
                        .file(mockFile)
                        .header("Authorization", "Bearer validToken")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.filename").value("validFile.pdf"));
    }

    @Test
    void testUploadFile_ValidationFailure_ReturnsBadRequest() throws Exception {
        // Arrange: Create a MockMultipartFile
        MockMultipartFile mockFile = new MockMultipartFile("file", "invalidFile.pdf",
                MediaType.APPLICATION_PDF_VALUE, "Some content".getBytes());

        // Mocking the ConstraintViolation for the "filename" field
        ConstraintViolation<FichierOffreStageDTO> mockViolation = mock(ConstraintViolation.class);

        // Mock the Path object and ensure getPropertyPath() does not return null
        jakarta.validation.Path mockPath = mock(jakarta.validation.Path.class);
        when(mockViolation.getPropertyPath()).thenReturn(mockPath);
        when(mockPath.toString()).thenReturn("filename");

        // Stubbing the violation message
        when(mockViolation.getMessage()).thenReturn("Invalid filename format. Only PDF files are allowed.");

        // Create a set of violations to mock the validator behavior
        Set<ConstraintViolation<FichierOffreStageDTO>> violations = new HashSet<>();
        violations.add(mockViolation);

        // Mock the ConstraintViolationException to return the violations
        ConstraintViolationException mockConstraintViolationException = new ConstraintViolationException(violations);

        // Simulate the service throwing the mocked ConstraintViolationException
        when(fichierOffreStageService.saveFile(any(MultipartFile.class), any(String.class)))
                .thenThrow(mockConstraintViolationException);

        // Act & Assert: Perform the request and check for BadRequest (400) response and validate error messages
        mockMvc.perform(multipart("/api/offres-stage/upload")
                        .file(mockFile)
                        .header("Authorization", "Bearer validToken")
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
        when(fichierOffreStageService.saveFile(any(MultipartFile.class), any(String.class)))
                .thenThrow(new IOException("File error"));

        // Act & Assert: Perform the request and expect InternalServerError (500)
        mockMvc.perform(multipart("/api/offres-stage/upload")
                        .file(mockFile)
                        .header("Authorization", "Bearer validToken")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isInternalServerError());
    }
}
