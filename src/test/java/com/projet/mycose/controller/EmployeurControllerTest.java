package com.projet.mycose.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projet.mycose.modele.auth.Role;
import com.projet.mycose.service.EmployeurService;
import com.projet.mycose.service.dto.*;
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
public class EmployeurControllerTest {
    private MockMvc mockMvc;
    @Mock
    private EmployeurService employeurService;
    @InjectMocks
    private EmployeurController employeurController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(employeurController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    public void testCreationDeCompte_Succes() throws Exception {
        RegisterEmployeurDTO newEmployeur = new RegisterEmployeurDTO(
                "Karim", "Mihoubi", "438-532-2729", "mihoubi@gmail.com", "Mimi123$", "Couche-Tard"
        );

        when(employeurService.creationDeCompte(any(), any(), any(), any(), any(), any()))
                .thenReturn(new EmployeurDTO(1L, "Karim", "Mihoubi", "mihoubi@gmail.com", "438-532-2729", "Couche-Tard", Role.EMPLOYEUR));

        ObjectMapper objectMapper = new ObjectMapper();
        String employeurJson = objectMapper.writeValueAsString(newEmployeur);

        this.mockMvc.perform(post("/entreprise/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(employeurJson)
                        .with(csrf())
                        .with(user("karim").password("Mimi123$").roles("EMPLOYEUR")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nom").value("Mihoubi"))
                .andExpect(jsonPath("$.prenom").value("Karim"))
                .andExpect(jsonPath("$.courriel").value("mihoubi@gmail.com"))
                .andExpect(jsonPath("$.numeroDeTelephone").value("438-532-2729"))
                .andExpect(jsonPath("$.role").value("EMPLOYEUR"));
    }

    @Test
    public void testCreationDeCompte_EchecAvecConflit() throws Exception {
        RegisterEmployeurDTO newEmployeur = new RegisterEmployeurDTO(
                "Karim", "Mihoubi", "438-532-2729", "mihoubi@gmail.com", "Mimi123$", "Couche-Tard"
        );

        ObjectMapper objectMapper = new ObjectMapper();
        String employeurJson = objectMapper.writeValueAsString(newEmployeur);

        this.mockMvc.perform(post("/entreprise/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(employeurJson)
                        .with(csrf())
                        .with(user("michel").password("Mimi123$").roles("EMPLOYEUR")))
                .andExpect(status().isConflict());
    }

    @Test
    public void testCheckForConflict_Echec() throws Exception {
        CourrielTelephoneDTO courrielTelephoneDTO = new CourrielTelephoneDTO("mihoubi@gmail.com", "438-639-2638");

        when(employeurService.credentialsDejaPris("mihoubi@gmail.com", "438-639-2638")).thenReturn(true);

        ObjectMapper objectMapper = new ObjectMapper();
        String courrielTelephoneString = objectMapper.writeValueAsString(courrielTelephoneDTO);

        this.mockMvc.perform(post("/entreprise/register/check-for-conflict")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(courrielTelephoneString)
                        .with(csrf())
                        .with(user("karim").password("Mimi123$").roles("EMPLOYEUR")))
                .andExpect(status().isConflict());
    }

    @Test
    public void testCheckForConflict_Succes() throws Exception {
        CourrielTelephoneDTO courrielTelephoneDTO = new CourrielTelephoneDTO("mihoubi@gmail.com", "438-639-2638");

        ObjectMapper objectMapper = new ObjectMapper();
        String courrielTelephoneString = objectMapper.writeValueAsString(courrielTelephoneDTO);

        this.mockMvc.perform(post("/entreprise/register/check-for-conflict")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(courrielTelephoneString)
                        .with(csrf())
                        .with(user("karim").password("Mimi123$").roles("EMPLOYEUR")))
                .andExpect(status().isOk());
    }
}
