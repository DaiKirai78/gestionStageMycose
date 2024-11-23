package com.projet.mycose.controller;

import com.projet.mycose.dto.ContratDTO;
import com.projet.mycose.exceptions.AuthenticationException;
import com.projet.mycose.exceptions.GlobalExceptionHandler;
import com.projet.mycose.modele.*;
import com.projet.mycose.modele.auth.Credentials;
import com.projet.mycose.modele.auth.Role;
import com.projet.mycose.service.ContratService;
import jakarta.persistence.PersistenceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class ContratControllerTest {
    private MockMvc mockMvc;

    private Contrat contrat;
    private Employeur employeur;
    private Etudiant etudiant;
    private GestionnaireStage gestionnaireStage;
    private OffreStage offreStage;

    @Mock
    private ContratService contratService;

    @InjectMocks
    private ContratController contratController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(contratController).setControllerAdvice(new GlobalExceptionHandler()).build();

        etudiant = new Etudiant();
        etudiant.setId(1L);

        employeur = new Employeur();
        employeur.setId(2L);

        gestionnaireStage = new GestionnaireStage();
        gestionnaireStage.setId(3L);

        offreStage = new FormulaireOffreStage();
        offreStage.setId(1L);

        contrat = new Contrat();
        contrat.setId(1L);
        contrat.setEtudiant(etudiant);
        contrat.setEmployeur(employeur);
        contrat.setGestionnaireStage(gestionnaireStage);
        contrat.setOffreStageid(offreStage.getId());
    }

    @Test
    void upload_Success() throws Exception {
        // Arrange
        ContratDTO contratDTO = new ContratDTO();
        contratDTO.setEtudiantId(etudiant.getId());
        contratDTO.setEmployeurId(employeur.getId());
        contratDTO.setGestionnaireStageId(gestionnaireStage.getId());
        contratDTO.setOffreStageId(offreStage.getId());

        when(contratService.save(anyLong(), anyLong(), anyLong(), anyLong())).thenReturn(contratDTO);

        // Act & Assert
        mockMvc.perform(multipart("/contrat/upload")
                        .param("etudiantId", "1")
                        .param("employeurId", "2")
                        .param("gestionnaireStageId", "3")
                        .param("offreStageId", "1"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.etudiantId").value(1L))
                .andExpect(jsonPath("$.employeurId").value(2L))
                .andExpect(jsonPath("$.gestionnaireStageId").value(3L))
                .andExpect(jsonPath("$.offreStageId").value(1L));
    }

    @Test
    void upload_ShouldReturnUnauthorized_WhenAuthenticationException() throws Exception {
        // Arrange
        when(contratService.save(anyLong(), anyLong(), anyLong(), anyLong())).thenThrow(new AuthenticationException(HttpStatus.UNAUTHORIZED, "Unauthorized access"));

        // Act & Assert
        mockMvc.perform(multipart("/contrat/upload")
                        .param("etudiantId", "1")
                        .param("employeurId", "2")
                        .param("gestionnaireStageId", "3")
                        .param("offreStageId", "1"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Unauthorized access"))
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.timestamp").isNumber());
    }

    @Test
    void upload_ShouldThrowRuntimeException_WhenIOException() throws Exception {
        // Arrange
        when(contratService.save(anyLong(), anyLong(), anyLong(), anyLong())).thenThrow(new PersistenceException("Erreur lors de la lecture du fichier PDF"));

        // Act & Assert
        mockMvc.perform(multipart("/contrat/upload")
                        .param("etudiantId", "1")
                        .param("employeurId", "2")
                        .param("gestionnaireStageId", "3")
                        .param("offreStageId", "1"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Erreur lors de la lecture du fichier PDF"))
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.timestamp").isNumber());
    }

    @Test
    void getContractByIdtest() throws Exception {
        // Arrange
        when(contratService.getContractById(1L)).thenReturn(ContratDTO.toDTO(contrat));

        // Act & Assert
        mockMvc.perform(get("/contrat/getContractById")
                        .param("contratId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.etudiantId").value(1L))
                .andExpect(jsonPath("$.employeurId").value(2L))
                .andExpect(jsonPath("$.gestionnaireStageId").value(3L))
                .andExpect(jsonPath("$.offreStageId").value(1L));
    }
}
