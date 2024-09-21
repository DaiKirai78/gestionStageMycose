package com.projet.mycose.service;

import com.projet.mycose.modele.Etudiant;
import com.projet.mycose.modele.auth.Role;
import com.projet.mycose.repository.EtudiantRepository;
import com.projet.mycose.service.dto.EtudiantDTO;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

public class EtudiantServiceTest {
    @Test
    public void creationDeCompteAvecSucces() {
        //Arrange
        EtudiantRepository etudiantRepositoryMock = Mockito.mock(EtudiantRepository.class);
        EtudiantService etudiantService = new EtudiantService(etudiantRepositoryMock);

        Etudiant etudiant = new Etudiant(1L, "Karim", "Mihoubi", "438-532-2729", "mihoubi@gmail.com", "$2y$10$iXgJopQP9JaxKujH2nOgn.S8BCNEdhKQwRcC/7DxDRu3G6SMShC3G");
        when(etudiantRepositoryMock.save(any(Etudiant.class))).thenReturn(etudiant);

        //Act
        EtudiantDTO etudiantDTO = etudiantService.creationDeCompte("Karim", "Mihoubi", "438-532-2729", "mihoubi@gmail.com", "$2y$10$iXgJopQP9JaxKujH2nOgn.S8BCNEdhKQwRcC/7DxDRu3G6SMShC3G");

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
        EtudiantService etudiantService = new EtudiantService(etudiantRepositoryMock);

        doThrow(new DataIntegrityViolationException("Courriel déjà utilisé"))
                .when(etudiantRepositoryMock).save(any(Etudiant.class));

        //Act & Assert
        DataIntegrityViolationException exception = Assertions.assertThrows(
                DataIntegrityViolationException.class,
                () -> etudiantService.creationDeCompte("Karim", "Mihoubi", "438-532-2729", "mihoubi@gmail.com", "$2y$10$iXgJopQP9JaxKujH2nOgn.S8BCNEdhKQwRcC/7DxDRu3G6SMShC3G")
        );

        Assertions.assertEquals("Courriel déjà utilisé", exception.getMessage());
    }

    @Test
    public void creationDeCompteAvecEchec_NumeroDeTelephoneInvalide() {
        //Arrange
        EtudiantRepository etudiantRepositoryMock = Mockito.mock(EtudiantRepository.class);
        EtudiantService etudiantService = new EtudiantService(etudiantRepositoryMock);

        String numeroDeTelephoneInvalide = "12345abc";

        //Act & Assert
        IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> etudiantService.creationDeCompte("Karim", "Mihoubi", numeroDeTelephoneInvalide, "mihoubi@gmail.com", "$2y$10$iXgJopQP9JaxKujH2nOgn.S8BCNEdhKQwRcC/7DxDRu3G6SMShC3G")
        );

        Assertions.assertEquals("Le numéro de téléphone de l'utilisateur est invalide", exception.getMessage());
    }

    @Test
    public void creationDeCompteAvecEchec_CourrielInvalide() {
        //Arrange
        EtudiantRepository etudiantRepositoryMock = Mockito.mock(EtudiantRepository.class);
        EtudiantService etudiantService = new EtudiantService(etudiantRepositoryMock);

        String courrielInvalide = "jeSuisUnEmailQuiNeRespectePasLesConventionsDÉcriture";

        //Act & Assert
        IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> etudiantService.creationDeCompte("Karim", "Mihoubi", "450-389-2628", courrielInvalide, "$2y$10$iXgJopQP9JaxKujH2nOgn.S8BCNEdhKQwRcC/7DxDRu3G6SMShC3G")
        );

        Assertions.assertEquals("Le courriel de l'utilisateur est invalide", exception.getMessage());
    }

    @Test
    public void creationDeCompteAvecEchec_MotDePasseInvalide_PasEncode() {
        //Arrange
        EtudiantRepository etudiantRepositoryMock = Mockito.mock(EtudiantRepository.class);
        EtudiantService etudiantService = new EtudiantService(etudiantRepositoryMock);

        String motDePasseInvalide = "motDePassePasEncodé";

        //Act & Assert
        IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> etudiantService.creationDeCompte("Karim", "Mihoubi", "450-389-2628", "mihoubi@gmail.com", motDePasseInvalide)
        );

        Assertions.assertEquals("Le mot de passe de l'utilisateur est invalide", exception.getMessage());
    }

    @Test
    public void creationDeCompteAvecEchec_MotDePasseInvalide_Null() {
        //Arrange
        EtudiantRepository etudiantRepositoryMock = Mockito.mock(EtudiantRepository.class);
        EtudiantService etudiantService = new EtudiantService(etudiantRepositoryMock);

        String motDePasseInvalide = null;

        //Act & Assert
        IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> etudiantService.creationDeCompte("Karim", "Mihoubi", "450-389-2628", "mihoubi@gmail.com", motDePasseInvalide)
        );

        Assertions.assertEquals("Le mot de passe de l'utilisateur est invalide", exception.getMessage());
    }

    @Test
    public void creationDeCompteAvecEchec_PrenomInvalide() {
        //Arrange
        EtudiantRepository etudiantRepositoryMock = Mockito.mock(EtudiantRepository.class);
        EtudiantService etudiantService = new EtudiantService(etudiantRepositoryMock);

        String prenomInvalide = "3426unPrenomInvalide28382";

        //Act & Assert
        IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> etudiantService.creationDeCompte(prenomInvalide, "Mihoubi", "450-389-2628", "mihoubi@gmail.com", "$2y$10$iXgJopQP9JaxKujH2nOgn.S8BCNEdhKQwRcC/7DxDRu3G6SMShC3G")
        );

        Assertions.assertEquals("Le prénom de l'utilisateur est invalide", exception.getMessage());
    }

    @Test
    public void creationDeCompteAvecEchec_NomInvalide() {
        //Arrange
        EtudiantRepository etudiantRepositoryMock = Mockito.mock(EtudiantRepository.class);
        EtudiantService etudiantService = new EtudiantService(etudiantRepositoryMock);

        String nomInvalide = "3426unNomInvalide28382";

        //Act & Assert
        IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> etudiantService.creationDeCompte("Karim", nomInvalide, "450-389-2628", "mihoubi@gmail.com", "$2y$10$iXgJopQP9JaxKujH2nOgn.S8BCNEdhKQwRcC/7DxDRu3G6SMShC3G")
        );

        Assertions.assertEquals("Le nom de l'utilisateur est invalide", exception.getMessage());
    }

    @Test
    public void testGetEtudiantByCourriel_EtudiantExiste() {
        // Arrange
        EtudiantRepository etudiantRepositoryMock = Mockito.mock(EtudiantRepository.class);
        EtudiantService etudiantService = new EtudiantService(etudiantRepositoryMock);

        Etudiant etudiant = new Etudiant(1L, "Karim", "Mihoubi", "438-532-2729", "mihoubi@gmail.com", "$2y$10$iXgJopQP9JaxKujH2nOgn.S8BCNEdhKQwRcC/7DxDRu3G6SMShC3G");
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
        EtudiantService etudiantService = new EtudiantService(etudiantRepositoryMock);

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
        EtudiantService etudiantService = new EtudiantService(etudiantRepositoryMock);

        Etudiant etudiant = new Etudiant(1L, "Karim", "Mihoubi", "438-532-2729", "mihoubi@gmail.com", "$2y$10$iXgJopQP9JaxKujH2nOgn.S8BCNEdhKQwRcC/7DxDRu3G6SMShC3G");
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
        EtudiantService etudiantService = new EtudiantService(etudiantRepositoryMock);

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
        EtudiantService etudiantService = new EtudiantService(etudiantRepositoryMock);

        Etudiant etudiant = new Etudiant(1L, "Karim", "Mihoubi", "438-532-2729", "mihoubi@gmail.com", "$2y$10$iXgJopQP9JaxKujH2nOgn.S8BCNEdhKQwRcC/7DxDRu3G6SMShC3G");
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
        EtudiantService etudiantService = new EtudiantService(etudiantRepositoryMock);

        Etudiant etudiant = new Etudiant(1L, "Karim", "Mihoubi", "438-532-2729", "mihoubi@gmail.com", "$2y$10$iXgJopQP9JaxKujH2nOgn.S8BCNEdhKQwRcC/7DxDRu3G6SMShC3G");
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
        EtudiantService etudiantService = new EtudiantService(etudiantRepositoryMock);

        when(etudiantRepositoryMock.findEtudiantByCourriel(any())).thenReturn(Optional.empty());
        when(etudiantRepositoryMock.findEtudiantByNumeroDeTelephone(any())).thenReturn(Optional.empty());

        // Act
        boolean result = etudiantService.credentialsDejaPris("email@inexistant.com", "000-000-0000");

        // Assert
        Assertions.assertFalse(result);
    }

    @Test
    public void toEntityEtudiantDTO() {
        // Arrange
        EtudiantDTO etudiantDTO = new EtudiantDTO(1L, "Karim", "Mihoubi", "mihoubi@gmail.com", "438-532-2729", Role.ETUDIANT);
        // Act
        Etudiant etudiant = EtudiantDTO.toEntity(etudiantDTO);
        // Assert
        Assertions.assertEquals(etudiant.getId(), 1L);
        Assertions.assertEquals(etudiant.getPrenom(), "Karim");
        Assertions.assertEquals(etudiant.getNom(), "Mihoubi");
        Assertions.assertEquals(etudiant.getCourriel(), "mihoubi@gmail.com");
        Assertions.assertEquals(etudiant.getNumeroDeTelephone(), "438-532-2729");
        Assertions.assertEquals(etudiant.getRole(), Role.ETUDIANT);
    }
}
