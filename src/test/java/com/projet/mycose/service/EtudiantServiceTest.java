package com.projet.mycose.service;

import com.projet.mycose.modele.*;
import com.projet.mycose.modele.auth.Role;
import com.projet.mycose.repository.EtudiantRepository;
import com.projet.mycose.repository.OffreStageRepository;
import com.projet.mycose.service.dto.EnseignantDTO;
import com.projet.mycose.service.dto.EtudiantDTO;
import com.projet.mycose.service.dto.OffreStageDTO;
import jdk.jshell.execution.Util;
import lombok.experimental.UtilityClass;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EtudiantServiceTest {

    @Mock
    private EtudiantRepository etudiantRepositoryMock;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UtilisateurService utilisateurService;

    @Mock
    private OffreStageRepository offreStageRepositoryMock;

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
        assertEquals(etudiantDTO.getId(), 1);
        assertEquals(etudiantDTO.getPrenom(), "Karim");
        assertEquals(etudiantDTO.getNom(), "Mihoubi");
        assertEquals(etudiantDTO.getCourriel(), "mihoubi@gmail.com");
        assertEquals(etudiantDTO.getNumeroDeTelephone(), "438-532-2729");
        assertEquals(etudiantDTO.getProgramme(), Programme.TECHNIQUE_INFORMATIQUE);
        assertEquals(etudiantDTO.getRole(), Role.ETUDIANT);
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
        assertNull(result);
    }

    @Test
    public void testListeOffreStageToDTO() {
        //Arrange
        List<OffreStage> mockOffreStageList = new ArrayList<>();
        FormulaireOffreStage mockFormulaireOffreStage = new FormulaireOffreStage("unTitre", "uneEntreprise", "unEmployeur", "unEmail@mail.com", "unsite.com", "uneLocalisation", "1000", "uneDescription", null);
        FichierOffreStage mockFichierOffreStage = new FichierOffreStage("unTitre", "uneEntreprise", "nom.pdf", "data".getBytes(), null);

        mockOffreStageList.add(mockFormulaireOffreStage);
        mockOffreStageList.add(mockFichierOffreStage);

        //Act
        //List<OffreStageDTO> resultat = etudiantService.

        //Assert
    }

    @Test
    public void testGetStages_Success() {
        // Arrange
        String token = "unTokenValide";
        Long etudiantId = 1L;
        int page = 0;
        PageRequest pageRequest = PageRequest.of(page, 10);
        Employeur createur = new Employeur(2L, "unPrenom", "unNom", "514-222-0385", "courriel@courriel.com", "123123123", "uneEntreprise");

        FormulaireOffreStage mockFormulaireOffreStage = new FormulaireOffreStage("unTitreForm", "uneEntreprise", "unEmployeur", "unEmail@mail.com", "unsite.com", "uneLocalisation", "1000", "uneDescription", createur);
        FichierOffreStage mockFichierOffreStage = new FichierOffreStage("unTitreFichier", "uneEntreprise", "nom.pdf", "data".getBytes(), createur);
        List<OffreStage> mockOffresListe = new ArrayList<>();
        mockOffresListe.add(mockFormulaireOffreStage);
        mockOffresListe.add(mockFichierOffreStage);

        Page<OffreStage> offresPage = new PageImpl<>(mockOffresListe, pageRequest, 2);

        when(utilisateurService.getUserIdByToken(token)).thenReturn(etudiantId);
        when(offreStageRepositoryMock.findOffresByEtudiantId(etudiantId, pageRequest)).thenReturn(offresPage);

        // Act
        List<OffreStageDTO> result = etudiantService.getStages(token, page);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("unTitreForm", result.get(0).getTitle());
        assertEquals("unTitreFichier", result.get(1).getTitle());

        verify(utilisateurService, times(1)).getUserIdByToken(token);
        verify(offreStageRepositoryMock, times(1)).findOffresByEtudiantId(etudiantId, pageRequest);
    }

    @Test
    public void testGetStages_Null() {
        // Arrange
        String token = "unTokenValide";
        Long etudiantId = 1L;
        int page = 0;
        PageRequest pageRequest = PageRequest.of(page, 10);
        Page<OffreStage> offresPage = new PageImpl<>(List.of(), pageRequest, 0);

        when(utilisateurService.getUserIdByToken(token)).thenReturn(etudiantId);
        when(offreStageRepositoryMock.findOffresByEtudiantId(etudiantId, pageRequest)).thenReturn(offresPage);

        // Act
        List<OffreStageDTO> result = etudiantService.getStages(token, page);

        // Assert
        assertNull(result);

        verify(utilisateurService, times(1)).getUserIdByToken(token);
        verify(offreStageRepositoryMock, times(1)).findOffresByEtudiantId(etudiantId, pageRequest);
    }

}
