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
import java.util.Optional;

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
                Programme.TECHNIQUE_INFORMATIQUE,
                Etudiant.ContractStatus.NO_CONTRACT
        );
        
        int page = 0;
        PageRequest pageRequest = PageRequest.of(page, 10);
        List<Etudiant> listeEtudiantMock = new ArrayList<>();
        listeEtudiantMock.add(etudiant1);

        Page<Etudiant> etudiantsPage = new PageImpl<>(listeEtudiantMock, pageRequest, 2);

        when(utilisateurRepository.findAllEtudiantsSansEnseignants(Programme.TECHNIQUE_INFORMATIQUE,pageRequest)).thenReturn(etudiantsPage);
        // Act
        List<EtudiantDTO> result = gestionnaireStageService.getEtudiantsSansEnseignants(page, Programme.TECHNIQUE_INFORMATIQUE);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("unCourriel@mail.com", result.get(0).getCourriel());

        verify(utilisateurRepository, times(1)).findAllEtudiantsSansEnseignants(Programme.TECHNIQUE_INFORMATIQUE, pageRequest);
    }

    @Test
    public void testGetEtudiantsSansEnseignants_Null() {
        int page = 0;
        PageRequest pageRequest = PageRequest.of(page, 10);
        Page<Etudiant> etudiantsPage = new PageImpl<>(List.of(), pageRequest, 0);

        when(utilisateurRepository.findAllEtudiantsSansEnseignants(null, pageRequest)).thenReturn(etudiantsPage);

        // Act
        List<EtudiantDTO> result = gestionnaireStageService.getEtudiantsSansEnseignants(page, null);

        // Assert
        assertNull(result);
        verify(utilisateurRepository, times(1)).findAllEtudiantsSansEnseignants(null, pageRequest);
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
        when(utilisateurRepository.countAllEtudiantsSansEnseignants(Programme.TECHNIQUE_INFORMATIQUE)).thenReturn(30);

        //Act
        int nombrePage = gestionnaireStageService.getAmountOfPages(Programme.TECHNIQUE_INFORMATIQUE);

        //Assert
        assertEquals(nombrePage, 3);
        verify(utilisateurRepository, times(1)).countAllEtudiantsSansEnseignants(Programme.TECHNIQUE_INFORMATIQUE);
    }

    @Test
    public void testGetAmountOfPage_NumberNotEndWithZero() {
        //Arrange
        when(utilisateurRepository.countAllEtudiantsSansEnseignants(Programme.TECHNIQUE_INFORMATIQUE)).thenReturn(43);

        //Act
        int nombrePage = gestionnaireStageService.getAmountOfPages(Programme.TECHNIQUE_INFORMATIQUE);

        //Assert
        assertEquals(nombrePage, 5);
        verify(utilisateurRepository, times(1)).countAllEtudiantsSansEnseignants(Programme.TECHNIQUE_INFORMATIQUE);
    }

    @Test
    public void testAssignerEnseignantEtudiant_Success() {
        // Arrange
        Long idEtudiant = 1L;
        Long idEnseignant = 2L;

        Etudiant etudiant = new Etudiant(
                1L,
                "unPrenom",
                "unNom",
                "555-666-7777",
                "unCourriel@mail.com",
                "unMotDePasse",
                Programme.TECHNIQUE_INFORMATIQUE,
                Etudiant.ContractStatus.NO_CONTRACT
        );

        Enseignant enseignant = new Enseignant(
                2L,
                "unPrenom",
                "unNom",
                "555-666-7777",
                "unCourriel@mail.com",
                "unMotDePasse"
        );

        when(utilisateurRepository.findUtilisateurById(idEtudiant)).thenReturn(Optional.of(etudiant));
        when(utilisateurRepository.findUtilisateurById(idEnseignant)).thenReturn(Optional.of(enseignant));

        // Act
        gestionnaireStageService.assignerEnseigantEtudiant(idEtudiant, idEnseignant);

        // Assert
        assertEquals(enseignant, etudiant.getEnseignantAssignee());
        assertTrue(enseignant.getEtudiantsAssignees().contains(etudiant));
        verify(utilisateurRepository).save(etudiant);
        verify(utilisateurRepository).save(enseignant);
    }

    @Test
    void testAssignerEnseignantEtudiant_nullIdEtudiant() {
        // Arrange
        Long idEnseignant = 2L;

        // Act & Assert
        Exception exception = assertThrows(NullPointerException.class, () ->
                gestionnaireStageService.assignerEnseigantEtudiant(null, idEnseignant)
        );
        assertEquals("ID Étudiant ne peut pas être NULL", exception.getMessage());
    }

    @Test
    void testAssignerEnseignantEtudiant_utilisateurNonTrouve() {
        // Arrange
        Long idEtudiant = 1L;
        Long idEnseignant = 2L;

        when(utilisateurRepository.findUtilisateurById(idEtudiant)).thenReturn(Optional.empty());
        when(utilisateurRepository.findUtilisateurById(idEnseignant)).thenReturn(Optional.of(new Enseignant()));

        when(utilisateurRepository.findUtilisateurById(idEtudiant)).thenReturn(Optional.of(new Etudiant()));
        when(utilisateurRepository.findUtilisateurById(idEnseignant)).thenReturn(Optional.empty());

        // Act
        gestionnaireStageService.assignerEnseigantEtudiant(idEtudiant, idEnseignant);
        gestionnaireStageService.assignerEnseigantEtudiant(idEtudiant, idEnseignant);

        // Assert
        verify(utilisateurRepository, never()).save(any(Etudiant.class));
        verify(utilisateurRepository, never()).save(any(Enseignant.class));
        verify(utilisateurRepository, never()).save(any(Etudiant.class));
        verify(utilisateurRepository, never()).save(any(Enseignant.class));
    }

    @Test
    void testAssignerEnseignantEtudiant_nullIdEnseignant() {
        // Arrange
        Long idEtudiant = 1L;

        // Act & Assert
        Exception exception = assertThrows(NullPointerException.class, () ->
                gestionnaireStageService.assignerEnseigantEtudiant(idEtudiant, null)
        );
        assertEquals("ID Enseignant ne peut pas être NULL", exception.getMessage());
    }

}
