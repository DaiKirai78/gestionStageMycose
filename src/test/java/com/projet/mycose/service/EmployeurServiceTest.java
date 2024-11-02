package com.projet.mycose.service;

import com.projet.mycose.dto.ContratDTO;
import com.projet.mycose.dto.LoginDTO;
import com.projet.mycose.modele.*;
import com.projet.mycose.modele.auth.Credentials;
import com.projet.mycose.modele.auth.Role;
import com.projet.mycose.repository.ContratRepository;
import com.projet.mycose.repository.EmployeurRepository;
import com.projet.mycose.repository.OffreStageRepository;
import com.projet.mycose.dto.EmployeurDTO;
import com.projet.mycose.dto.OffreStageDTO;
import com.projet.mycose.repository.UtilisateurRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
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

import java.time.Year;
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
    private OffreStageRepository offreStageRepositoryMock;

    @Mock
    private ContratRepository contratRepositoryMock;

    @Mock
    private UtilisateurRepository utilisateurRepository;

    @Mock
    AuthenticationManager authenticationManager;
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
        String token = "unTokenValide";
        Long employeurId = 1L;
        int page = 0;
        PageRequest pageRequest = PageRequest.of(page, 10);
        Page<Contrat> contratsPage = new PageImpl<>(List.of(), pageRequest, 0);

        when(utilisateurService.getMyUserId()).thenReturn(employeurId);
        when(contratRepositoryMock.findContratsBySignatureEmployeurIsNullAndEmployeur_Id(employeurId, pageRequest)).thenReturn(contratsPage);

        // Act
        List<ContratDTO> resultat = employeurService.getAllContratsNonSignes(page);

        // Assert
        assertNull(resultat);

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
    public void testEnregistrerSignature_Success() throws Exception {
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
    public void testEnregistrerSignature_WrongPassword() throws Exception {
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
        String result = employeurService.enregistrerSignature(signature, password, contratId);

        // Assert
        assertEquals("Mauvais mot de passe", result);
        verify(contratRepositoryMock, never()).save(any(Contrat.class));
    }

    @Test
    public void testEnregistrerSignature_ContratNotFound() throws Exception {
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
        String result = employeurService.enregistrerSignature(signature, password, contratId);

        // Assert
        assertEquals("Aucun contrat trouvé", result);
        verify(contratRepositoryMock, never()).save(any(Contrat.class));
    }

    @Test
    public void testEnregistrerSignature_UtilisateurNotFound() throws Exception {
        // Arrange
        Long contratId = 1L;
        String password = "motDePasse";
        byte[] signatureBytes = "dummySignature".getBytes();
        MockMultipartFile signature = new MockMultipartFile("signature", signatureBytes);

        when(utilisateurService.getMyUserId()).thenReturn(1L);
        when(utilisateurRepository.findUtilisateurById(1L)).thenReturn(Optional.empty());

        // Act
        String result = employeurService.enregistrerSignature(signature, password, contratId);

        // Assert
        assertEquals("L'utilisateur n'existe pas", result);
        verify(contratRepositoryMock, never()).save(any(Contrat.class));
    }


}
