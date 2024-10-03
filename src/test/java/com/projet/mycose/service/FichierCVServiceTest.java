package com.projet.mycose.service;

import com.projet.mycose.modele.FichierCV;
import com.projet.mycose.repository.FichierCVRepository;
import com.projet.mycose.service.dto.FichierCVDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

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

    @Test
    // Parce que dans setup ya des when qui sont pas utilisé
    // donc ça fait une erreur.
    // MockitioSetting stricness linient retire l'erreur
    @MockitoSettings(strictness = Strictness.LENIENT)
    void testGetWaitingCV_Success() {
        // Act
        when(fileRepository.getFichierCVSByStatusEquals(FichierCV.Status.WAITING, PageRequest.of(0, 10)))
                .thenReturn(Optional.of(new ArrayList<>((Arrays.asList(fichierCV, fichierCV, fichierCV, fichierCV, fichierCV, fichierCV, fichierCV, fichierCV, fichierCV, fichierCV)))));
        when(fileRepository.getFichierCVSByStatusEquals(FichierCV.Status.WAITING, PageRequest.of(1, 10)))
                .thenReturn(Optional.of(new ArrayList<>((Arrays.asList(fichierCV, fichierCV, fichierCV, fichierCV)))));;
        when(modelMapper.map(any(FichierCV.class), eq(FichierCVDTO.class))).thenReturn(fichierCVDTO);

        // Assert
        Assertions.assertThat(fichierCVService.getWaitingCv(0).size()).isEqualTo(10);
        Assertions.assertThat(fichierCVService.getWaitingCv(1).size()).isEqualTo(4);
    }
    @Test
    @MockitoSettings(strictness = Strictness.LENIENT)
    void testGetWaitingCV_PageVide() {
        // Act
        when(fileRepository.getFichierCVSByStatusEquals(FichierCV.Status.WAITING, PageRequest.of(1, 10)))
                .thenReturn(Optional.empty());

        // Assert
        Assertions.assertThat(fichierCVService.getWaitingCv(1).size()).isEqualTo(0);
    }


    @Test
    @MockitoSettings(strictness = Strictness.LENIENT)
    void testGetAmountOfPage_NumberEndWithZero_Success() {
        // Act
        when(fileRepository.count()).thenReturn(30L);

        // Assert
        Assertions.assertThat(fichierCVService.getAmountOfPages()).isEqualTo(3);
    }
    @Test
    @MockitoSettings(strictness = Strictness.LENIENT)
    void testGetAmountOfPage_NumberNotEndWithZero_Success() {
        // Act
        when(fileRepository.count()).thenReturn(43L);

        // Assert
        Assertions.assertThat(fichierCVService.getAmountOfPages()).isEqualTo(4);
    }
}
