package com.projet.mycose.service;

import com.projet.mycose.modele.Enseignant;
import com.projet.mycose.modele.auth.Role;
import com.projet.mycose.repository.EnseignantRepository;
import com.projet.mycose.service.dto.EnseignantDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

public class EnseignantServiceTest {
    @Test
    public void creationDeCompteAvecSucces() {
        //Arrange
        EnseignantRepository enseignantRepositoryMock = Mockito.mock(EnseignantRepository.class);
        PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
        EnseignantService enseignantService = new EnseignantService(enseignantRepositoryMock, passwordEncoder);

        Enseignant enseignant = new Enseignant(1L, "Karim", "Mihoubi", "438-532-2729", "mihoubi@gmail.com", "Mimi123$");
        when(enseignantRepositoryMock.save(any(Enseignant.class))).thenReturn(enseignant);

        //Act
        EnseignantDTO enseignantDTO = enseignantService.creationDeCompte("Karim", "Mihoubi", "438-532-2729", "mihoubi@gmail.com", "Mimi123$");

        //Assert
        Assertions.assertEquals(enseignantDTO.getId(), 1);
        Assertions.assertEquals(enseignantDTO.getPrenom(), "Karim");
        Assertions.assertEquals(enseignantDTO.getNom(), "Mihoubi");
        Assertions.assertEquals(enseignantDTO.getCourriel(), "mihoubi@gmail.com");
        Assertions.assertEquals(enseignantDTO.getNumeroDeTelephone(), "438-532-2729");
        Assertions.assertEquals(enseignantDTO.getRole(), Role.ENSEIGNANT);
    }

    @Test
    public void creationDeCompteAvecEchec_CourrielDejaUtilise() {
        //Arrange
        EnseignantRepository enseignantRepositoryMock = Mockito.mock(EnseignantRepository.class);
        PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
        EnseignantService enseignantService = new EnseignantService(enseignantRepositoryMock, passwordEncoder);

        doThrow(new DataIntegrityViolationException("Courriel déjà utilisé"))
                .when(enseignantRepositoryMock).save(any(Enseignant.class));

        //Act & Assert
        DataIntegrityViolationException exception = Assertions.assertThrows(
                DataIntegrityViolationException.class,
                () -> enseignantService.creationDeCompte("Karim", "Mihoubi", "438-532-2729", "mihoubi@gmail.com", "Mimi123$")
        );

        Assertions.assertEquals("Courriel déjà utilisé", exception.getMessage());
    }

    @Test
    public void creationDeCompteAvecEchec_NumeroDeTelephoneInvalide() {
        //Arrange
        EnseignantRepository enseignantRepositoryMock = Mockito.mock(EnseignantRepository.class);
        PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
        EnseignantService enseignantService = new EnseignantService(enseignantRepositoryMock, passwordEncoder);

        String numeroDeTelephoneInvalide = "12345abc";

        //Act & Assert
        Assertions.assertThrows(
                NullPointerException.class,
                () -> enseignantService.creationDeCompte("Karim", "Mihoubi", numeroDeTelephoneInvalide, "mihoubi@gmail.com", "Mimi123$")
        );
    }

    @Test
    public void creationDeCompteAvecEchec_CourrielInvalide() {
        //Arrange
        EnseignantRepository enseignantRepositoryMock = Mockito.mock(EnseignantRepository.class);
        PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
        EnseignantService enseignantService = new EnseignantService(enseignantRepositoryMock, passwordEncoder);

        String courrielInvalide = "jeSuisUnEmailQuiNeRespectePasLesConventionsDÉcriture";

        //Act & Assert
        Assertions.assertThrows(
                NullPointerException.class,
                () -> enseignantService.creationDeCompte("Karim", "Mihoubi", "450-389-2628", courrielInvalide, "Mimi123$")
        );
    }

    @Test
    public void creationDeCompteAvecEchec_MotDePasseInvalide_PasEncode() {
        //Arrange
        EnseignantRepository enseignantRepositoryMock = Mockito.mock(EnseignantRepository.class);
        PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
        EnseignantService enseignantService = new EnseignantService(enseignantRepositoryMock, passwordEncoder);

        String motDePasseInvalide = "motDePassePasEncodé";

        //Act & Assert
        Assertions.assertThrows(
                NullPointerException.class,
                () -> enseignantService.creationDeCompte("Karim", "Mihoubi", "450-389-2628", "mihoubi@gmail.com", motDePasseInvalide)
        );
    }

    @Test
    public void creationDeCompteAvecEchec_MotDePasseInvalide_Null() {
        //Arrange
        EnseignantRepository enseignantRepositoryMock = Mockito.mock(EnseignantRepository.class);
        PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
        EnseignantService enseignantService = new EnseignantService(enseignantRepositoryMock, passwordEncoder);

        String motDePasseInvalide = null;

        //Act & Assert
        Assertions.assertThrows(
                NullPointerException.class,
                () -> enseignantService.creationDeCompte("Karim", "Mihoubi", "450-389-2628", "mihoubi@gmail.com", motDePasseInvalide)
        );
    }

    @Test
    public void creationDeCompteAvecEchec_PrenomInvalide() {
        //Arrange
        EnseignantRepository enseignantRepositoryMock = Mockito.mock(EnseignantRepository.class);
        PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
        EnseignantService enseignantService = new EnseignantService(enseignantRepositoryMock, passwordEncoder);

        String prenomInvalide = "3426unPrenomInvalide28382";

        //Act & Assert
        Assertions.assertThrows(
                NullPointerException.class,
                () -> enseignantService.creationDeCompte(prenomInvalide, "Mihoubi", "450-389-2628", "mihoubi@gmail.com", "Mimi123$")
        );
    }

    @Test
    public void creationDeCompteAvecEchec_NomInvalide() {
        //Arrange
        EnseignantRepository enseignantRepositoryMock = Mockito.mock(EnseignantRepository.class);
        PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
        EnseignantService enseignantService = new EnseignantService(enseignantRepositoryMock, passwordEncoder);

        String nomInvalide = "3426unNomInvalide28382";

        //Act & Assert
        Assertions.assertThrows(
                NullPointerException.class,
                () -> enseignantService.creationDeCompte("Karim", nomInvalide, "450-389-2628", "mihoubi@gmail.com", "Mimi123$")
        );
    }

    @Test
    public void testGetEtudiantByCourriel_EtudiantExiste() {
        // Arrange
        EnseignantRepository enseignantRepositoryMock = Mockito.mock(EnseignantRepository.class);
        PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
        EnseignantService enseignantService = new EnseignantService(enseignantRepositoryMock, passwordEncoder);

        Enseignant enseignant = new Enseignant(1L, "Karim", "Mihoubi", "438-532-2729", "mihoubi@gmail.com", "Mimi123$");
        when(enseignantRepositoryMock.findEnseignantByCourriel("mihoubi@gmail.com")).thenReturn(Optional.of(enseignant));

        // Act
        EnseignantDTO enseignantDTO = enseignantService.getEnseignantByCourriel("mihoubi@gmail.com");

        // Assert
        Assertions.assertNotNull(enseignantDTO);
        Assertions.assertEquals(enseignantDTO.getCourriel(), "mihoubi@gmail.com");
    }

    @Test
    public void testGetEtudiantByCourriel_EtudiantInexistant() {
        // Arrange
        EnseignantRepository enseignantRepositoryMock = Mockito.mock(EnseignantRepository.class);
        PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
        EnseignantService enseignantService = new EnseignantService(enseignantRepositoryMock, passwordEncoder);

        when(enseignantRepositoryMock.findEnseignantByCourriel("inexistant@gmail.com")).thenReturn(Optional.empty());

        // Act
        EnseignantDTO enseignantDTO = enseignantService.getEnseignantByCourriel("inexistant@gmail.com");

        // Assert
        Assertions.assertNull(enseignantDTO);
    }


    @Test
    public void testGetEtudiantByTelephone_EtudiantExiste() {
        // Arrange
        EnseignantRepository enseignantRepositoryMock = Mockito.mock(EnseignantRepository.class);
        PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
        EnseignantService enseignantService = new EnseignantService(enseignantRepositoryMock, passwordEncoder);

        Enseignant enseignant = new Enseignant(1L, "Karim", "Mihoubi", "438-532-2729", "mihoubi@gmail.com", "Mimi123$");
        when(enseignantRepositoryMock.findEnseignantByNumeroDeTelephone("438-532-2729")).thenReturn(Optional.of(enseignant));

        // Act
        EnseignantDTO enseignantDTO = enseignantService.getEnseignantByTelephone("438-532-2729");

        // Assert
        Assertions.assertNotNull(enseignantDTO);
        Assertions.assertEquals(enseignantDTO.getNumeroDeTelephone(), "438-532-2729");
    }

    @Test
    public void testGetEtudiantByTelephone_EtudiantInexistant() {
        // Arrange
        EnseignantRepository enseignantRepositoryMock = Mockito.mock(EnseignantRepository.class);
        PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
        EnseignantService enseignantService = new EnseignantService(enseignantRepositoryMock, passwordEncoder);

        when(enseignantRepositoryMock.findEnseignantByNumeroDeTelephone("438-532-2729")).thenReturn(Optional.empty());

        // Act
        EnseignantDTO enseignantDTO = enseignantService.getEnseignantByTelephone("438-532-2729");

        // Assert
        Assertions.assertNull(enseignantDTO);
    }

    @Test
    public void testCredentialsDejaPris_CourrielPris() {
        // Arrange
        EnseignantRepository enseignantRepositoryMock = Mockito.mock(EnseignantRepository.class);
        PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
        EnseignantService enseignantService = new EnseignantService(enseignantRepositoryMock, passwordEncoder);

        Enseignant enseignant = new Enseignant(1L, "Karim", "Mihoubi", "438-532-2729", "mihoubi@gmail.com", "Mimi123$");
        when(enseignantRepositoryMock.findEnseignantByCourriel("mihoubi@gmail.com")).thenReturn(Optional.of(enseignant));
        when(enseignantRepositoryMock.findEnseignantByNumeroDeTelephone(any())).thenReturn(Optional.empty());

        // Act
        boolean result = enseignantService.credentialsDejaPris("mihoubi@gmail.com", "000-000-0000");

        // Assert
        Assertions.assertTrue(result);
    }

    @Test
    public void testCredentialsDejaPris_TelephonePris() {
        // Arrange
        EnseignantRepository enseignantRepositoryMock = Mockito.mock(EnseignantRepository.class);
        PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
        EnseignantService enseignantService = new EnseignantService(enseignantRepositoryMock, passwordEncoder);

        Enseignant enseignant = new Enseignant(1L, "Karim", "Mihoubi", "438-532-2729", "mihoubi@gmail.com", "Mimi123$");
        when(enseignantRepositoryMock.findEnseignantByCourriel(any())).thenReturn(Optional.empty());
        when(enseignantRepositoryMock.findEnseignantByNumeroDeTelephone("438-532-2729")).thenReturn(Optional.of(enseignant));

        // Act
        boolean result = enseignantService.credentialsDejaPris("email@inexistant.com", "438-532-2729");

        // Assert
        Assertions.assertTrue(result);
    }

    @Test
    public void testCredentialsDejaPris_AucunPris() {
        // Arrange
        EnseignantRepository enseignantRepositoryMock = Mockito.mock(EnseignantRepository.class);
        PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
        EnseignantService enseignantService = new EnseignantService(enseignantRepositoryMock, passwordEncoder);

        when(enseignantRepositoryMock.findEnseignantByCourriel(any())).thenReturn(Optional.empty());
        when(enseignantRepositoryMock.findEnseignantByNumeroDeTelephone(any())).thenReturn(Optional.empty());

        // Act
        boolean result = enseignantService.credentialsDejaPris("email@inexistant.com", "000-000-0000");

        // Assert
        Assertions.assertFalse(result);
    }
}
