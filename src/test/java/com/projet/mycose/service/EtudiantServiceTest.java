package com.projet.mycose.service;

import com.projet.mycose.modele.Etudiant;
import com.projet.mycose.modele.auth.Role;
import com.projet.mycose.repository.EtudiantRepository;
import com.projet.mycose.service.dto.EtudiantDTO;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

public class EtudiantServiceTest {
    @Test
    public void creationDeCompteAvecSucces() {
        //Arrange
        EtudiantRepository etudiantRepositoryMock = Mockito.mock(EtudiantRepository.class);
        PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
        EtudiantService etudiantService = new EtudiantService(etudiantRepositoryMock, passwordEncoder);

        Etudiant etudiant = new Etudiant(1L, "Karim", "Mihoubi", "438-532-2729", "mihoubi@gmail.com", "Mimi123$", "Technique de l'informatique");
        when(etudiantRepositoryMock.save(any(Etudiant.class))).thenReturn(etudiant);

        //Act
        EtudiantDTO etudiantDTO = etudiantService.creationDeCompte("Karim", "Mihoubi", "438-532-2729", "mihoubi@gmail.com", "Mimi123$", "Technique de l'informatique");

        //Assert
        Assertions.assertEquals(etudiantDTO.getId(), 1);
        Assertions.assertEquals(etudiantDTO.getPrenom(), "Karim");
        Assertions.assertEquals(etudiantDTO.getNom(), "Mihoubi");
        Assertions.assertEquals(etudiantDTO.getCourriel(), "mihoubi@gmail.com");
        Assertions.assertEquals(etudiantDTO.getNumeroDeTelephone(), "438-532-2729");
        Assertions.assertEquals(etudiantDTO.getProgramme(), "Technique de l'informatique");
        Assertions.assertEquals(etudiantDTO.getRole(), Role.ETUDIANT);
    }

    @Test
    public void creationDeCompteAvecEchec_CourrielDejaUtilise() {
        //Arrange
        EtudiantRepository etudiantRepositoryMock = Mockito.mock(EtudiantRepository.class);
        PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
        EtudiantService etudiantService = new EtudiantService(etudiantRepositoryMock, passwordEncoder);

        doThrow(new DataIntegrityViolationException("Courriel déjà utilisé"))
                .when(etudiantRepositoryMock).save(any(Etudiant.class));

        //Act & Assert
        DataIntegrityViolationException exception = Assertions.assertThrows(
                DataIntegrityViolationException.class,
                () -> etudiantService.creationDeCompte("Karim", "Mihoubi", "438-532-2729", "mihoubi@gmail.com", "Mimi123$", "Technique de l'informatique")
        );

        Assertions.assertEquals("Courriel déjà utilisé", exception.getMessage());
    }

    @Test
    public void creationDeCompteAvecEchec_NumeroDeTelephoneInvalide() {
        //Arrange
        EtudiantRepository etudiantRepositoryMock = Mockito.mock(EtudiantRepository.class);
        PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
        EtudiantService etudiantService = new EtudiantService(etudiantRepositoryMock, passwordEncoder);

        String numeroDeTelephoneInvalide = "12345abc";

        //Act & Assert
        Assertions.assertThrows(
                NullPointerException.class,
                () -> etudiantService.creationDeCompte("Karim", "Mihoubi", numeroDeTelephoneInvalide, "mihoubi@gmail.com", "Mimi123$", "Technique de l'informatique")
        );
    }

    @Test
    public void creationDeCompteAvecEchec_CourrielInvalide() {
        //Arrange
        EtudiantRepository etudiantRepositoryMock = Mockito.mock(EtudiantRepository.class);
        PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
        EtudiantService etudiantService = new EtudiantService(etudiantRepositoryMock, passwordEncoder);

        String courrielInvalide = "jeSuisUnEmailQuiNeRespectePasLesConventionsDÉcriture";

        //Act & Assert
        Assertions.assertThrows(
                NullPointerException.class,
                () -> etudiantService.creationDeCompte("Karim", "Mihoubi", "450-389-2628", courrielInvalide, "Mimi123$", "Technique de l'informatique")
        );
    }

    @Test
    public void creationDeCompteAvecEchec_MotDePasseInvalide_PasEncode() {
        //Arrange
        EtudiantRepository etudiantRepositoryMock = Mockito.mock(EtudiantRepository.class);
        PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
        EtudiantService etudiantService = new EtudiantService(etudiantRepositoryMock, passwordEncoder);

        String motDePasseInvalide = "motDePassePasEncodé";

        //Act & Assert
        Assertions.assertThrows(
                NullPointerException.class,
                () -> etudiantService.creationDeCompte("Karim", "Mihoubi", "450-389-2628", "mihoubi@gmail.com", motDePasseInvalide, "Technique de l'informatique")
        );
    }

    @Test
    public void creationDeCompteAvecEchec_MotDePasseInvalide_Null() {
        //Arrange
        EtudiantRepository etudiantRepositoryMock = Mockito.mock(EtudiantRepository.class);
        PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
        EtudiantService etudiantService = new EtudiantService(etudiantRepositoryMock, passwordEncoder);

        String motDePasseInvalide = null;

        //Act & Assert
        Assertions.assertThrows(
                NullPointerException.class,
                () -> etudiantService.creationDeCompte("Karim", "Mihoubi", "450-389-2628", "mihoubi@gmail.com", motDePasseInvalide, "Technique de l'informatique")
        );
    }

    @Test
    public void creationDeCompteAvecEchec_PrenomInvalide() {
        //Arrange
        EtudiantRepository etudiantRepositoryMock = Mockito.mock(EtudiantRepository.class);
        PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
        EtudiantService etudiantService = new EtudiantService(etudiantRepositoryMock, passwordEncoder);

        String prenomInvalide = "3426unPrenomInvalide28382";

        //Act & Assert
        Assertions.assertThrows(
                NullPointerException.class,
                () -> etudiantService.creationDeCompte(prenomInvalide, "Mihoubi", "450-389-2628", "mihoubi@gmail.com", "Mimi123$", "Technique de l'informatique")
        );
    }

    @Test
    public void creationDeCompteAvecEchec_NomInvalide() {
        //Arrange
        EtudiantRepository etudiantRepositoryMock = Mockito.mock(EtudiantRepository.class);
        PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
        EtudiantService etudiantService = new EtudiantService(etudiantRepositoryMock, passwordEncoder);

        String nomInvalide = "3426unNomInvalide28382";

        //Act & Assert
        Assertions.assertThrows(
                NullPointerException.class,
                () -> etudiantService.creationDeCompte("Karim", nomInvalide, "450-389-2628", "mihoubi@gmail.com", "Mimi123$", "Technique de l'informatique")
        );
    }

    @Test
    public void testGetEtudiantByCourriel_EtudiantExiste() {
        // Arrange
        EtudiantRepository etudiantRepositoryMock = Mockito.mock(EtudiantRepository.class);
        PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
        EtudiantService etudiantService = new EtudiantService(etudiantRepositoryMock, passwordEncoder);

        Etudiant etudiant = new Etudiant(1L, "Karim", "Mihoubi", "438-532-2729", "mihoubi@gmail.com", "Mimi123$", "Technique de l'informatique");
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
        PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
        EtudiantService etudiantService = new EtudiantService(etudiantRepositoryMock, passwordEncoder);

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
        PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
        EtudiantService etudiantService = new EtudiantService(etudiantRepositoryMock, passwordEncoder);

        Etudiant etudiant = new Etudiant(1L, "Karim", "Mihoubi", "438-532-2729", "mihoubi@gmail.com", "Mimi123$", "Technique de l'informatique");
        when(etudiantRepositoryMock.findEtudiantByNumeroDeTelephone("438-532-2729")).thenReturn(Optional.of(etudiant));

        // Act
        EtudiantDTO etudiantDTO = etudiantService.getEtudiantByTelephone("438-532-2729");

        // Assert
        Assertions.assertNotNull(etudiantDTO);
        Assertions.assertEquals(etudiantDTO.getNumeroDeTelephone(), "438-532-2729");
    }

    @Test
    public void testGetEtudiantByTelephone_EtudiantInexistant() {
        // Arrange
        EtudiantRepository etudiantRepositoryMock = Mockito.mock(EtudiantRepository.class);
        PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
        EtudiantService etudiantService = new EtudiantService(etudiantRepositoryMock, passwordEncoder);

        when(etudiantRepositoryMock.findEtudiantByNumeroDeTelephone("438-532-2729")).thenReturn(Optional.empty());

        // Act
        EtudiantDTO etudiantDTO = etudiantService.getEtudiantByTelephone("438-532-2729");

        // Assert
        Assertions.assertNull(etudiantDTO);
    }

    @Test
    public void testCredentialsDejaPris_CourrielPris() {
        // Arrange
        EtudiantRepository etudiantRepositoryMock = Mockito.mock(EtudiantRepository.class);
        PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
        EtudiantService etudiantService = new EtudiantService(etudiantRepositoryMock, passwordEncoder);

        Etudiant etudiant = new Etudiant(1L, "Karim", "Mihoubi", "438-532-2729", "mihoubi@gmail.com", "Mimi123$", "Technique de l'informatique");
        when(etudiantRepositoryMock.findEtudiantByCourriel("mihoubi@gmail.com")).thenReturn(Optional.of(etudiant));
        when(etudiantRepositoryMock.findEtudiantByNumeroDeTelephone(any())).thenReturn(Optional.empty());

        // Act
        boolean result = etudiantService.credentialsDejaPris("mihoubi@gmail.com", "000-000-0000");

        // Assert
        Assertions.assertTrue(result);
    }

    @Test
    public void testCredentialsDejaPris_TelephonePris() {
        // Arrange
        EtudiantRepository etudiantRepositoryMock = Mockito.mock(EtudiantRepository.class);
        PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
        EtudiantService etudiantService = new EtudiantService(etudiantRepositoryMock, passwordEncoder);

        Etudiant etudiant = new Etudiant(1L, "Karim", "Mihoubi", "438-532-2729", "mihoubi@gmail.com", "Mimi123$", "Technique de l'informatique");
        when(etudiantRepositoryMock.findEtudiantByCourriel(any())).thenReturn(Optional.empty());
        when(etudiantRepositoryMock.findEtudiantByNumeroDeTelephone("438-532-2729")).thenReturn(Optional.of(etudiant));

        // Act
        boolean result = etudiantService.credentialsDejaPris("email@inexistant.com", "438-532-2729");

        // Assert
        Assertions.assertTrue(result);
    }

    @Test
    public void testCredentialsDejaPris_AucunPris() {
        // Arrange
        EtudiantRepository etudiantRepositoryMock = Mockito.mock(EtudiantRepository.class);
        PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
        EtudiantService etudiantService = new EtudiantService(etudiantRepositoryMock, passwordEncoder);

        when(etudiantRepositoryMock.findEtudiantByCourriel(any())).thenReturn(Optional.empty());
        when(etudiantRepositoryMock.findEtudiantByNumeroDeTelephone(any())).thenReturn(Optional.empty());

        // Act
        boolean result = etudiantService.credentialsDejaPris("email@inexistant.com", "000-000-0000");

        // Assert
        Assertions.assertFalse(result);
    }
}
