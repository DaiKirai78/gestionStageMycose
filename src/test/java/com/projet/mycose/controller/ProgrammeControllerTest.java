package com.projet.mycose.controller;

import com.projet.mycose.modele.Programme;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@ExtendWith(MockitoExtension.class)
public class ProgrammeControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private ProgrammeController programmeController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(programmeController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void testGetProgrammes_Success() throws Exception {
        // Arrange
        List<String> expectedProgrammes = Arrays.stream(Programme.values())
                .map(Programme::toString)
                .toList();

        // Act & Assert
        mockMvc.perform(get("/api/programme")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // 200 OK
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(expectedProgrammes.size())))
                .andExpect(jsonPath("$", containsInAnyOrder(
                        expectedProgrammes.toArray()
                )));
    }
}
