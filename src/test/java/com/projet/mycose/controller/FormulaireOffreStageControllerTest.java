package com.projet.mycose.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projet.mycose.service.FormulaireOffreStageService;
import com.projet.mycose.service.UtilisateurService;
import com.projet.mycose.service.dto.FormulaireOffreStageDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class FormulaireOffreStageControllerTest {

    @Mock
    private FormulaireOffreStageService formulaireOffreStageService;

    @Mock
    private UtilisateurService utilisateurService;

    @InjectMocks
    private FormulaireOffreStageController formulaireOffreStageController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private FormulaireOffreStageDTO validFormulaireOffreStageDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(formulaireOffreStageController)
                .setControllerAdvice(new GlobalExceptionHandler()) // Register the GlobalExceptionHandler
                .build();

        objectMapper = new ObjectMapper();

        validFormulaireOffreStageDTO = new FormulaireOffreStageDTO();
        validFormulaireOffreStageDTO.setId(1L);
        validFormulaireOffreStageDTO.setEntrepriseName("Valid Enterprise");
        validFormulaireOffreStageDTO.setEmployerName("John Doe");
        validFormulaireOffreStageDTO.setEmail("email@example.com");
        validFormulaireOffreStageDTO.setWebsite("http://www.example.com");
        validFormulaireOffreStageDTO.setTitle("Valid Title");
        validFormulaireOffreStageDTO.setLocation("Valid Location");
        validFormulaireOffreStageDTO.setSalary("50000.00");
        validFormulaireOffreStageDTO.setDescription("Valid description.");


    }

    @Test
    void testUploadForm_Success() throws Exception {
        // Arrange
        when(formulaireOffreStageService.save(any(FormulaireOffreStageDTO.class), any(String.class)))
                .thenReturn(validFormulaireOffreStageDTO);


        // Act & Assert
        mockMvc.perform(post("/api/offres/upload")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer token")
                        .content(objectMapper.writeValueAsString(validFormulaireOffreStageDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.entrepriseName").value("Valid Enterprise"))
                .andExpect(jsonPath("$.title").value("Valid Title"));
    }

    @Test
    void testUploadForm_InvalidEmail() throws Exception {
        // Arrange
        validFormulaireOffreStageDTO.setEmail("invalid-email");

        // Act & Assert
        mockMvc.perform(post("/api/offres/upload")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer token")
                        .content(objectMapper.writeValueAsString(validFormulaireOffreStageDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.email").value("Invalid email format."));
    }

    @Test
    void testUploadForm_EmptyFields() throws Exception {
        // Arrange
        FormulaireOffreStageDTO invalidFormDTO = new FormulaireOffreStageDTO(); // Missing required fields

        // Act & Assert
        mockMvc.perform(post("/api/offres/upload")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer token")
                        .content(objectMapper.writeValueAsString(invalidFormDTO)))
                .andExpect(status().isBadRequest())
                //TODO: A enlever les commentaires quand les champs seront ajoutés au front-end
                //.andExpect(jsonPath("$.entrepriseName").value("Enterprise name is required."))
                .andExpect(jsonPath("$.employerName").value("Employer name is required."))
                .andExpect(jsonPath("$.email").value("Email is required."))
                .andExpect(jsonPath("$.website").value("Website is required."))
                //.andExpect(jsonPath("$.title").value("Title is required."))
                .andExpect(jsonPath("$.location").value("Location is required."))
                .andExpect(jsonPath("$.salary").value("Salary is required."))
                .andExpect(jsonPath("$.description").value("Description is required."));
    }

    @Test
    void testUploadForm_InvalidSalary() throws Exception {
        // Arrange
        validFormulaireOffreStageDTO.setSalary("invalid-salary");

        // Act & Assert
        mockMvc.perform(post("/api/offres/upload")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer token")
                        .content(objectMapper.writeValueAsString(validFormulaireOffreStageDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.salary").value("Salary must be a valid number with up to 10 digits and 2 decimal places."));
    }


    //TODO: A enlever les commentaires quand les champs seront ajoutés au front-end
//    @Test
//    void testUploadForm_TooLongTitle() throws Exception {
//        // Arrange
//        validFormulaireOffreStageDTO.setTitle("A".repeat(101)); // Exceeds 100 characters
//
//        // Act & Assert
//        mockMvc.perform(post("/api/offres/upload")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(validFormulaireOffreStageDTO)))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.title").value("Title cannot exceed 100 characters."));
//    }

    @Test
    void testUploadForm_TooLongDescription() throws Exception {
        // Arrange
        validFormulaireOffreStageDTO.setDescription("A".repeat(501)); // Exceeds 500 characters

        // Act & Assert
        mockMvc.perform(post("/api/offres/upload")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer token")
                        .content(objectMapper.writeValueAsString(validFormulaireOffreStageDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.description").value("Description cannot exceed 500 characters."));
    }

}
