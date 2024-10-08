package com.projet.mycose.service;

import com.projet.mycose.modele.Employeur;
import com.projet.mycose.modele.FormulaireOffreStage;
import com.projet.mycose.repository.FormulaireOffreStageRepository;
import com.projet.mycose.repository.UtilisateurRepository;
import com.projet.mycose.service.dto.FormulaireOffreStageDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FormulaireOffreStageServiceTest {

    @Mock
    private FormulaireOffreStageRepository formulaireOffreStageRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private UtilisateurRepository utilisateurRepository;

    @Mock
    private UtilisateurService utilisateurService;

    @InjectMocks
    private FormulaireOffreStageService formulaireOffreStageService;

    private FormulaireOffreStage formulaireOffreStage;
    private FormulaireOffreStageDTO formulaireOffreStageDTO;

    @BeforeEach
    void setUp() {
        formulaireOffreStage = new FormulaireOffreStage();
        formulaireOffreStage.setId(1L);
        formulaireOffreStage.setTitle("Test Stage");

        formulaireOffreStageDTO = new FormulaireOffreStageDTO();
        formulaireOffreStageDTO.setId(1L);
        formulaireOffreStageDTO.setTitle("Test Stage");
        formulaireOffreStageDTO.setCreateur_id(1L);

        when(utilisateurRepository.findById(anyLong()))
                .thenReturn(Optional.of(new Employeur()));

        when(utilisateurService.getUserIdByToken(anyString()))
                .thenReturn(1L);

    }

    @Test
    void testSave_Success() {
        // Arrange
        when(modelMapper.map(any(FormulaireOffreStageDTO.class), eq(FormulaireOffreStage.class)))
                .thenReturn(formulaireOffreStage);
        when(formulaireOffreStageRepository.save(any(FormulaireOffreStage.class)))
                .thenReturn(formulaireOffreStage);
        when(modelMapper.map(any(FormulaireOffreStage.class), eq(FormulaireOffreStageDTO.class)))
                .thenReturn(formulaireOffreStageDTO);

        // Act
        FormulaireOffreStageDTO result = formulaireOffreStageService.save(formulaireOffreStageDTO, "token");

        // Assert
        assertNotNull(result);
        assertEquals(formulaireOffreStageDTO.getId(), result.getId());
        assertEquals(formulaireOffreStageDTO.getTitle(), result.getTitle());
        verify(formulaireOffreStageRepository, times(1)).save(any(FormulaireOffreStage.class));
    }

    @Test
    void testSave_RepositorySaveFailure() {
        // Arrange
        when(modelMapper.map(any(FormulaireOffreStageDTO.class), eq(FormulaireOffreStage.class)))
                .thenReturn(formulaireOffreStage);
        when(formulaireOffreStageRepository.save(any(FormulaireOffreStage.class)))
                .thenThrow(new RuntimeException("Database Error"));


        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            formulaireOffreStageService.save(formulaireOffreStageDTO, "token");
        });

        assertEquals("Database Error", exception.getMessage());
        verify(formulaireOffreStageRepository, times(1)).save(any(FormulaireOffreStage.class));
    }
}
