package com.projet.mycose.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.AuthenticationException;

import java.io.IOException;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationEntryPointTest {

    @InjectMocks
    private JwtAuthenticationEntryPoint entryPoint;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private AuthenticationException authException;

    private final String exceptionMessage = "Unauthorized access";

    @BeforeEach
    void setUp() {
        // Initialize mocks before each test if necessary
    }

    @Test
    void commence_ShouldSetContentTypeToJsonAndSendUnauthorizedError() throws IOException, ServletException {
        // Arrange
        when(authException.getMessage()).thenReturn(exceptionMessage);

        // Act
        entryPoint.commence(request, response, authException);

        // Assert
        InOrder inOrder = inOrder(response);
        inOrder.verify(response).setContentType("application/json");
        inOrder.verify(response).sendError(HttpServletResponse.SC_UNAUTHORIZED, exceptionMessage);

        // Ensure no other interactions
        verifyNoMoreInteractions(response);
    }

    @Test
    void commence_WithNullExceptionMessage_ShouldSendErrorWithNullMessage() throws IOException, ServletException {
        // Arrange
        when(authException.getMessage()).thenReturn(null);

        // Act
        entryPoint.commence(request, response, authException);

        // Assert
        InOrder inOrder = inOrder(response);
        inOrder.verify(response).setContentType("application/json");
        inOrder.verify(response).sendError(HttpServletResponse.SC_UNAUTHORIZED, null);

        // Ensure no other interactions
        verifyNoMoreInteractions(response);
    }

    @Test
    void commence_ShouldThrowIOException_WhenSendErrorFails() throws IOException {
        // Arrange
        when(authException.getMessage()).thenReturn(exceptionMessage);
        doThrow(new IOException("Failed to send error")).when(response).sendError(HttpServletResponse.SC_UNAUTHORIZED, exceptionMessage);

        // Act & Assert
        IOException thrownException = assertThrows(IOException.class, () -> {
            entryPoint.commence(request, response, authException);
        }, "Expected commence() to throw IOException");

        assertEquals("Failed to send error", thrownException.getMessage());

        // Verify that setContentType was called before sendError
        InOrder inOrder = inOrder(response);
        inOrder.verify(response).setContentType("application/json");
        inOrder.verify(response).sendError(HttpServletResponse.SC_UNAUTHORIZED, exceptionMessage);
    }
}
