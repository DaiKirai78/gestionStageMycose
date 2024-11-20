package com.projet.mycose.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projet.mycose.dto.*;
import com.projet.mycose.exceptions.AuthenticationException;
import com.projet.mycose.exceptions.GlobalExceptionHandler;
import com.projet.mycose.exceptions.SignaturePersistenceException;
import com.projet.mycose.modele.Etudiant;
import com.projet.mycose.modele.OffreStage;
import com.projet.mycose.modele.Programme;
import com.projet.mycose.modele.auth.Role;
import com.projet.mycose.service.EtudiantService;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class EtudiantControllerTest {

    private MockMvc mockMvc;
    @Mock
    private EtudiantService etudiantService;

    @InjectMocks
    private EtudiantController etudiantController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(etudiantController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    public void testCreationDeCompte_Succes() throws Exception {
        RegisterEtudiantDTO newEtudiant = new RegisterEtudiantDTO(
                "Karim", "Mihoubi", "438-532-2729", "mihoubi@gmail.com", "Mimi123$", Programme.TECHNIQUE_INFORMATIQUE
        );

        when(etudiantService.creationDeCompte(any(), any(), any(), any(), any(), any()))
                .thenReturn(new EtudiantDTO(1L, "Karim", "Mihoubi", "mihoubi@gmail.com", "438-532-2729", Role.ETUDIANT, Programme.TECHNIQUE_INFORMATIQUE, Etudiant.ContractStatus.NO_CONTRACT));

        ObjectMapper objectMapper = new ObjectMapper();
        String etudiantJson = objectMapper.writeValueAsString(newEtudiant);

        this.mockMvc.perform(post("/etudiant/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(etudiantJson)
                        .with(csrf())
                        .with(user("karim").password("Mimi123$").roles("ETUDIANT")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nom").value("Mihoubi"))
                .andExpect(jsonPath("$.prenom").value("Karim"))
                .andExpect(jsonPath("$.courriel").value("mihoubi@gmail.com"))
                .andExpect(jsonPath("$.numeroDeTelephone").value("438-532-2729"))
                .andExpect(jsonPath("$.role").value("ETUDIANT"))
                .andExpect(jsonPath("$.programme").value(Programme.TECHNIQUE_INFORMATIQUE.name()))
                .andExpect(jsonPath("$.contractStatus").value(Etudiant.ContractStatus.NO_CONTRACT.name()));
    }

    @Test
    public void testCreationDeCompte_EchecAvecConflit() throws Exception {
        RegisterEtudiantDTO newEtudiant = new RegisterEtudiantDTO(
                "Michel", "Genereux", "437-930-2483", "mihoubi@gmail.com", "Mimi123$", Programme.TECHNIQUE_INFORMATIQUE
        );

        ObjectMapper objectMapper = new ObjectMapper();
        String etudiantJson = objectMapper.writeValueAsString(newEtudiant);

        this.mockMvc.perform(post("/etudiant/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(etudiantJson)
                        .with(csrf())
                        .with(user("michel").password("Mimi123$").roles("ETUDIANT")))
                .andExpect(status().isConflict());
    }

    @Test
    public void testGetStages_Success() throws Exception{
        // Arrange

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
                2021
                );

        List<OffreStageDTO> mockListeOffres = new ArrayList<>();
        mockListeOffres.add(mockFormulaire);
        when(etudiantService.getStages(0)).thenReturn(mockListeOffres);

        // Act & Assert
        mockMvc.perform(get("/etudiant/getStages")
                        .param("pageNumber", String.valueOf(0))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isAccepted())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L));

    }

    @Test
    public void testGetStages_Error() throws Exception {
        //Arrange
        when(etudiantService.getStages(0)).thenThrow(new RuntimeException());


        //Act & Assert
        mockMvc.perform(get("/etudiant/getStages")
                .param("pageNumber", String.valueOf(0))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testGetAmountOfPages_Success() throws Exception {
        //Arrange
        when(etudiantService.getAmountOfPages()).thenReturn(2);

        //Act & Assert
        mockMvc.perform(get("/etudiant/pages")
                        .header("Authorization", "tokenValide"))
                .andExpect(status().isAccepted())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("2"));
    }

    @Test
    public void testGetAmountOfPages_Error() throws Exception {
        //Arrange
        when(etudiantService.getAmountOfPages()).thenThrow(new RuntimeException());

        //Act & Assert
        mockMvc.perform(get("/etudiant/pages")
                .header("Authorization", "tokenValide"))
                .andExpect(status().isNoContent());

    }

    @Test
    public void testRechercheOffre_Success() throws Exception{
        //Arrange

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
                2021
        );

        List<OffreStageDTO> mockListeOffres = new ArrayList<>();
        mockListeOffres.add(mockFormulaire);
        when(etudiantService.getStagesByRecherche(0, "uneRecherche")).thenReturn(mockListeOffres);

        //Act & Assert
        mockMvc.perform(post("/etudiant/recherche-offre")
                        .param("pageNumber", String.valueOf(0))
                        .param("recherche", "uneRecherche")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isAccepted())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    public void testRechercheOffre_Error() throws Exception {
        //Arrange
        when(etudiantService.getStagesByRecherche(0, "uneRecherche")).thenThrow(new RuntimeException());


        //Act & Assert
        mockMvc.perform(post("/etudiant/recherche-offre")
                        .param("pageNumber", String.valueOf(0))
                        .param("recherche", "uneRecherche")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturnUnauthorizedWhenAuthenticationFails() throws Exception {
        // Arrange
        MockMultipartFile signatureFile = new MockMultipartFile(
                "signature",
                "signature.png",
                MediaType.IMAGE_PNG_VALUE,
                "Dummy Image Content".getBytes()
        );

        Long contratId = 123L;
        String password = "wrongPassword";

        when(etudiantService.enregistrerSignature(
                any(),
                eq(password),
                eq(contratId))
        ).thenThrow(new AuthenticationException(HttpStatus.UNAUTHORIZED, "Email ou mot de passe invalide."));

        // Act
        mockMvc.perform(multipart("/etudiant/enregistrerSignature")
                        .file(signatureFile)
                        .param("contratId", String.valueOf(contratId))
                        .param("password", password)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Email ou mot de passe invalide."))
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.timestamp").isNumber());

        // Assert

        // Verify that the service was called once
        verify(etudiantService, times(1))
                .enregistrerSignature(any(), eq(password), eq(contratId));
    }

    @Test
    void shouldReturnAcceptedStatus() throws Exception {
        // Arrange
        MockMultipartFile signatureFile = new MockMultipartFile(
                "signature",
                "signature.png",
                MediaType.IMAGE_PNG_VALUE,
                "Dummy Image Content".getBytes()
        );

        Long contratId = 123L;
        String password = "securePassword";
        String expectedResponseMessage = "Signature sauvegardée";

        when(etudiantService.enregistrerSignature(
                any(),
                eq(password),
                eq(contratId))
        ).thenReturn(expectedResponseMessage);

        // Act
        MvcResult result = mockMvc.perform(multipart("/etudiant/enregistrerSignature")
                        .file(signatureFile)
                        .param("contratId", String.valueOf(contratId))
                        .param("password", password)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isAccepted())
                .andReturn();

        // Assert
        String responseContent = result.getResponse().getContentAsString();
        assertEquals("Signature sauvegardÃ©e", responseContent, "Unexpected response content.");

        // Verify that the service was called once
        verify(etudiantService, times(1))
                .enregistrerSignature(any(), eq(password), eq(contratId));
    }

    @Test
    void shouldReturnBadRequestWhenSignatureMissing() throws Exception {
        // Arrange
        Long contratId = 123L;
        String password = "securePassword";

        // Act
        mockMvc.perform(multipart("/etudiant/enregistrerSignature")
                        // No file uploaded
                        .param("contratId", String.valueOf(contratId))
                        .param("password", password)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Missing part: signature"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.timestamp").isNumber());

        // Assert

        // Verify that the service was never called
        verify(etudiantService, never())
                .enregistrerSignature(any(), anyString(), anyLong());
    }

    @Test
    void shouldReturnBadRequestWhenContratIdMissing() throws Exception {
        // Arrange
        MockMultipartFile signatureFile = new MockMultipartFile(
                "signature",
                "signature.png",
                MediaType.IMAGE_PNG_VALUE,
                "Dummy Image Content".getBytes()
        );
        String password = "securePassword";

        // Act
        mockMvc.perform(multipart("/etudiant/enregistrerSignature")
                        .file(signatureFile)
                        // Missing contratId
                        .param("password", password)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Missing parameter: contratId"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.timestamp").isNumber());

        // Assert
        // Verify that the service was never called
        verify(etudiantService, never())
                .enregistrerSignature(any(), anyString(), anyLong());
    }

    @Test
    void shouldReturnBadRequestWhenPasswordMissing() throws Exception {
        // Arrange
        MockMultipartFile signatureFile = new MockMultipartFile(
                "signature",
                "signature.png",
                MediaType.IMAGE_PNG_VALUE,
                "Dummy Image Content".getBytes()
        );
        Long contratId = 123L;

        // Act
        mockMvc.perform(multipart("/etudiant/enregistrerSignature")
                        .file(signatureFile)
                        .param("contratId", String.valueOf(contratId))
                        // Missing password
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Missing parameter: password"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.timestamp").isNumber());

        // Assert
        // Verify that the service was never called
        verify(etudiantService, never())
                .enregistrerSignature(any(), anyString(), anyLong());
    }

    @Test
    void shouldReturnNotFoundWhenUserNotFound() throws Exception {
        // Arrange
        MockMultipartFile signatureFile = new MockMultipartFile(
                "signature",
                "signature.png",
                MediaType.IMAGE_PNG_VALUE,
                "Dummy Image Content".getBytes()
        );

        Long contratId = 123L;
        String password = "securePassword";

        when(etudiantService.enregistrerSignature(
                any(),
                eq(password),
                eq(contratId))
        ).thenThrow(new AuthenticationException(HttpStatus.NOT_FOUND, "Utilisateur not found"));

        // Act
        mockMvc.perform(multipart("/etudiant/enregistrerSignature")
                        .file(signatureFile)
                        .param("contratId", String.valueOf(contratId))
                        .param("password", password)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Utilisateur not found"))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.timestamp").isNumber());

        // Assert

        // Verify that the service was called once
        verify(etudiantService, times(1))
                .enregistrerSignature(any(), eq(password), eq(contratId));
    }

    @Test
    void shouldReturnNotFoundWhenContratNotFound() throws Exception {
        // Arrange
        MockMultipartFile signatureFile = new MockMultipartFile(
                "signature",
                "signature.png",
                MediaType.IMAGE_PNG_VALUE,
                "Dummy Image Content".getBytes()
        );

        Long contratId = 999L; // Assume this ID does not exist
        String password = "securePassword";

        when(etudiantService.enregistrerSignature(
                any(),
                eq(password),
                eq(contratId))
        ).thenThrow(new AuthenticationException(HttpStatus.NOT_FOUND, "Contrat not found"));

        // Act
        mockMvc.perform(multipart("/etudiant/enregistrerSignature")
                        .file(signatureFile)
                        .param("contratId", String.valueOf(contratId))
                        .param("password", password)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Contrat not found"))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.timestamp").isNumber());


        // Assert
        // Verify that the service was called once
        verify(etudiantService, times(1))
                .enregistrerSignature(any(), eq(password), eq(contratId));
    }

    @Test
    void shouldReturnInternalServerErrorWhenSavingSignatureFails() throws Exception {
        // Arrange
        MockMultipartFile signatureFile = new MockMultipartFile(
                "signature",
                "signature.png",
                MediaType.IMAGE_PNG_VALUE,
                "Dummy Image Content".getBytes()
        );

        Long contratId = 123L;
        String password = "securePassword";

        when(etudiantService.enregistrerSignature(
                any(),
                eq(password),
                eq(contratId))
        ).thenThrow(new SignaturePersistenceException("Error while saving signature"));

        // Act
        mockMvc.perform(multipart("/etudiant/enregistrerSignature")
                        .file(signatureFile)
                        .param("contratId", String.valueOf(contratId))
                        .param("password", password)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Error while saving signature"))
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.timestamp").isNumber());

        // Assert

        // Verify that the service was called once
        verify(etudiantService, times(1))
                .enregistrerSignature(any(), eq(password), eq(contratId));
    }

    @Test
    public void testGetAllContratsNonSignes() throws Exception {
        int page = 1;
        // Arrange
        ContratDTO contrat1 = new ContratDTO();
        contrat1.setId(1L);

        ContratDTO contrat2 = new ContratDTO();
        contrat2.setId(2L);

        List<ContratDTO> contrats = Arrays.asList(contrat1, contrat2);

        when(etudiantService.getAllContratsNonSignes(page)).thenReturn(contrats);

        // Act & Assert
        mockMvc.perform(get("/etudiant/getContratsNonSignees")
                        .param("page", String.valueOf(page))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[1].id", is(2)));
    }

    @Test
    public void testGetAmountOfPagesOfCandidaturesNonSignees_Success() throws Exception {
        int expectedPages = 5;
        // Arrange
        when(etudiantService.getAmountOfPagesOfContractNonSignees()).thenReturn(expectedPages);

        // Act & Assert
        mockMvc.perform(get("/etudiant/pagesContrats")
                        .with(csrf()))
                .andExpect(status().isAccepted())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(String.valueOf(expectedPages)));
    }

    @Test
    public void testGetAmountOfPagesOfCandidaturesNonSignees_Exception() throws Exception {
        // Arrange
        when(etudiantService.getAmountOfPagesOfContractNonSignees()).thenThrow(new RuntimeException("Some exception"));

        // Act & Assert
        mockMvc.perform(get("/etudiant/pagesContrats")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }
}