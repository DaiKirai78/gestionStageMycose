package com.projet.mycose.controller;

import com.projet.mycose.exceptions.GlobalExceptionHandler;
import com.projet.mycose.exceptions.UserNotFoundException;
import com.projet.mycose.modele.FichierCV;
import com.projet.mycose.exceptions.AuthenticationException;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@ExtendWith(MockitoExtension.class)
public class FichierCVControllerTest {

    @Mock
    private FichierCVService fichierCVService;

    @Mock
    private UtilisateurService utilisateurService;

    @InjectMocks
    private FichierCVController fichierCVController;

    private MockMvc mockMvc;

    private MockMultipartFile mockFile;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(fichierCVController)
                .setControllerAdvice(new GlobalExceptionHandler()) // Register GlobalExceptionHandler
                .build();


        mockFile = new MockMultipartFile("file", "validFile.pdf",
                MediaType.APPLICATION_PDF_VALUE, "Some content".getBytes());
    }

    @Test
    void testUploadFile_Success() throws Exception {
        // Arrange

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
    void testUploadFile_SuccessWithDeletion() throws Exception {
        // Arrange

        FichierCVDTO validFichierCVDTO = new FichierCVDTO();
        validFichierCVDTO.setId(1L);
        validFichierCVDTO.setFilename("validFile.pdf");
        validFichierCVDTO.setFileData("Base64FileData"); // Example Base64 data
        validFichierCVDTO.setEtudiant_id(1L);

        FichierCVDTO oldCVDTO = new FichierCVDTO();

        when(fichierCVService.saveFile(any(MultipartFile.class)))
                .thenReturn(validFichierCVDTO);

        when(utilisateurService.getMyUserId()).thenReturn(1L);

        when(fichierCVService.getCurrentCV_returnNullIfEmpty(1L)).thenReturn(oldCVDTO);

        when(fichierCVService.deleteCurrentCV()).thenReturn(oldCVDTO);

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
    void testUploadFile_UnauthorizedAccess_ReturnsUnauthorized() throws Exception {
        // Arrange
        when(utilisateurService.getMyUserId()).thenThrow(new AuthenticationException(HttpStatus.UNAUTHORIZED, "Incorrect username or password"));

        // Act & Assert
        mockMvc.perform(multipart("/api/cv/upload")
                        .file(mockFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testUploadFile_FileNotFound_ReturnsInternalServerError() throws Exception {
        // Arrange
        when(utilisateurService.getMyUserId()).thenReturn(1L);
        when(fichierCVService.getCurrentCV_returnNullIfEmpty(1L)).thenThrow(new RuntimeException("Fichier non trouvé"));

        // Act & Assert
        mockMvc.perform(multipart("/api/cv/upload")
                        .file(mockFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testUploadFile_ValidationFailure_ReturnsBadRequest() throws Exception {

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
    void getWaitingCV_ReturnsBadRequest() throws Exception {
        // Arrange
        when(fichierCVService.getWaitingCv(1)).thenThrow(new IllegalArgumentException("Page commence à 1"));

        // Act & Assert
        mockMvc.perform(get("/api/cv/waitingcv?page=1"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Page commence à 1"));
    }

    @Test
    void getTotalWaitingCVs_Success() throws Exception {
        // Arrange
        when(fichierCVService.getTotalWaitingCVs()).thenReturn(5L);

        // Act & Assert
        mockMvc.perform(get("/api/cv/totalwaitingcvs"))
                .andExpect(status().isOk())
                .andExpect(content().string("5"));
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
    void testGetCV_UnauthorizedAccess_ReturnsUnauthorized() throws Exception {
        // Arrange
        when(fichierCVService.getCurrentCVDTO()).thenThrow(new AuthenticationException(HttpStatus.UNAUTHORIZED, "Incorrect username or password"));

        // Act & Assert
        mockMvc.perform(multipart("/api/cv/current")
                        .file(mockFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetCV_FileNotFound_ReturnsInternalServerError() throws Exception {
        // Arrange
        when(fichierCVService.getCurrentCVDTO()).thenThrow(new RuntimeException("Fichier non trouvé"));

        // Act & Assert
        mockMvc.perform(multipart("/api/cv/current")
                        .file(mockFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isInternalServerError());
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
        doThrow(new UserNotFoundException())
                .when(fichierCVService)
                .changeStatus(1L, FichierCV.Status.ACCEPTED, "asd");
        // Assert
        mockMvc.perform(patch("/api/cv/accept?id=1")
                        .content("{\"commentaire\": \"asd\"}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void test_acceptCv_DescriptionIsNull() throws Exception {
        // Act & Assert
        mockMvc.perform(patch("/api/cv/accept?id=1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"commentaire\": null}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Description field is missing"));
    }

    @Test
    void test_acceptCv_NoBody() throws Exception {
        // Act & Assert
        mockMvc.perform(patch("/api/cv/accept?id=1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", containsString("Required request body is missing:")))
                .andExpect(jsonPath("$.status", is(500)))
                .andExpect(jsonPath("$.timestamp").isNumber());
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
    void test_RefuseCvEmptyDescription() throws Exception {
        // Act & Assert
        mockMvc.perform(patch("/api/cv/refuse?id=1")
                        .content("{\"commentaire\": \"\"}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Description field is missing"));
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

    @Test
    public void testGetCV_Success() throws Exception {
        Long etudiantId = 1L;
        FichierCVDTO fichierCVDTO = new FichierCVDTO();
        fichierCVDTO.setId(100L);
        fichierCVDTO.setEtudiant_id(etudiantId);
        fichierCVDTO.setFilename("cv_john_doe.pdf");

        when(fichierCVService.getCurrentCVByEtudiantID(etudiantId)).thenReturn(fichierCVDTO);

        mockMvc.perform(get("/api/cv/get-cv-by-etudiant-id/{id}", etudiantId)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(100)))
                .andExpect(jsonPath("$.etudiant_id", is(etudiantId.intValue())))
                .andExpect(jsonPath("$.filename", is("cv_john_doe.pdf")));
    }

    @Test
    public void testGetCV_ServiceException() throws Exception {
        Long etudiantId = 2L;

        when(fichierCVService.getCurrentCVByEtudiantID(etudiantId))
                .thenThrow(new RuntimeException("Fichier non trouvé"));

        mockMvc.perform(get("/api/cv/get-cv-by-etudiant-id/{id}", etudiantId)
                        .with(csrf()))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Fichier non trouvé"));
    }

    @Test
    public void testGetCV_InvalidId_ReturnsBadRequest() throws Exception {
        String invalidId = "abc";

        mockMvc.perform(get("/api/cv/get-cv-by-etudiant-id/{id}", invalidId)
                        .with(csrf()))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is("Failed to convert value of type 'java.lang.String' to required type 'java.lang.Long'; For input string: \"abc\"")))
                .andExpect(jsonPath("$.status", is(500)))
                .andExpect(jsonPath("$.timestamp").isNumber());
    }

    @Test
    public void testGetCV_NullId_ReturnsBadRequest() throws Exception {
        // Assuming the ID is required and cannot be null
        mockMvc.perform(get("/api/cv/get-cv-by-etudiant-id/{id}", (Object) null)
                        .with(csrf()))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is("An unexpected error occurred: No endpoint GET /api/cv/get-cv-by-etudiant-id/.")))
                .andExpect(jsonPath("$.status", is(500)))
                .andExpect(jsonPath("$.timestamp").isNumber());
    }

    @Test
    public void testGetCV_DifferentException() throws Exception {
        Long etudiantId = 4L;

        when(fichierCVService.getCurrentCVByEtudiantID(etudiantId))
                .thenThrow(new IllegalArgumentException("Invalid argument"));

        mockMvc.perform(get("/api/cv/get-cv-by-etudiant-id/{id}", etudiantId)
                        .with(csrf()))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Fichier non trouvé")); // As per controller's catch block
    }
}
