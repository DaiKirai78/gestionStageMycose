package com.projet.mycose.service;

import com.projet.mycose.dto.EnseignantDTO;
import com.projet.mycose.dto.EtudiantDTO;
import com.projet.mycose.modele.Enseignant;
import com.projet.mycose.modele.Etudiant;
import com.projet.mycose.modele.GestionnaireStage;
import com.projet.mycose.modele.Programme;
import com.projet.mycose.repository.GestionnaireStageRepository;
import com.projet.mycose.repository.UtilisateurRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class GestionnaireStageServiceTest {

    @Mock
    private UtilisateurService utilisateurService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private GestionnaireStageRepository gestionnaireStageRepository;

    @Mock
    private UtilisateurRepository utilisateurRepository;

    @InjectMocks
    private GestionnaireStageService gestionnaireStageService;

    @BeforeEach
    void setUp() {
    }

    @Test
    void shouldSaveGestionnaireStage_WhenCredentialsAreNotTaken() {
        // Arrange
        String prenom = "John";
        String nom = "Doe";
        String numeroTelephone = "1234567890";
        String courriel = "john.doe@example.com";
        String motDePasse = "password123";
        String encodedPassword = "encodedPassword123";

        // Mock the behavior of utilisateurService to indicate credentials are not taken
        when(utilisateurService.credentialsDejaPris(courriel, numeroTelephone)).thenReturn(false);

        // Mock the behavior of passwordEncoder to return an encoded password
        when(passwordEncoder.encode(motDePasse)).thenReturn(encodedPassword);

        // Act
        gestionnaireStageService.creationDeCompte(prenom, nom, numeroTelephone, courriel, motDePasse);

        // Assert
        // Capture the GestionnaireStage object passed to the save method
        ArgumentCaptor<GestionnaireStage> gestionnaireStageCaptor = ArgumentCaptor.forClass(GestionnaireStage.class);
        verify(gestionnaireStageRepository, times(1)).save(gestionnaireStageCaptor.capture());

        GestionnaireStage savedGestionnaireStage = gestionnaireStageCaptor.getValue();
        assertNotNull(savedGestionnaireStage, "Saved GestionnaireStage should not be null");
        assertEquals(prenom, savedGestionnaireStage.getPrenom(), "Prenom should match");
        assertEquals(nom, savedGestionnaireStage.getNom(), "Nom should match");
        assertEquals(numeroTelephone, savedGestionnaireStage.getNumeroDeTelephone(), "NumeroTelephone should match");
        assertEquals(courriel, savedGestionnaireStage.getCourriel(), "Courriel should match");
        assertEquals(encodedPassword, savedGestionnaireStage.getMotDePasse(), "MotDePasse should be encoded");
    }

    @Test
    void shouldNotSaveGestionnaireStage_WhenCredentialsAreAlreadyTaken() {
        // Arrange
        String prenom = "Jane";
        String nom = "Smith";
        String numeroTelephone = "0987654321";
        String courriel = "jane.smith@example.com";
        String motDePasse = "securePassword";

        // Mock the behavior of utilisateurService to indicate credentials are already taken
        when(utilisateurService.credentialsDejaPris(courriel, numeroTelephone)).thenReturn(true);

        // Act
        gestionnaireStageService.creationDeCompte(prenom, nom, numeroTelephone, courriel, motDePasse);

        // Assert
        // Verify that the save method was never called
        verify(gestionnaireStageRepository, never()).save(any(GestionnaireStage.class));
    }

    @Test
    public void testGetEtudiantsSansEnseignants_Sucess() {
        // Arrange
        Etudiant etudiant1 = new Etudiant(
                1L,
                "unPrenom",
                "unNom",
                "555-656-0965",
                "unCourriel@mail.com",
                "unMotDePasse",
                Programme.TECHNIQUE_INFORMATIQUE
        );

        Etudiant etudiant2 = new Etudiant(
                2L,
                "unPrenom2",
                "unNom2",
                "444-454-0965",
                "unCourriel2@mail.com",
                "unMotDePasse2",
                Programme.GENIE_LOGICIEL
        );

        int page = 0;
        PageRequest pageRequest = PageRequest.of(page, 10);
        List<Etudiant> listeEtudiantMock = new ArrayList<>();
        listeEtudiantMock.add(etudiant1);
        listeEtudiantMock.add(etudiant2);

        Page<Etudiant> etudiantsPage = new PageImpl<>(listeEtudiantMock, pageRequest, 2);

        when(utilisateurRepository.findAllEtudiantsSansEnseignants(pageRequest)).thenReturn(etudiantsPage);
        // Act
        List<EtudiantDTO> result = gestionnaireStageService.getEtudiantsSansEnseignants(page);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("unCourriel@mail.com", result.get(0).getCourriel());
        assertEquals("unCourriel2@mail.com", result.get(1).getCourriel());

        verify(utilisateurRepository, times(1)).findAllEtudiantsSansEnseignants(pageRequest);
    }

    @Test
    public void testGetEtudiantsSansEnseignants_Null() {
        int page = 0;
        PageRequest pageRequest = PageRequest.of(page, 10);
        Page<Etudiant> etudiantsPage = new PageImpl<>(List.of(), pageRequest, 0);

        when(utilisateurRepository.findAllEtudiantsSansEnseignants(pageRequest)).thenReturn(etudiantsPage);

        // Act
        List<EtudiantDTO> result = gestionnaireStageService.getEtudiantsSansEnseignants(page);

        // Assert
        assertNull(result);
        verify(utilisateurRepository, times(1)).findAllEtudiantsSansEnseignants(pageRequest);
    }

    @Test
    public void testGetEnseignantsParRecherche_Success() {
        // Arrange
        Enseignant enseignant1 = new Enseignant(
                1L,
                "unPrenom",
                "unNom",
                "555-444-3333",
                "unCourriel@mail.com",
                "unMotDePasse"
        );

        Enseignant enseignant2 = new Enseignant(
                2L,
                "unPrenom2",
                "unNom2",
                "444-555-2222",
                "unCourriel2@mail.com",
                "unMotDePasse2"
        );

        List<Enseignant> listeEnseignantMock = new ArrayList<>();
        listeEnseignantMock.add(enseignant1);
        listeEnseignantMock.add(enseignant2);

        when(utilisateurRepository.findAllEnseignantsBySearch("uneRecherche")).thenReturn(listeEnseignantMock);

        // Act
        List<EnseignantDTO> result = gestionnaireStageService.getEnseignantsParRecherche("uneRecherche");

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("unCourriel@mail.com", result.get(0).getCourriel());
        assertEquals("unCourriel2@mail.com", result.get(1).getCourriel());

        verify(utilisateurRepository, times(1)).findAllEnseignantsBySearch("uneRecherche");
    }

    @Test
    public void testGetEnseignantsParRecherche_Null() {
        // Arrange
        when(utilisateurRepository.findAllEnseignantsBySearch("uneRecherche")).thenReturn(new ArrayList<>());

        // Act
        List<EnseignantDTO> result = gestionnaireStageService.getEnseignantsParRecherche("uneRecherche");

        // Assert
        assertNull(result);
        verify(utilisateurRepository, times(1)).findAllEnseignantsBySearch("uneRecherche");
    }

    @Test
    public void testGetAmountOfPage_NumberEndWithZero() {
        //Arrange
        when(utilisateurRepository.countAllEtudiantsSansEnseignants()).thenReturn(30);

        //Act
        int nombrePage = gestionnaireStageService.getAmountOfPages();

        //Assert
        assertEquals(nombrePage, 3);
        verify(utilisateurRepository, times(1)).countAllEtudiantsSansEnseignants();
    }

    @Test
    public void testGetAmountOfPage_NumberNotEndWithZero() {
        //Arrange
        when(utilisateurRepository.countAllEtudiantsSansEnseignants()).thenReturn(43);

        //Act
        int nombrePage = gestionnaireStageService.getAmountOfPages();

        //Assert
        assertEquals(nombrePage, 5);
        verify(utilisateurRepository, times(1)).countAllEtudiantsSansEnseignants();
    }

}
