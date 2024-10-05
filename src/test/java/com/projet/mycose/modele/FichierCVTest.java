package com.projet.mycose.modele;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;

class FichierCVTest {

    private FichierCV fichierCV;

    @Mock
    private Etudiant etudiant;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        fichierCV = new FichierCV();
        fichierCV.setFilename("test.pdf");
        fichierCV.setData("Sample PDF content".getBytes());
        fichierCV.setEtudiant(etudiant);
    }

    @Test
    @DisplayName("prePersist sets status to WAITING when status is null")
    void prePersist_SetsStatusToWaiting_WhenStatusIsNull() {
        // Arrange
        fichierCV.setStatus(null);

        // Act
        fichierCV.prePersist();

        // Assert
        assertNotNull(fichierCV.getStatus(), "Status should not be null after prePersist");
        assertEquals(FichierCV.Status.WAITING, fichierCV.getStatus(), "Status should be set to WAITING");
    }

    @Test
    @DisplayName("prePersist does not change status when status is already set")
    void prePersist_DoesNotChangeStatus_WhenStatusIsAlreadySet() {
        // Arrange
        fichierCV.setStatus(FichierCV.Status.ACCEPTED);

        // Act
        fichierCV.prePersist();

        // Assert
        assertNotNull(fichierCV.getStatus(), "Status should not be null after prePersist");
        assertEquals(FichierCV.Status.ACCEPTED, fichierCV.getStatus(), "Status should remain as ACCEPTED");
    }
}
