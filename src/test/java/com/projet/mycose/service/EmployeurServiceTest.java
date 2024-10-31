package com.projet.mycose.service;

import com.projet.mycose.modele.*;
import com.projet.mycose.modele.auth.Role;
import com.projet.mycose.repository.EmployeurRepository;
import com.projet.mycose.repository.OffreStageRepository;
import com.projet.mycose.dto.EmployeurDTO;
import com.projet.mycose.dto.OffreStageDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class EmployeurServiceTest {

    @Mock
    private EmployeurRepository employeurRepository;

    @Mock
    private UtilisateurService utilisateurService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private OffreStageRepository offreStageRepositoryMock;
    @InjectMocks
    private EmployeurService employeurService;

    @Test
    public void creationDeCompteAvecSucces() {
        //Arrange
        Employeur employeur = new Employeur(1L, "Karim", "Mihoubi", "438-532-2729", "mihoubi@gmail.com", "Mimi123$", "Couche-Tard");
        when(employeurRepository.save(any(Employeur.class))).thenReturn(employeur);

        //Act
        EmployeurDTO employeurDTO = employeurService.creationDeCompte("Karim", "Mihoubi", "438-532-2729", "mihoubi@gmail.com", "Mimi123$", "Couche-Tard");

        //Assert
        assertEquals(employeurDTO.getId(), 1);
        assertEquals(employeurDTO.getPrenom(), "Karim");
        assertEquals(employeurDTO.getNom(), "Mihoubi");
        assertEquals(employeurDTO.getCourriel(), "mihoubi@gmail.com");
        assertEquals(employeurDTO.getNumeroDeTelephone(), "438-532-2729");
        assertEquals(employeurDTO.getRole(), Role.EMPLOYEUR);
    }

    @Test
    public void creationDeCompteAvecEchec_NumeroDeTelephoneInvalide() {
        //Arrange

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

        String nomInvalide = "3426unNomInvalide28382";

        //Act & Assert
        Assertions.assertThrows(
                NullPointerException.class,
                () -> employeurService.creationDeCompte("Karim", nomInvalide, "450-389-2628", "mihoubi@gmail.com", "Mimi123$", "Couche-Tard")
        );
    }

    @Test
    public void testCredentialsDejaPris_CourrielPris() {
        // Arrange
        when(utilisateurService.credentialsDejaPris("mihoubi@gmail.com", "450-691-0000")).thenReturn(true);

        // Act
        EmployeurDTO result = employeurService.creationDeCompte("Karim", "Mihoubi", "450-691-0000", "mihoubi@gmail.com", "Mimi123$", "Couche-Tard");

        // Assert
        assertNull(result);
    }

    @Test
    public void testGetStages_Success() {
        // Arrange
        String token = "unTokenValide";
        Long createurId = 1L;
        int page = 0;
        PageRequest pageRequest = PageRequest.of(page, 10);
        Employeur createur = new Employeur(2L, "unPrenom", "unNom", "514-222-0385", "courriel@courriel.com", "123123123", "uneEntreprise");

        FormulaireOffreStage mockFormulaireOffreStage = new FormulaireOffreStage("unTitreForm", "uneEntreprise", "unEmployeur", "unEmail@mail.com", "unsite.com", "uneLocalisation", "1000", "uneDescription", createur, OffreStage.Visibility.PUBLIC, null, OffreStage.Status.ACCEPTED, SessionEcole.AUTOMNE, Year.of(2021));
        FichierOffreStage mockFichierOffreStage = new FichierOffreStage("unTitreFichier", "uneEntreprise", "nom.pdf", "data".getBytes(), createur, OffreStage.Visibility.PUBLIC, null, OffreStage.Status.ACCEPTED, SessionEcole.AUTOMNE, Year.of(2021));
        List<OffreStage> mockOffresListe = new ArrayList<>();
        mockOffresListe.add(mockFormulaireOffreStage);
        mockOffresListe.add(mockFichierOffreStage);

        Page<OffreStage> offresPage = new PageImpl<>(mockOffresListe, pageRequest, 2);

        when(utilisateurService.getMyUserId()).thenReturn(createurId);
        when(offreStageRepositoryMock.findOffreStageByCreateurId(createurId, pageRequest)).thenReturn(offresPage);

        // Act
        List<OffreStageDTO> result = employeurService.getStages(page);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("unTitreForm", result.get(0).getTitle());
        assertEquals("unTitreFichier", result.get(1).getTitle());

        verify(utilisateurService, times(1)).getMyUserId();
        verify(offreStageRepositoryMock, times(1)).findOffreStageByCreateurId(createurId, pageRequest);
    }

    @Test
    public void testGetStages_Null() {
        // Arrange
        String token = "unTokenValide";
        Long createurId = 1L;
        int page = 0;
        PageRequest pageRequest = PageRequest.of(page, 10);
        Page<OffreStage> offresPage = new PageImpl<>(List.of(), pageRequest, 0);

        when(utilisateurService.getMyUserId()).thenReturn(createurId);
        when(offreStageRepositoryMock.findOffreStageByCreateurId(createurId, pageRequest)).thenReturn(offresPage);

        // Act
        List<OffreStageDTO> result = employeurService.getStages(page);

        // Assert
        assertNull(result);

        verify(utilisateurService, times(1)).getMyUserId();
        verify(offreStageRepositoryMock, times(1)).findOffreStageByCreateurId(createurId, pageRequest);
    }

    @Test
    public void testGetAmountOfPage_NumberEndWithZero() {
        //Arrange
        when(utilisateurService.getMyUserId()).thenReturn(1L);
        when(offreStageRepositoryMock.countByCreateurId(1L)).thenReturn(30);

        //Act
        int nombrePage = employeurService.getAmountOfPages();

        //Assert
        assertEquals(nombrePage, 3);
        verify(offreStageRepositoryMock, times(1)).countByCreateurId(1L);
    }

    @Test
    public void testGetAmountOfPage_NumberNotEndWithZero() {
        //Arrange
        when(utilisateurService.getMyUserId()).thenReturn(1L);
        when(offreStageRepositoryMock.countByCreateurId(1L)).thenReturn(43);

        //Act
        int nombrePage = employeurService.getAmountOfPages();

        //Assert
        assertEquals(nombrePage, 5);
        verify(offreStageRepositoryMock, times(1)).countByCreateurId(1L);
    }

}
