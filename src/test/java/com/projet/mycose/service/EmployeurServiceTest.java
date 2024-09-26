package com.projet.mycose.service;

import com.projet.mycose.modele.Employeur;
import com.projet.mycose.modele.auth.Role;
import com.projet.mycose.repository.EmployeurRepository;
import com.projet.mycose.service.dto.EmployeurDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

public class EmployeurServiceTest {
    @Test
    public void creationDeCompteAvecSucces() {
        //Arrange
        EmployeurRepository employeurRepositoryMock = Mockito.mock(EmployeurRepository.class);
        PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
        EmployeurService employeurService = new EmployeurService(employeurRepositoryMock, passwordEncoder);

        Employeur employeur = new Employeur(1L, "Karim", "Mihoubi", "438-532-2729", "mihoubi@gmail.com", "Mimi123$", "Couche-Tard");
        when(employeurRepositoryMock.save(any(Employeur.class))).thenReturn(employeur);

        //Act
        EmployeurDTO employeurDTO = employeurService.creationDeCompte("Karim", "Mihoubi", "438-532-2729", "mihoubi@gmail.com", "Mimi123$", "Couche-Tard");

        //Assert
        Assertions.assertEquals(employeurDTO.getId(), 1);
        Assertions.assertEquals(employeurDTO.getPrenom(), "Karim");
        Assertions.assertEquals(employeurDTO.getNom(), "Mihoubi");
        Assertions.assertEquals(employeurDTO.getCourriel(), "mihoubi@gmail.com");
        Assertions.assertEquals(employeurDTO.getNumeroDeTelephone(), "438-532-2729");
        Assertions.assertEquals(employeurDTO.getRole(), Role.EMPLOYEUR);
    }

    @Test
    public void creationDeCompteAvecEchec_CourrielDejaUtilise() {
        //Arrange
        EmployeurRepository employeurRepositoryMock = Mockito.mock(EmployeurRepository.class);
        PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
        EmployeurService employeurService = new EmployeurService(employeurRepositoryMock, passwordEncoder);

        doThrow(new DataIntegrityViolationException("Courriel déjà utilisé"))
                .when(employeurRepositoryMock).save(any(Employeur.class));

        //Act & Assert
        DataIntegrityViolationException exception = Assertions.assertThrows(
                DataIntegrityViolationException.class,
                () -> employeurService.creationDeCompte("Karim", "Mihoubi", "438-532-2729", "mihoubi@gmail.com", "Mimi123$", "Couche-Tard")
        );

        Assertions.assertEquals("Courriel déjà utilisé", exception.getMessage());
    }

    @Test
    public void creationDeCompteAvecEchec_NumeroDeTelephoneInvalide() {
        //Arrange
        EmployeurRepository employeurRepositoryMock = Mockito.mock(EmployeurRepository.class);
        PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
        EmployeurService employeurService = new EmployeurService(employeurRepositoryMock, passwordEncoder);

        String numeroDeTelephoneInvalide = "12345abc";

        //Act & Assert
        Assertions.assertThrows(
                NullPointerException.class,
                () -> employeurService.creationDeCompte("Karim", "Mihoubi", numeroDeTelephoneInvalide, "mihoubi@gmail.com", "Mimi123$", "Couche-Tard")
        );
    }

    @Test
    public void creationDeCompteAvecEchec_CourrielInvalide() {
        //Arrange
        EmployeurRepository employeurRepositoryMock = Mockito.mock(EmployeurRepository.class);
        PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
        EmployeurService employeurService = new EmployeurService(employeurRepositoryMock, passwordEncoder);

        String courrielInvalide = "jeSuisUnEmailQuiNeRespectePasLesConventionsDÉcriture";

        //Act & Assert
        Assertions.assertThrows(
                NullPointerException.class,
                () -> employeurService.creationDeCompte("Karim", "Mihoubi", "450-389-2628", courrielInvalide, "Mimi123$", "Couche-Tard")
        );
    }

    @Test
    public void creationDeCompteAvecEchec_MotDePasseInvalide_PasEncode() {
        //Arrange
        EmployeurRepository employeurRepositoryMock = Mockito.mock(EmployeurRepository.class);
        PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
        EmployeurService employeurService = new EmployeurService(employeurRepositoryMock, passwordEncoder);

        String motDePasseInvalide = "motDePassePasEncodé";

        //Act & Assert
        Assertions.assertThrows(
                NullPointerException.class,
                () -> employeurService.creationDeCompte("Karim", "Mihoubi", "450-389-2628", "mihoubi@gmail.com", motDePasseInvalide, "Couche-Tard")
        );
    }

    @Test
    public void creationDeCompteAvecEchec_MotDePasseInvalide_Null() {
        //Arrange
        EmployeurRepository employeurRepositoryMock = Mockito.mock(EmployeurRepository.class);
        PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
        EmployeurService employeurService = new EmployeurService(employeurRepositoryMock, passwordEncoder);

        String motDePasseInvalide = null;

        //Act & Assert
        Assertions.assertThrows(
                NullPointerException.class,
                () -> employeurService.creationDeCompte("Karim", "Mihoubi", "450-389-2628", "mihoubi@gmail.com", motDePasseInvalide, "Couche-Tard")
        );
    }

    @Test
    public void creationDeCompteAvecEchec_PrenomInvalide() {
        //Arrange
        EmployeurRepository employeurRepositoryMock = Mockito.mock(EmployeurRepository.class);
        PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
        EmployeurService employeurService = new EmployeurService(employeurRepositoryMock, passwordEncoder);

        String prenomInvalide = "3426unPrenomInvalide28382";

        //Act & Assert
        Assertions.assertThrows(
                NullPointerException.class,
                () -> employeurService.creationDeCompte(prenomInvalide, "Mihoubi", "450-389-2628", "mihoubi@gmail.com", "Mimi123$", "Couche-Tard")
        );
    }

    @Test
    public void creationDeCompteAvecEchec_NomInvalide() {
        //Arrange
        EmployeurRepository employeurRepositoryMock = Mockito.mock(EmployeurRepository.class);
        PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
        EmployeurService employeurService = new EmployeurService(employeurRepositoryMock, passwordEncoder);

        String nomInvalide = "3426unNomInvalide28382";

        //Act & Assert
        Assertions.assertThrows(
                NullPointerException.class,
                () -> employeurService.creationDeCompte("Karim", nomInvalide, "450-389-2628", "mihoubi@gmail.com", "Mimi123$", "Couche-Tard")
        );
    }

    @Test
    public void testGetEtudiantByCourriel_EtudiantExiste() {
        // Arrange
        EmployeurRepository employeurRepositoryMock = Mockito.mock(EmployeurRepository.class);
        PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
        EmployeurService employeurService = new EmployeurService(employeurRepositoryMock, passwordEncoder);

        Employeur employeur = new Employeur(1L, "Karim", "Mihoubi", "438-532-2729", "mihoubi@gmail.com", "Mimi123$", "Couche-Tard");
        when(employeurRepositoryMock.findEmployeurByCourriel("mihoubi@gmail.com")).thenReturn(Optional.of(employeur));

        // Act
        EmployeurDTO employeurDTO = employeurService.getEmployeurByCourriel("mihoubi@gmail.com");

        // Assert
        Assertions.assertNotNull(employeurDTO);
        Assertions.assertEquals(employeurDTO.getCourriel(), "mihoubi@gmail.com");
    }

    @Test
    public void testGetEtudiantByCourriel_EtudiantInexistant() {
        // Arrange
        EmployeurRepository employeurRepositoryMock = Mockito.mock(EmployeurRepository.class);
        PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
        EmployeurService employeurService = new EmployeurService(employeurRepositoryMock, passwordEncoder);

        when(employeurRepositoryMock.findEmployeurByCourriel("inexistant@gmail.com")).thenReturn(Optional.empty());

        // Act
        EmployeurDTO employeurDTO = employeurService.getEmployeurByCourriel("inexistant@gmail.com");

        // Assert
        Assertions.assertNull(employeurDTO);
    }


    @Test
    public void testGetEtudiantByTelephone_EtudiantExiste() {
        // Arrange
        EmployeurRepository employeurRepositoryMock = Mockito.mock(EmployeurRepository.class);
        PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
        EmployeurService employeurService = new EmployeurService(employeurRepositoryMock, passwordEncoder);

        Employeur employeur = new Employeur(1L, "Karim", "Mihoubi", "438-532-2729", "mihoubi@gmail.com", "Mimi123$", "Couche-Tard");
        when(employeurRepositoryMock.findEmployeurByNumeroDeTelephone("438-532-2729")).thenReturn(Optional.of(employeur));

        // Act
        EmployeurDTO employeurDTO = employeurService.getEmployeurByTelephone("438-532-2729");

        // Assert
        Assertions.assertNotNull(employeurDTO);
        Assertions.assertEquals(employeurDTO.getNumeroDeTelephone(), "438-532-2729");
    }

    @Test
    public void testGetEtudiantByTelephone_EtudiantInexistant() {
        // Arrange
        EmployeurRepository employeurRepositoryMock = Mockito.mock(EmployeurRepository.class);
        PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
        EmployeurService employeurService = new EmployeurService(employeurRepositoryMock, passwordEncoder);

        when(employeurRepositoryMock.findEmployeurByNumeroDeTelephone("438-532-2729")).thenReturn(Optional.empty());

        // Act
        EmployeurDTO employeurDTO = employeurService.getEmployeurByTelephone("438-532-2729");

        // Assert
        Assertions.assertNull(employeurDTO);
    }

    @Test
    public void testCredentialsDejaPris_CourrielPris() {
        // Arrange
        EmployeurRepository employeurRepositoryMock = Mockito.mock(EmployeurRepository.class);
        PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
        EmployeurService employeurService = new EmployeurService(employeurRepositoryMock, passwordEncoder);

        Employeur employeur = new Employeur(1L, "Karim", "Mihoubi", "438-532-2729", "mihoubi@gmail.com", "Mimi123$", "Couche-Tard");
        when(employeurRepositoryMock.findEmployeurByCourriel("mihoubi@gmail.com")).thenReturn(Optional.of(employeur));
        when(employeurRepositoryMock.findEmployeurByNumeroDeTelephone(any())).thenReturn(Optional.empty());

        // Act
        boolean result = employeurService.credentialsDejaPris("mihoubi@gmail.com", "000-000-0000");

        // Assert
        Assertions.assertTrue(result);
    }

    @Test
    public void testCredentialsDejaPris_TelephonePris() {
        // Arrange
        EmployeurRepository employeurRepositoryMock = Mockito.mock(EmployeurRepository.class);
        PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
        EmployeurService employeurService = new EmployeurService(employeurRepositoryMock, passwordEncoder);

        Employeur employeur = new Employeur(1L, "Karim", "Mihoubi", "438-532-2729", "mihoubi@gmail.com", "Mimi123$", "Couche-Tard");
        when(employeurRepositoryMock.findEmployeurByCourriel(any())).thenReturn(Optional.empty());
        when(employeurRepositoryMock.findEmployeurByNumeroDeTelephone("438-532-2729")).thenReturn(Optional.of(employeur));

        // Act
        boolean result = employeurService.credentialsDejaPris("email@inexistant.com", "438-532-2729");

        // Assert
        Assertions.assertTrue(result);
    }

    @Test
    public void testCredentialsDejaPris_AucunPris() {
        // Arrange
        EmployeurRepository employeurRepositoryMock = Mockito.mock(EmployeurRepository.class);
        PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
        EmployeurService employeurService = new EmployeurService(employeurRepositoryMock, passwordEncoder);

        when(employeurRepositoryMock.findEmployeurByCourriel(any())).thenReturn(Optional.empty());
        when(employeurRepositoryMock.findEmployeurByNumeroDeTelephone(any())).thenReturn(Optional.empty());

        // Act
        boolean result = employeurService.credentialsDejaPris("email@inexistant.com", "000-000-0000");

        // Assert
        Assertions.assertFalse(result);
    }
}
