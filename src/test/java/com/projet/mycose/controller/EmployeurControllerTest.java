package com.projet.mycose.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projet.mycose.modele.OffreStage;
import com.projet.mycose.modele.auth.Role;
import com.projet.mycose.service.EmployeurService;
import com.projet.mycose.service.UtilisateurService;
import com.projet.mycose.service.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
        // Arrange
        String authHeader = "Bearer unTokenValide";

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
                OffreStage.Status.WAITING
        );

        List<OffreStageDTO> mockListeOffres = new ArrayList<>();
        mockListeOffres.add(mockFormulaire);
        when(employeurService.getStages(authHeader, 0)).thenReturn(mockListeOffres);

        // Act & Assert
        mockMvc.perform(post("/entreprise/getOffresPosted")
                        .header("Authorization", authHeader).param("pageNumber", String.valueOf(0))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isAccepted())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].status").value("WAITING"));

    }

    @Test
    public void testGetStages_Error() throws Exception {
        //Arrange
        when(employeurService.getStages("token", 0)).thenThrow(new RuntimeException());


        //Act & Assert
        mockMvc.perform(post("/entreprise/getOffresPosted")
                        .header("Authorization", "unToken")
                        .param("pageNumber", String.valueOf(0))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testGetAmountOfPages_Error() throws Exception {
        //Arrange
        when(employeurService.getAmountOfPages("token")).thenThrow(new RuntimeException());

        //Act & Assert
        mockMvc.perform(get("/entreprise/pages")
                        .header("Authorization", "tokenValide"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testGetAmountOfPages_Success() throws Exception {
        //Arrange
        when(employeurService.getAmountOfPages("tokenValide")).thenReturn(2);

        //Act & Assert
        mockMvc.perform(get("/entreprise/pages")
                        .header("Authorization", "tokenValide"))
                .andExpect(status().isAccepted())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("2"));
    }
}
