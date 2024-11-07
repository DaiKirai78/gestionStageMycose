package com.projet.mycose.service;

import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import com.projet.mycose.dto.ContratDTO;
import com.projet.mycose.dto.EnseignantDTO;
import com.projet.mycose.dto.EtudiantDTO;
import com.projet.mycose.modele.*;
import com.projet.mycose.modele.auth.Credentials;
import com.projet.mycose.modele.auth.Role;
import com.projet.mycose.repository.ContratRepository;
import com.projet.mycose.repository.GestionnaireStageRepository;
import com.projet.mycose.repository.UtilisateurRepository;
import com.projet.mycose.security.exception.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
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
    private ContratRepository contratRepository;

    @Mock
    private UtilisateurRepository utilisateurRepository;

    @InjectMocks
    private GestionnaireStageService gestionnaireStageService;

    @Mock
    AuthenticationManager authenticationManager;

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

    @Test
    public void testGetAllContratsNonSignes_Success() throws Exception {
        List<Contrat> contrats = List.of(new Contrat());
        Page<Contrat> pageContrats = new PageImpl<>(contrats, PageRequest.of(0, 10), contrats.size());

        when(contratRepository.findContratsBySignatureEmployeurIsNotNullAndSignatureEtudiantIsNotNullAndSignatureGestionnaireIsNull(PageRequest.of(0, 10)))
                .thenReturn(pageContrats);

        List<ContratDTO> result = gestionnaireStageService.getAllContratsNonSignes(0);

        assertEquals(contrats.size(), result.size());
    }

    @Test
    public void testGetAllContratsNonSignes_NotFound() {
        Page<Contrat> emptyPage = new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 10), 0);

        when(contratRepository.findContratsBySignatureEmployeurIsNotNullAndSignatureEtudiantIsNotNullAndSignatureGestionnaireIsNull(PageRequest.of(0, 10)))
                .thenReturn(emptyPage);

        assertThrows(ChangeSetPersister.NotFoundException.class, () -> gestionnaireStageService.getAllContratsNonSignes(0));
    }

    @Test
    void testGetAmountOfPagesOfContractNonSignees_0() {
        when(contratRepository.countContratsBySignatureEmployeurIsNotNullAndSignatureEtudiantIsNotNullAndSignatureGestionnaireIsNull()).thenReturn(0);

        Integer pages = gestionnaireStageService.getAmountOfPagesOfContractNonSignees();

        assertEquals(0, pages);
    }

    @Test
    void testGetAmountOfPagesOfContractNonSignees_10() {
        when(contratRepository.countContratsBySignatureEmployeurIsNotNullAndSignatureEtudiantIsNotNullAndSignatureGestionnaireIsNull()).thenReturn(10);

        Integer pages = gestionnaireStageService.getAmountOfPagesOfContractNonSignees();

        assertEquals(1, pages);
    }

    @Test
    void testGetAmountOfPagesOfContractNonSignees_13() {
        when(contratRepository.countContratsBySignatureEmployeurIsNotNullAndSignatureEtudiantIsNotNullAndSignatureGestionnaireIsNull()).thenReturn(13);

        Integer pages = gestionnaireStageService.getAmountOfPagesOfContractNonSignees();

        assertEquals(2, pages);
    }

    @Test
    void testGetAllContratsSignes_Success() throws ChangeSetPersister.NotFoundException {
        int page = 0;
        int annee = 2024;

        Contrat contrat1 = new Contrat();
        Contrat contrat2 = new Contrat();

        List<Contrat> contrats = List.of(contrat1, contrat2);
        Page<Contrat> contratPage = new PageImpl<>(contrats, PageRequest.of(page, 10), contrats.size());

        when(contratRepository.findContratSigneeParGestionnaire(annee, PageRequest.of(page, 10)))
                .thenReturn(contratPage);

        List<ContratDTO> result = gestionnaireStageService.getAllContratsSignes(page, annee);

        assertEquals(2, result.size());
        verify(contratRepository).findContratSigneeParGestionnaire(annee, PageRequest.of(page, 10));
    }

    @Test
    void testGetAllContratsSignes_NoContractsFound() {
        int page = 0;
        int annee = 2024;

        Page<Contrat> emptyPage = new PageImpl<>(List.of());

        when(contratRepository.findContratSigneeParGestionnaire(annee, PageRequest.of(page, 10)))
                .thenReturn(emptyPage);

        assertThrows(ChangeSetPersister.NotFoundException.class, () -> {
            gestionnaireStageService.getAllContratsSignes(page, annee);
        });
    }

    @Test
    void testGetAmountOfPagesOfContractSignees_WithContracts() {
        int annee = 2024;
        int amountOfRows = 25;

        when(contratRepository.countByContratSigneeParGestionnaire(annee))
                .thenReturn(amountOfRows);

        int result = gestionnaireStageService.getAmountOfPagesOfContractSignees(annee);

        assertEquals(3, result);
    }

    @Test
    void testGetAmountOfPagesOfContractSignees_NoContracts() {
        int annee = 2024;

        when(contratRepository.countByContratSigneeParGestionnaire(annee))
                .thenReturn(0);

        int result = gestionnaireStageService.getAmountOfPagesOfContractSignees(annee);

        assertEquals(0, result);
    }

    @Test
    public void testEnregistrerSignature_Success() throws Exception {
        // Arrange
        Long gestionnaireId = 1L;
        Long contratId = 1L;
        String password = "motDePasse";
        GestionnaireStage utilisateur = new GestionnaireStage();
        utilisateur.setCredentials(new Credentials("unEmail@mail.com", password, Role.EMPLOYEUR));

        Contrat contrat = new Contrat();
        contrat.setId(contratId);
        byte[] signatureBytes = "dummySignature".getBytes();
        MockMultipartFile signature = new MockMultipartFile("signature", "signature.jpg", "image/jpeg", signatureBytes);

        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(utilisateurService.getMyUserId()).thenReturn(gestionnaireId);
        when(utilisateurRepository.findUtilisateurById(gestionnaireId)).thenReturn(Optional.of(utilisateur));
        when(contratRepository.findById(contratId)).thenReturn(Optional.of(contrat));

        // Act
        String result = gestionnaireStageService.enregistrerSignature(signature, password, contratId);

        // Assert
        assertEquals("Signature sauvegardée", result);
        verify(contratRepository, times(1)).save(contrat);
        assertArrayEquals(signatureBytes, contrat.getSignatureGestionnaire());
    }

    @Test
    public void testEnregistrerSignature_WrongPassword() {
        // Arrange
        Long contratId = 1L;
        String password = "wrongPassword";
        MockMultipartFile signature = new MockMultipartFile("signature", "signature.jpg", "image/jpeg", "dummy".getBytes());

        GestionnaireStage utilisateur = new GestionnaireStage();
        utilisateur.setCredentials(new Credentials("unEmail@mail.com", password, Role.EMPLOYEUR));

        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(false);
        when(utilisateurService.getMyUserId()).thenReturn(utilisateur.getId());
        when(utilisateurRepository.findUtilisateurById(utilisateur.getId())).thenReturn(Optional.of(utilisateur));

        // Act & Assert
        assertThrows(BadCredentialsException.class, () -> {
            gestionnaireStageService.enregistrerSignature(signature, password, contratId);
        });
        verify(contratRepository, never()).save(any(Contrat.class));
    }

    @Test
    public void testEnregistrerSignature_NoUserFound() {
        // Arrange
        Long contratId = 1L;
        String password = "wrongPassword";
        MockMultipartFile signature = new MockMultipartFile("signature", "signature.jpg", "image/jpeg", "dummy".getBytes());

        GestionnaireStage utilisateur = new GestionnaireStage();
        utilisateur.setCredentials(new Credentials("unEmail@mail.com", password, Role.EMPLOYEUR));

        when(utilisateurService.getMyUserId()).thenReturn(utilisateur.getId());
        when(utilisateurRepository.findUtilisateurById(utilisateur.getId())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> {
            gestionnaireStageService.enregistrerSignature(signature, password, contratId);
        });
        verify(contratRepository, never()).save(any(Contrat.class));
    }

    @Test
    public void testEnregistrerSignature_NoContractFound() {
        // Arrange
        Long gestionnaireId = 1L;
        Long contratId = 1L;
        String password = "motDePasse";
        GestionnaireStage utilisateur = new GestionnaireStage();
        utilisateur.setCredentials(new Credentials("unEmail@mail.com", password, Role.EMPLOYEUR));

        Contrat contrat = new Contrat();
        contrat.setId(contratId);
        byte[] signatureBytes = "dummySignature".getBytes();
        MockMultipartFile signature = new MockMultipartFile("signature", "signature.jpg", "image/jpeg", signatureBytes);

        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(utilisateurService.getMyUserId()).thenReturn(gestionnaireId);
        when(utilisateurRepository.findUtilisateurById(gestionnaireId)).thenReturn(Optional.of(utilisateur));
        when(contratRepository.findById(contratId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ChangeSetPersister.NotFoundException.class, () -> {
            gestionnaireStageService.enregistrerSignature(signature, password, contratId);
        });
        verify(contratRepository, never()).save(any(Contrat.class));
    }

    @Test
    void testGetContratSignee_Success() throws IOException {
        // Arrange
        Contrat contrat = new Contrat();
        contrat.setPdf(createTemporaryPdf());
        contrat.setSignatureGestionnaire(createTemporaryPng());
        contrat.setSignatureEtudiant(createTemporaryPng());
        contrat.setSignatureEmployeur(createTemporaryPng());
        long contratId = 1L;
        when(contratRepository.findById(contratId)).thenReturn(Optional.of(contrat));

        // Act
        String result = gestionnaireStageService.getContratSignee(contratId);

        // Assert
        assertNotNull(result);
    }

    @Test
    void testGetContratSignee_ContractNotFound() {
        // Arrange
        long contratId = 1L;
        when(contratRepository.findById(contratId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> {
            gestionnaireStageService.getContratSignee(contratId);
        });
    }

    @Test
    void testGetContratSignee_MissingSignatures() throws IOException {
        // Arrange
        Contrat contrat = new Contrat();
        contrat.setPdf(createTemporaryPdf());
        contrat.setSignatureGestionnaire(createTemporaryPng());
        contrat.setSignatureEtudiant(createTemporaryPng());
        contrat.setSignatureEmployeur(createTemporaryPng());

        long contratId = 1L;
        contrat.setSignatureGestionnaire(null);
        when(contratRepository.findById(contratId)).thenReturn(Optional.of(contrat));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            gestionnaireStageService.getContratSignee(contratId);
        });
    }

    private byte[] createTemporaryPdf() throws IOException {
        File tempFile = File.createTempFile("test-document-", ".pdf");
        tempFile.deleteOnExit();

        Document document = new Document();

        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            PdfWriter.getInstance(document, fos);

            document.open();

            document.add(new Paragraph("Ceci est un document PDF de test."));

            document.close();
        }

        return Files.readAllBytes(tempFile.toPath());
    }

    private byte[] createTemporaryPng() throws IOException {
        BufferedImage bufferedImage = new BufferedImage(200, 100, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = bufferedImage.createGraphics();
        g.setPaint(Color.WHITE);
        g.fillRect(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());
        g.setPaint(Color.BLACK);
        g.drawString("Ceci est une image de test.", 20, 40);
        g.dispose();

        File tempFile = File.createTempFile("test-signature-", ".png");
        tempFile.deleteOnExit();
        javax.imageio.ImageIO.write(bufferedImage, "png", tempFile);
        return Files.readAllBytes(tempFile.toPath());
    }
    @Test
    public void testGetYearFirstContratUploaded_NormalSuccess() {
        // Arrange
        Date date1 = new Date(121, 5, 15);
        Date date2 = new Date(119, 3, 10);
        when(contratRepository.findDistinctCreatedAtForSignedContrats()).thenReturn(List.of(date1, date2));

        // Act
        Set<Integer> result = gestionnaireStageService.getYearFirstContratUploaded();

        // Assert
        assertEquals(Set.of(2019, 2021), result);
    }
    @Test
    public void testGetYearFirstContratUploaded_Empty() {
        // Arrange
        when(contratRepository.findDistinctCreatedAtForSignedContrats()).thenReturn(List.of());

        // Act
        Set<Integer> result = gestionnaireStageService.getYearFirstContratUploaded();

        // Assert
        assertEquals(Set.of(), result);
    }


}
