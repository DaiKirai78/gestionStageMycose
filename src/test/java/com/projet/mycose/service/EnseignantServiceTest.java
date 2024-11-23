package com.projet.mycose.service;

import com.projet.mycose.exceptions.ResourceConflictException;
import com.projet.mycose.modele.Enseignant;
import com.projet.mycose.modele.auth.Role;
import com.projet.mycose.repository.EnseignantRepository;
import com.projet.mycose.dto.EnseignantDTO;
import com.projet.mycose.repository.UtilisateurRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EnseignantServiceTest {



    @Mock
    private EnseignantRepository enseignantRepositoryMock;

    @Mock
    private UtilisateurService utilisateurService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private EnseignantService enseignantService;

    @Test
    public void creationDeCompteAvecSucces() {
        //Arrange

        Enseignant enseignant = new Enseignant(1L, "Karim", "Mihoubi", "438-532-2729", "mihoubi@gmail.com", "Mimi123$");
        when(enseignantRepositoryMock.save(any(Enseignant.class))).thenReturn(enseignant);
        when(passwordEncoder.encode(eq("Mimi123$"))).thenReturn("mot de passe encodé");

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

        String nomInvalide = "3426unNomInvalide28382";

        //Act & Assert
        Assertions.assertThrows(
                NullPointerException.class,
                () -> enseignantService.creationDeCompte("Karim", nomInvalide, "450-389-2628", "mihoubi@gmail.com", "Mimi123$")
        );
    }



    @Test
    public void testCredentialsDejaPris_CourrielPris() {
        // Arrange
        when(utilisateurService.credentialsDejaPris("mihoubi@gmail.com", "450-389-2628")).thenReturn(true);

        // Act
        ResourceConflictException exception = Assertions.assertThrows(
                ResourceConflictException.class,
                () -> enseignantService.creationDeCompte("Karim", "Mihoubi", "450-389-2628", "mihoubi@gmail.com", "Mimi123$")
        );

        // Assert
        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
        assertEquals("Un enseignant avec ces credentials existe déjà", exception.getMessage());
    }
}
