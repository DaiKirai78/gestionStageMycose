package com.projet.mycose.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsTest {

    private CustomUserDetails userDetails;

    private final String username = "michel";
    private final List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("GESTIONNAIRE_STAGE"));

    @BeforeEach
    void setUp() {
        Long userId = 1L;
        userDetails = new CustomUserDetails(userId, username, authorities);
    }

    @Test
    @DisplayName("getPassword should return null")
    void testGetPassword_ReturnsNull() {
        // Act
        String password = userDetails.getPassword();

        // Assert
        assertNull(password, "getPassword should return null");
    }

    @Test
    @DisplayName("getUsername should return the correct username")
    void testGetUsername_ReturnsUsername() {
        // Act
        String actualUsername = userDetails.getUsername();

        // Assert
        assertEquals(username, actualUsername, "getUsername should return the correct username");
    }

    @Test
    @DisplayName("getAuthorities should return the correct authorities")
    void testGetAuthorities_ReturnsAuthorities() {
        // Act
        Collection<? extends GrantedAuthority> actualAuthorities = userDetails.getAuthorities();

        // Assert
        assertNotNull(actualAuthorities, "getAuthorities should not return null");
        assertEquals(authorities.size(), actualAuthorities.size(), "Authority size should match");
        assertTrue(actualAuthorities.containsAll(authorities), "getAuthorities should return the correct authorities");
    }

    @Test
    @DisplayName("isAccountNonExpired should return true by default")
    void testIsAccountNonExpired_ReturnsTrue() {
        // Act
        boolean isNonExpired = userDetails.isAccountNonExpired();

        // Assert
        assertTrue(isNonExpired, "isAccountNonExpired should return true by default");
    }

    @Test
    @DisplayName("isAccountNonLocked should return true by default")
    void testIsAccountNonLocked_ReturnsTrue() {
        // Act
        boolean isNonLocked = userDetails.isAccountNonLocked();

        // Assert
        assertTrue(isNonLocked, "isAccountNonLocked should return true by default");
    }

    @Test
    @DisplayName("isCredentialsNonExpired should return true by default")
    void testIsCredentialsNonExpired_ReturnsTrue() {
        // Act
        boolean isCredentialsNonExpired = userDetails.isCredentialsNonExpired();

        // Assert
        assertTrue(isCredentialsNonExpired, "isCredentialsNonExpired should return true by default");
    }

    @Test
    @DisplayName("isEnabled should return true by default")
    void testIsEnabled_ReturnsTrue() {
        // Act
        boolean isEnabled = userDetails.isEnabled();

        // Assert
        assertTrue(isEnabled, "isEnabled should return true by default");
    }
}
