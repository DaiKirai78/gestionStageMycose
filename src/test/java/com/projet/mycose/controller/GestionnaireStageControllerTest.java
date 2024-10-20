package com.projet.mycose.controller;

import com.projet.mycose.dto.EnseignantDTO;
import com.projet.mycose.dto.EtudiantDTO;
import com.projet.mycose.modele.Programme;
import com.projet.mycose.modele.auth.Role;
import com.projet.mycose.service.GestionnaireStageService;
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
public class GestionnaireStageControllerTest {
    private MockMvc mockMvc;

    @Mock
    private GestionnaireStageService gestionnaireStageService;

    @InjectMocks
    private GestionnaireController gestionnaireController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(gestionnaireController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    public void testGetEtudiantsSansEnseignants_Success() throws Exception {
        // Arrange
        EtudiantDTO etudiantMock = new EtudiantDTO(
                1L,
                "unPrenom",
                "unNom",
                "unCourriel@mail.com",
                "555-666-4756",
                Role.ETUDIANT,
                Programme.TECHNIQUE_INFORMATIQUE
        );

        List<EtudiantDTO> listeEtudiantsDTOMock = new ArrayList<>();
        listeEtudiantsDTOMock.add(etudiantMock);
        when(gestionnaireStageService.getEtudiantsSansEnseignants(0)).thenReturn(listeEtudiantsDTOMock);

        // Act & Assert
        mockMvc.perform(post("/gestionnaire/getEtudiants")
                .param("pageNumber", String.valueOf(0))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isAccepted())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].courriel").value("unCourriel@mail.com"))
                .andExpect(jsonPath("$[0].numeroDeTelephone").value("555-666-4756"));
    }

    @Test
    public void testGetEtudiantsSansEnseignants_Error() throws Exception {
        //Arrange
        when(gestionnaireStageService.getEtudiantsSansEnseignants(0)).thenThrow(new RuntimeException());

        //Act & Assert
        mockMvc.perform(post("/gestionnaire/getEtudiants")
                        .param("pageNumber", String.valueOf(0))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testRechercherEnseignants_Success() throws Exception {
        // Arrange
        EnseignantDTO enseignantDTOMock = new EnseignantDTO(
                1L,
                "Vicente",
                "Cabezas",
                "vicen@gmail.com",
                "514-556-5566",
                Role.ENSEIGNANT
        );

        List<EnseignantDTO> listeEnseignantsDTOMock = new ArrayList<>();
        listeEnseignantsDTOMock.add(enseignantDTOMock);

        when(gestionnaireStageService.getEnseignantsParRecherche("vicente")).thenReturn(listeEnseignantsDTOMock);

        // Act & Assert
        mockMvc.perform(post("/gestionnaire/rechercheEnseignants")
                        .param("search", "vicente")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isAccepted())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].courriel").value("vicen@gmail.com"))
                .andExpect(jsonPath("$[0].numeroDeTelephone").value("514-556-5566"));
    }
}
