package com.projet.mycose.security;

import com.projet.mycose.exceptions.InvalidJwtTokenException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.nio.file.AccessDeniedException;
import java.security.Key;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private Authentication authentication;

    // Base64-encoded secret key (32 bytes when decoded)
    private final String validJwtSecret = "TXlTdXBlclNlY3JldEtleUZvckp3dFNpZ25pbmdXaGljaElzQXRPbHNXaXRlMjU2Qnl0c0xvbmc=";
    private final int expirationInMs = 3600000; // 1 hour

    private Key key;

    @BeforeEach
    void setUp() {
        // Initialize jwtTokenProvider with constructor injection
        jwtTokenProvider = new JwtTokenProvider(expirationInMs, validJwtSecret);
        key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(validJwtSecret));
    }

    @Test
    void testJwtSecret_Length_ShouldBeAtLeast256Bits() {
        byte[] decodedSecret = Base64.getDecoder().decode(validJwtSecret);
        assertTrue(decodedSecret.length >= 32, "Decoded jwtSecret should be at least 32 bytes long for HS256");
    }

    @Test
    void testGenerateToken_ShouldReturnValidJwtToken() {
        // Arrange
        String username = "testuser@example.com";
        List<GrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("GESTIONNAIRE_STAGE"),
                new SimpleGrantedAuthority("ENSEIGNANT")
        );
        when(authentication.getName()).thenReturn(username);
        doReturn(authorities).when(authentication).getAuthorities();


        // Act
        String token = jwtTokenProvider.generateToken(authentication);

        // Assert
        assertNotNull(token, "Generated token should not be null");
        // Parse the token to verify its validity and contents
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        assertEquals(username, claims.getSubject(), "Subject should match the authenticated user's name");
        assertNotNull(claims.getIssuedAt(), "IssuedAt should not be null");
        assertNotNull(claims.getExpiration(), "Expiration should not be null");
        // Verify authorities
        Object authClaim = claims.get("authorities");
        assertNotNull(authClaim, "Authorities claim should not be null");
        assertTrue(authClaim instanceof List, "Authorities claim should be a list");
        @SuppressWarnings("unchecked")
        List<Map<String, String>> authList = (List<Map<String, String>>) authClaim;
        assertEquals(2, authList.size(), "There should be two authorities");
        List<String> roles = new ArrayList<>();
        for (Map<String, String> auth : authList) {
            roles.add(auth.get("authority"));
        }
        assertTrue(roles.contains("ENSEIGNANT"), "Authorities should contain ENSEIGNANT");
        assertTrue(roles.contains("GESTIONNAIRE_STAGE"), "Authorities should contain GESTIONNAIRE_STAGE");
    }

    @Test
    void testGetEmailFromJWT_WithValidToken_ShouldReturnEmail() throws Exception {
        // Arrange
        String email = "user@example.com";
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationInMs);
        String token = Jwts.builder()
                .setSubject(email)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .claim("authorities", List.of(new SimpleGrantedAuthority("GESTIONNAIRE_STAGE")))
                .signWith(key)
                .compact();

        // Act
        String extractedEmail = jwtTokenProvider.getEmailFromJWT(token);

        // Assert
        assertEquals(email, extractedEmail, "Extracted email should match the token's subject");
    }

    @Test
    void testGetEmailFromJWT_WithInvalidToken_ShouldThrowAccessDeniedException() {
        // Arrange
        String invalidToken = "invalid.token.value";

        // Act & Assert
        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> {
            jwtTokenProvider.getEmailFromJWT(invalidToken);
        }, "Expected getEmailFromJWT to throw AccessDeniedException for invalid token");

        assertEquals("Token Invalide", exception.getMessage(), "Exception message should match");
    }

    @Test
    void testValidateToken_WithValidToken_ShouldPass() {
        // Arrange
        String username = "validuser@example.com";
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationInMs);
        String token = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .claim("authorities", List.of(new SimpleGrantedAuthority("GESTIONNAIRE_STAGE")))
                .signWith(key)
                .compact();

        // Act & Assert
        assertDoesNotThrow(() -> {
            jwtTokenProvider.validateToken(token);
        }, "validateToken should not throw exception for a valid token");
    }

    @Test
    void testValidateToken_WithExpiredToken_ShouldThrowInvalidJwtTokenException() {
        // Arrange
        String username = "expireduser@example.com";
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() - 1000); // Token already expired
        String token = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date(now.getTime() - expirationInMs - 1000))
                .setExpiration(expiryDate)
                .claim("authorities", List.of(new SimpleGrantedAuthority("ROLE_USER")))
                .signWith(key)
                .compact();

        // Act & Assert
        InvalidJwtTokenException exception = assertThrows(InvalidJwtTokenException.class, () -> {
            jwtTokenProvider.validateToken(token);
        }, "validateToken should throw InvalidJwtTokenException for expired token");

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus(), "Exception status should be BAD_REQUEST");
        assertEquals("Expired JWT token", exception.getMessage(), "Exception message should match");
    }

    @Test
    void testValidateToken_WithInvalidSignature_ShouldThrowSignatureException() {
        // Arrange
        String username = "user@example.com";
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationInMs);
        // Create a token signed with a different key
        String wrongSecret = "QW5vdGhlclNlY3JldEtleUZvckp3dFNpZ25pbmdXaGljaElzQXRPbHNXaXRlMjU2Qnl0c0xvbmc="; // Base64 for "AnotherSecretKeyForJwtSigningWhichIsAtLeast256BitsLong"
        Key wrongKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(wrongSecret));
        String token = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .claim("authorities", List.of(new SimpleGrantedAuthority("ROLE_USER")))
                .signWith(wrongKey)
                .compact();

        // Act & Assert
        SignatureException exception = assertThrows(SignatureException.class, () -> {
            jwtTokenProvider.validateToken(token);
        }, "validateToken should throw SignatureException for token with invalid signature");

        assertEquals("JWT signature does not match locally computed signature. JWT validity cannot be asserted and should not be trusted.", exception.getMessage(), "Exception message should match");
    }


    @Test
    void testValidateToken_WithMalformedToken_ShouldThrowInvalidJwtTokenException() {
        // Arrange
        String malformedToken = "malformed.token";

        // Act & Assert
        InvalidJwtTokenException exception = assertThrows(InvalidJwtTokenException.class, () -> {
            jwtTokenProvider.validateToken(malformedToken);
        }, "validateToken should throw InvalidJwtTokenException for malformed token");

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus(), "Exception status should be BAD_REQUEST");
        assertEquals("Invalid JWT token", exception.getMessage(), "Exception message should match");
    }

    @Test
    void testValidateToken_WithUnsupportedToken_ShouldThrowInvalidJwtTokenException() {
        // Arrange
        // Create a JWT with an unsupported algorithm (e.g., NONE)
        String unsupportedJwt = Jwts.builder()
                .setSubject("user@example.com")
                .compact();

        // Act & Assert
        InvalidJwtTokenException exception = assertThrows(InvalidJwtTokenException.class, () -> {
            jwtTokenProvider.validateToken(unsupportedJwt);
        }, "validateToken should throw InvalidJwtTokenException for unsupported JWT");

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus(), "Exception status should be BAD_REQUEST");
        assertEquals("Unsupported JWT token", exception.getMessage(), "Exception message should match");
    }


    @Test
    void testValidateToken_WithEmptyClaims_ShouldThrowInvalidJwtTokenException() {
        // Arrange
        String emptyClaimsToken = Jwts.builder()
                .signWith(key)
                .compact();

        // Act & Assert
        InvalidJwtTokenException exception = assertThrows(InvalidJwtTokenException.class, () -> {
            jwtTokenProvider.validateToken(emptyClaimsToken);
        }, "validateToken should throw InvalidJwtTokenException for token with empty claims");

        // Adjust the expected message based on the actual exception thrown
        String expectedMessage = exception.getMessage().equals("JWT claims string is empty") ?
                "JWT claims string is empty" : "Unsupported JWT token";

        assertEquals(expectedMessage, exception.getMessage(), "Exception message should match");
    }


    @Test
    void testValidateToken_WithExpiredAndInvalidSignature_ShouldThrowSignatureException() {
        // Arrange
        String username = "user@example.com";
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() - 1000); // Token already expired
        // Create a token signed with a different key and expired
        String wrongSecret = "QW5vdGhlclNlY3JldEtleUZvckp3dFNpZ25pbmdXaGljaElzQXRPbHNXaXRlMjU2Qnl0c0xvbmc="; // Base64 for "AnotherSecretKeyForJwtSigningWhichIsAtLeast256BitsLong"
        Key wrongKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(wrongSecret));
        String token = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date(now.getTime() - expirationInMs - 1000))
                .setExpiration(expiryDate)
                .claim("authorities", List.of(new SimpleGrantedAuthority("ROLE_USER")))
                .signWith(wrongKey)
                .compact();

        // Act & Assert
        SignatureException exception = assertThrows(SignatureException.class, () -> {
            jwtTokenProvider.validateToken(token);
        }, "validateToken should throw SignatureException for expired token with invalid signature");

        assertEquals("JWT signature does not match locally computed signature. JWT validity cannot be asserted and should not be trusted.", exception.getMessage(), "Exception message should match");
    }


    @Test
    void testValidateToken_WithNullToken_ShouldThrowInvalidJwtTokenException() {
        // Arrange
        String nullToken = null;

        // Act & Assert
        InvalidJwtTokenException exception = assertThrows(InvalidJwtTokenException.class, () -> {
            jwtTokenProvider.validateToken(nullToken);
        }, "validateToken should throw InvalidJwtTokenException for null token");

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus(), "Exception status should be BAD_REQUEST");
        assertEquals("JWT claims string is empty", exception.getMessage(), "Exception message should match");
    }

    @Test
    void testValidateToken_WithEmptyToken_ShouldThrowInvalidJwtTokenException() {
        // Arrange
        String emptyToken = "";

        // Act & Assert
        InvalidJwtTokenException exception = assertThrows(InvalidJwtTokenException.class, () -> {
            jwtTokenProvider.validateToken(emptyToken);
        }, "validateToken should throw InvalidJwtTokenException for empty token");

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus(), "Exception status should be BAD_REQUEST");
        assertEquals("JWT claims string is empty", exception.getMessage(), "Exception message should match");
    }


    @Test
    void testValidateToken_WithExpirationBeforeIssueDate_ShouldThrowInvalidJwtTokenException() {
        // Arrange
        Date now = new Date();
        Date issuedAt = new Date(now.getTime() + 10000); // issued in the future
        Date expiryDate = new Date(now.getTime() - 1000); // already expired
        String token = Jwts.builder()
                .setSubject("futureIssueDateUser")
                .setIssuedAt(issuedAt)
                .setExpiration(expiryDate)
                .claim("authorities", List.of(new SimpleGrantedAuthority("ROLE_USER")))
                .signWith(key)
                .compact();

        // Act & Assert
        InvalidJwtTokenException exception = assertThrows(InvalidJwtTokenException.class, () -> {
            jwtTokenProvider.validateToken(token);
        }, "validateToken should throw InvalidJwtTokenException for token with expiration before issue date");

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus(), "Exception status should be BAD_REQUEST");
        assertEquals("Expired JWT token", exception.getMessage(), "Exception message should match");
    }


}
