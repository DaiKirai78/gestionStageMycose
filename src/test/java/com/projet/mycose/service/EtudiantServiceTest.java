package com.projet.mycose.service;

import com.projet.mycose.modele.Etudiant;
import com.projet.mycose.modele.Programme;
import com.projet.mycose.modele.auth.Role;
import com.projet.mycose.repository.EtudiantRepository;
import com.projet.mycose.service.dto.EtudiantDTO;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EtudiantServiceTest {

    @Mock
    private EtudiantRepository etudiantRepositoryMock;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UtilisateurService utilisateurService;

    @InjectMocks
    private EtudiantService etudiantService;

    @Test
    public void creationDeCompteAvecSucces() {
        //Arrange
        Etudiant etudiant = new Etudiant(1L, "Karim", "Mihoubi", "438-532-2729", "mihoubi@gmail.com", "Mimi123$", Programme.TECHNIQUE_INFORMATIQUE);
        when(etudiantRepositoryMock.save(any(Etudiant.class))).thenReturn(etudiant);

        //Act
        EtudiantDTO etudiantDTO = etudiantService.creationDeCompte("Karim", "Mihoubi", "438-532-2729", "mihoubi@gmail.com", "Mimi123$", Programme.TECHNIQUE_INFORMATIQUE);

        //Assert
        Assertions.assertEquals(etudiantDTO.getId(), 1);
        Assertions.assertEquals(etudiantDTO.getPrenom(), "Karim");
        Assertions.assertEquals(etudiantDTO.getNom(), "Mihoubi");
        Assertions.assertEquals(etudiantDTO.getCourriel(), "mihoubi@gmail.com");
        Assertions.assertEquals(etudiantDTO.getNumeroDeTelephone(), "438-532-2729");
        Assertions.assertEquals(etudiantDTO.getProgramme(), Programme.TECHNIQUE_INFORMATIQUE);
        Assertions.assertEquals(etudiantDTO.getRole(), Role.ETUDIANT);
    }

    @Test
    public void creationDeCompteAvecEchec_NumeroDeTelephoneInvalide() {
        //Arrange

        String numeroDeTelephoneInvalide = "12345abc";

        //Act & Assert
        Assertions.assertThrows(
                NullPointerException.class,
                () -> etudiantService.creationDeCompte("Karim", "Mihoubi", numeroDeTelephoneInvalide, "mihoubi@gmail.com", "Mimi123$", Programme.TECHNIQUE_INFORMATIQUE)
        );
    }

    @Test
    public void creationDeCompteAvecEchec_CourrielInvalide() {
        //Arrange

        String courrielInvalide = "jeSuisUnEmailQuiNeRespectePasLesConventionsDÉcriture";

        //Act & Assert
        Assertions.assertThrows(
                NullPointerException.class,
                () -> etudiantService.creationDeCompte("Karim", "Mihoubi", "450-389-2628", courrielInvalide, "Mimi123$", Programme.TECHNIQUE_INFORMATIQUE)
        );
    }

    @Test
    public void creationDeCompteAvecEchec_MotDePasseInvalide_PasEncode() {
        //Arrange

        String motDePasseInvalide = "motDePassePasEncodé";

        //Act & Assert
        Assertions.assertThrows(
                NullPointerException.class,
                () -> etudiantService.creationDeCompte("Karim", "Mihoubi", "450-389-2628", "mihoubi@gmail.com", motDePasseInvalide, Programme.TECHNIQUE_INFORMATIQUE)
        );
    }

    @Test
    public void creationDeCompteAvecEchec_MotDePasseInvalide_Null() {
        //Arrange

        String motDePasseInvalide = null;

        //Act & Assert
        Assertions.assertThrows(
                NullPointerException.class,
                () -> etudiantService.creationDeCompte("Karim", "Mihoubi", "450-389-2628", "mihoubi@gmail.com", motDePasseInvalide, Programme.TECHNIQUE_INFORMATIQUE)
        );
    }

    @Test
    public void creationDeCompteAvecEchec_PrenomInvalide() {
        //Arrange

        String prenomInvalide = "3426unPrenomInvalide28382";

        //Act & Assert
        Assertions.assertThrows(
                NullPointerException.class,
                () -> etudiantService.creationDeCompte(prenomInvalide, "Mihoubi", "450-389-2628", "mihoubi@gmail.com", "Mimi123$", Programme.TECHNIQUE_INFORMATIQUE)
        );
    }

    @Test
    public void creationDeCompteAvecEchec_NomInvalide() {
        //Arrange

        String nomInvalide = "3426unNomInvalide28382";

        //Act & Assert
        Assertions.assertThrows(
                NullPointerException.class,
                () -> etudiantService.creationDeCompte("Karim", nomInvalide, "450-389-2628", "mihoubi@gmail.com", "Mimi123$", Programme.TECHNIQUE_INFORMATIQUE)
        );
    }

    @Test
    public void testCredentialsDejaPris_CourrielPris() {
        // Arrange
        when(utilisateurService.credentialsDejaPris("mihoubi@gmail.com", "450-691-0000")).thenReturn(true);

        // Act
        EtudiantDTO result = etudiantService.creationDeCompte("Karim", "Mihoubi", "450-691-0000", "mihoubi@gmail.com", "Mimi123$", Programme.TECHNIQUE_INFORMATIQUE);

        // Assert
        Assertions.assertNull(result);
    }
}
