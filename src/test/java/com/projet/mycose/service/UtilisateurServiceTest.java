package com.projet.mycose.service;

import com.projet.mycose.modele.Etudiant;
import com.projet.mycose.repository.EtudiantRepository;
import com.projet.mycose.repository.UtilisateurRepository;
import com.projet.mycose.security.JwtTokenProvider;
import com.projet.mycose.security.exception.UserNotFoundException;
import com.projet.mycose.service.dto.EtudiantDTO;
import com.projet.mycose.service.dto.LoginDTO;
import com.projet.mycose.service.dto.UtilisateurDTO;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.nio.file.AccessDeniedException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UtilisateurServiceTest {

    @Test
    public void testAuthentifierUtilisateur_Success() {
        // Arrange
        UtilisateurRepository utilisateurRepository = mock(UtilisateurRepository.class);
        EtudiantRepository etudiantRepository = mock(EtudiantRepository.class);
        AuthenticationManager authenticationManagerMock = mock(AuthenticationManager.class);
        Authentication authenticationMock = mock(Authentication.class);
        JwtTokenProvider jwtTokenProvider = mock(JwtTokenProvider.class);

        UtilisateurService utilisateurService = new UtilisateurService(utilisateurRepository, etudiantRepository, authenticationManagerMock, jwtTokenProvider);

        LoginDTO loginDTO = new LoginDTO("mihoubi@gmail.com", "065f45b37b5dad20b5a67158468161cdf35b1ae3cda2a666e7852feab7424897");

        when(authenticationManagerMock.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authenticationMock);
        when(jwtTokenProvider.generateToken(authenticationMock)).thenReturn("un-token-mocked");

        // Act
        String token = utilisateurService.authentificationUtilisateur(loginDTO);

        // Assert
        assertEquals("un-token-mocked", token);
        verify(authenticationManagerMock).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtTokenProvider).generateToken(authenticationMock);
    }

    @Test
    void testAuthentificationEtudiant_Failure_BadCredentials() {
        // Arrange
        UtilisateurRepository utilisateurRepository = mock(UtilisateurRepository.class);
        EtudiantRepository etudiantRepository = mock(EtudiantRepository.class);
        AuthenticationManager authenticationManagerMock = mock(AuthenticationManager.class);
        JwtTokenProvider jwtTokenProvider = mock(JwtTokenProvider.class);

        UtilisateurService utilisateurService = new UtilisateurService(utilisateurRepository, etudiantRepository, authenticationManagerMock, jwtTokenProvider);

        LoginDTO loginDTO = new LoginDTO("etudiant@example.com", "wrongPassword");

        when(authenticationManagerMock.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        // Act & Assert
        assertThrows(BadCredentialsException.class, () -> {
            utilisateurService.authentificationUtilisateur(loginDTO);
        });

        verify(authenticationManagerMock).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtTokenProvider, never()).generateToken(any());
    }

    @Test
    void testAuthentificationEtudiant_Failure_TokenGeneration_JWTError() {
        // Arrange
        UtilisateurRepository utilisateurRepository = mock(UtilisateurRepository.class);
        EtudiantRepository etudiantRepository = mock(EtudiantRepository.class);
        AuthenticationManager authenticationManagerMock = mock(AuthenticationManager.class);
        JwtTokenProvider jwtTokenProvider = mock(JwtTokenProvider.class);

        UtilisateurService utilisateurService = new UtilisateurService(utilisateurRepository, etudiantRepository, authenticationManagerMock, jwtTokenProvider);
        LoginDTO loginDTO = new LoginDTO("etudiant@example.com", "password");

        Authentication authenticationMock = mock(Authentication.class);
        when(authenticationManagerMock.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authenticationMock);

        when(jwtTokenProvider.generateToken(authenticationMock))
                .thenThrow(new JwtException("La génération de token a échouée"));

        // Act & Assert
        assertThrows(JwtException.class, () -> {
            utilisateurService.authentificationUtilisateur(loginDTO);
        });

        verify(authenticationManagerMock).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtTokenProvider).generateToken(authenticationMock);
    }

    @Test
    void testAuthentificationEtudiant_Failure_TokenGeneration_AuthNull() {
        // Arrange
        UtilisateurRepository utilisateurRepository = mock(UtilisateurRepository.class);
        EtudiantRepository etudiantRepository = mock(EtudiantRepository.class);
        AuthenticationManager authenticationManagerMock = mock(AuthenticationManager.class);
        JwtTokenProvider jwtTokenProvider = mock(JwtTokenProvider.class);

        UtilisateurService utilisateurService = new UtilisateurService(utilisateurRepository, etudiantRepository, authenticationManagerMock, jwtTokenProvider);

        // Act & Assert
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            utilisateurService.authentificationUtilisateur(new LoginDTO(null, "password"));
        });

        assertEquals("Le courriel et le mot de passe ne doivent pas être null", thrown.getMessage());

        verify(authenticationManagerMock, never()).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtTokenProvider, never()).generateToken(any(Authentication.class));
    }


    @Test
    void testAuthentificationEtudiant_Failure_NullFields() {
        // Arrange
        UtilisateurRepository utilisateurRepository = mock(UtilisateurRepository.class);
        EtudiantRepository etudiantRepository = mock(EtudiantRepository.class);
        AuthenticationManager authenticationManagerMock = mock(AuthenticationManager.class);
        JwtTokenProvider jwtTokenProvider = mock(JwtTokenProvider.class);

        UtilisateurService utilisateurService = new UtilisateurService(utilisateurRepository, etudiantRepository, authenticationManagerMock, jwtTokenProvider);

        LoginDTO loginDTOWithNullEmail = new LoginDTO(null, "password");
        LoginDTO loginDTOWithNullPassword = new LoginDTO("etudiant@example.com", null);


        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            utilisateurService.authentificationUtilisateur(loginDTOWithNullEmail);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            utilisateurService.authentificationUtilisateur(loginDTOWithNullPassword);
        });
        verify(authenticationManagerMock, never()).authenticate(any());
        verify(jwtTokenProvider, never()).generateToken(any());
    }

    @Test
    public void testGetMe_SuccesEtudiant() throws Exception {
        // Arrange
        UtilisateurRepository utilisateurRepository = mock(UtilisateurRepository.class);
        EtudiantRepository etudiantRepository = mock(EtudiantRepository.class);
        AuthenticationManager authenticationManagerMock = mock(AuthenticationManager.class);
        JwtTokenProvider jwtTokenProvider = mock(JwtTokenProvider.class);
        UtilisateurService utilisateurService = new UtilisateurService(utilisateurRepository, etudiantRepository, authenticationManagerMock, jwtTokenProvider);
        EtudiantService etudiantService = new EtudiantService(etudiantRepository);

        String token = "Bearer valid_token";
        String email = "mihoubi@gmail.com";
        Etudiant etudiant = new Etudiant(1L, "Karim", "Mihoubi", "438-532-2729", "mihoubi@gmail.com", "$2y$10$iXgJopQP9JaxKujH2nOgn.S8BCNEdhKQwRcC/7DxDRu3G6SMShC3G");

        when(etudiantRepository.save(any(Etudiant.class))).thenReturn(etudiant);
        when(jwtTokenProvider.getEmailFromJWT(anyString())).thenReturn(email);
        when(utilisateurRepository.findUtilisateurByCourriel(anyString())).thenReturn(Optional.of(etudiant));
        when(etudiantRepository.findById(anyLong())).thenReturn(Optional.of(etudiant));

        // Act
        etudiantService.creationDeCompte("Karim", "Mihoubi", "438-532-2729", "mihoubi@gmail.com", "$2y$10$iXgJopQP9JaxKujH2nOgn.S8BCNEdhKQwRcC/7DxDRu3G6SMShC3G");
        UtilisateurDTO result = utilisateurService.getMe(token);

        // Assert
        assertNotNull(result);
        verify(jwtTokenProvider).getEmailFromJWT("valid_token");
        verify(utilisateurRepository).findUtilisateurByCourriel(email);
    }

    @Test
    public void testGetMe_TokenNull() {
        // Arrange
        UtilisateurRepository utilisateurRepository = mock(UtilisateurRepository.class);
        EtudiantRepository etudiantRepository = mock(EtudiantRepository.class);
        AuthenticationManager authenticationManagerMock = mock(AuthenticationManager.class);
        JwtTokenProvider jwtTokenProvider = mock(JwtTokenProvider.class);
        UtilisateurService utilisateurService = new UtilisateurService(utilisateurRepository, etudiantRepository, authenticationManagerMock, jwtTokenProvider);
        String token = null;

        // Act & Assert
        assertThrows(AccessDeniedException.class, () -> {
            utilisateurService.getMe(token);
        });
    }

    @Test
    public void testGetMe_TokenSansBearer() {
        // Arrange
        UtilisateurRepository utilisateurRepository = mock(UtilisateurRepository.class);
        EtudiantRepository etudiantRepository = mock(EtudiantRepository.class);
        AuthenticationManager authenticationManagerMock = mock(AuthenticationManager.class);
        JwtTokenProvider jwtTokenProvider = mock(JwtTokenProvider.class);
        UtilisateurService utilisateurService = new UtilisateurService(utilisateurRepository, etudiantRepository, authenticationManagerMock, jwtTokenProvider);
        String token = "invalid_token";

        // Act & Assert
        assertThrows(AccessDeniedException.class, () -> {
            utilisateurService.getMe(token);
        });
    }

    @Test
    public void testGetMe_UtilisateurNonTrouve() throws AccessDeniedException {
        // Arrange
        UtilisateurRepository utilisateurRepository = mock(UtilisateurRepository.class);
        EtudiantRepository etudiantRepository = mock(EtudiantRepository.class);
        AuthenticationManager authenticationManagerMock = mock(AuthenticationManager.class);
        JwtTokenProvider jwtTokenProvider = mock(JwtTokenProvider.class);
        UtilisateurService utilisateurService = new UtilisateurService(utilisateurRepository, etudiantRepository, authenticationManagerMock, jwtTokenProvider);
        String token = "Bearer valid_token";
        String email = "inconnu@exemple.com";

        when(jwtTokenProvider.getEmailFromJWT(anyString())).thenReturn(email);
        when(utilisateurRepository.findUtilisateurByCourriel(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> {
            utilisateurService.getMe(token);
        });
    }

    @Test
    public void testGetMe_TokenInvalide() throws AccessDeniedException {
        // Arrange
        UtilisateurRepository utilisateurRepository = mock(UtilisateurRepository.class);
        EtudiantRepository etudiantRepository = mock(EtudiantRepository.class);
        AuthenticationManager authenticationManagerMock = mock(AuthenticationManager.class);
        JwtTokenProvider jwtTokenProvider = mock(JwtTokenProvider.class);
        UtilisateurService utilisateurService = new UtilisateurService(utilisateurRepository, etudiantRepository, authenticationManagerMock, jwtTokenProvider);
        String token = "Bearer invalid_token";

        when(jwtTokenProvider.getEmailFromJWT(anyString())).thenThrow(new AccessDeniedException("Token Invalide"));

        // Act & Assert
        assertThrows(AccessDeniedException.class, () -> {
            utilisateurService.getMe(token);
        });
    }
}
