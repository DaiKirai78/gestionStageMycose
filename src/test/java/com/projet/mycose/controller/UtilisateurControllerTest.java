package com.projet.mycose.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projet.mycose.modele.auth.Role;
import com.projet.mycose.service.EtudiantService;
import com.projet.mycose.service.UtilisateurService;
import com.projet.mycose.service.dto.EtudiantDTO;
import com.projet.mycose.service.dto.LoginDTO;
import com.projet.mycose.service.dto.UtilisateurDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UtilisateurController.class)
public class UtilisateurControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EtudiantService etudiantService;

    @MockBean
    private UtilisateurService utilisateurService;

    @Test
    public void testAuthentifierUtilisateur_Succes() throws Exception {
        LoginDTO loginDTO = new LoginDTO("mihoubi@gmail.com", "$2a$10$e0NRkvT7RRr3z8hDVoPYPOz1VsKUPF9EJb/Mc8SOP68GQkecCnIvO");

        String token = "Bearer valid_token";

        when(utilisateurService.authentificationUtilisateur(any(LoginDTO.class))).thenReturn(token);

        ObjectMapper objectMapper = new ObjectMapper();
        String loginDTOJson = objectMapper.writeValueAsString(loginDTO);

        this.mockMvc.perform(post("/utilisateur/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginDTOJson)
                        .with(csrf())
                        .with(user("karim").password("$2a$10$e0NRkvT7RRr3z8hDVoPYPOz1VsKUPF9EJb/Mc8SOP68GQkecCnIvO").roles("ETUDIANT")))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.accessToken").value(token))
                .andExpect(jsonPath("$.tokenType").value("BEARER"));
    }

    @Test
    public void testAuthentifierUtilisateur_Unauthorized_EmailNull() throws Exception {
        LoginDTO loginDTO = new LoginDTO(null, "$2a$10$e0NRkvT7RRr3z8hDVoPYPOz1VsKUPF9EJb/Mc8SOP68GQkecCnIvO");

        when(utilisateurService.authentificationUtilisateur(any(LoginDTO.class)))
                .thenThrow(new IllegalArgumentException("Le courriel de l'utilisateur est invalide"));

        String loginDTOJson = new ObjectMapper().writeValueAsString(loginDTO);

        mockMvc.perform(post("/utilisateur/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginDTOJson)
                        .with(csrf())
                        .with(user("karim").password("$2a$10$e0NRkvT7RRr3z8hDVoPYPOz1VsKUPF9EJb/Mc8SOP68GQkecCnIvO").roles("ETUDIANT")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testAuthentifierUtilisateur_Unauthorized_EmailInvalide() throws Exception {
        LoginDTO loginDTO = new LoginDTO("email_invalide", "$2a$10$e0NRkvT7RRr3z8hDVoPYPOz1VsKUPF9EJb/Mc8SOP68GQkecCnIvO");

        when(utilisateurService.authentificationUtilisateur(any(LoginDTO.class)))
                .thenThrow(new IllegalArgumentException("Le courriel de l'utilisateur est invalide"));

        String loginDTOJson = new ObjectMapper().writeValueAsString(loginDTO);

        mockMvc.perform(post("/utilisateur/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginDTOJson)
                        .with(csrf())
                        .with(user("karim").password("$2a$10$e0NRkvT7RRr3z8hDVoPYPOz1VsKUPF9EJb/Mc8SOP68GQkecCnIvO").roles("ETUDIANT")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testAuthentifierUtilisateur_Unauthorized_MDPNull() throws Exception {
        LoginDTO loginDTO = new LoginDTO("mihoubi@gmail.com", null);

        when(utilisateurService.authentificationUtilisateur(any(LoginDTO.class)))
                .thenThrow(new IllegalArgumentException("Le mot de passe de l'utilisateur est invalide"));

        String loginDTOJson = new ObjectMapper().writeValueAsString(loginDTO);

        mockMvc.perform(post("/utilisateur/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginDTOJson)
                        .with(csrf())
                        .with(user("karim").roles("ETUDIANT")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testAuthentifierUtilisateur_Unauthorized_MDPInvalide() throws Exception {
        LoginDTO loginDTO = new LoginDTO("mihoubi@gmail.com", "123abc");

        when(utilisateurService.authentificationUtilisateur(any(LoginDTO.class)))
                .thenThrow(new IllegalArgumentException("Le mot de passe de l'utilisateur est invalide"));

        String loginDTOJson = new ObjectMapper().writeValueAsString(loginDTO);

        mockMvc.perform(post("/utilisateur/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginDTOJson)
                        .with(csrf())
                        .with(user("karim").roles("ETUDIANT")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testGetMe_Success() throws Exception {
        String token = "Bearer valid_token";
        EtudiantDTO utilisateurDTO = new EtudiantDTO(1L, "Karim", "Mihoubi", "mihoubi@gmail.com", "438-508-2345", Role.ETUDIANT);

        when(utilisateurService.getMe(anyString())).thenReturn(utilisateurDTO);

        mockMvc.perform(post("/utilisateur/me")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(user("karim").roles("ETUDIANT")))
                .andExpect(status().isAccepted())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.prenom").value("Karim"))
                .andExpect(jsonPath("$.nom").value("Mihoubi"))
                .andExpect(jsonPath("$.courriel").value("mihoubi@gmail.com"));

        verify(utilisateurService).getMe("Bearer valid_token");
    }

    @Test
    public void testGetMe_Failure() throws Exception {
        String token = "Bearer invalid_token";

        when(utilisateurService.getMe(anyString())).thenThrow(new RuntimeException("Erreur d'authentification"));

        mockMvc.perform(post("/utilisateur/me")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                .with(csrf())
                .with(user("karim").roles("ETUDIANT")))
                .andExpect(status().isInternalServerError());

        verify(utilisateurService).getMe("Bearer invalid_token");
    }

}
