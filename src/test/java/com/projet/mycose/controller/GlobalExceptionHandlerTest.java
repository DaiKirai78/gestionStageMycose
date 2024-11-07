package com.projet.mycose.controller;

import com.projet.mycose.exceptions.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    private MethodArgumentNotValidException exception;
    private BindingResult bindingResult;

    @BeforeEach
    void setUp() {
        bindingResult = mock(BindingResult.class);
        exception = new MethodArgumentNotValidException(null, bindingResult);
    }

    @Test
    void testHandleValidationExceptions() {
        // Arrange
        FieldError fieldError1 = new FieldError("objectName", "field1", "Field1 is invalid");
        FieldError fieldError2 = new FieldError("objectName", "field2", "Field2 is invalid");

        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError1, fieldError2));

        // Act
        ResponseEntity<Map<String, String>> response = globalExceptionHandler.handleValidationExceptions(exception);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertEquals("Field1 is invalid", response.getBody().get("field1"));
        assertEquals("Field2 is invalid", response.getBody().get("field2"));

        verify(bindingResult, times(1)).getFieldErrors();
    }
}
