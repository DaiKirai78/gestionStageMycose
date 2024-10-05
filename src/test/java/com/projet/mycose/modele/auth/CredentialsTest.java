package com.projet.mycose.modele.auth;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class CredentialsTest {

    @Test
    void getAuthorities_ReturnsCorrectAuthority_ForGestionnaireStageRole() {
        // Arrange
        Credentials credentials = Credentials.builder()
                .email("admin@domain.com")
                .password("adminPass")
                .role(Role.GESTIONNAIRE_STAGE)
                .build();

        // Act
        Collection<? extends GrantedAuthority> authorities = credentials.getAuthorities();

        // Assert
        assertNotNull(authorities, "Authorities should not be null");
        assertEquals(1, authorities.size(), "There should be exactly one authority");
        assertTrue(authorities.contains(new SimpleGrantedAuthority("GESTIONNAIRE_STAGE")),
                "Authorities should contain GESTIONNAIRE_STAGE");
    }

    @Test
    void getAuthorities_ReturnsCorrectAuthority_ForEtudiantRole() {
        // Arrange
        Credentials credentials = Credentials.builder()
                .email("student@domain.com")
                .password("studentPass")
                .role(Role.ETUDIANT)
                .build();

        // Act
        Collection<? extends GrantedAuthority> authorities = credentials.getAuthorities();

        // Assert
        assertNotNull(authorities, "Authorities should not be null");
        assertEquals(1, authorities.size(), "There should be exactly one authority");
        assertTrue(authorities.contains(new SimpleGrantedAuthority("ETUDIANT")),
                "Authorities should contain ETUDIANT");
    }

    @Test
    void getAuthorities_ReturnsCorrectAuthority_ForEmployeurRole() {
        // Arrange
        Credentials credentials = Credentials.builder()
                .email("employer@domain.com")
                .password("employerPass")
                .role(Role.EMPLOYEUR)
                .build();

        // Act
        Collection<? extends GrantedAuthority> authorities = credentials.getAuthorities();

        // Assert
        assertNotNull(authorities, "Authorities should not be null");
        assertEquals(1, authorities.size(), "There should be exactly one authority");
        assertTrue(authorities.contains(new SimpleGrantedAuthority("EMPLOYEUR")),
                "Authorities should contain EMPLOYEUR");
    }

    @Test
    void getAuthorities_ReturnsCorrectAuthority_ForEnseignantRole() {
        // Arrange
        Credentials credentials = Credentials.builder()
                .email("teacher@domain.com")
                .password("teacherPass")
                .role(Role.ENSEIGNANT)
                .build();

        // Act
        Collection<? extends GrantedAuthority> authorities = credentials.getAuthorities();

        // Assert
        assertNotNull(authorities, "Authorities should not be null");
        assertEquals(1, authorities.size(), "There should be exactly one authority");
        assertTrue(authorities.contains(new SimpleGrantedAuthority("ENSEIGNANT")),
                "Authorities should contain ENSEIGNANT");
    }

    @Test
    void getPassword_ReturnsCorrectPassword() {
        // Arrange
        String password = "securePassword!";
        Credentials credentials = Credentials.builder()
                .email("user@domain.com")
                .password(password)
                .role(Role.GESTIONNAIRE_STAGE)
                .build();

        // Act
        String retrievedPassword = credentials.getPassword();

        // Assert
        assertEquals(password, retrievedPassword, "The retrieved password should match the set password");
    }

    @Test
    void getUsername_ReturnsCorrectEmail() {
        // Arrange
        String email = "user@domain.com";
        Credentials credentials = Credentials.builder()
                .email(email)
                .password("password123")
                .role(Role.GESTIONNAIRE_STAGE)
                .build();

        // Act
        String retrievedEmail = credentials.getUsername();

        // Assert
        assertEquals(email, retrievedEmail, "The retrieved email should match the set email");
    }

    @Test
    void isAccountNonExpired_ReturnsTrue() {
        // Arrange
        Credentials credentials = Credentials.builder()
                .email("user@domain.com")
                .password("password123")
                .role(Role.GESTIONNAIRE_STAGE)
                .build();

        // Act
        boolean isNonExpired = credentials.isAccountNonExpired();

        // Assert
        assertTrue(isNonExpired, "isAccountNonExpired should always return true");
    }

    @Test
    void isAccountNonLocked_ReturnsTrue() {
        // Arrange
        Credentials credentials = Credentials.builder()
                .email("user@domain.com")
                .password("password123")
                .role(Role.GESTIONNAIRE_STAGE)
                .build();

        // Act
        boolean isNonLocked = credentials.isAccountNonLocked();

        // Assert
        assertTrue(isNonLocked, "isAccountNonLocked should always return true");
    }

    @Test
    void isCredentialsNonExpired_ReturnsTrue() {
        // Arrange
        Credentials credentials = Credentials.builder()
                .email("user@domain.com")
                .password("password123")
                .role(Role.GESTIONNAIRE_STAGE)
                .build();

        // Act
        boolean isCredentialsNonExpired = credentials.isCredentialsNonExpired();

        // Assert
        assertTrue(isCredentialsNonExpired, "isCredentialsNonExpired should always return true");
    }

    @Test
    void isEnabled_ReturnsTrue() {
        // Arrange
        Credentials credentials = Credentials.builder()
                .email("user@domain.com")
                .password("password123")
                .role(Role.GESTIONNAIRE_STAGE)
                .build();

        // Act
        boolean isEnabled = credentials.isEnabled();

        // Assert
        assertTrue(isEnabled, "isEnabled should always return true");
    }
}
