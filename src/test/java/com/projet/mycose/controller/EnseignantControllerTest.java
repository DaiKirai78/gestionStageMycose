package com.projet.mycose.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.projet.mycose.dto.EtudiantDTO;
import com.projet.mycose.dto.FicheEvaluationMilieuStageDTO;
import com.projet.mycose.exceptions.GlobalExceptionHandler;
import com.projet.mycose.exceptions.UserNotFoundException;
import com.projet.mycose.modele.FicheEvaluationMilieuStage;
import com.projet.mycose.modele.FicheEvaluationMilieuStage.*;
import com.projet.mycose.modele.auth.Role;
import com.projet.mycose.service.EnseignantService;
import com.projet.mycose.service.UtilisateurService;
import com.projet.mycose.dto.EnseignantDTO;
import com.projet.mycose.dto.RegisterEnseignantDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class EnseignantControllerTest {
    private MockMvc mockMvc;
    @Mock
    private EnseignantService enseignantService;

    @Mock
    private UtilisateurService utilisateurService;

    @InjectMocks
    private EnseignantController enseignantController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        mockMvc = MockMvcBuilders.standaloneSetup(enseignantController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    public void testCreationDeCompte_Succes() throws Exception {
        RegisterEnseignantDTO newEnseignant = new RegisterEnseignantDTO(
                "Karim", "Mihoubi", "438-532-2729", "mihoubi@gmail.com", "Mimi123$"
        );

        when(enseignantService.creationDeCompte(any(), any(), any(), any(), any()))
                .thenReturn(new EnseignantDTO(1L, "Karim", "Mihoubi", "mihoubi@gmail.com", "438-532-2729", Role.ENSEIGNANT));

        ObjectMapper objectMapper = new ObjectMapper();
        String enseignantJson = objectMapper.writeValueAsString(newEnseignant);

        this.mockMvc.perform(post("/enseignant/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(enseignantJson)
                        .with(csrf())
                        .with(user("karim").password("Mimi123$").roles("ENSEIGNANT")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nom").value("Mihoubi"))
                .andExpect(jsonPath("$.prenom").value("Karim"))
                .andExpect(jsonPath("$.courriel").value("mihoubi@gmail.com"))
                .andExpect(jsonPath("$.numeroDeTelephone").value("438-532-2729"))
                .andExpect(jsonPath("$.role").value("ENSEIGNANT"));
    }

    @Test
    public void testCreationDeCompte_EchecAvecConflit() throws Exception {
        RegisterEnseignantDTO newEnseignant = new RegisterEnseignantDTO(
                "Michel", "Genereux", "437-930-2483", "mihoubi@gmail.com", "Mimi123$"
        );

        ObjectMapper objectMapper = new ObjectMapper();
        String enseignantJson = objectMapper.writeValueAsString(newEnseignant);

        this.mockMvc.perform(post("/enseignant/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(enseignantJson)
                        .with(csrf())
                        .with(user("michel").password("Mimi123$").roles("ENSEIGNANT")))
                .andExpect(status().isConflict());
    }

    @Test
    public void getAllEtudiantsAEvaluerParProf_Success() throws Exception {
        // Arrange
        Long enseignantId = 1L;
        EtudiantDTO etudiant1 = new EtudiantDTO();
        etudiant1.setId(2L);
        etudiant1.setNom("Tyson");

        EtudiantDTO etudiant2 = new EtudiantDTO();
        etudiant2.setId(3L);
        etudiant2.setNom("Ali");

        List<EtudiantDTO> listeEtudiants = new ArrayList<>();
        listeEtudiants.add(etudiant1);
        listeEtudiants.add(etudiant2);

        PageRequest pageRequest = PageRequest.of(1, 10);

        Page<EtudiantDTO> pageEtudiants = new PageImpl<>(listeEtudiants, pageRequest, 2);

        when(enseignantService.getAllEtudiantsAEvaluerParProf(enseignantId, 1)).thenReturn(pageEtudiants);


        // Act & Assert
        mockMvc.perform(get("/enseignant/getAllEtudiantsAEvaluer")
                .param("enseignantId", String.valueOf(enseignantId))
                .param("pageNumber", "1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content[0].id").value(2L))
                .andExpect(jsonPath("$.content[0].nom").value("Tyson"))
                .andExpect(jsonPath("$.content[1].id").value(3L))
                .andExpect(jsonPath("$.content[1].nom").value("Ali"));
    }

    @Test
    public void getAllEtudiantsAEvaluerParProf_UserNotFound() throws Exception {
        // Arrange
        Long enseignantId = 1L;

        when(enseignantService.getAllEtudiantsAEvaluerParProf(enseignantId, 1)).thenThrow(new UserNotFoundException());


        // Act & Assert
        mockMvc.perform(get("/enseignant/getAllEtudiantsAEvaluer")
                        .param("enseignantId", String.valueOf(enseignantId))
                        .param("pageNumber", "1"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Utilisateur not found"));
    }


    @Test
    public void testEnregistrerFicheEvaluationStagiaire_Success() throws Exception {
        // Arrange: Create a sample FicheEvaluationMilieuStageDTO
        FicheEvaluationMilieuStageDTO dto = new FicheEvaluationMilieuStageDTO();
        dto.setId(1L);
        dto.setNomEntreprise("Entreprise Exemple");
        dto.setNomPersonneContact("Jean Dupont");
        dto.setAdresseEntreprise("123 Rue Principale");
        dto.setVilleEntreprise("Paris");
        dto.setCodePostalEntreprise("75001");
        dto.setTelephoneEntreprise("0123456789");
        dto.setTelecopieurEntreprise("0987654321");
        dto.setNomStagiaire("Marie Curie");
        dto.setDateDebutStage(LocalDateTime.of(2024, 5, 1, 9, 0));
        dto.setNumeroStage(1);
        dto.setEvaluationQA(FicheEvaluationMilieuStage.EvaluationMilieuStageReponses.TOTALEMENT_EN_ACCORD);
        dto.setEvaluationQB(EvaluationMilieuStageReponses.PLUTOT_EN_ACCORD);
        dto.setEvaluationQC(EvaluationMilieuStageReponses.PLUTOT_EN_DESACCORD);
        dto.setNombreHeuresParSemainePremierMois(35.0f);
        dto.setNombreHeuresParSemaineDeuxiemeMois(35.0f);
        dto.setNombreHeuresParSemaineTroisiemeMois(35.0f);
        dto.setEvaluationQD(EvaluationMilieuStageReponses.TOTALEMENT_EN_ACCORD);
        dto.setEvaluationQE(EvaluationMilieuStageReponses.PLUTOT_EN_ACCORD);
        dto.setEvaluationQF(EvaluationMilieuStageReponses.PLUTOT_EN_DESACCORD);
        dto.setEvaluationQG(EvaluationMilieuStageReponses.IMPOSSIBLE_DE_SE_PRONONCER);
        dto.setSalaireHoraire(15.5f);
        dto.setEvaluationQH(EvaluationMilieuStageReponses.TOTALEMENT_EN_ACCORD);
        dto.setEvaluationQI(EvaluationMilieuStageReponses.PLUTOT_EN_ACCORD);
        dto.setEvaluationQJ(EvaluationMilieuStageReponses.PLUTOT_EN_DESACCORD);
        dto.setCommentaires("Bon stage.");
        dto.setMilieuAPrivilegier(MilieuAPrivilegierReponses.PREMIER_STAGE);
        dto.setMilieuPretAAccueillirNombreStagiaires(MilieuPretAAccueillirNombreStagiairesReponses.DEUX);
        dto.setMilieuDesireAccueillirMemeStagiaire(OuiNonReponses.OUI);
        dto.setMillieuOffreQuartsTravailVariables(OuiNonReponses.NON);
        dto.setQuartTravailDebut1(LocalDateTime.of(2024, 5, 1, 9, 0));
        dto.setQuartTravailFin1(LocalDateTime.of(2024, 5, 1, 17, 0));
        dto.setQuartTravailDebut2(LocalDateTime.of(2024, 5, 2, 9, 0));
        dto.setQuartTravailFin2(LocalDateTime.of(2024, 5, 2, 17, 0));
        dto.setQuartTravailDebut3(LocalDateTime.of(2024, 5, 3, 9, 0));
        dto.setQuartTravailFin3(LocalDateTime.of(2024, 5, 3, 17, 0));

        // Mock the service method to do nothing (void method)
        Mockito.doNothing().when(enseignantService).enregistrerFicheEvaluationMilieuStage(any(FicheEvaluationMilieuStageDTO.class), eq(1L));

        // Convert DTO to JSON
        String dtoJson = objectMapper.writeValueAsString(dto);

        // Act & Assert: Perform POST request and expect 200 OK
        mockMvc.perform(post("/enseignant/saveFicheEvaluationMilieuStage")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(dtoJson)
                        .param("etudiantId", "1")
                        .with(csrf()))
                .andExpect(status().isOk());

        // Verify that the service method was called once with the correct parameters
        Mockito.verify(enseignantService, Mockito.times(1)).enregistrerFicheEvaluationMilieuStage(any(FicheEvaluationMilieuStageDTO.class), eq(1L));
    }

    @Test
    public void testEnregistrerFicheEvaluationStagiaire_ValidationFailure() throws Exception {
        // Arrange: Create an invalid FicheEvaluationMilieuStageDTO (missing required fields)
        FicheEvaluationMilieuStageDTO dto = new FicheEvaluationMilieuStageDTO();
        // Intentionally leaving out required fields like nomEntreprise, etc.

        // Convert DTO to JSON
        String dtoJson = objectMapper.writeValueAsString(dto);

        // Act & Assert: Perform POST request and expect 400 Bad Request with validation errors
        mockMvc.perform(post("/enseignant/saveFicheEvaluationMilieuStage")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(dtoJson)
                        .param("etudiantId", "1")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", containsString("nomStagiaire=Le nom du stagiaire ne peut pas être vide.")))
                .andExpect(jsonPath("$.message", containsString("salaireHoraire=Le salaire horaire ne peut pas être nul.")))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.timestamp").isNumber());
    }

    @Test
    public void testEnregistrerFicheEvaluationStagiaire_ServiceException() throws Exception {
        // Arrange: Create a valid FicheEvaluationMilieuStageDTO
        FicheEvaluationMilieuStageDTO dto = new FicheEvaluationMilieuStageDTO();
        dto.setId(1L);
        dto.setNomEntreprise("Entreprise Exemple");
        dto.setNomPersonneContact("Jean Dupont");
        dto.setAdresseEntreprise("123 Rue Principale");
        dto.setVilleEntreprise("Paris");
        dto.setCodePostalEntreprise("75001");
        dto.setTelephoneEntreprise("0123456789");
        dto.setTelecopieurEntreprise("0987654321");
        dto.setNomStagiaire("Marie Curie");
        dto.setDateDebutStage(LocalDateTime.of(2024, 5, 1, 9, 0));
        dto.setNumeroStage(1);
        dto.setEvaluationQA(EvaluationMilieuStageReponses.TOTALEMENT_EN_ACCORD);
        dto.setEvaluationQB(EvaluationMilieuStageReponses.PLUTOT_EN_ACCORD);
        dto.setEvaluationQC(EvaluationMilieuStageReponses.PLUTOT_EN_DESACCORD);
        dto.setNombreHeuresParSemainePremierMois(35.0f);
        dto.setNombreHeuresParSemaineDeuxiemeMois(35.0f);
        dto.setNombreHeuresParSemaineTroisiemeMois(35.0f);
        dto.setEvaluationQD(EvaluationMilieuStageReponses.TOTALEMENT_EN_ACCORD);
        dto.setEvaluationQE(EvaluationMilieuStageReponses.PLUTOT_EN_ACCORD);
        dto.setEvaluationQF(EvaluationMilieuStageReponses.PLUTOT_EN_DESACCORD);
        dto.setEvaluationQG(EvaluationMilieuStageReponses.IMPOSSIBLE_DE_SE_PRONONCER);
        dto.setSalaireHoraire(15.5f);
        dto.setEvaluationQH(EvaluationMilieuStageReponses.TOTALEMENT_EN_ACCORD);
        dto.setEvaluationQI(EvaluationMilieuStageReponses.PLUTOT_EN_ACCORD);
        dto.setEvaluationQJ(EvaluationMilieuStageReponses.PLUTOT_EN_DESACCORD);
        dto.setCommentaires("Bon stage.");
        dto.setMilieuAPrivilegier(MilieuAPrivilegierReponses.PREMIER_STAGE);
        dto.setMilieuPretAAccueillirNombreStagiaires(MilieuPretAAccueillirNombreStagiairesReponses.DEUX);
        dto.setMilieuDesireAccueillirMemeStagiaire(OuiNonReponses.OUI);
        dto.setMillieuOffreQuartsTravailVariables(OuiNonReponses.NON);
        dto.setQuartTravailDebut1(LocalDateTime.of(2024, 5, 1, 9, 0));
        dto.setQuartTravailFin1(LocalDateTime.of(2024, 5, 1, 17, 0));
        dto.setQuartTravailDebut2(LocalDateTime.of(2024, 5, 2, 9, 0));
        dto.setQuartTravailFin2(LocalDateTime.of(2024, 5, 2, 17, 0));
        dto.setQuartTravailDebut3(LocalDateTime.of(2024, 5, 3, 9, 0));
        dto.setQuartTravailFin3(LocalDateTime.of(2024, 5, 3, 17, 0));

        // Mock the service method to throw an exception
        Mockito.doThrow(new RuntimeException("Service error")).when(enseignantService).enregistrerFicheEvaluationMilieuStage(any(FicheEvaluationMilieuStageDTO.class), eq(1L));

        // Convert DTO to JSON
        String dtoJson = objectMapper.writeValueAsString(dto);

        // Act & Assert: Perform POST request and expect 500 Internal Server Error
        mockMvc.perform(post("/enseignant/saveFicheEvaluationMilieuStage")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(dtoJson)
                        .param("etudiantId", "1")
                        .with(csrf()))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Service error"));
    }
}
