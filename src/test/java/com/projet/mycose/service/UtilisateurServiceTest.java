package com.projet.mycose.service;

import com.projet.mycose.repository.EtudiantRepository;
import com.projet.mycose.repository.UtilisateurRepository;
import com.projet.mycose.security.JwtTokenProvider;
import com.projet.mycose.service.dto.LoginDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

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
        String token = utilisateurService.authentificationEtudiant(loginDTO);

        // Assert
        Assertions.assertEquals("un-token-mocked", token);
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
            utilisateurService.authentificationEtudiant(loginDTO);
        });

        verify(authenticationManagerMock).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtTokenProvider, never()).generateToken(any());
    }
}
