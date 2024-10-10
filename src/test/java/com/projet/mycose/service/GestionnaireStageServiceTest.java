package com.projet.mycose.service;

import com.projet.mycose.modele.GestionnaireStage;
import com.projet.mycose.repository.GestionnaireStageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

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
}
