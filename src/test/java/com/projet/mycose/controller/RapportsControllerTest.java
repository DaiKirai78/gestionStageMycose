package com.projet.mycose.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.projet.mycose.dto.EtudiantDTO;
import com.projet.mycose.dto.OffreStageDTO;
import com.projet.mycose.modele.Etudiant;
import com.projet.mycose.service.EtudiantService;
import com.projet.mycose.service.OffreStageService;
import com.projet.mycose.exceptions.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class RapportsControllerTest {

    private MockMvc mockMvc;

    @Mock
    private OffreStageService offreStageService;

    @Mock
    private EtudiantService etudiantService;

    @InjectMocks
    private RapportsController rapportsController;

    private OffreStageDTO offreStageDTO1;
    private OffreStageDTO offreStageDTO2;
    private OffreStageDTO offreStageDTO3;
    private OffreStageDTO offreStageDTO4;
    private EtudiantDTO etudiantDTO1;
    private EtudiantDTO etudiantDTO2;



    @BeforeEach
    public void setUp() {
        offreStageDTO1 = new OffreStageDTO();
        offreStageDTO1.setId(1L);
        offreStageDTO1.setTitle("Offre 1");
        offreStageDTO1.setEntrepriseName("Entreprise 1");

        offreStageDTO2 = new OffreStageDTO();
        offreStageDTO2.setId(2L);
        offreStageDTO2.setTitle("Offre 2");
        offreStageDTO2.setEntrepriseName("Entreprise 2");

        offreStageDTO3 = new OffreStageDTO();
        offreStageDTO3.setId(3L);
        offreStageDTO3.setTitle("Offre 3");
        offreStageDTO3.setEntrepriseName("Entreprise 3");

        offreStageDTO4 = new OffreStageDTO();
        offreStageDTO4.setId(4L);
        offreStageDTO4.setTitle("Offre 4");
        offreStageDTO4.setEntrepriseName("Entreprise 4");

        etudiantDTO1 = new EtudiantDTO();
        etudiantDTO1.setId(1L);
        etudiantDTO1.setNom("John");
        etudiantDTO1.setPrenom("Doe");
        etudiantDTO1.setCourriel("eliescrummaster@gmail.com");

        etudiantDTO2 = new EtudiantDTO();
        etudiantDTO2.setId(2L);
        etudiantDTO2.setNom("Jane");
        etudiantDTO2.setPrenom("Smith");
        etudiantDTO2.setCourriel("wonka@gmail.com");

        // Initialize MockMvc with the controller and exception handler
        mockMvc = MockMvcBuilders.standaloneSetup(rapportsController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    public void rapportOffresNonValidees_ReturnsListOfOffreStageDTO() throws Exception {
        // Arrange
        List<OffreStageDTO> offresNonValidees = Arrays.asList(
                offreStageDTO1,
                offreStageDTO2
        );

        when(offreStageService.getWaitingOffreStage()).thenReturn(offresNonValidees);

        // Act & Assert
        mockMvc.perform(get("/api/rapports/offres-non-validees")
                        .with(user("user").password("password").roles("USER"))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].title", is("Offre 1")))
                .andExpect(jsonPath("$[0].entrepriseName", is("Entreprise 1")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].title", is("Offre 2")))
                .andExpect(jsonPath("$[1].entrepriseName", is("Entreprise 2")));
    }

    @Test
    public void rapportOffresValidees_ReturnsListOfOffreStageDTO() throws Exception {
        // Arrange
        List<OffreStageDTO> offresValidees = Arrays.asList(
                offreStageDTO3,
                offreStageDTO4
        );

        when(offreStageService.getAcceptedOffreStage()).thenReturn(offresValidees);

        // Act & Assert
        mockMvc.perform(get("/api/rapports/offres-validees")
                        .with(user("user").password("password").roles("USER"))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(3)))
                .andExpect(jsonPath("$[0].title", is("Offre 3")))
                .andExpect(jsonPath("$[0].entrepriseName", is("Entreprise 3")))
                .andExpect(jsonPath("$[1].id", is(4)))
                .andExpect(jsonPath("$[1].title", is("Offre 4")))
                .andExpect(jsonPath("$[1].entrepriseName", is("Entreprise 4")));
    }

    @Test
    public void rapportAllEtudiants_ReturnsListOfEtudiantDTO() throws Exception {
        // Arrange
        List<EtudiantDTO> etudiants = Arrays.asList(
                etudiantDTO1,
                etudiantDTO2
        );

        when(etudiantService.getAllEtudiants()).thenReturn(etudiants);

        // Act & Assert
        mockMvc.perform(get("/api/rapports/all-etudiants")
                        .with(user("user").password("password").roles("USER"))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].nom", is("John")))
                .andExpect(jsonPath("$[0].prenom", is("Doe")))
                .andExpect(jsonPath("$[0].courriel", is("eliescrummaster@gmail.com")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].nom", is("Jane")))
                .andExpect(jsonPath("$[1].prenom", is("Smith")))
                .andExpect(jsonPath("$[1].courriel", is("wonka@gmail.com")));
    }

    @Test
    public void rapportEtudiantsSansCV_ReturnsListOfEtudiantDTO() throws Exception {
        // Arrange
        List<EtudiantDTO> etudiantsSansCV = Arrays.asList(
                etudiantDTO1
        );

        when(etudiantService.getEtudiantsSansFichierCV()).thenReturn(etudiantsSansCV);

        // Act & Assert
        mockMvc.perform(get("/api/rapports/etudiants-sans-cv")
                        .with(user("user").password("password").roles("USER"))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].nom", is("John")))
                .andExpect(jsonPath("$[0].prenom", is("Doe")))
                .andExpect(jsonPath("$[0].courriel", is("eliescrummaster@gmail.com")));
    }

    @Test
    public void rapportEtudiantsAvecCVWaiting_ReturnsListOfEtudiantDTO() throws Exception {
        // Arrange
        List<EtudiantDTO> etudiantsAvecCVWaiting = Arrays.asList(
                etudiantDTO1
        );

        when(etudiantService.getEtudiantsWithFichierCVWaiting()).thenReturn(etudiantsAvecCVWaiting);

        // Act & Assert
        mockMvc.perform(get("/api/rapports/etudiants-avec-cv-waiting")
                        .with(user("user").password("password").roles("USER"))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].nom", is("John")))
                .andExpect(jsonPath("$[0].prenom", is("Doe")))
                .andExpect(jsonPath("$[0].courriel", is("eliescrummaster@gmail.com")));
    }

    @Test
    public void rapportEtudiantsSansConvocation_ReturnsListOfEtudiantDTO() throws Exception {
        // Arrange
        List<EtudiantDTO> etudiantsSansConvocation = Arrays.asList(
                etudiantDTO1
        );

        when(etudiantService.getEtudiantsSansConvocation()).thenReturn(etudiantsSansConvocation);

        // Act & Assert
        mockMvc.perform(get("/api/rapports/etudiants-sans-convocation")
                        .with(user("user").password("password").roles("USER"))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].nom", is("John")))
                .andExpect(jsonPath("$[0].prenom", is("Doe")))
                .andExpect(jsonPath("$[0].courriel", is("eliescrummaster@gmail.com")));
    }

    @Test
    public void rapportEtudiantsAvecConvocation_ReturnsListOfEtudiantDTO() throws Exception {
        // Arrange
        List<EtudiantDTO> etudiantsAvecConvocation = Arrays.asList(
                etudiantDTO1
        );

        when(etudiantService.getEtudiantsAvecConvocation()).thenReturn(etudiantsAvecConvocation);

        // Act & Assert
        mockMvc.perform(get("/api/rapports/etudiants-avec-convocation")
                        .with(user("user").password("password").roles("USER"))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].nom", is("John")))
                .andExpect(jsonPath("$[0].prenom", is("Doe")))
                .andExpect(jsonPath("$[0].courriel", is("eliescrummaster@gmail.com")));
    }

    @Test
    public void rapportEtudiantsInterviewed_ReturnsListOfEtudiantDTO() throws Exception {
        // Arrange
        List<EtudiantDTO> etudiantsInterviewed = Arrays.asList(
                etudiantDTO1
        );

        when(etudiantService.getEtudiantsInterviewed()).thenReturn(etudiantsInterviewed);

        // Act & Assert
        mockMvc.perform(get("/api/rapports/etudiants-interviewed")
                        .with(user("user").password("password").roles("USER"))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].nom", is("John")))
                .andExpect(jsonPath("$[0].prenom", is("Doe")))
                .andExpect(jsonPath("$[0].courriel", is("eliescrummaster@gmail.com")));
    }
}
