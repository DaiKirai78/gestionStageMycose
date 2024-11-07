package com.projet.mycose.service;

import com.projet.mycose.modele.*;
import com.projet.mycose.modele.auth.Credentials;
import com.projet.mycose.modele.auth.Role;
import com.projet.mycose.repository.ContratRepository;
import com.projet.mycose.repository.EtudiantRepository;
import com.projet.mycose.repository.OffreStageRepository;
import com.projet.mycose.dto.EtudiantDTO;
import com.projet.mycose.dto.OffreStageDTO;
import com.projet.mycose.repository.UtilisateurRepository;
import com.projet.mycose.exceptions.UserNotFoundException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.Year;
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
    private ContratRepository contratRepository;

    @Mock
    private UtilisateurRepository utilisateurRepository;

    @Mock
    private UtilisateurService utilisateurService;

    @Mock
    private OffreStageRepository offreStageRepositoryMock;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private EtudiantService etudiantService;

    @Test
    public void creationDeCompteAvecSucces() {
        //Arrange
        Etudiant etudiant = new Etudiant(1L, "Karim", "Mihoubi", "438-532-2729", "mihoubi@gmail.com", "Mimi123$", Programme.TECHNIQUE_INFORMATIQUE, Etudiant.ContractStatus.NO_CONTRACT);
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
    public void testGetStages_Success() {
        // Arrange
        String token = "unTokenValide";
        Long etudiantId = 1L;
        int page = 0;
        PageRequest pageRequest = PageRequest.of(page, 10);
        Employeur createur = new Employeur(2L, "unPrenom", "unNom", "514-222-0385", "courriel@courriel.com", "123123123", "uneEntreprise");

        FormulaireOffreStage mockFormulaireOffreStage = new FormulaireOffreStage("unTitreForm", "uneEntreprise", "unEmployeur", "unEmail@mail.com", "unsite.com", "uneLocalisation", "1000", "uneDescription", createur, OffreStage.Visibility.PUBLIC, null, OffreStage.Status.ACCEPTED, OffreStage.SessionEcole.AUTOMNE, Year.of(2022));
        FichierOffreStage mockFichierOffreStage = new FichierOffreStage("unTitreFichier", "uneEntreprise", "nom.pdf", "data".getBytes(), createur, OffreStage.Visibility.PUBLIC, null, OffreStage.Status.ACCEPTED, OffreStage.SessionEcole.AUTOMNE, Year.of(2022));
        List<OffreStage> mockOffresListe = new ArrayList<>();
        mockOffresListe.add(mockFormulaireOffreStage);
        mockOffresListe.add(mockFichierOffreStage);

        Page<OffreStage> offresPage = new PageImpl<>(mockOffresListe, pageRequest, 2);

        when(utilisateurService.getMyUserId()).thenReturn(etudiantId);
        when(offreStageRepositoryMock.findOffresByEtudiantId(etudiantId, pageRequest)).thenReturn(offresPage);

        // Act
        List<OffreStageDTO> result = etudiantService.getStages(page);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("unTitreForm", result.get(0).getTitle());
        assertEquals("unTitreFichier", result.get(1).getTitle());

        verify(utilisateurService, times(1)).getMyUserId();
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

        when(utilisateurService.getMyUserId()).thenReturn(etudiantId);
        when(offreStageRepositoryMock.findOffresByEtudiantId(etudiantId, pageRequest)).thenReturn(offresPage);

        // Act
        List<OffreStageDTO> result = etudiantService.getStages(page);

        // Assert
        assertNull(result);

        verify(utilisateurService, times(1)).getMyUserId();
        verify(offreStageRepositoryMock, times(1)).findOffresByEtudiantId(etudiantId, pageRequest);
    }

    @Test
    public void testGetAmountOfPage_NumberEndWithZero() {
        //Arrange
        when(utilisateurService.getMyUserId()).thenReturn(1L);
        when(offreStageRepositoryMock.countByEtudiantsId(1L)).thenReturn(30);

        //Act
        int nombrePage = etudiantService.getAmountOfPages();

        //Assert
        assertEquals(nombrePage, 3);
        verify(offreStageRepositoryMock, times(1)).countByEtudiantsId(1L);
    }

    @Test
    public void testGetAmountOfPage_NumberNotEndWithZero() {
        //Arrange
        when(utilisateurService.getMyUserId()).thenReturn(1L);
        when(offreStageRepositoryMock.countByEtudiantsId(1L)).thenReturn(43);

        //Act
        int nombrePage = etudiantService.getAmountOfPages();

        //Assert
        assertEquals(nombrePage, 5);
        verify(offreStageRepositoryMock, times(1)).countByEtudiantsId(1L);
    }

    @Test
    public void testGetStagesByRecherche_Success() {
        // Arrange
        String token = "unTokenValide";
        Long etudiantId = 1L;
        int page = 0;
        String recherche = "Developpeur";

        PageRequest pageRequest = PageRequest.of(page, 10);
        Employeur createur = new Employeur(2L, "unPrenom", "unNom", "514-222-0385", "courriel@courriel.com", "123123123", "uneEntreprise");

        FormulaireOffreStage mockFormulaireOffreStage = new FormulaireOffreStage("Titre Form", "Entreprise A", "Employeur A", "emailA@mail.com", "siteA.com", "Localisation A", "1000", "Description A", createur, OffreStage.Visibility.PUBLIC, null, OffreStage.Status.ACCEPTED, OffreStage.SessionEcole.AUTOMNE, Year.of(2022));
        FichierOffreStage mockFichierOffreStage = new FichierOffreStage("Titre Fichier", "Entreprise B", "nomB.pdf", "data".getBytes(), createur, OffreStage.Visibility.PUBLIC, null, OffreStage.Status.ACCEPTED, OffreStage.SessionEcole.AUTOMNE, Year.of(2022));
        List<OffreStage> mockOffresListe = new ArrayList<>();
        mockOffresListe.add(mockFormulaireOffreStage);
        mockOffresListe.add(mockFichierOffreStage);

        Page<OffreStage> offresPage = new PageImpl<>(mockOffresListe, pageRequest, mockOffresListe.size());

        when(utilisateurService.getMyUserId()).thenReturn(etudiantId);
        when(offreStageRepositoryMock.findOffresByEtudiantIdWithSearch(etudiantId, recherche, pageRequest)).thenReturn(offresPage);

        // Act
        List<OffreStageDTO> result = etudiantService.getStagesByRecherche(page, recherche);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Titre Form", result.get(0).getTitle());
        assertEquals("Titre Fichier", result.get(1).getTitle());

        verify(utilisateurService, times(1)).getMyUserId();
        verify(offreStageRepositoryMock, times(1)).findOffresByEtudiantIdWithSearch(etudiantId, recherche, pageRequest);
    }

    @Test
    public void testGetStagesByRecherche_Null() {
        // Arrange
        String token = "unTokenValide";
        Long etudiantId = 1L;
        int page = 0;
        String recherche = "NonExistant";

        PageRequest pageRequest = PageRequest.of(page, 10);
        Page<OffreStage> offresPage = new PageImpl<>(List.of(), pageRequest, 0);  // Page vide

        when(utilisateurService.getMyUserId()).thenReturn(etudiantId);
        when(offreStageRepositoryMock.findOffresByEtudiantIdWithSearch(etudiantId, recherche, pageRequest)).thenReturn(offresPage);

        // Act
        List<OffreStageDTO> result = etudiantService.getStagesByRecherche(page, recherche);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(utilisateurService, times(1)).getMyUserId();
        verify(offreStageRepositoryMock, times(1)).findOffresByEtudiantIdWithSearch(etudiantId, recherche, pageRequest);
    }

    @Test
    public void testGetEtudiantsContratEnDemande() {
        // Arrange
        Etudiant etudiant = new Etudiant(1L, "Karim", "Mihoubi", "438-532-2729", "mihoubi@gmail.com", "Mimi123$", Programme.TECHNIQUE_INFORMATIQUE, Etudiant.ContractStatus.PENDING);
        List<EtudiantDTO> etudiantDTOList = new ArrayList<>();
        etudiantDTOList.add(EtudiantDTO.toDTO(etudiant));
        List<Etudiant> etudiantList = new ArrayList<>();
        etudiantList.add(etudiant);
        when(etudiantRepositoryMock.findEtudiantsByContractStatusEquals(Etudiant.ContractStatus.PENDING)).thenReturn(etudiantList);

        // Act
        List<EtudiantDTO> vraieEtudiantsList = etudiantService.getEtudiantsContratEnDemande();

        // Assert
        assertEquals(etudiantDTOList.getFirst().getId(), vraieEtudiantsList.getFirst().getId());
        assertEquals(etudiantDTOList.getLast().getId(), vraieEtudiantsList.getLast().getId());
        assertEquals(etudiantDTOList.getFirst().getContractStatus(), vraieEtudiantsList.getFirst().getContractStatus());
        assertEquals(etudiantDTOList.getLast().getContractStatus(), vraieEtudiantsList.getLast().getContractStatus());
        assertEquals(etudiantDTOList.size(), vraieEtudiantsList.size());
        verify(etudiantRepositoryMock, times(1)).findEtudiantsByContractStatusEquals(Etudiant.ContractStatus.PENDING);
    }

    @Test
    public void testGetAmountOfPageEtudiantContratEnDemande_NumberEndWithZero() {
        //Arrange
        when(etudiantRepositoryMock.countByContractStatusEquals(Etudiant.ContractStatus.PENDING)).thenReturn(30);

        //Act
        int nombrePage = etudiantService.getEtudiantsSansContratPages();

        //Assert
        assertEquals(nombrePage, 3);
        verify(etudiantRepositoryMock, times(1)).countByContractStatusEquals(Etudiant.ContractStatus.PENDING);
    }

    @Test
    public void testGetAmountOfPageEtudiantContratEnDemande_NumberNotEndWithZero() {
        //Arrange
        when(etudiantRepositoryMock.countByContractStatusEquals(Etudiant.ContractStatus.PENDING)).thenReturn(43);

        //Act
        int nombrePage = etudiantService.getEtudiantsSansContratPages();

        //Assert
        assertEquals(nombrePage, 5);
        verify(etudiantRepositoryMock, times(1)).countByContractStatusEquals(Etudiant.ContractStatus.PENDING);
    }

    @Test
    public void testGetAmountOfPageEtudiantContratEnDemande_NoStudent() {
        //Arrange
        when(etudiantRepositoryMock.countByContractStatusEquals(Etudiant.ContractStatus.PENDING)).thenReturn(0);

        //Act
        int nombrePage = etudiantService.getEtudiantsSansContratPages();

        //Assert
        assertEquals(nombrePage, 0);
        verify(etudiantRepositoryMock, times(1)).countByContractStatusEquals(Etudiant.ContractStatus.PENDING);
    }

    @Test
    public void testEnregistrerSignature_Success() throws IOException {
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

        when(utilisateurService.getMyUserId()).thenReturn(gestionnaireId);
        when(utilisateurRepository.findUtilisateurById(gestionnaireId)).thenReturn(Optional.of(utilisateur));
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(contratRepository.findById(contratId)).thenReturn(Optional.of(contrat));

        // Act
        String result = etudiantService.enregistrerSignature(signature, password, contratId);

        // Assert
        assertEquals("Signature sauvegardée", result);
        verify(utilisateurService, times(1)).getMyUserId();
        verify(utilisateurRepository, times(1)).findUtilisateurById(gestionnaireId);
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(contratRepository, times(1)).findById(contratId);
        verify(contratRepository, times(1)).save(contrat);
        assertArrayEquals(signatureBytes, contrat.getSignatureEtudiant());
    }
    @Test
    public void testEnregistrerSignature_WrongPassword() {
        // Arrange
        Long gestionnaireId = 3L;
        Long contratId = 1L;
        String password = "wrongPassword";
        MockMultipartFile signature = new MockMultipartFile("signature", "signature.jpg", "image/jpeg", "dummy".getBytes());
        GestionnaireStage utilisateur = new GestionnaireStage();
        utilisateur.setCredentials(new Credentials("unEmail@mail.com", password, Role.EMPLOYEUR));
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(false);
        when(utilisateurService.getMyUserId()).thenReturn(gestionnaireId);
        when(utilisateurRepository.findUtilisateurById(gestionnaireId)).thenReturn(Optional.of(utilisateur));
        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                etudiantService.enregistrerSignature(signature, password, contratId)
        );

        assertEquals("401 UNAUTHORIZED \"Email ou mot de passe invalide.\"", exception.getMessage());

        verify(utilisateurService, times(1)).getMyUserId();
        verify(utilisateurRepository, times(1)).findUtilisateurById(gestionnaireId);
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(contratRepository, never()).findById(anyLong());
        verify(contratRepository, never()).save(any(Contrat.class));
    }
    @Test
    public void testEnregistrerSignature_NoUserFound() {
        // Arrange
        Long gestionnaireId = 1L;
        Long contratId = 1L;
        String password = "wrongPassword";
        MockMultipartFile signature = new MockMultipartFile("signature", "signature.jpg", "image/jpeg", "dummy".getBytes());
        GestionnaireStage utilisateur = new GestionnaireStage();
        utilisateur.setCredentials(new Credentials("unEmail@mail.com", password, Role.EMPLOYEUR));
        when(utilisateurService.getMyUserId()).thenReturn(gestionnaireId);
        when(utilisateurRepository.findUtilisateurById(gestionnaireId)).thenReturn(Optional.empty());
        // Act & Assert
        Exception exception = assertThrows(UserNotFoundException.class, () ->
                etudiantService.enregistrerSignature(signature, password, contratId)
        );

        assertEquals("Utilisateur not found", exception.getMessage());


        verify(utilisateurService, times(1)).getMyUserId();
        verify(utilisateurRepository, times(1)).findUtilisateurById(gestionnaireId);
        verify(authenticationManager, never()).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(contratRepository, never()).findById(anyLong());
        verify(contratRepository, never()).save(any(Contrat.class));
    }
    @Test
    public void testEnregistrerSignature_NoContractFound() {
        // Arrange
        Long gestionnaireId = 1L;
        Long contratId = 1L;
        String password = "motDePasse";
        //Gestionnaire de stage parce qu'on a un empty builder et c'est un mock anyways
        GestionnaireStage utilisateur = new GestionnaireStage();
        utilisateur.setCredentials(new Credentials("unEmail@mail.com", password, Role.ETUDIANT));
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
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                etudiantService.enregistrerSignature(signature, password, contratId)
        );

        assertEquals("404 NOT_FOUND \"Contrat not found\"", exception.getMessage());

        verify(utilisateurService, times(1)).getMyUserId();
        verify(utilisateurRepository, times(1)).findUtilisateurById(gestionnaireId);
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(contratRepository, times(1)).findById(contratId);
        verify(contratRepository, never()).save(any(Contrat.class));
    }
}
