package com.projet.mycose.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projet.mycose.modele.auth.Role;
import com.projet.mycose.service.EtudiantService;
import com.projet.mycose.service.dto.EtudiantDTO;
import com.projet.mycose.service.dto.RegisterEtudiantDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EtudiantController.class)
public class EtudiantControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private EtudiantService etudiantService;

    @Test
    public void testCreationDeCompte_Succes() throws Exception {
        RegisterEtudiantDTO newEtudiant = new RegisterEtudiantDTO(
                "Karim", "Mihoubi", "438-532-2729", "mihoubi@gmail.com", "Mimi827$"
        );

        when(etudiantService.creationDeCompte(any(), any(), any(), any(), any()))
                .thenReturn(new EtudiantDTO(1L, "Karim", "Mihoubi", "438-532-2729", "mihoubi@gmail.com", Role.ETUDIANT));

        ObjectMapper objectMapper = new ObjectMapper();
        String etudiantJson = objectMapper.writeValueAsString(newEtudiant);

        this.mockMvc.perform(post("/etudiant/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(etudiantJson)
                        .with(csrf())
                        .with(user("karim").password("Mimi827$").roles("ETUDIANT")))
                .andExpect(status().isCreated());
    }

    @Test
    public void testCreationDeCompte_EchecBadRequest() throws Exception {
        RegisterEtudiantDTO newEtudiant = new RegisterEtudiantDTO(
                "Michel", "Genereux", "437-930-2483", "mihoubi@gmail.com", "Mimi827$"
        );

        when(etudiantService.creationDeCompte(
                "Michel", "Genereux", "437-930-2483", "mihoubi@gmail.com", "Mimi827$"))
                .thenReturn(null);

        ObjectMapper objectMapper = new ObjectMapper();
        String etudiantJson = objectMapper.writeValueAsString(newEtudiant);

        this.mockMvc.perform(post("/etudiant/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(etudiantJson)
                        .with(csrf())
                        .with(user("michel").password("Mimi827$").roles("ETUDIANT")))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testCreationDeCompte_EchecInternalServerError() throws Exception {
        RegisterEtudiantDTO newEtudiant = new RegisterEtudiantDTO(
                "Michel", "Genereux", "437-930-2483", "mihoubi@gmail.com", "Mimi827$"
        );

        when(etudiantService.creationDeCompte(
                "Michel", "Genereux", "437-930-2483", "mihoubi@gmail.com", "Mimi827$"))
                .thenThrow(new RuntimeException("Erreur en provenance du serveur"));

        ObjectMapper objectMapper = new ObjectMapper();
        String etudiantJson = objectMapper.writeValueAsString(newEtudiant);

        this.mockMvc.perform(post("/etudiant/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(etudiantJson)
                        .with(csrf())
                        .with(user("michel").password("Mimi827$").roles("ETUDIANT")))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void testCreationDeCompte_EchecAvecConflit() throws Exception {
        when(etudiantService.credentialsDejaPris("mihoubi@gmail.com", "437-930-2483")).thenReturn(true);

        this.mockMvc.perform(get("/etudiant/register/check-for-conflict/mihoubi@gmail.com_437-930-2483")
                        .with(csrf())
                        .with(user("karim").password("Mimi123$").roles("ETUDIANT")))
                .andExpect(status().isConflict());
    }

    @Test
    public void testCheckForConflict_EchecInternalServerError() throws Exception {
        when(etudiantService.credentialsDejaPris("mihoubi@gmail.com", "437-930-2483"))
                .thenThrow(new RuntimeException("Erreur en provenance du serveur"));

        this.mockMvc.perform(get("/etudiant/register/check-for-conflict/mihoubi@gmail.com_437-930-2483")
                        .with(csrf())
                        .with(user("karim").password("Mimi123$").roles("ETUDIANT")))
                .andExpect(status().isInternalServerError());
    }
}
