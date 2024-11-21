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
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
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
        // (Not necessary to serialize the DTO to JSON since we're sending form data)

        // Define all necessary form parameters
        String nomEntreprise = "Entreprise Exemple";
        String nomPersonneContact = "Jean Dupont";
        String adresseEntreprise = "123 Rue Principale";
        String villeEntreprise = "Paris";
        String codePostalEntreprise = "75001";
        String telephoneEntreprise = "0123456789";
        String telecopieurEntreprise = "0987654321";
        String nomStagiaire = "Marie Curie";
        String dateDebutStage = "2024-05-01T09:00:00"; // ISO format
        String numeroStage = "1";
        String evalQA = "TOTALEMENT_EN_ACCORD"; // Assuming enum values as strings
        String evalQB = "PLUTOT_EN_ACCORD";
        String evalQC = "PLUTOT_EN_DESACCORD";
        String nombreHeuresParSemainePremierMois = "35.0";
        String nombreHeuresParSemaineDeuxiemeMois = "35.0";
        String nombreHeuresParSemaineTroisiemeMois = "35.0";
        String evalQD = "TOTALEMENT_EN_ACCORD";
        String evalQE = "PLUTOT_EN_ACCORD";
        String evalQF = "PLUTOT_EN_DESACCORD";
        String evalQG = "IMPOSSIBLE_DE_SE_PRONONCER";
        String salaireHoraire = "15.5";
        String evalQH = "TOTALEMENT_EN_ACCORD";
        String evalQI = "PLUTOT_EN_ACCORD";
        String evalQJ = "PLUTOT_EN_DESACCORD";
        String commentaires = "Bon stage.";
        String milieuAPrivilegier = "PREMIER_STAGE";
        String milieuPretAAccueillirNombreStagiaires = "DEUX";
        String milieuDesireAccueillirMemeStagiaire = "OUI";
        String millieuOffreQuartsTravailVariables = "NON";
        String quartTravailDebut1 = "2024-05-01T09:00:00";
        String quartTravailFin1 = "2024-05-01T17:00:00";
        String quartTravailDebut2 = "2024-05-02T09:00:00";
        String quartTravailFin2 = "2024-05-02T17:00:00";
        String quartTravailDebut3 = "2024-05-03T09:00:00";
        String quartTravailFin3 = "2024-05-03T17:00:00";

        // Mock the service method to do nothing (void method)
        Mockito.doNothing().when(enseignantService).enregistrerFicheEvaluationMilieuStage(any(FicheEvaluationMilieuStageDTO.class), eq(1L));

        // Act & Assert: Perform POST request and expect 200 OK
        mockMvc.perform(post("/enseignant/saveFicheEvaluationMilieuStage")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("etudiantId", "1")
                        .param("nomEntreprise", nomEntreprise)
                        .param("nomPersonneContact", nomPersonneContact)
                        .param("adresseEntreprise", adresseEntreprise)
                        .param("villeEntreprise", villeEntreprise)
                        .param("codePostalEntreprise", codePostalEntreprise)
                        .param("telephoneEntreprise", telephoneEntreprise)
                        .param("telecopieurEntreprise", telecopieurEntreprise)
                        .param("nomStagiaire", nomStagiaire)
                        .param("dateDebutStage", dateDebutStage)
                        .param("numeroStage", numeroStage)
                        .param("evalQA", evalQA)
                        .param("evalQB", evalQB)
                        .param("evalQC", evalQC)
                        .param("nombreHeuresParSemainePremierMois", nombreHeuresParSemainePremierMois)
                        .param("nombreHeuresParSemaineDeuxiemeMois", nombreHeuresParSemaineDeuxiemeMois)
                        .param("nombreHeuresParSemaineTroisiemeMois", nombreHeuresParSemaineTroisiemeMois)
                        .param("evalQD", evalQD)
                        .param("evalQE", evalQE)
                        .param("evalQF", evalQF)
                        .param("evalQG", evalQG)
                        .param("salaireHoraire", salaireHoraire)
                        .param("evalQH", evalQH)
                        .param("evalQI", evalQI)
                        .param("evalQJ", evalQJ)
                        .param("commentaires", commentaires)
                        .param("milieuAPrivilegier", milieuAPrivilegier)
                        .param("milieuPretAAccueillirNombreStagiaires", milieuPretAAccueillirNombreStagiaires)
                        .param("milieuDesireAccueillirMemeStagiaire", milieuDesireAccueillirMemeStagiaire)
                        .param("millieuOffreQuartsTravailVariables", millieuOffreQuartsTravailVariables)
                        .param("quartTravailDebut1", quartTravailDebut1)
                        .param("quartTravailFin1", quartTravailFin1)
                        .param("quartTravailDebut2", quartTravailDebut2)
                        .param("quartTravailFin2", quartTravailFin2)
                        .param("quartTravailDebut3", quartTravailDebut3)
                        .param("quartTravailFin3", quartTravailFin3)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk());

        // Verify that the service method was called once with the correct parameters
        Mockito.verify(enseignantService, Mockito.times(1)).enregistrerFicheEvaluationMilieuStage(any(FicheEvaluationMilieuStageDTO.class), eq(1L));
    }

    @Test
    public void testEnregistrerFicheEvaluationStagiaire_ServiceException() throws Exception {
        // Arrange: Define all necessary form parameters
        String nomEntreprise = "Entreprise Exemple";
        String nomPersonneContact = "Jean Dupont";
        String adresseEntreprise = "123 Rue Principale";
        String villeEntreprise = "Paris";
        String codePostalEntreprise = "75001";
        String telephoneEntreprise = "0123456789";
        String telecopieurEntreprise = "0987654321";
        String nomStagiaire = "Marie Curie";
        String dateDebutStage = "2024-05-01T09:00:00"; // ISO format
        String numeroStage = "1";
        String evalQA = "TOTALEMENT_EN_ACCORD"; // Assuming enum values as strings
        String evalQB = "PLUTOT_EN_ACCORD";
        String evalQC = "PLUTOT_EN_DESACCORD";
        String nombreHeuresParSemainePremierMois = "35.0";
        String nombreHeuresParSemaineDeuxiemeMois = "35.0";
        String nombreHeuresParSemaineTroisiemeMois = "35.0";
        String evalQD = "TOTALEMENT_EN_ACCORD";
        String evalQE = "PLUTOT_EN_ACCORD";
        String evalQF = "PLUTOT_EN_DESACCORD";
        String evalQG = "IMPOSSIBLE_DE_SE_PRONONCER";
        String salaireHoraire = "15.5";
        String evalQH = "TOTALEMENT_EN_ACCORD";
        String evalQI = "PLUTOT_EN_ACCORD";
        String evalQJ = "PLUTOT_EN_DESACCORD";
        String commentaires = "Bon stage.";
        String milieuAPrivilegier = "PREMIER_STAGE";
        String milieuPretAAccueillirNombreStagiaires = "DEUX";
        String milieuDesireAccueillirMemeStagiaire = "OUI";
        String millieuOffreQuartsTravailVariables = "NON";
        String quartTravailDebut1 = "2024-05-01T09:00:00";
        String quartTravailFin1 = "2024-05-01T17:00:00";
        String quartTravailDebut2 = "2024-05-02T09:00:00";
        String quartTravailFin2 = "2024-05-02T17:00:00";
        String quartTravailDebut3 = "2024-05-03T09:00:00";
        String quartTravailFin3 = "2024-05-03T17:00:00";

        // Mock the service method to throw a RuntimeException
        Mockito.doThrow(new RuntimeException("Service error"))
                .when(enseignantService)
                .enregistrerFicheEvaluationMilieuStage(any(FicheEvaluationMilieuStageDTO.class), eq(1L));

        // Act & Assert: Perform POST request with form data and expect 500 Internal Server Error
        mockMvc.perform(post("/enseignant/saveFicheEvaluationMilieuStage")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("etudiantId", "1")
                        .param("nomEntreprise", nomEntreprise)
                        .param("nomPersonneContact", nomPersonneContact)
                        .param("adresseEntreprise", adresseEntreprise)
                        .param("villeEntreprise", villeEntreprise)
                        .param("codePostalEntreprise", codePostalEntreprise)
                        .param("telephoneEntreprise", telephoneEntreprise)
                        .param("telecopieurEntreprise", telecopieurEntreprise)
                        .param("nomStagiaire", nomStagiaire)
                        .param("dateDebutStage", dateDebutStage)
                        .param("numeroStage", numeroStage)
                        .param("evalQA", evalQA)
                        .param("evalQB", evalQB)
                        .param("evalQC", evalQC)
                        .param("nombreHeuresParSemainePremierMois", nombreHeuresParSemainePremierMois)
                        .param("nombreHeuresParSemaineDeuxiemeMois", nombreHeuresParSemaineDeuxiemeMois)
                        .param("nombreHeuresParSemaineTroisiemeMois", nombreHeuresParSemaineTroisiemeMois)
                        .param("evalQD", evalQD)
                        .param("evalQE", evalQE)
                        .param("evalQF", evalQF)
                        .param("evalQG", evalQG)
                        .param("salaireHoraire", salaireHoraire)
                        .param("evalQH", evalQH)
                        .param("evalQI", evalQI)
                        .param("evalQJ", evalQJ)
                        .param("commentaires", commentaires)
                        .param("milieuAPrivilegier", milieuAPrivilegier)
                        .param("milieuPretAAccueillirNombreStagiaires", milieuPretAAccueillirNombreStagiaires)
                        .param("milieuDesireAccueillirMemeStagiaire", milieuDesireAccueillirMemeStagiaire)
                        .param("millieuOffreQuartsTravailVariables", millieuOffreQuartsTravailVariables)
                        .param("quartTravailDebut1", quartTravailDebut1)
                        .param("quartTravailFin1", quartTravailFin1)
                        .param("quartTravailDebut2", quartTravailDebut2)
                        .param("quartTravailFin2", quartTravailFin2)
                        .param("quartTravailDebut3", quartTravailDebut3)
                        .param("quartTravailFin3", quartTravailFin3)
                        .with(SecurityMockMvcRequestPostProcessors.user("user").password("password").roles("USER"))
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Service error"))
                .andExpect(jsonPath("$.status").value(500));
    }
}
