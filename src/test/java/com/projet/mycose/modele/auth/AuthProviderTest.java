package com.projet.mycose.modele.auth;

import com.projet.mycose.modele.Utilisateur;
import com.projet.mycose.repository.UtilisateurRepository;
import com.projet.mycose.security.AuthProvider;
import com.projet.mycose.exceptions.AuthenticationException;
import com.projet.mycose.exceptions.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat; // Optional for AssertJ
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class AuthProviderTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UtilisateurRepository utilisateurRepository;

    @InjectMocks
    private AuthProvider authProvider;

    private Utilisateur utilisateur;

    @BeforeEach
    void setUp() {
        // Initialize a mock Utilisateur instance
        utilisateur = mock(Utilisateur.class);
        when(utilisateur.getCourriel()).thenReturn("john.doe@example.com");
        when(utilisateur.getMotDePasse()).thenReturn("encodedPassword");
        when(utilisateur.getAuthorities()).thenReturn(Collections.emptyList());
    }

    /**
     * Test for authenticate(Authentication authentication) - Success Scenario
     * Ensures that the method returns a valid Authentication token when credentials are correct.
     */
    @Test
    @DisplayName("authenticate() should return a valid Authentication token when credentials are correct")
    void testAuthenticate_Success() {
        // Arrange
        String email = "john.doe@example.com";
        String rawPassword = "password123";
        String encodedPassword = "encodedPassword123";

        // Mock Authentication object
        Authentication authentication = new UsernamePasswordAuthenticationToken(email, rawPassword);

        // Configure mocks
        when(utilisateurRepository.findUtilisateurByCourriel(email)).thenReturn(Optional.of(utilisateur));
        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(true);
        when(utilisateur.getMotDePasse()).thenReturn(encodedPassword);

        // Act
        Authentication result = authProvider.authenticate(authentication);

        // Assert
        assertNotNull(result, "The returned Authentication should not be null");
        assertTrue(result.isAuthenticated(), "The Authentication should be marked as authenticated");
        assertEquals(email, result.getPrincipal(), "The principal should be the user's email");
        assertEquals(encodedPassword, result.getCredentials(), "The credentials should be the user's encoded password");
        assertEquals(utilisateur.getAuthorities(), result.getAuthorities(), "The authorities should match the user's authorities");

        // Verify interactions
        verify(utilisateurRepository, times(1)).findUtilisateurByCourriel(email);
        verify(passwordEncoder, times(1)).matches(rawPassword, encodedPassword);
    }

    /**
     * Test for authenticate(Authentication authentication) - Failure Scenario: User Not Found
     * Ensures that the method throws UserNotFoundException when the user does not exist.
     */
    @Test
    @DisplayName("authenticate() should throw UserNotFoundException when user is not found")
    void testAuthenticate_UserNotFound() {
        // Arrange
        String email = "non.existent@example.com";
        String rawPassword = "password123";

        // Mock Authentication object
        Authentication authentication = new UsernamePasswordAuthenticationToken(email, rawPassword);

        // Configure mocks
        when(utilisateurRepository.findUtilisateurByCourriel(email)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> {
            authProvider.authenticate(authentication);
        }, "Expected authenticate() to throw UserNotFoundException, but it didn't");

        // Verify interactions
        verify(utilisateurRepository, times(1)).findUtilisateurByCourriel(email);
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    /**
     * Test for authenticate(Authentication authentication) - Failure Scenario: Password Mismatch
     * Ensures that the method throws AuthenticationException when the password does not match.
     */
    @Test
    @DisplayName("authenticate() should throw AuthenticationException when password does not match")
    void testAuthenticate_PasswordMismatch() {
        // Arrange
        String email = "john.doe@example.com";
        String rawPassword = "wrongPassword";
        String encodedPassword = "encodedPassword123";

        // Mock Authentication object
        Authentication authentication = new UsernamePasswordAuthenticationToken(email, rawPassword);

        // Configure mocks
        when(utilisateurRepository.findUtilisateurByCourriel(email)).thenReturn(Optional.of(utilisateur));
        when(utilisateur.getMotDePasse()).thenReturn(encodedPassword);
        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(false);

        // Act & Assert
        AuthenticationException exception = assertThrows(AuthenticationException.class, () -> {
            authProvider.authenticate(authentication);
        }, "Expected authenticate() to throw AuthenticationException, but it didn't");

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus(), "Exception status should be FORBIDDEN");
        assertEquals("Incorrect username or password", exception.getMessage(), "Exception message should match");

        // Verify interactions
        verify(utilisateurRepository, times(1)).findUtilisateurByCourriel(email);
        verify(passwordEncoder, times(1)).matches(rawPassword, encodedPassword);
    }

    /**
     * Test for supports(Class<?> authentication) - Success Scenario
     * Ensures that the method returns true for UsernamePasswordAuthenticationToken.
     */
    @Test
    @DisplayName("supports() should return true for UsernamePasswordAuthenticationToken")
    void testSupports_UsernamePasswordAuthenticationToken() {
        // Arrange
        Class<?> authenticationClass = UsernamePasswordAuthenticationToken.class;

        // Act
        boolean result = authProvider.supports(authenticationClass);

        // Assert
        assertTrue(result, "supports() should return true for UsernamePasswordAuthenticationToken");
    }

    /**
     * Test for supports(Class<?> authentication) - Failure Scenario
     * Ensures that the method returns false for unsupported Authentication types.
     */
    @Test
    @DisplayName("supports() should return false for unsupported Authentication types")
    void testSupports_UnsupportedAuthentication() {
        // Arrange
        Class<?> authenticationClass = Authentication.class; // Generic Authentication type

        // Act
        boolean result = authProvider.supports(authenticationClass);

        // Assert
        assertFalse(result, "supports() should return false for unsupported Authentication types");
    }
}
