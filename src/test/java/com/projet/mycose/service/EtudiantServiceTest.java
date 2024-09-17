package com.projet.mycose.service;

import com.projet.mycose.modele.Etudiant;
import com.projet.mycose.modele.auth.Role;
import com.projet.mycose.repository.EtudiantRepository;
import com.projet.mycose.service.dto.EtudiantDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

public class EtudiantServiceTest {
    @Test
    public void creationDeCompteAvecSucces() {
        //Arrange
        EtudiantRepository etudiantRepositoryMock = Mockito.mock(EtudiantRepository.class);
        PasswordEncoder passwordEncoderMock = Mockito.mock(PasswordEncoder.class);
        EtudiantService etudiantService = new EtudiantService(etudiantRepositoryMock, passwordEncoderMock);

        Etudiant etudiant = new Etudiant(1L, "Karim", "Mihoubi", "438-532-2729", "mihoubi@gmail.com", "Mimi828");
        when(etudiantRepositoryMock.save(any(Etudiant.class))).thenReturn(etudiant);

        //Act
        EtudiantDTO etudiantDTO = etudiantService.creationDeCompte("Karim", "Mihoubi", "4385322729", "mihoubi@gmail.com", "Mimi828");

        //Assert
        Assertions.assertEquals(etudiantDTO.getId(), 1);
        Assertions.assertEquals(etudiantDTO.getPrenom(), "Karim");
        Assertions.assertEquals(etudiantDTO.getNom(), "Mihoubi");
        Assertions.assertEquals(etudiantDTO.getCourriel(), "mihoubi@gmail.com");
        Assertions.assertEquals(etudiantDTO.getNumeroDeTelephone(), "438-532-2729");
        Assertions.assertEquals(etudiantDTO.getRole(), Role.ETUDIANT);
    }

    @Test
    public void creationDeCompteAvecEchec_CourrielDejaUtilise() {
        //Arrange
        EtudiantRepository etudiantRepositoryMock = Mockito.mock(EtudiantRepository.class);
        PasswordEncoder passwordEncoderMock = Mockito.mock(PasswordEncoder.class);
        EtudiantService etudiantService = new EtudiantService(etudiantRepositoryMock, passwordEncoderMock);

        doThrow(new DataIntegrityViolationException("Courriel déjà utilisé"))
                .when(etudiantRepositoryMock).save(any(Etudiant.class));

        //Act & Assert
        DataIntegrityViolationException exception = Assertions.assertThrows(
                DataIntegrityViolationException.class,
                () -> etudiantService.creationDeCompte("Karim", "Mihoubi", "4385322729", "mihoubi@gmail.com", "Mimi828")
        );

        Assertions.assertEquals("Courriel déjà utilisé", exception.getMessage());
    }

    @Test
    public void creationDeCompteAvecEchec_NumeroDeTelephoneInvalide() {
        //Arrange
        EtudiantRepository etudiantRepositoryMock = Mockito.mock(EtudiantRepository.class);
        PasswordEncoder passwordEncoderMock = Mockito.mock(PasswordEncoder.class);
        EtudiantService etudiantService = new EtudiantService(etudiantRepositoryMock, passwordEncoderMock);

        String numeroDeTelephoneInvalide = "12345abc";

        //Act & Assert
        IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> etudiantService.creationDeCompte("Karim", "Mihoubi", numeroDeTelephoneInvalide, "mihoubi@gmail.com", "Mimi828")
        );

        Assertions.assertEquals("Le numéro de téléphone est invalide", exception.getMessage());
    }

    @Test
    public void creationDeCompteAvecEchec_CourrielInvalide() {
        //Arrange
        EtudiantRepository etudiantRepositoryMock = Mockito.mock(EtudiantRepository.class);
        PasswordEncoder passwordEncoderMock = Mockito.mock(PasswordEncoder.class);
        EtudiantService etudiantService = new EtudiantService(etudiantRepositoryMock, passwordEncoderMock);

        String courrielInvalide = "jeSuisUnEmailQuiNeRespectePasLesConventionsDÉcriture";

        //Act & Assert
        IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> etudiantService.creationDeCompte("Karim", "Mihoubi", "4503892628", courrielInvalide, "Mimi828")
        );

        Assertions.assertEquals("Le courriel est invalide", exception.getMessage());
    }

    @Test
    public void testGetEtudiantByCourriel_EtudiantExiste() {
        // Arrange
        EtudiantRepository etudiantRepositoryMock = Mockito.mock(EtudiantRepository.class);
        PasswordEncoder passwordEncoderMock = Mockito.mock(PasswordEncoder.class);
        EtudiantService etudiantService = new EtudiantService(etudiantRepositoryMock, passwordEncoderMock);

        Etudiant etudiant = new Etudiant(1L, "Karim", "Mihoubi", "4385322729", "mihoubi@gmail.com", "Mimi828");
        when(etudiantRepositoryMock.findEtudiantByCourriel("mihoubi@gmail.com")).thenReturn(Optional.of(etudiant));

        // Act
        EtudiantDTO etudiantDTO = etudiantService.getEtudiantByCourriel("mihoubi@gmail.com");

        // Assert
        Assertions.assertNotNull(etudiantDTO);
        Assertions.assertEquals(etudiantDTO.getCourriel(), "mihoubi@gmail.com");
    }

    @Test
    public void testGetEtudiantByCourriel_EtudiantInexistant() {
        // Arrange
        EtudiantRepository etudiantRepositoryMock = Mockito.mock(EtudiantRepository.class);
        PasswordEncoder passwordEncoderMock = Mockito.mock(PasswordEncoder.class);
        EtudiantService etudiantService = new EtudiantService(etudiantRepositoryMock, passwordEncoderMock);

        when(etudiantRepositoryMock.findEtudiantByCourriel("inexistant@gmail.com")).thenReturn(Optional.empty());

        // Act
        EtudiantDTO etudiantDTO = etudiantService.getEtudiantByCourriel("inexistant@gmail.com");

        // Assert
        Assertions.assertNull(etudiantDTO);
    }


    @Test
    public void testGetEtudiantByTelephone_EtudiantExiste() {
        // Arrange
        EtudiantRepository etudiantRepositoryMock = Mockito.mock(EtudiantRepository.class);
        PasswordEncoder passwordEncoderMock = Mockito.mock(PasswordEncoder.class);
        EtudiantService etudiantService = new EtudiantService(etudiantRepositoryMock, passwordEncoderMock);

        Etudiant etudiant = new Etudiant(1L, "Karim", "Mihoubi", "4385322729", "mihoubi@gmail.com", "Mimi828");
        when(etudiantRepositoryMock.findEtudiantByNumeroDeTelephone("4385322729")).thenReturn(Optional.of(etudiant));

        // Act
        EtudiantDTO etudiantDTO = etudiantService.getEtudiantByTelephone("4385322729");

        // Assert
        Assertions.assertNotNull(etudiantDTO);
        Assertions.assertEquals(etudiantDTO.getNumeroDeTelephone(), "4385322729");
    }

    @Test
    public void testGetEtudiantByTelephone_EtudiantInexistant() {
        // Arrange
        EtudiantRepository etudiantRepositoryMock = Mockito.mock(EtudiantRepository.class);
        PasswordEncoder passwordEncoderMock = Mockito.mock(PasswordEncoder.class);
        EtudiantService etudiantService = new EtudiantService(etudiantRepositoryMock, passwordEncoderMock);

        when(etudiantRepositoryMock.findEtudiantByNumeroDeTelephone("4385322729")).thenReturn(Optional.empty());

        // Act
        EtudiantDTO etudiantDTO = etudiantService.getEtudiantByTelephone("4385322729");

        // Assert
        Assertions.assertNull(etudiantDTO);
    }

    @Test
    public void testCredentialsDejaPris_CourrielPris() {
        // Arrange
        EtudiantRepository etudiantRepositoryMock = Mockito.mock(EtudiantRepository.class);
        EtudiantService etudiantService = new EtudiantService(etudiantRepositoryMock, null);

        Etudiant etudiant = new Etudiant(1L, "Karim", "Mihoubi", "4385322729", "mihoubi@gmail.com", "Mimi828");
        when(etudiantRepositoryMock.findEtudiantByCourriel("mihoubi@gmail.com")).thenReturn(Optional.of(etudiant));
        when(etudiantRepositoryMock.findEtudiantByNumeroDeTelephone(any())).thenReturn(Optional.empty());

        // Act
        boolean result = etudiantService.credentialsDejaPris("mihoubi@gmail.com", "0000000000");

        // Assert
        Assertions.assertTrue(result);
    }

    @Test
    public void testCredentialsDejaPris_TelephonePris() {
        // Arrange
        EtudiantRepository etudiantRepositoryMock = Mockito.mock(EtudiantRepository.class);
        EtudiantService etudiantService = new EtudiantService(etudiantRepositoryMock, null);

        Etudiant etudiant = new Etudiant(1L, "Karim", "Mihoubi", "4385322729", "mihoubi@gmail.com", "Mimi828");
        when(etudiantRepositoryMock.findEtudiantByCourriel(any())).thenReturn(Optional.empty());
        when(etudiantRepositoryMock.findEtudiantByNumeroDeTelephone("4385322729")).thenReturn(Optional.of(etudiant));

        // Act
        boolean result = etudiantService.credentialsDejaPris("email@inexistant.com", "4385322729");

        // Assert
        Assertions.assertTrue(result);
    }

    @Test
    public void testCredentialsDejaPris_AucunPris() {
        // Arrange
        EtudiantRepository etudiantRepositoryMock = Mockito.mock(EtudiantRepository.class);
        EtudiantService etudiantService = new EtudiantService(etudiantRepositoryMock, null);

        when(etudiantRepositoryMock.findEtudiantByCourriel(any())).thenReturn(Optional.empty());
        when(etudiantRepositoryMock.findEtudiantByNumeroDeTelephone(any())).thenReturn(Optional.empty());

        // Act
        boolean result = etudiantService.credentialsDejaPris("email@inexistant.com", "0000000000");

        // Assert
        Assertions.assertFalse(result);
    }
}
