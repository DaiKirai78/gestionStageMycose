package com.projet.mycose.service;

import com.projet.mycose.dto.ContratDTO;
import com.projet.mycose.dto.EtudiantDTO;
import com.projet.mycose.dto.FicheEvaluationStagiaireDTO;
import com.projet.mycose.exceptions.AuthenticationException;
import com.projet.mycose.exceptions.ResourceNotFoundException;
import com.projet.mycose.exceptions.UserNotFoundException;
import com.projet.mycose.modele.*;
import com.projet.mycose.modele.auth.Credentials;
import com.projet.mycose.modele.auth.Role;
import com.projet.mycose.repository.ContratRepository;
import com.projet.mycose.repository.EmployeurRepository;
import com.projet.mycose.dto.EmployeurDTO;
import com.projet.mycose.repository.FicheEvaluationStagiaireRepository;
import com.projet.mycose.repository.UtilisateurRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
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
import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    private ContratRepository contratRepositoryMock;

    @Mock
    private UtilisateurRepository utilisateurRepository;

    @Mock
    AuthenticationManager authenticationManager;

    @Mock
    FicheEvaluationStagiaireRepository ficheEvaluationStagiaireRepositoryMock;

    @Mock
    ModelMapper modelMapperMock;
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
    public void testGetAllContratsNonSignees_Success() {
        String token = "unTokenValide";
        Long employeurId = 1L;
        int page = 0;
        PageRequest pageRequest = PageRequest.of(page, 10);
        Employeur employeur = new Employeur(2L,
                "unPrenom",
                "unNom",
                "514-222-0385",
                "courriel@courriel.com",
                "123123123",
                "uneEntreprise"
        );

        Contrat contratMock = new Contrat();
        contratMock.setId(2L);
        List<Contrat> contratListeMock = new ArrayList<>();
        contratListeMock.add(contratMock);

        Page<Contrat> contratsPage = new PageImpl<>(contratListeMock, pageRequest, 1);

        when(utilisateurService.getMyUserId()).thenReturn(employeurId);
        when(contratRepositoryMock.findContratsBySignatureEmployeurIsNullAndEmployeur_Id(employeurId, pageRequest)).thenReturn(contratsPage);

        // Act
        List<ContratDTO> resultat = employeurService.getAllContratsNonSignes(page);

        // Assert
        assertNotNull(resultat);
        assertEquals(1, resultat.size());

        verify(utilisateurService, times(1)).getMyUserId();
        verify(contratRepositoryMock, times(1)).findContratsBySignatureEmployeurIsNullAndEmployeur_Id(employeurId, pageRequest);
    }

    @Test
    public void testGetAllContratsNonSignees_Null() {
        // Arrange
        Long employeurId = 1L;
        int page = 0;
        PageRequest pageRequest = PageRequest.of(page, 10);
        Page<Contrat> contratsPage = new PageImpl<>(List.of(), pageRequest, 0);

        when(utilisateurService.getMyUserId()).thenReturn(employeurId);
        when(contratRepositoryMock.findContratsBySignatureEmployeurIsNullAndEmployeur_Id(employeurId, pageRequest)).thenReturn(contratsPage);

        // Act
        List<ContratDTO> resultat = employeurService.getAllContratsNonSignes(page);

        // Assert
        assertEquals(resultat, List.of());

        verify(utilisateurService, times(1)).getMyUserId();
        verify(contratRepositoryMock, times(1)).findContratsBySignatureEmployeurIsNullAndEmployeur_Id(employeurId, pageRequest);
    }

    @Test
    public void testGetAmountOfPageContratsNonSignees_NumberEndWithZero() {
        //Arrange
        when(utilisateurService.getMyUserId()).thenReturn(1L);
        when(contratRepositoryMock.countBySignatureEmployeurIsNullAndEmployeurId(1L)).thenReturn(30);

        //Act
        int nombrePages = employeurService.getAmountOfPagesOfContractNonSignees();

        //Assert
        assertEquals(nombrePages, 3);
        verify(contratRepositoryMock, times(1)).countBySignatureEmployeurIsNullAndEmployeurId(1L);
    }

    @Test
    public void testGetAmountOfPageContratsNonSignees_NumberNotEndWithZero() {
        //Arrange
        when(utilisateurService.getMyUserId()).thenReturn(1L);
        when(contratRepositoryMock.countBySignatureEmployeurIsNullAndEmployeurId(1L)).thenReturn(43);

        //Act
        int nombrePages = employeurService.getAmountOfPagesOfContractNonSignees();

        //Assert
        assertEquals(nombrePages, 5);
        verify(contratRepositoryMock, times(1)).countBySignatureEmployeurIsNullAndEmployeurId(1L);
    }

    @Test
    public void testEnregistrerSignature_Success() throws IOException {
        // Arrange
        Long employeurId = 1L;
        Long contratId = 1L;
        String password = "motDePasse";
        Employeur employeur = new Employeur();
        employeur.setCredentials(new Credentials("unEmail@mail.com", password, Role.EMPLOYEUR));
        Contrat contrat = new Contrat();
        contrat.setId(contratId);
        byte[] signatureBytes = "dummySignature".getBytes();
        MockMultipartFile signature = new MockMultipartFile("signature", "signature.jpg", "image/jpeg", signatureBytes);

        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(utilisateurService.getMyUserId()).thenReturn(employeurId);
        when(utilisateurRepository.findUtilisateurById(employeurId)).thenReturn(Optional.of(employeur));
        when(contratRepositoryMock.findById(contratId)).thenReturn(Optional.of(contrat));

        // Act
        String result = employeurService.enregistrerSignature(signature, password, contratId);

        // Assert
        assertEquals("Signature sauvegardée", result);
        verify(contratRepositoryMock, times(1)).save(contrat);
        assertArrayEquals(signatureBytes, contrat.getSignatureEmployeur());
    }

    @Test
    public void testEnregistrerSignature_WrongPassword() {
        // Arrange
        Long contratId = 1L;
        String password = "motDePasse";
        byte[] signatureBytes = "dummySignature".getBytes();
        MockMultipartFile signature = new MockMultipartFile("signature", signatureBytes);

        Employeur employeur = new Employeur();
        employeur.setCredentials(new Credentials("unEmail@mail.com", password, Role.EMPLOYEUR));

        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(false);
        when(utilisateurService.getMyUserId()).thenReturn(1L);
        when(utilisateurRepository.findUtilisateurById(1L)).thenReturn(Optional.of(employeur));

        // Act
        AuthenticationException exception = assertThrows(AuthenticationException.class, () ->
                employeurService.enregistrerSignature(signature, password, contratId)
        );

        // Assert
        assertEquals("Email ou mot de passe invalide.", exception.getMessage());
        verify(contratRepositoryMock, never()).save(any(Contrat.class));
    }

    @Test
    public void testEnregistrerSignature_ContratNotFound() {
        // Arrange
        Long contratId = 1L;
        String password = "motDePasse";
        byte[] signatureBytes = "dummySignature".getBytes();
        MockMultipartFile signature = new MockMultipartFile("signature", signatureBytes);

        Employeur employeur = new Employeur();
        employeur.setCredentials(new Credentials("unEmail@mail.com", password, Role.EMPLOYEUR));

        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(utilisateurService.getMyUserId()).thenReturn(1L);
        when(utilisateurRepository.findUtilisateurById(1L)).thenReturn(Optional.of(employeur));
        when(contratRepositoryMock.findById(contratId)).thenReturn(Optional.empty());

        // Act
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                employeurService.enregistrerSignature(signature, password, contratId)
        );

        // Assert
        assertEquals("Contrat non trouvé", exception.getMessage());
        verify(contratRepositoryMock, never()).save(any(Contrat.class));
    }

    @Test
    public void testEnregistrerSignature_UtilisateurNotFound() {
        // Arrange
        Long contratId = 1L;
        String password = "motDePasse";
        byte[] signatureBytes = "dummySignature".getBytes();
        MockMultipartFile signature = new MockMultipartFile("signature", signatureBytes);

        when(utilisateurService.getMyUserId()).thenReturn(1L);
        when(utilisateurRepository.findUtilisateurById(1L)).thenReturn(Optional.empty());

        // Act
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () ->
                employeurService.enregistrerSignature(signature, password, contratId)
        );

        // Assert
        assertEquals("Utilisateur not found", exception.getMessage());
        verify(contratRepositoryMock, never()).save(any(Contrat.class));
    }

    @Test
    public void testEnregistrerFicheEvaluationStagiaire_Success() throws AccessDeniedException {
        // Arrange
        Long employeurId = 1L;
        Long etudiantId = 2L;

        Employeur employeur = new Employeur();
        employeur.setId(employeurId);
        Etudiant etudiant = new Etudiant();
        etudiant.setId(etudiantId);

        Contrat contrat =  new Contrat();

        byte[] signatureBytes = "dummySignature".getBytes();
        MockMultipartFile signature = new MockMultipartFile("signature", "signature.jpg", "image/jpeg", signatureBytes);

        FicheEvaluationStagiaireDTO ficheEvaluationStagiaireDTO = new FicheEvaluationStagiaireDTO();
        ficheEvaluationStagiaireDTO.setId(3L);
        ficheEvaluationStagiaireDTO.setNumeroTelephone("555-444-3333");
        ficheEvaluationStagiaireDTO.setFonctionSuperviseur("Manager");

        when(utilisateurService.getMeUtilisateur()).thenReturn(employeur);
        when(utilisateurRepository.findUtilisateurById(etudiantId)).thenReturn(Optional.of(etudiant));
        when(modelMapperMock.map(ficheEvaluationStagiaireDTO, FicheEvaluationStagiaire.class))
                .thenReturn(new FicheEvaluationStagiaire());
        when(contratRepositoryMock.findContratActiveOfEtudiantAndEmployeur(etudiantId, employeurId, Etudiant.ContractStatus.ACTIVE)).thenReturn(Optional.of(contrat));

        ArgumentCaptor<FicheEvaluationStagiaire> ficheCaptor = ArgumentCaptor.forClass(FicheEvaluationStagiaire.class);

        // Act
        employeurService.enregistrerFicheEvaluationStagiaire(ficheEvaluationStagiaireDTO, etudiantId, signature);

        // Assert
        verify(utilisateurService, times(1)).getMeUtilisateur();
        verify(utilisateurRepository, times(1)).findUtilisateurById(etudiantId);
        verify(ficheEvaluationStagiaireRepositoryMock, times(1)).save(ficheCaptor.capture());

        FicheEvaluationStagiaire ficheSaved = ficheCaptor.getValue();
        assertNotNull(ficheSaved);
        assertEquals(employeur, ficheSaved.getEmployeur());
        assertEquals(etudiant, ficheSaved.getEtudiant());
        assertEquals(contrat, ficheSaved.getContrat());
    }

    @Test
    public void testEnregistrerFicheEvaluationStagiaire_AccessDenied() throws AccessDeniedException {
        // Arrange
        Long etudiantId = 2L;
        FicheEvaluationStagiaireDTO ficheEvaluationStagiaireDTO = new FicheEvaluationStagiaireDTO();
        ficheEvaluationStagiaireDTO.setId(3L);
        ficheEvaluationStagiaireDTO.setNumeroTelephone("555-444-3333");
        ficheEvaluationStagiaireDTO.setFonctionSuperviseur("Manager");

        byte[] signatureBytes = "dummySignature".getBytes();
        MockMultipartFile signature = new MockMultipartFile("signature", "signature.jpg", "image/jpeg", signatureBytes);

        when(utilisateurService.getMeUtilisateur()).thenThrow(new AccessDeniedException("Access Denied"));

        // Act
        AuthenticationException exception = assertThrows(AuthenticationException.class, () ->
                employeurService.enregistrerFicheEvaluationStagiaire(ficheEvaluationStagiaireDTO, etudiantId, signature)
        );

        // Assert
        assertEquals("Problème d'authentification", exception.getMessage());
        verify(utilisateurRepository, never()).findUtilisateurById(anyLong());
        verify(ficheEvaluationStagiaireRepositoryMock, never()).save(any(FicheEvaluationStagiaire.class));
    }

    @Test
    public void testEnregistrerFicheEvaluationStagiaire_UserNotFound() throws AccessDeniedException {
        // Arrange
        Long etudiantId = 2L;
        Employeur employeur = new Employeur();
        employeur.setId(1L);

        byte[] signatureBytes = "dummySignature".getBytes();
        MockMultipartFile signature = new MockMultipartFile("signature", "signature.jpg", "image/jpeg", signatureBytes);

        FicheEvaluationStagiaireDTO ficheEvaluationStagiaireDTO = new FicheEvaluationStagiaireDTO();
        ficheEvaluationStagiaireDTO.setId(3L);
        ficheEvaluationStagiaireDTO.setNumeroTelephone("555-444-3333");
        ficheEvaluationStagiaireDTO.setFonctionSuperviseur("Manager");

        when(utilisateurService.getMeUtilisateur()).thenReturn(employeur);
        when(utilisateurRepository.findUtilisateurById(etudiantId)).thenReturn(Optional.empty());

        // Act
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () ->
                employeurService.enregistrerFicheEvaluationStagiaire(ficheEvaluationStagiaireDTO, etudiantId, signature)
        );

        // Assert
        assertEquals("Utilisateur not found", exception.getMessage());
        verify(ficheEvaluationStagiaireRepositoryMock, never()).save(any(FicheEvaluationStagiaire.class));
    }

    @Test
    public void testEnregistrerFicheEvaluationStagiaire_ContratNotFound() throws AccessDeniedException {
        // Arrange
        Long etudiantId = 2L;
        Etudiant etudiant = new Etudiant();
        etudiant.setId(etudiantId);
        Employeur employeur = new Employeur();
        employeur.setId(1L);

        byte[] signatureBytes = "dummySignature".getBytes();
        MockMultipartFile signature = new MockMultipartFile("signature", "signature.jpg", "image/jpeg", signatureBytes);

        FicheEvaluationStagiaireDTO ficheEvaluationStagiaireDTO = new FicheEvaluationStagiaireDTO();
        ficheEvaluationStagiaireDTO.setId(3L);
        ficheEvaluationStagiaireDTO.setNumeroTelephone("555-444-3333");
        ficheEvaluationStagiaireDTO.setFonctionSuperviseur("Manager");

        when(utilisateurService.getMeUtilisateur()).thenReturn(employeur);
        when(utilisateurRepository.findUtilisateurById(etudiantId)).thenReturn(Optional.of(etudiant));
        when(modelMapperMock.map(ficheEvaluationStagiaireDTO, FicheEvaluationStagiaire.class))
                .thenReturn(new FicheEvaluationStagiaire());
        when(contratRepositoryMock.findContratActiveOfEtudiantAndEmployeur(etudiantId, employeur.getId(), Etudiant.ContractStatus.ACTIVE)).thenReturn(Optional.empty());

        // Act
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                employeurService.enregistrerFicheEvaluationStagiaire(ficheEvaluationStagiaireDTO, etudiantId, signature)
        );

        // Assert
        assertEquals("Contrat de l'étudiant non trouvé", exception.getMessage());
        verify(ficheEvaluationStagiaireRepositoryMock, never()).save(any(FicheEvaluationStagiaire.class));
    }

    @Test
    public void testGetAllEtudiantsNonEvalues_Success() throws AccessDeniedException {
        // Arrange
        Long employeurId = 1L;
        Employeur employeur = new Employeur();
        employeur.setId(employeurId);

        List<Etudiant> listeFind = new ArrayList<>();
        Etudiant etudiant1 = new Etudiant();
        etudiant1.setId(2L);
        etudiant1.setNom("Albert");

        Etudiant etudiant2 = new Etudiant();
        etudiant2.setId(3L);
        etudiant2.setNom("Newton");

        listeFind.add(etudiant1);
        listeFind.add(etudiant2);

        EtudiantDTO dto1 = new EtudiantDTO();
        dto1.setId(etudiant1.getId());
        dto1.setNom(etudiant1.getNom());

        EtudiantDTO dto2 = new EtudiantDTO();
        dto2.setId(etudiant2.getId());
        dto2.setNom(etudiant2.getNom());

        PageRequest pageRequest = PageRequest.of(1, 10);
        Page<Etudiant> pageFind = new PageImpl<>(listeFind, pageRequest, 2);

        when(utilisateurService.getMeUtilisateur()).thenReturn(employeur);
        when(ficheEvaluationStagiaireRepositoryMock.findAllEtudiantWhereNotEvaluated(employeurId, Etudiant.ContractStatus.ACTIVE, pageRequest)).thenReturn(pageFind);

        when(modelMapperMock.map(etudiant1, EtudiantDTO.class))
                .thenReturn(dto1);
        when(modelMapperMock.map(etudiant2, EtudiantDTO.class))
                .thenReturn(dto2);

        // Act
        Page<EtudiantDTO> listeRetourne = employeurService.getAllEtudiantsNonEvalues(employeurId, 1);

        //Assert
        verify(ficheEvaluationStagiaireRepositoryMock, times(1)).findAllEtudiantWhereNotEvaluated(employeurId, Etudiant.ContractStatus.ACTIVE, pageRequest);
        assertEquals(listeRetourne.getContent().size(), 2);
        assertEquals(listeRetourne.getContent().get(0).getId(), 2L);
        assertEquals(listeRetourne.getContent().get(0).getNom(), "Albert");
        assertEquals(listeRetourne.getContent().get(1).getId(), 3L);
        assertEquals(listeRetourne.getContent().get(1).getNom(), "Newton");
    }

    @Test
    public void testGetAllEtudiantsNonEvalues_AccessDenied() throws AccessDeniedException {
        // Arrange
        Long employeurId = 1L;

        when(utilisateurService.getMeUtilisateur()).thenThrow(new AccessDeniedException("Problème d'authentification"));

        // Act

        AuthenticationException exception = assertThrows(AuthenticationException.class, () ->
                employeurService.getAllEtudiantsNonEvalues(employeurId, 1)
        );

        // Assert
        assertEquals("Problème d'authentification", exception.getMessage());
        verify(ficheEvaluationStagiaireRepositoryMock, never()).save(any(FicheEvaluationStagiaire.class));
    }

    @Test
    public void testGetAllEtudiantsNonEvalues_ListeNotFound() throws AccessDeniedException {
        // Arrange
        Long employeurId = 1L;
        Employeur employeur = new Employeur();
        employeur.setId(employeurId);

        when(utilisateurService.getMeUtilisateur()).thenReturn(employeur);
        when(ficheEvaluationStagiaireRepositoryMock.findAllEtudiantWhereNotEvaluated(eq(employeurId), eq(Etudiant.ContractStatus.ACTIVE), any(PageRequest.class))).thenReturn(Page.empty());

        // Act
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                employeurService.getAllEtudiantsNonEvalues(employeurId, 1)
        );

        // Assert
        assertEquals("Aucun Étudiant Trouvé", exception.getMessage());
        verify(ficheEvaluationStagiaireRepositoryMock, never()).save(any(FicheEvaluationStagiaire.class));
    }



}
