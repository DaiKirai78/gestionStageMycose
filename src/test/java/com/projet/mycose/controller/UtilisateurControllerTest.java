package com.projet.mycose.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projet.mycose.modele.auth.Role;
import com.projet.mycose.service.EtudiantService;
import com.projet.mycose.service.UtilisateurService;
import com.projet.mycose.service.dto.CourrielTelephoneDTO;
import com.projet.mycose.service.dto.EtudiantDTO;
import com.projet.mycose.service.dto.LoginDTO;
import com.projet.mycose.service.dto.UtilisateurDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class UtilisateurControllerTest {

    private MockMvc mockMvc;

    @Mock
    private EtudiantService etudiantService;

    @Mock
    private UtilisateurService utilisateurService;

    @InjectMocks
    private UtilisateurController utilisateurController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(utilisateurController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    public void testAuthentifierUtilisateur_Succes() throws Exception {
        LoginDTO loginDTO = new LoginDTO("mihoubi@gmail.com", "Mihoubi123$");

        String token = "Bearer valid_token";

        when(utilisateurService.authentificationUtilisateur(any(LoginDTO.class))).thenReturn(token);

        ObjectMapper objectMapper = new ObjectMapper();
        String loginDTOJson = objectMapper.writeValueAsString(loginDTO);

        this.mockMvc.perform(post("/utilisateur/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginDTOJson)
                        .with(csrf())
                        .with(user("karim").password("Mihoubi123$").roles("ETUDIANT")))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.accessToken").value(token))
                .andExpect(jsonPath("$.tokenType").value("BEARER"));
    }

    @Test
    public void testAuthentifierUtilisateur_IsUnauthorized() throws Exception {
        LoginDTO loginDTO = new LoginDTO("example@gmail.com", "Mihoubi123$");

        when(utilisateurService.authentificationUtilisateur(any(LoginDTO.class)))
                .thenThrow(new IllegalArgumentException());

        String loginDTOJson = new ObjectMapper().writeValueAsString(loginDTO);

        mockMvc.perform(post("/utilisateur/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginDTOJson)
                        .with(csrf())
                        .with(user("karim").password("Mihoubi123$").roles("ETUDIANT")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testAuthentifierUtilisateur_BadRequest_EmailInvalide() throws Exception {
        LoginDTO loginDTO = new LoginDTO("email_invalide", "Mihoubi123$");

        String loginDTOJson = new ObjectMapper().writeValueAsString(loginDTO);

        mockMvc.perform(post("/utilisateur/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginDTOJson)
                        .with(csrf())
                        .with(user("karim").password("Mihoubi123$").roles("ETUDIANT")))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testAuthentifierUtilisateur_BadRequest_MDPNull() throws Exception {
        LoginDTO loginDTO = new LoginDTO("mihoubi@gmail.com", null);

        String loginDTOJson = new ObjectMapper().writeValueAsString(loginDTO);

        mockMvc.perform(post("/utilisateur/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginDTOJson)
                        .with(csrf())
                        .with(user("karim").roles("ETUDIANT")))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testAuthentifierUtilisateur_BadRequest_MDPInvalide() throws Exception {
        LoginDTO loginDTO = new LoginDTO("mihoubi@gmail.com", "123abc");

        String loginDTOJson = new ObjectMapper().writeValueAsString(loginDTO);

        mockMvc.perform(post("/utilisateur/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginDTOJson)
                        .with(csrf())
                        .with(user("karim").roles("ETUDIANT")))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetMe_Success() throws Exception {
        String token = "Bearer valid_token";
        EtudiantDTO utilisateurDTO = new EtudiantDTO(1L, "Karim", "Mihoubi", "mihoubi@gmail.com", "438-508-2345", Role.ETUDIANT, "Technique de l'informatique");

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
                .andExpect(jsonPath("$.courriel").value("mihoubi@gmail.com"))
                .andExpect(jsonPath("$.numeroDeTelephone").value("438-508-2345"))
                .andExpect(jsonPath("$.role").value("ETUDIANT"))
                .andExpect(jsonPath("$.programme").value("Technique de l'informatique"));

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
                .andExpect(status().isBadRequest());

        verify(utilisateurService).getMe("Bearer invalid_token");
    }



    @Test
    public void testCheckForConflict_Echec() throws Exception {
        CourrielTelephoneDTO courrielTelephoneDTO = new CourrielTelephoneDTO("mihoubi@gmail.com", "438-639-2638");

        when(utilisateurService.credentialsDejaPris("mihoubi@gmail.com", "438-639-2638")).thenReturn(true);

        ObjectMapper objectMapper = new ObjectMapper();
        String courrielTelephoneString = objectMapper.writeValueAsString(courrielTelephoneDTO);

        this.mockMvc.perform(post("/utilisateur/register/check-for-conflict")
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

        this.mockMvc.perform(post("/utilisateur/register/check-for-conflict")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(courrielTelephoneString)
                        .with(csrf())
                        .with(user("karim").password("Mimi123$").roles("EMPLOYEUR")))
                .andExpect(status().isOk());
    }

}
