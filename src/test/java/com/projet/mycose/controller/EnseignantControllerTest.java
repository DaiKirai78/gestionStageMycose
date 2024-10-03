package com.projet.mycose.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projet.mycose.modele.auth.Role;
import com.projet.mycose.service.EnseignantService;
import com.projet.mycose.service.UtilisateurService;
import com.projet.mycose.service.dto.CourrielTelephoneDTO;
import com.projet.mycose.service.dto.EnseignantDTO;
import com.projet.mycose.service.dto.RegisterEnseignantDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class EnseignantControllerTest {
    private MockMvc mockMvc;
    @Mock
    private EnseignantService enseignantService;

    @Mock
    private UtilisateurService utilisateurService;

    @InjectMocks
    private EnseignantController enseignantController;

    @BeforeEach
    void setUp() {
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
}
