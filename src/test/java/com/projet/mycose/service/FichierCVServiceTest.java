package com.projet.mycose.service;

import com.projet.mycose.modele.FichierCV;
import com.projet.mycose.repository.FichierCVRepository;
import com.projet.mycose.service.dto.FichierCVDTO;
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
public class FichierCVServiceTest {

    @Mock
    private FichierCVRepository fileRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private Validator validator;

    @Mock
    private MultipartFile multipartFile;

    @InjectMocks
    private FichierCVService fichierCVService;

    private FichierCV fichierCV;
    private FichierCVDTO fichierCVDTO;

    @BeforeEach
    void setUp() throws IOException {
        fichierCV = new FichierCV();
        fichierCV.setId(1L);
        fichierCV.setFilename("test.pdf");
        fichierCV.setData("Test file data".getBytes());

        fichierCVDTO = new FichierCVDTO();
        fichierCVDTO.setFilename("test.pdf");
        fichierCVDTO.setFileData(Base64.getEncoder().encodeToString("Test file data".getBytes()));

        when(multipartFile.getOriginalFilename()).thenReturn("test.pdf");
        when(multipartFile.getBytes()).thenReturn("Test file data".getBytes());
    }

    @Test
    void testSaveFile_Success() throws IOException {
        // Arrange
        when(validator.validate(any(FichierCVDTO.class))).thenReturn(Collections.emptySet());
        when(fileRepository.save(any(FichierCV.class))).thenReturn(fichierCV);
        when(modelMapper.map(any(FichierCV.class), eq(FichierCVDTO.class))).thenReturn(fichierCVDTO);
        when(modelMapper.map(any(FichierCVDTO.class), eq(FichierCV.class))).thenReturn(fichierCV);

        // Act
        FichierCVDTO result = fichierCVService.saveFile(multipartFile);

        // Assert
        assertNotNull(result);
        assertEquals(fichierCVDTO.getFilename(), result.getFilename());
        assertEquals(fichierCVDTO.getFileData(), result.getFileData());
        verify(fileRepository, times(1)).save(any(FichierCV.class));
    }

    @Test
    void testSaveFile_ValidationFails() throws IOException {
        // Arrange
        ConstraintViolation<FichierCVDTO> violation = mock(ConstraintViolation.class);
        Set<ConstraintViolation<FichierCVDTO>> violations = Set.of(violation);
        when(validator.validate(any(FichierCVDTO.class))).thenReturn(violations);

        // Act & Assert
        assertThrows(ConstraintViolationException.class, () -> {
            fichierCVService.saveFile(multipartFile);
        });
        verify(fileRepository, never()).save(any(FichierCV.class));
    }

    @Test
    void testSaveFile_IOError() throws IOException {
        // Arrange
        when(multipartFile.getBytes()).thenThrow(new IOException("IO Error"));

        // Act & Assert
        assertThrows(IOException.class, () -> {
            fichierCVService.saveFile(multipartFile);
        });
        verify(fileRepository, never()).save(any(FichierCV.class));
    }
}
