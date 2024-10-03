package com.projet.mycose.controller;

import com.projet.mycose.modele.FichierCV;
import com.projet.mycose.service.FichierCVService;
import com.projet.mycose.service.dto.FichierCVDTO;
import com.projet.mycose.service.dto.FichierOffreStageDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class FichierCVControllerTest {

    @Mock
    private FichierCVService fichierCVService;

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

        when(fichierCVService.saveFile(any(MultipartFile.class)))
                .thenReturn(validFichierCVDTO);

        // Act & Assert
        mockMvc.perform(multipart("/api/cv/upload")
                        .file(mockFile)
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
                .andExpect(status().isOk());
    }
    @Test
    void getWaitingCv_FoundCvs() throws Exception {

        // Arrange
        FichierCVDTO fichierCVDTO = new FichierCVDTO();
        fichierCVDTO.setId(1L);
        fichierCVDTO.setFilename("validFile.pdf");
        fichierCVDTO.setFileData("Base64FileData");

        List<FichierCVDTO> fichierCVDTOS = new ArrayList<>();

        fichierCVDTOS.add(fichierCVDTO);
        fichierCVDTOS.add(fichierCVDTO);

        // Act
        when(fichierCVService.getWaitingCv(0)).thenReturn(fichierCVDTOS);

        // Assert
        mockMvc.perform(get("/api/cv/waitingcv?page=0"))
                .andExpect(status().isOk());
    }
    @Test
    void test_getAmountOfPages_OK() throws Exception {
        // Act
        when(fichierCVService.getAmountOfPages()).thenReturn(4);

        // Assert
        mockMvc.perform(get("/api/cv/pages"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(content().string("4"))
                .andExpect(status().isOk());
    }
    @Test
    void test_acceptCv_OK() throws Exception {
        // Act
        doNothing().when(fichierCVService).changeStatus(1L, FichierCV.Status.ACCEPTED, "asd");

        // Assert
        mockMvc.perform(post("/api/cv/accept?id=1")
                        .content("asd")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
    @Test
    void test_acceptCv_UserNotFound() throws Exception {
        // Act
        doThrow(ChangeSetPersister.NotFoundException.class)
                .when(fichierCVService)
                .changeStatus(1L, FichierCV.Status.ACCEPTED, "asd");

        // Assert
        mockMvc.perform(post("/api/cv/accept?id=1")
                        .content("asd")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
    @Test
    void test_refuseCv_OK() throws Exception {
        // Act
        doNothing().when(fichierCVService).changeStatus(1L, FichierCV.Status.REFUSED, "asd");

        // Assert
        mockMvc.perform(post("/api/cv/refuse?id=1")
                        .content("asd")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
    @Test
    void test_refusetCv_UserNotFound() throws Exception {
        // Act
        doThrow(ChangeSetPersister.NotFoundException.class)
                .when(fichierCVService)
                .changeStatus(1L, FichierCV.Status.REFUSED, "asd");

        // Assert
        mockMvc.perform(post("/api/cv/refuse?id=1")
                        .content("asd")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
