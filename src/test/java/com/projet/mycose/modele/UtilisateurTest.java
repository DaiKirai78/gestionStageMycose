package com.projet.mycose.modele;

import com.projet.mycose.modele.auth.Credentials;
import com.projet.mycose.modele.auth.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class UtilisateurTest {

    private Employeur employeur;
    private Credentials credentials;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        credentials = Credentials.builder()
                .email("employer@example.com")
                .password("employerPass")
                .role(Role.EMPLOYEUR)
                .build();

        employeur = Employeur.builder()
                .prenom("Alice")
                .nom("Smith")
                .numeroDeTelephone("0987654321")
                .courriel("employer@example.com")
                .motDePasse("employerPass")
                .nomOrganisation("TechCorp")
                .build();
    }

    @Test
    @DisplayName("getAuthorities() returns correct authority for EMPLOYEUR role")
    void getAuthorities_ReturnsCorrectAuthority_ForEmployeurRole() {
        // Act
        Collection<? extends GrantedAuthority> authorities = employeur.getAuthorities();

        // Assert
        assertNotNull(authorities, "Authorities should not be null");
        assertEquals(1, authorities.size(), "There should be exactly one authority");
        assertTrue(authorities.contains(new SimpleGrantedAuthority("EMPLOYEUR")),
                "Authorities should contain EMPLOYEUR");
    }

    @Test
    @DisplayName("getPassword() returns the correct password")
    void getPassword_ReturnsCorrectPassword() {
        // Act
        String password = employeur.getMotDePasse();

        // Assert
        assertEquals("employerPass", password, "getPassword() should return the correct password");
    }
}
