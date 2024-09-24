package com.projet.mycose.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projet.mycose.service.EtudiantService;
import com.projet.mycose.service.UtilisateurService;
import com.projet.mycose.service.dto.LoginDTO;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

}
