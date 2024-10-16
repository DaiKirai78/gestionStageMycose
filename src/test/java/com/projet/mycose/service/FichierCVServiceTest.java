package com.projet.mycose.service;

import com.projet.mycose.modele.Etudiant;
import com.projet.mycose.modele.FichierCV;
import com.projet.mycose.modele.Programme;
import com.projet.mycose.repository.EtudiantRepository;
import com.projet.mycose.repository.FichierCVRepository;
import com.projet.mycose.dto.FichierCVDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.crossstore.ChangeSetPersister;
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
    private EtudiantRepository etudiantRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private Validator validator;

    @Mock
    private MultipartFile multipartFile;

    @Mock
    private UtilisateurService utilisateurService;

    @InjectMocks
    private FichierCVService fichierCVService;

    private FichierCV fichierCV;
    private FichierCVDTO fichierCVDTO;

    private Etudiant etudiant;

    @BeforeEach
    void setUp() throws IOException {
        fichierCV = new FichierCV();
        fichierCV.setId(1L);
        fichierCV.setFilename("test.pdf");
        fichierCV.setData("Test file data".getBytes());
        etudiant = new Etudiant();
        etudiant.setProgramme(Programme.TECHNIQUE_INFORMATIQUE);
        etudiant.setId(1L);
        fichierCV.setEtudiant(etudiant);


        fichierCVDTO = new FichierCVDTO();
        fichierCVDTO.setFilename("test.pdf");
        fichierCVDTO.setFileData(Base64.getEncoder().encodeToString("Test file data".getBytes()));
        fichierCVDTO.setEtudiant_id(1L);
        fichierCV.setStatus(FichierCV.Status.WAITING);



    }

    @Test
    void testSaveFile_Success() throws IOException {
        // Arrange
        when(validator.validate(any(FichierCVDTO.class))).thenReturn(Collections.emptySet());
        when(fileRepository.save(any(FichierCV.class))).thenReturn(fichierCV);
        when(modelMapper.map(any(FichierCV.class), eq(FichierCVDTO.class))).thenReturn(fichierCVDTO);
        when(modelMapper.map(any(FichierCVDTO.class), eq(FichierCV.class))).thenReturn(fichierCV);
        when(etudiantRepository.findById(anyLong())).thenReturn(Optional.of(etudiant));

        when(multipartFile.getBytes()).thenReturn("Test file data".getBytes());

        when(utilisateurService.getUserIdByToken(anyString()))
                .thenReturn(1L);

        // Act
        FichierCVDTO result = fichierCVService.saveFile(multipartFile, "1L");

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

        when(multipartFile.getBytes()).thenReturn("Test file data".getBytes());
        when(multipartFile.getOriginalFilename()).thenReturn("test.pdf");

        // Act & Assert
        assertThrows(ConstraintViolationException.class, () -> {
            fichierCVService.saveFile(multipartFile, "1L");
        });
        verify(fileRepository, never()).save(any(FichierCV.class));
    }

    @Test
    void testSaveFile_IOError() throws IOException {
        // Arrange
        when(multipartFile.getBytes()).thenThrow(new IOException("IO Error"));

        // Act & Assert
        assertThrows(IOException.class, () -> {
            fichierCVService.saveFile(multipartFile, "1L");
        });
        verify(fileRepository, never()).save(any(FichierCV.class));
    }

    @Test
    void testGetWaitingCV_Success() {
        // Act
        when(fileRepository.getFichierCVSByStatusEquals(FichierCV.Status.WAITING, PageRequest.of(0, 10)))
                .thenReturn(Optional.of(new ArrayList<>((Arrays.asList(fichierCV, fichierCV, fichierCV, fichierCV, fichierCV, fichierCV, fichierCV, fichierCV, fichierCV, fichierCV)))));
        when(fileRepository.getFichierCVSByStatusEquals(FichierCV.Status.WAITING, PageRequest.of(1, 10)))
                .thenReturn(Optional.of(new ArrayList<>((Arrays.asList(fichierCV, fichierCV, fichierCV, fichierCV)))));;

        // Assert
        Assertions.assertThat(fichierCVService.getWaitingCv(1).size()).isEqualTo(10);
        Assertions.assertThat(fichierCVService.getWaitingCv(2).size()).isEqualTo(4);
    }
    @Test
    void testGetWaitingCV_PageVide() {
        // Act
        when(fileRepository.getFichierCVSByStatusEquals(FichierCV.Status.WAITING, PageRequest.of(0, 10)))
                .thenReturn(Optional.empty());

        // Assert
        Assertions.assertThat(fichierCVService.getWaitingCv(1).size()).isEqualTo(0);
    }

    @Test
    void testGetFile_Success() {
        // Arrange

        when(fileRepository.findById(1L)).thenReturn(Optional.of(fichierCV));
        when(modelMapper.map(fichierCV, FichierCVDTO.class)).thenReturn(fichierCVDTO);

        // Act
        FichierCVDTO result = fichierCVService.getFile(1L);

        // Assert
        assertNotNull(result, "The returned FichierCVDTO should not be null");
        assertEquals(fichierCVDTO.getFilename(), result.getFilename());
        assertEquals(fichierCVDTO.getFileData(), result.getFileData());
        verify(fileRepository, times(1)).findById(1L);
        verify(modelMapper, times(1)).map(fichierCV, FichierCVDTO.class);
    }

    @Test
    void testGetFile_NotFound() {
        // Arrange
        when(fileRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            fichierCVService.getFile(1L);
        }, "Expected getFile() to throw, but it didn't");

        assertEquals("Fichier non trouvé", exception.getMessage(), "Exception message should match");
        verify(fileRepository, times(1)).findById(1L);
        verify(modelMapper, never()).map(any(FichierCV.class), eq(FichierCVDTO.class));
    }

    @Test
    void testGetCurrentCV_Success() {
        // Arrange



        when(utilisateurService.getUserIdByToken(anyString()))
                .thenReturn(1L);


        when(fileRepository.getFirstByEtudiant_IdAndStatusEqualsOrStatusEqualsOrStatusEquals(etudiant.getId(), FichierCV.Status.ACCEPTED, FichierCV.Status.WAITING, FichierCV.Status.REFUSED))
                .thenReturn(Optional.of(fichierCV));
        when(modelMapper.map(fichierCV, FichierCVDTO.class)).thenReturn(fichierCVDTO);

        // Act
        FichierCVDTO result = fichierCVService.getCurrentCV("1L");

        // Assert
        assertNotNull(result, "The returned FichierCVDTO should not be null");
        assertEquals(fichierCVDTO.getFilename(), result.getFilename());
        assertEquals(fichierCVDTO.getFileData(), result.getFileData());
        verify(fileRepository, times(1)).getFirstByEtudiant_IdAndStatusEqualsOrStatusEqualsOrStatusEquals(etudiant.getId(), FichierCV.Status.ACCEPTED, FichierCV.Status.WAITING, FichierCV.Status.REFUSED);
        verify(modelMapper, times(1)).map(fichierCV, FichierCVDTO.class);
    }

    @Test
    void testGetCurrentCV_NotFound() {
        // Arrange
        when(fileRepository.getFirstByEtudiant_IdAndStatusEqualsOrStatusEqualsOrStatusEquals(etudiant.getId(), FichierCV.Status.ACCEPTED, FichierCV.Status.WAITING, FichierCV.Status.REFUSED))
                .thenReturn(Optional.empty());



        when(utilisateurService.getUserIdByToken(anyString()))
                .thenReturn(1L);


        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            fichierCVService.getCurrentCV("1L");
        }, "Expected getCurrentCV() to throw, but it didn't");

        assertEquals("Fichier non trouvé", exception.getMessage(), "Exception message should match");
        verify(fileRepository, times(1)).getFirstByEtudiant_IdAndStatusEqualsOrStatusEqualsOrStatusEquals(etudiant.getId(), FichierCV.Status.ACCEPTED, FichierCV.Status.WAITING, FichierCV.Status.REFUSED);
        verify(modelMapper, never()).map(any(FichierCV.class), eq(FichierCVDTO.class));
    }
    @Test
    void testDeleteCurrentCV_Success() {
        // Arrange



        when(utilisateurService.getUserIdByToken(anyString()))
                .thenReturn(1L);


        when(fileRepository.getFirstByEtudiant_IdAndStatusEqualsOrStatusEqualsOrStatusEquals(etudiant.getId(), FichierCV.Status.ACCEPTED, FichierCV.Status.WAITING, FichierCV.Status.REFUSED))
                .thenReturn(Optional.of(fichierCV));
        when(fileRepository.save(any(FichierCV.class))).thenReturn(fichierCV);
        when(modelMapper.map(fichierCV, FichierCVDTO.class)).thenReturn(fichierCVDTO);

        // Act
        FichierCVDTO result = fichierCVService.deleteCurrentCV("1L");

        // Assert
        assertNotNull(result, "The returned FichierCVDTO should not be null");

        assertEquals(fichierCVDTO.getFilename(), result.getFilename());
        assertEquals(fichierCVDTO.getFileData(), result.getFileData());

        // Capture the FichierCV passed to save() to verify status change
        ArgumentCaptor<FichierCV> fichierCVCaptor = ArgumentCaptor.forClass(FichierCV.class);
        verify(fileRepository).save(fichierCVCaptor.capture());
        FichierCV savedFichierCV = fichierCVCaptor.getValue();
        assertEquals(FichierCV.Status.DELETED, savedFichierCV.getStatus(), "Status should be set to DELETED");

        verify(fileRepository, times(1)).getFirstByEtudiant_IdAndStatusEqualsOrStatusEqualsOrStatusEquals(etudiant.getId(), FichierCV.Status.ACCEPTED, FichierCV.Status.WAITING, FichierCV.Status.REFUSED);
        verify(fileRepository, times(1)).save(any(FichierCV.class));
        verify(modelMapper, times(1)).map(fichierCV, FichierCVDTO.class);
    }
    @Test
    void testDeleteCurrentCV_NotFound() {
        // Arrange
        when(fileRepository.getFirstByEtudiant_IdAndStatusEqualsOrStatusEqualsOrStatusEquals(etudiant.getId(), FichierCV.Status.ACCEPTED, FichierCV.Status.WAITING, FichierCV.Status.REFUSED))
                .thenReturn(Optional.empty());



        when(utilisateurService.getUserIdByToken(anyString()))
                .thenReturn(1L);


        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            fichierCVService.deleteCurrentCV("1L");
        }, "Expected deleteCurrentCV() to throw, but it didn't");

        assertEquals("Fichier non trouvé", exception.getMessage(), "Exception message should match");
        verify(fileRepository, times(1)).getFirstByEtudiant_IdAndStatusEqualsOrStatusEqualsOrStatusEquals(etudiant.getId(), FichierCV.Status.ACCEPTED, FichierCV.Status.WAITING, FichierCV.Status.REFUSED);
        verify(fileRepository, never()).save(any());
        verify(modelMapper, never()).map(any(FichierCV.class), eq(FichierCVDTO.class));
    }


    @Test
    void testGetAmountOfPage_NumberEndWithZero_Success() {
        // Act
        when(fileRepository.countAllByStatusEquals(FichierCV.Status.WAITING)).thenReturn(30L);

        // Assert
        Assertions.assertThat(fichierCVService.getAmountOfPages()).isEqualTo(3);
    }
    @Test
    void testGetAmountOfPage_NumberNotEndWithZero_Success() {
        // Act
        when(fileRepository.countAllByStatusEquals(FichierCV.Status.WAITING)).thenReturn(43L);

        // Assert
        Assertions.assertThat(fichierCVService.getAmountOfPages()).isEqualTo(5);
    }
    @Test
    void testChangeStatus_Success() throws ChangeSetPersister.NotFoundException {

        // Act
        when(fileRepository.findById(1L)).thenReturn(Optional.of(fichierCV));

        // Assert
        fichierCVService.changeStatus(1L, FichierCV.Status.ACCEPTED, "asd");
    }

    @Test
    void testChangeStatus_CvNotFound() {

        // Act
        when(fileRepository.findById(1L)).thenReturn(Optional.empty());

        // Assert
        assertThrows(ChangeSetPersister.NotFoundException.class, () -> {
            fichierCVService.changeStatus(1L, FichierCV.Status.ACCEPTED, "asd");
        });
    }
}