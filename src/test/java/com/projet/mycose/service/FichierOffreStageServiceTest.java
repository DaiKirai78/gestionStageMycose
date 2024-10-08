package com.projet.mycose.service;

import com.projet.mycose.modele.Employeur;
import com.projet.mycose.modele.FichierOffreStage;
import com.projet.mycose.modele.Utilisateur;
import com.projet.mycose.repository.FichierOffreStageRepository;
import com.projet.mycose.repository.UtilisateurRepository;
import com.projet.mycose.service.dto.FichierOffreStageDTO;
import com.projet.mycose.service.dto.UtilisateurDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FichierOffreStageServiceTest {

    @Mock
    private FichierOffreStageRepository fileRepository;

    @Mock
    private UtilisateurRepository utilisateurRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private Validator validator;

    @Mock
    private MultipartFile multipartFile;

    @Mock
    private UtilisateurService utilisateurService;

    @InjectMocks
    private FichierOffreStageService fichierOffreStageService;

    private FichierOffreStage fichierOffreStage;
    private FichierOffreStageDTO fichierOffreStageDTO;

    @BeforeEach
    void setUp() throws IOException {
        Employeur createur = new Employeur();
        createur.setId(1L);


        fichierOffreStage = new FichierOffreStage();
        fichierOffreStage.setId(1L);
        fichierOffreStage.setFilename("test.pdf");
        fichierOffreStage.setData("Test file data".getBytes());
        fichierOffreStage.setCreateur(createur);

        fichierOffreStageDTO = new FichierOffreStageDTO();
        fichierOffreStageDTO.setFilename("test.pdf");
        fichierOffreStageDTO.setFileData(Base64.getEncoder().encodeToString("Test file data".getBytes()));
        fichierOffreStageDTO.setCreateur_id(1L);

        when(multipartFile.getOriginalFilename()).thenReturn("test.pdf");
        when(multipartFile.getBytes()).thenReturn("Test file data".getBytes());

        when(utilisateurService.getUserIdByToken(anyString()))
                .thenReturn(1L);
    }

    @Test
    void testSaveFile_Success() throws IOException {
        // Arrange
        when(validator.validate(any(FichierOffreStageDTO.class))).thenReturn(Collections.emptySet());
        when(fileRepository.save(any(FichierOffreStage.class))).thenReturn(fichierOffreStage);
        when(modelMapper.map(any(FichierOffreStage.class), eq(FichierOffreStageDTO.class))).thenReturn(fichierOffreStageDTO);
        when(modelMapper.map(any(FichierOffreStageDTO.class), eq(FichierOffreStage.class))).thenReturn(fichierOffreStage);
        when(utilisateurRepository.findById(1L)).thenReturn(java.util.Optional.of(new Employeur()));


        // Act

        FichierOffreStageDTO result = fichierOffreStageService.saveFile(multipartFile, "1L");

        // Assert
        assertNotNull(result);
        assertEquals(fichierOffreStageDTO.getFilename(), result.getFilename());
        assertEquals(fichierOffreStageDTO.getFileData(), result.getFileData());
        verify(fileRepository, times(1)).save(any(FichierOffreStage.class));
    }

    @Test
    void testSaveFile_ValidationFails() throws IOException {
        // Arrange
        ConstraintViolation<FichierOffreStageDTO> violation = mock(ConstraintViolation.class);
        Set<ConstraintViolation<FichierOffreStageDTO>> violations = Set.of(violation);
        when(validator.validate(any(FichierOffreStageDTO.class))).thenReturn(violations);

        // Act & Assert
        assertThrows(ConstraintViolationException.class, () -> {
            fichierOffreStageService.saveFile(multipartFile, "1L");
        });
        verify(fileRepository, never()).save(any(FichierOffreStage.class));
    }

    @Test
    void testSaveFile_IOError() throws IOException {
        // Arrange
        when(multipartFile.getBytes()).thenThrow(new IOException("IO Error"));

        // Act & Assert
        assertThrows(IOException.class, () -> {
            fichierOffreStageService.saveFile(multipartFile, "1L");
        });
        verify(fileRepository, never()).save(any(FichierOffreStage.class));
    }
}
