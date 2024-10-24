package com.projet.mycose.controller;

import com.projet.mycose.dto.EnseignantDTO;
import com.projet.mycose.dto.EtudiantDTO;
import com.projet.mycose.modele.Etudiant;
import com.projet.mycose.modele.Programme;
import com.projet.mycose.modele.auth.Role;
import com.projet.mycose.repository.UtilisateurRepository;
import com.projet.mycose.service.EtudiantService;
import com.projet.mycose.service.GestionnaireStageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class GestionnaireStageControllerTest {
    private MockMvc mockMvc;

    @Mock
    private GestionnaireStageService gestionnaireStageService;

    @Mock
    private EtudiantService etudiantService;

    @InjectMocks
    private GestionnaireController gestionnaireController;

    EtudiantDTO etudiantMock;
    EtudiantDTO etudiantMock2;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(gestionnaireController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
         etudiantMock = new EtudiantDTO(
                1L,
                "unPrenom",
                "unNom",
                "unCourriel@mail.com",
                "555-666-4756",
                Role.ETUDIANT,
                Programme.TECHNIQUE_INFORMATIQUE
        );
         etudiantMock2 = new EtudiantDTO(
                2L,
                "unPrenom",
                "unNom",
                "unCourriel@mail.com",
                "555-666-4756",
                Role.ETUDIANT,
                Programme.TECHNIQUE_INFORMATIQUE
        );

    }

    @Test
    public void testGetEtudiantsSansEnseignants_Success() throws Exception {
        // Arrange

        List<EtudiantDTO> listeEtudiantsDTOMock = new ArrayList<>();
        listeEtudiantsDTOMock.add(etudiantMock);
        when(gestionnaireStageService.getEtudiantsSansEnseignants(0, Programme.TECHNIQUE_INFORMATIQUE)).thenReturn(listeEtudiantsDTOMock);

        // Act & Assert
        mockMvc.perform(post("/gestionnaire/getEtudiants")
                .param("pageNumber", String.valueOf(0))
                .param("programme", Programme.TECHNIQUE_INFORMATIQUE.toString())
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
        when(gestionnaireStageService.getEtudiantsSansEnseignants(0, null)).thenThrow(new RuntimeException());

        //Act & Assert
        mockMvc.perform(post("/gestionnaire/getEtudiants")
                        .param("pageNumber", String.valueOf(0))
                        .param("programme", Programme.TECHNIQUE_INFORMATIQUE.toString())
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

    @Test
    public void testRechercherEnseignants_Error() throws Exception {
        //Arrange
        when(gestionnaireStageService.getEnseignantsParRecherche("uneValeur")).thenThrow(new RuntimeException());

        //Act & Assert
        mockMvc.perform(post("/gestionnaire/rechercheEnseignants")
                        .param("search", "uneValeur")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testGetAmountOfPages_Success() throws Exception {
        //Arrange
        when(gestionnaireStageService.getAmountOfPages()).thenReturn(2);

        //Act & Assert
        mockMvc.perform(get("/gestionnaire/getEtudiantsPages"))
                .andExpect(status().isAccepted())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("2"));
    }

    @Test
    public void testGetAmountOfPages_Error() throws Exception {
        //Arrange
        when(gestionnaireStageService.getAmountOfPages()).thenThrow(new RuntimeException());

        //Act & Assert
        mockMvc.perform(get("/gestionnaire/getEtudiantsPages"))
                .andExpect(status().isNoContent());

    }

    @Test
    public void testAssignerEnseignatEtudiant_Success() throws Exception {
        // Arrange
        Long idEtudiant = 1L;
        Long idEnseignant = 2L;

        // Act
        doNothing().when(gestionnaireStageService).assignerEnseigantEtudiant(idEtudiant, idEnseignant);

        // Assert & Assert
        mockMvc.perform(post("/gestionnaire/assignerEnseignantEtudiant")
                        .param("idEtudiant", String.valueOf(idEtudiant))
                        .param("idEnseignant", String.valueOf(idEnseignant)))
                .andExpect(status().isOk());

        verify(gestionnaireStageService, times(1)).assignerEnseigantEtudiant(idEtudiant, idEnseignant);
    }


    @Test
    public void testAssignerEnseignantEtudiant_Error() throws Exception{
        // Arrange
        Long idEtudiant = 1L;
        Long idEnseignant = 2L;

        doThrow(new RuntimeException()).when(gestionnaireStageService).assignerEnseigantEtudiant(idEtudiant, idEnseignant);

        // Act & Assert
        mockMvc.perform(post("/gestionnaire/assignerEnseignantEtudiant")
                        .param("idEtudiant", String.valueOf(idEtudiant))
                        .param("idEnseignant", String.valueOf(idEnseignant)))
                .andExpect(status().isNoContent());

        verify(gestionnaireStageService, times(1)).assignerEnseigantEtudiant(idEtudiant, idEnseignant);
    }

    @Test
    void getEtudiantsByProgramme_Success() throws Exception {
        // Arrange
        Programme programme = Programme.TECHNIQUE_INFORMATIQUE;
        List<EtudiantDTO> etudiants = Arrays.asList(
                etudiantMock,
                etudiantMock2
        );

        when(etudiantService.findEtudiantsByProgramme(programme)).thenReturn(etudiants);

        // Act & Assert
        mockMvc.perform(get("/gestionnaire/getEtudiantsParProgramme")
                        .param("programme", "TECHNIQUE_INFORMATIQUE")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isAccepted())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(etudiants.size()))
                .andExpect(jsonPath("$[0].id").value(etudiants.get(0).getId()))
                .andExpect(jsonPath("$[0].nom").value(etudiants.get(0).getNom()))
                .andExpect(jsonPath("$[1].id").value(etudiants.get(1).getId()))
                .andExpect(jsonPath("$[1].nom").value(etudiants.get(1).getNom()));

        verify(etudiantService, times(1)).findEtudiantsByProgramme(programme);
    }

    @Test
    void getEtudiantsByProgramme_ServiceThrowsException() throws Exception {
        // Arrange
        Programme programme = Programme.TECHNIQUE_INFORMATIQUE;

        when(etudiantService.findEtudiantsByProgramme(programme))
                .thenThrow(new RuntimeException("Service failure"));

        // Act & Assert
        mockMvc.perform(get("/gestionnaire/getEtudiantsParProgramme")
                        .param("programme", "TECHNIQUE_INFORMATIQUE")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(etudiantService, times(1)).findEtudiantsByProgramme(programme);
    }

    @Test
    void getEtudiantsByProgramme_EmptyList() throws Exception {
        // Arrange
        Programme programme = Programme.TECHNIQUE_INFORMATIQUE;
        List<EtudiantDTO> etudiants = Collections.emptyList();

        when(etudiantService.findEtudiantsByProgramme(programme)).thenReturn(etudiants);

        // Act & Assert
        mockMvc.perform(get("/gestionnaire/getEtudiantsParProgramme")
                        .param("programme", "TECHNIQUE_INFORMATIQUE")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isAccepted())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(0));

        verify(etudiantService, times(1)).findEtudiantsByProgramme(programme);
    }
}
