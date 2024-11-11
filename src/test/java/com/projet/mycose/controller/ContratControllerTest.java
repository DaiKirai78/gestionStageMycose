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

    @Mock
    private ContratService contratService;

    @InjectMocks
    private ContratController contratController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(contratController).setControllerAdvice(new GlobalExceptionHandler()).build();

        etudiant = new Etudiant();
        etudiant.setId(1L);
        etudiant.setPrenom("Roberto");
        etudiant.setNom("Berrios");
        Credentials credentials = new Credentials("roby@gmail.com", "passw0rd", Role.ETUDIANT);
        etudiant.setCredentials(credentials);
        etudiant.setProgramme(Programme.GENIE_LOGICIEL);
        etudiant.setContractStatus(Etudiant.ContractStatus.PENDING);

        employeur = new Employeur();
        employeur.setId(2L);
        employeur.setPrenom("Jean");
        employeur.setNom("Tremblay");
        employeur.setEntrepriseName("McDonald");
        Credentials credentials2 = new Credentials("jean@gmail.com", "passw0rd", Role.EMPLOYEUR);
        employeur.setCredentials(credentials2);
        employeur.setNumeroDeTelephone("450-374-3783");

        gestionnaireStage = new GestionnaireStage();
        gestionnaireStage.setId(3L);
        gestionnaireStage.setPrenom("Patrice");
        gestionnaireStage.setNom("Brodeur");
        Credentials credentials3 = new Credentials("patrice@gmail.com", "passw0rd", Role.GESTIONNAIRE_STAGE);
        gestionnaireStage.setCredentials(credentials3);
        gestionnaireStage.setNumeroDeTelephone("438-646-3245");

        contrat = new Contrat();
        contrat.setEtudiant(etudiant);
        contrat.setEmployeur(employeur);
        contrat.setStatus(Contrat.Status.INACTIVE);
    }

    @Test
    void upload_Success() throws Exception {
        // Arrange
        ContratDTO contratDTO = new ContratDTO();
        contratDTO.setEtudiantId(1L);
        contratDTO.setEmployeurId(2L);
        contratDTO.setGestionnaireStageId(3L);

        when(contratService.save(anyLong(), anyLong(), anyLong())).thenReturn(contratDTO);

        // Act & Assert
        mockMvc.perform(multipart("/contrat/upload")
                        .param("etudiantId", "1")
                        .param("employeurId", "2")
                        .param("gestionnaireStageId", "3"))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.etudiantId").value(1L))
                .andExpect(jsonPath("$.employeurId").value(2L))
                .andExpect(jsonPath("$.gestionnaireStageId").value(3L));
    }

    @Test
    void upload_ShouldReturnUnauthorized_WhenAuthenticationException() throws Exception {
        // Arrange
        when(contratService.save(anyLong(), anyLong(), anyLong())).thenThrow(new AuthenticationException(HttpStatus.UNAUTHORIZED, "Unauthorized access"));

        // Act & Assert
        mockMvc.perform(multipart("/contrat/upload")
                        .param("etudiantId", "1")
                        .param("employeurId", "2")
                        .param("gestionnaireStageId", "3"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Unauthorized access"))
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.timestamp").isNumber());
    }

    @Test
    void upload_ShouldThrowRuntimeException_WhenIOException() throws Exception {
        // Arrange
        when(contratService.save(anyLong(), anyLong(), anyLong())).thenThrow(new PersistenceException("Erreur lors de la lecture du fichier PDF"));

        // Act & Assert
        mockMvc.perform(multipart("/contrat/upload")
                        .param("etudiantId", "1")
                        .param("employeurId", "2")
                        .param("gestionnaireStageId", "3"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Erreur lors de la lecture du fichier PDF"))
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.timestamp").isNumber());
    }
}
