package com.projet.mycose.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projet.mycose.dto.*;
import com.projet.mycose.modele.OffreStage;
import com.projet.mycose.modele.Programme;
import com.projet.mycose.modele.auth.Role;
import com.projet.mycose.service.EmployeurService;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
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
                2022
        );

        List<OffreStageDTO> mockListeOffres = new ArrayList<>();
        mockListeOffres.add(mockFormulaire);
        when(employeurService.getStages(0)).thenReturn(mockListeOffres);

        // Act & Assert
        mockMvc.perform(post("/entreprise/getOffresPosted")
                        .param("pageNumber", String.valueOf(0))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isAccepted())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].status").value("WAITING"));

    }

    @Test
    public void testGetStages_Error() throws Exception {
        //Arrange
        when(employeurService.getStages(0)).thenThrow(new RuntimeException());


        //Act & Assert
        mockMvc.perform(post("/entreprise/getOffresPosted")
                        .param("pageNumber", String.valueOf(0))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testGetAmountOfPages_Error() throws Exception {
        //Arrange
        when(employeurService.getAmountOfPages()).thenThrow(new RuntimeException());

        //Act & Assert
        mockMvc.perform(get("/entreprise/pages"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testGetAmountOfPages_Success() throws Exception {
        //Arrange
        when(employeurService.getAmountOfPages()).thenReturn(2);

        //Act & Assert
        mockMvc.perform(get("/entreprise/pages"))
                .andExpect(status().isAccepted())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("2"));
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
                null
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
    public void testGetAllContratsNonSignees_Error() throws Exception {
        //Arrange
        when(employeurService.getAllContratsNonSignes(0)).thenThrow(new RuntimeException());

        //Act & Assert
        mockMvc.perform(post("/entreprise/getContratsNonSignees")
                .param("pageNumber", "0")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
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
    public void testGetAmountOfPagesContratsNonSignees_Error() throws Exception {
        //Arrange
        when(employeurService.getAmountOfPagesOfContractNonSignees()).thenThrow(new RuntimeException());

        //Act & Assert
        mockMvc.perform(get("/entreprise/pagesContrats"))
                .andExpect(status().isNoContent());
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

        when(employeurService.enregistrerSignature(any(MultipartFile.class), anyString(), any(Long.class))).thenThrow(new RuntimeException());

        // Act & Assert
        mockMvc.perform(multipart("/entreprise/enregistrerSignature")
                        .file(signatureFile)
                        .param("contratId", "1")
                        .param("password", "Passw0rd")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

}
