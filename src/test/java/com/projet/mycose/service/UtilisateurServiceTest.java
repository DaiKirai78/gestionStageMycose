package com.projet.mycose.service;

import com.projet.mycose.repository.EtudiantRepository;
import com.projet.mycose.repository.UtilisateurRepository;
import com.projet.mycose.security.JwtTokenProvider;
import com.projet.mycose.service.dto.LoginDTO;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
}
