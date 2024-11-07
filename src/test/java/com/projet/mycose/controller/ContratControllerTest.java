package com.projet.mycose.controller;

import com.projet.mycose.dto.ContratDTO;
import com.projet.mycose.exceptions.AuthenticationException;
import com.projet.mycose.exceptions.GlobalExceptionHandler;
import com.projet.mycose.modele.Contrat;
import com.projet.mycose.modele.Employeur;
import com.projet.mycose.modele.Etudiant;
import com.projet.mycose.modele.Programme;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

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

        String content = "Ceci est un contenu de test";

        contrat = new Contrat();
        contrat.setEtudiant(etudiant);
        contrat.setEmployeur(employeur);
        contrat.setStatus(Contrat.Status.INACTIVE);
        contrat.setPdf(content.getBytes());
    }

    @Test
    void upload_Success() throws Exception {
        // Arrange
        MockMultipartFile file = new MockMultipartFile("contratPDF", "testfile.pdf", MediaType.APPLICATION_PDF_VALUE, "Test content".getBytes());
        ContratDTO contratDTO = new ContratDTO();
        contratDTO.setPdf("Base64Content");
        contratDTO.setEtudiantId(1L);
        contratDTO.setEmployeurId(2L);

        when(contratService.save(any(MultipartFile.class), anyLong(), anyLong())).thenReturn(contratDTO);

        // Act & Assert
        mockMvc.perform(multipart("/contrat/upload")
                        .file(file)
                        .param("etudiantId", "1")
                        .param("employeurId", "2"))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.pdf").value("Base64Content"))
                .andExpect(jsonPath("$.etudiantId").value(1L))
                .andExpect(jsonPath("$.employeurId").value(2L));
    }

    @Test
    void upload_ShouldReturnUnauthorized_WhenAuthenticationException() throws Exception {
        // Arrange
        MockMultipartFile file = new MockMultipartFile("contratPDF", "testfile.pdf", MediaType.APPLICATION_PDF_VALUE, "Test content".getBytes());

        when(contratService.save(any(MultipartFile.class), anyLong(), anyLong())).thenThrow(new AuthenticationException(HttpStatus.UNAUTHORIZED, "Unauthorized access"));

        // Act & Assert
        mockMvc.perform(multipart("/contrat/upload")
                        .file(file)
                        .param("etudiantId", "1")
                        .param("employeurId", "2"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Unauthorized access"))
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.timestamp").isNumber());
    }

    @Test
    void upload_ShouldThrowRuntimeException_WhenIOException() throws Exception {
        // Arrange
        MockMultipartFile file = new MockMultipartFile("contratPDF", "testfile.pdf", MediaType.APPLICATION_PDF_VALUE, "Test content".getBytes());

        when(contratService.save(any(MultipartFile.class), anyLong(), anyLong())).thenThrow(new PersistenceException("Erreur lors de la lecture du fichier PDF"));

        // Act & Assert
        mockMvc.perform(multipart("/contrat/upload")
                        .file(file)
                        .param("etudiantId", "1")
                        .param("employeurId", "2"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Erreur lors de la lecture du fichier PDF"))
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.timestamp").isNumber());
    }
}
