package com.projet.mycose.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projet.mycose.dto.EtudiantDTO;
import com.projet.mycose.dto.FormulaireOffreStageDTO;
import com.projet.mycose.dto.OffreStageDTO;
import com.projet.mycose.dto.RegisterEtudiantDTO;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
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
        mockMvc.perform(post("/etudiant/getStages")
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
        mockMvc.perform(post("/etudiant/getStages")
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

}
