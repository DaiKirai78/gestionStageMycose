package com.projet.mycose.security;

import com.projet.mycose.modele.GestionnaireStage;
import com.projet.mycose.modele.Utilisateur;
import com.projet.mycose.repository.UtilisateurRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtTokenProvider tokenProvider;

    @Mock
    private UtilisateurRepository utilisateurRepository;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private final String validToken = "valid.jwt.token";
    private final String userEmail = "eliescrummaster@gmail.com";

    private Utilisateur utilisateur;

    @BeforeEach
    void setUp() {
        utilisateur = new GestionnaireStage("Elie", "Boucher-Gendron", "4506992425", userEmail, "Passw0rd");
    }

    @AfterEach
    void tearDown() {
        // Clear the security context after each test to prevent side effects
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternal_WithValidToken_ShouldAuthenticateUser() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("Bearer " + validToken);
        doNothing().when(tokenProvider).validateToken(validToken);
        when(tokenProvider.getEmailFromJWT(validToken)).thenReturn(userEmail);
        when(utilisateurRepository.findUtilisateurByCourriel(userEmail)).thenReturn(Optional.of(utilisateur));
        when(request.getRequestURI()).thenReturn("/api/offres-stages");

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        // Verify that authentication is set in the security context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication, "Authentication should not be null");
        assertEquals(userEmail, authentication.getName(), "Authentication name should match user email");
        assertTrue(authentication.isAuthenticated(), "Authentication should be marked as authenticated");

        Set<GrantedAuthority> expectedAuthorities = new HashSet<>(utilisateur.getAuthorities());
        Set<GrantedAuthority> actualAuthorities = new HashSet<>(authentication.getAuthorities());

        assertEquals(expectedAuthorities, actualAuthorities, "Authorities should match");

        // Verify that the filter chain is proceeded
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void doFilterInternal_WithInvalidToken_ShouldNotAuthenticateUser() throws ServletException, IOException {
        // Arrange
        String invalidToken = "invalid.jwt.token";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + invalidToken);
        when(request.getRequestURI()).thenReturn("/api/offres-stages");
        doThrow(new RuntimeException("Invalid token")).when(tokenProvider).validateToken(invalidToken);


        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        // Authentication should not be set
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNull(authentication, "Authentication should be null for invalid token");

        // Verify that the filter chain is proceeded
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void doFilterInternal_WithNoToken_ShouldNotAuthenticateUser() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn(null);
        when(request.getRequestURI()).thenReturn("/api/offres-stages");

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        // Authentication should not be set
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNull(authentication, "Authentication should be null when no token is provided");

        // Verify that the filter chain is proceeded
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void doFilterInternal_WithUserNotFound_ShouldLogErrorAndNotAuthenticate() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("Bearer " + validToken);
        doNothing().when(tokenProvider).validateToken(validToken);
        when(tokenProvider.getEmailFromJWT(validToken)).thenReturn(userEmail);
        when(utilisateurRepository.findUtilisateurByCourriel(userEmail)).thenReturn(Optional.empty());
        when(request.getRequestURI()).thenReturn("/api/offres-stages");

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        // Authentication should not be set
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNull(authentication, "Authentication should be null when user is not found");

        // Verify that the filter chain is proceeded
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void getJWTFromRequest_WithBearerToken_ShouldReturnToken() {
        // Arrange
        String bearerToken = "Bearer " + validToken;
        when(request.getHeader("Authorization")).thenReturn(bearerToken);

        // Use reflection to access the private method
        String extractedToken = invokeGetJWTFromRequest();

        // Assert
        assertEquals(validToken, extractedToken, "Extracted token should match the provided token");
    }

    @Test
    void getJWTFromRequest_WithMalformedHeader_ShouldReturnNull() {
        // Arrange
        String malformedHeader = "Bear " + validToken; // Incorrect prefix
        when(request.getHeader("Authorization")).thenReturn(malformedHeader);

        // Act
        String extractedToken = invokeGetJWTFromRequest();

        // Assert
        assertNull(extractedToken, "Extracted token should be null for malformed header");
    }

    @Test
    void getJWTFromRequest_WithNoAuthorizationHeader_ShouldReturnNull() {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn(null);

        // Act
        String extractedToken = invokeGetJWTFromRequest();

        // Assert
        assertNull(extractedToken, "Extracted token should be null when no Authorization header is present");
    }

    private String invokeGetJWTFromRequest() {
        try {
            java.lang.reflect.Method method = JwtAuthenticationFilter.class.getDeclaredMethod("getJWTFromRequest", HttpServletRequest.class);
            method.setAccessible(true);
            return (String) method.invoke(jwtAuthenticationFilter, request);
        } catch (Exception e) {
            fail("Failed to invoke getJWTFromRequest: " + e.getMessage());
            return null;
        }
    }
}
