package com.projet.mycose.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projet.mycose.dto.*;
import com.projet.mycose.exceptions.GlobalExceptionHandler;
import com.projet.mycose.exceptions.SignaturePersistenceException;
import com.projet.mycose.modele.Contrat;
import com.projet.mycose.modele.auth.Role;
import com.projet.mycose.service.EmployeurService;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class EmployeurControllerTest {
    private MockMvc mockMvc;
    @Mock
    private EmployeurService employeurService;

    @InjectMocks
    private EmployeurController employeurController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(employeurController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    public void testCreationDeCompte_Succes() throws Exception {
        RegisterEmployeurDTO newEmployeur = new RegisterEmployeurDTO(
                "Karim", "Mihoubi", "438-532-2729", "mihoubi@gmail.com", "Mimi123$", "Couche-Tard"
        );

        when(employeurService.creationDeCompte(any(), any(), any(), any(), any(), any()))
                .thenReturn(new EmployeurDTO(1L, "Karim", "Mihoubi", "mihoubi@gmail.com", "438-532-2729", "Couche-Tard", Role.EMPLOYEUR));

        ObjectMapper objectMapper = new ObjectMapper();
        String employeurJson = objectMapper.writeValueAsString(newEmployeur);

        this.mockMvc.perform(post("/entreprise/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(employeurJson)
                        .with(csrf())
                        .with(user("karim").password("Mimi123$").roles("EMPLOYEUR")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nom").value("Mihoubi"))
                .andExpect(jsonPath("$.prenom").value("Karim"))
                .andExpect(jsonPath("$.courriel").value("mihoubi@gmail.com"))
                .andExpect(jsonPath("$.numeroDeTelephone").value("438-532-2729"))
                .andExpect(jsonPath("$.role").value("EMPLOYEUR"));
    }

    @Test
    public void testCreationDeCompte_EchecAvecConflit() throws Exception {
        RegisterEmployeurDTO newEmployeur = new RegisterEmployeurDTO(
                "Karim", "Mihoubi", "438-532-2729", "mihoubi@gmail.com", "Mimi123$", "Couche-Tard"
        );

        ObjectMapper objectMapper = new ObjectMapper();
        String employeurJson = objectMapper.writeValueAsString(newEmployeur);

        this.mockMvc.perform(post("/entreprise/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(employeurJson)
                        .with(csrf())
                        .with(user("michel").password("Mimi123$").roles("EMPLOYEUR")))
                .andExpect(status().isConflict());
    }

    @Test
    public void testGetAllContratsNonSignees_Success() throws Exception {
        //Arrange
        ContratDTO contratDTOMock = new ContratDTO(
                1L,
                null,
                null,
                null,
                null,
                2L,
                3L,
                Contrat.Status.ACTIVE
        );

        List<ContratDTO> listeContratsMock = new ArrayList<>();
        listeContratsMock.add(contratDTOMock);
        when(employeurService.getAllContratsNonSignes(0)).thenReturn(listeContratsMock);

        //Act & Assert
        mockMvc.perform(post("/entreprise/getContratsNonSignees")
                .param("pageNumber", "0")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isAccepted())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    public void testGetAllContratsNonSignees_Empty() throws Exception {
        //Arrange

        //Act & Assert
        mockMvc.perform(post("/entreprise/getContratsNonSignees")
                .param("pageNumber", "0")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isAccepted())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("[]"));
    }

    @Test
    public void testGetAmountOfPagesContratsNonSignees_Success() throws Exception {
        //Arrange
        when(employeurService.getAmountOfPagesOfContractNonSignees()).thenReturn(2);

        //Act & Assert
        mockMvc.perform(get("/entreprise/pagesContrats"))
                .andExpect(status().isAccepted())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("2"));
    }

    @Test
    public void testGetAmountOfPagesContratsNonSigneesIsZero() throws Exception {
        //Arrange

        //Act & Assert
        mockMvc.perform(get("/entreprise/pagesContrats"))
                .andExpect(status().isAccepted())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("0"));
    }

    @Test
    public void testEnregistrerSignature_Success() throws Exception {
        // Arrange
        LoginDTO loginDTOMock = new LoginDTO("username", "password");
        MockMultipartFile signatureFile = new MockMultipartFile("signature", "signature.png", "image/png", "test signature content".getBytes());

        when(employeurService.enregistrerSignature(any(MultipartFile.class), anyString(), any(Long.class))).thenReturn("Signature enregistree avec succes");

        // Act & Assert
        mockMvc.perform(multipart("/entreprise/enregistrerSignature")
                        .file(signatureFile)
                        .param("contratId", "1")
                        .param("password", "Passw0rd")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isAccepted())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("Signature enregistree avec succes"));
    }


    @Test
    public void testEnregistrerSignature_Error() throws Exception {
        // Arrange
        MockMultipartFile signatureFile = new MockMultipartFile("signature", "signature.png", "image/png", "test signature content".getBytes());

        Long contratId = 123L;
        String password = "securePassword";

        when(employeurService.enregistrerSignature(any(MultipartFile.class), anyString(), any(Long.class))).thenThrow(new SignaturePersistenceException("Error while saving signature"));

        // Act & Assert
        mockMvc.perform(multipart("/entreprise/enregistrerSignature")
                        .file(signatureFile)
                        .param("contratId", String.valueOf(contratId))
                        .param("password", password)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Error while saving signature"))
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.timestamp").isNumber());

        // Assert
        // Verify that the service was called once
        verify(employeurService, times(1))
                .enregistrerSignature(any(), eq(password), eq(contratId));
    }

    @Test
    public void testEnregistrerFicheEvaluationStagiaire_Success() throws Exception {
        // Arrange
        Long etudiantId = 2L;

        FicheEvaluationStagiaireDTO ficheEvaluationStagiaireDTOMock = new FicheEvaluationStagiaireDTO();
        ficheEvaluationStagiaireDTOMock.setId(1L);
        ficheEvaluationStagiaireDTOMock.setNumeroTelephone("555-444-3333");
        ficheEvaluationStagiaireDTOMock.setFonctionSuperviseur("Manager");

        doNothing().when(employeurService).enregistrerFicheEvaluationStagiaire(any(FicheEvaluationStagiaireDTO.class), eq(etudiantId));

        ObjectMapper objectMapper = new ObjectMapper();
        String ficheEvaluationJson = objectMapper.writeValueAsString(ficheEvaluationStagiaireDTOMock);

        // Act & Assert
        mockMvc.perform(post("/entreprise/saveFicheEvaluation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ficheEvaluationJson)
                        .param("etudiantId", etudiantId.toString()))
                .andExpect(status().isOk());
        verify(employeurService, times(1)).enregistrerFicheEvaluationStagiaire(any(FicheEvaluationStagiaireDTO.class), eq(etudiantId));
    }

    @Test
    public void testEnregistrerFicheEvaluationStagiaire_Error() throws Exception {
        //Arrange
        FicheEvaluationStagiaireDTO ficheEvaluationStagiaireDTOMock = new FicheEvaluationStagiaireDTO();

        doNothing().when(employeurService).enregistrerFicheEvaluationStagiaire(ficheEvaluationStagiaireDTOMock, 2L);
        ObjectMapper objectMapper = new ObjectMapper();
        String ficheEvaluationJson = objectMapper.writeValueAsString(ficheEvaluationStagiaireDTOMock);

        //Act & Assert
        mockMvc.perform(post("/entreprise/saveFicheEvaluation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(ficheEvaluationJson)
                .param("etudiantId", String.valueOf(2L)))
                .andExpect(status().isInternalServerError());
        verify(employeurService, times(0)).enregistrerFicheEvaluationStagiaire(ficheEvaluationStagiaireDTOMock, 2L);
    }

}
