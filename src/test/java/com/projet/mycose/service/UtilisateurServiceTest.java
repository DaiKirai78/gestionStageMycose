package com.projet.mycose.service;

import com.projet.mycose.modele.*;
import com.projet.mycose.repository.EmployeurRepository;
import com.projet.mycose.repository.EnseignantRepository;
import com.projet.mycose.repository.EtudiantRepository;
import com.projet.mycose.repository.UtilisateurRepository;
import com.projet.mycose.security.JwtTokenProvider;
import com.projet.mycose.security.exception.UserNotFoundException;
import com.projet.mycose.service.dto.EmployeurDTO;
import com.projet.mycose.service.dto.EnseignantDTO;
import com.projet.mycose.service.dto.EtudiantDTO;
import com.projet.mycose.service.dto.LoginDTO;
import com.projet.mycose.service.dto.UtilisateurDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.AccessDeniedException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UtilisateurServiceTest {

    @Mock
    private UtilisateurRepository utilisateurRepository;

    @Mock
    private EtudiantRepository etudiantRepository;

    @Mock
    private EmployeurRepository employeurRepository;

    @Mock
    private EnseignantRepository enseignantRepository;

    @Mock
    private AuthenticationManager authenticationManagerMock;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private UtilisateurService utilisateurService;

    private Etudiant etudiant;
    private Employeur employeur;
    private Enseignant enseignant;

    @BeforeEach
    void setUp() {
        etudiant = Etudiant.builder()
                .id(1L)
                .prenom("John")
                .nom("Doe")
                .numeroDeTelephone("123-456-7890")
                .courriel("john.doe@example.com")
                .motDePasse("password123")
                .programme(Programme.TECHNIQUE_INFORMATIQUE)
                .build();

        employeur = Employeur.builder()
                .id(2L)
                .prenom("Jane")
                .nom("Smith")
                .numeroDeTelephone("098-765-4321")
                .courriel("jane.smith@techcorp.com")
                .motDePasse("securePass!")
                .entrepriseName("TechCorp")
                .build();

        enseignant = Enseignant.builder()
                .id(3L)
                .prenom("Emily")
                .nom("Clark")
                .numeroDeTelephone("555-555-5555")
                .courriel("emily.clark@university.edu")
                .motDePasse("teach123")
                .build();
    }

    @Test
    public void testAuthentifierUtilisateur_Success() {
        // Arrange
        LoginDTO loginDTO = new LoginDTO("mihoubi@gmail.com", "Mimi123$");
        Authentication authenticationMock = mock(Authentication.class);

        when(authenticationManagerMock.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authenticationMock);
        when(jwtTokenProvider.generateToken(authenticationMock)).thenReturn("un-token-mocked");

        // Act
        String token = utilisateurService.authentificationUtilisateur(loginDTO);

        // Assert
        assertEquals("un-token-mocked", token);
        verify(authenticationManagerMock).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtTokenProvider).generateToken(authenticationMock);
    }

    @Test
    void testAuthentificationEtudiant_Failure_BadCredentials() {
        // Arrange
        LoginDTO loginDTO = new LoginDTO("etudiant@example.com", "wrongPassword");

        when(authenticationManagerMock.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new IllegalArgumentException("Le mot de passe de l'utilisateur est invalide"));

        // Act & Assert
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            utilisateurService.authentificationUtilisateur(loginDTO);
        });

        assertEquals("Le mot de passe de l'utilisateur est invalide", thrown.getMessage());

        verify(authenticationManagerMock).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtTokenProvider, never()).generateToken(any());
    }

    @Test
    public void testGetMe_SuccesEtudiant() throws Exception {
        // Arrange
        String token = "Bearer valid_token";
        String email = "mihoubi@gmail.com";

        when(jwtTokenProvider.getEmailFromJWT("valid_token")).thenReturn(email);
        when(utilisateurRepository.findUtilisateurByCourriel(email)).thenReturn(Optional.of(etudiant));

        // Act
        UtilisateurDTO result = utilisateurService.getMe(token);

        // Assert
        assertNotNull(result);
        assertTrue(result instanceof EtudiantDTO);
        verify(jwtTokenProvider).getEmailFromJWT("valid_token");
    }

    @Test
    public void testGetMe_TokenNull() {
        // Arrange
        String token = null;

        // Act & Assert
        assertThrows(AccessDeniedException.class, () -> {
            utilisateurService.getMe(token);
        });
    }

    @Test
    public void testGetMe_TokenSansBearer() {
        // Arrange
        String token = "invalid_token";

        // Act & Assert
        assertThrows(AccessDeniedException.class, () -> {
            utilisateurService.getMe(token);
        });
    }

    @Test
    public void testGetMe_UtilisateurNonTrouve() throws AccessDeniedException {
        // Arrange
        String token = "Bearer valid_token";
        String email = "inconnu@exemple.com";

        when(jwtTokenProvider.getEmailFromJWT("valid_token")).thenReturn(email);
        when(utilisateurRepository.findUtilisateurByCourriel(email)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> {
            utilisateurService.getMe(token);
        });

        verify(jwtTokenProvider).getEmailFromJWT("valid_token");
        verify(utilisateurRepository).findUtilisateurByCourriel(email);
        verify(modelMapper, never()).map(any(), any());
    }

    @Test
    public void testGetMe_TokenInvalide() throws AccessDeniedException {
        // Arrange
        String token = "Bearer invalid_token";

        when(jwtTokenProvider.getEmailFromJWT("invalid_token")).thenThrow(new AccessDeniedException("Token Invalide"));

        // Act & Assert
        assertThrows(AccessDeniedException.class, () -> {
            utilisateurService.getMe(token);
        });

        verify(jwtTokenProvider).getEmailFromJWT("invalid_token");
        verify(utilisateurRepository, never()).findUtilisateurByCourriel(anyString());
        verify(modelMapper, never()).map(any(), any());
    }

    @Test
    void testGetEtudiantDTO_Success() {
        // Arrange
        Long etudiantId = etudiant.getId();
        when(etudiantRepository.findById(etudiantId)).thenReturn(Optional.of(etudiant));
        EtudiantDTO etudiantDTO = EtudiantDTO.toDTO(etudiant);

        // Act
        EtudiantDTO result = utilisateurService.getEtudiantDTO(etudiantId);

        // Assert
        assertNotNull(result, "The returned EtudiantDTO should not be null");
        assertEquals(etudiantDTO.getId(), result.getId(), "The returned EtudiantDTO should match the expected DTO");
        verify(etudiantRepository, times(1)).findById(etudiantId);
    }

    @Test
    void testGetEtudiantDTO_NotFound() {
        // Arrange
        Long etudiantId = 999L;
        when(etudiantRepository.findById(etudiantId)).thenReturn(Optional.empty());

        // Act
        EtudiantDTO result = utilisateurService.getEtudiantDTO(etudiantId);

        // Assert
        assertNotNull(result, "The returned EtudiantDTO should not be null");
        assertEquals(EtudiantDTO.empty().getId(), result.getId(), "The returned EtudiantDTO should be empty");
        verify(etudiantRepository, times(1)).findById(etudiantId);
        verify(modelMapper, never()).map(any(), eq(EtudiantDTO.class));
    }

    @Test
    void testGetEmployeurDTO_Success() {
        // Arrange
        Long employeurId = employeur.getId();
        when(employeurRepository.findById(employeurId)).thenReturn(Optional.of(employeur));
        EmployeurDTO employeurDTO = EmployeurDTO.toDTO(employeur);

        // Act
        EmployeurDTO result = utilisateurService.getEmployeurDTO(employeurId);

        // Assert
        assertNotNull(result, "The returned EmployeurDTO should not be null");
        assertEquals(employeurDTO.getId(), result.getId(), "The returned EmployeurDTO should match the expected DTO");
        verify(employeurRepository, times(1)).findById(employeurId);
    }

    @Test
    void testGetEmployeurDTO_NotFound() {
        // Arrange
        Long employeurId = 999L;
        EmployeurDTO employeurDTO = EmployeurDTO.empty();
        when(employeurRepository.findById(employeurId)).thenReturn(Optional.empty());

        // Act
        EmployeurDTO result = utilisateurService.getEmployeurDTO(employeurId);

        // Assert
        assertNotNull(result, "The returned EmployeurDTO should not be null");
        assertEquals(EmployeurDTO.empty().getId(), result.getId(), "The returned EtudiantDTO should be empty");
        verify(employeurRepository, times(1)).findById(employeurId);
        verify(modelMapper, never()).map(any(), eq(EmployeurDTO.class));
    }

    @Test
    void testGetEnseignantDTO_Success() {
        // Arrange
        Long enseignantId = enseignant.getId();
        when(enseignantRepository.findById(enseignantId)).thenReturn(Optional.of(enseignant));
        EnseignantDTO enseignantDTO = EnseignantDTO.toDTO(enseignant);

        // Act
        EnseignantDTO result = utilisateurService.getEnseignantDTO(enseignantId);

        // Assert
        assertNotNull(result, "The returned EnseignantDTO should not be null");
        assertEquals(enseignantDTO.getId(), result.getId(), "The returned EnseignantDTO should match the expected DTO");
        verify(enseignantRepository, times(1)).findById(enseignantId);
    }

    @Test
    void testGetEnseignantDTO_NotFound() {
        // Arrange
        Long enseignantId = 999L;
        when(enseignantRepository.findById(enseignantId)).thenReturn(Optional.empty());

        // Act
        EnseignantDTO result = utilisateurService.getEnseignantDTO(enseignantId);

        // Assert
        assertNotNull(result, "The returned EnseignantDTO should not be null");
        assertEquals(EnseignantDTO.empty().getId(), result.getId(), "The returned EtudiantDTO should be empty");
        verify(enseignantRepository, times(1)).findById(enseignantId);
        verify(modelMapper, never()).map(any(), eq(EnseignantDTO.class));
    }

    @Test
    void testGetUtilisateurByCourriel_Success() {
        // Arrange
        String courriel = "jane.smith@techcorp.com";
        when(utilisateurRepository.findUtilisateurByCourriel(courriel)).thenReturn(Optional.of(employeur));
        EmployeurDTO employeurDTO = EmployeurDTO.toDTO(employeur);

        // Act
        UtilisateurDTO result = utilisateurService.getUtilisateurByCourriel(courriel);

        // Assert
        assertNotNull(result, "The returned UtilisateurDTO should not be null");
        assertEquals(employeurDTO.getId(), result.getId(), "The returned UtilisateurDTO should match the expected DTO");
        verify(utilisateurRepository, times(1)).findUtilisateurByCourriel(courriel);
    }

    @Test
    void testGetUtilisateurByCourriel_NotFound() {
        // Arrange
        String courriel = "nonexistent@domain.com";
        when(utilisateurRepository.findUtilisateurByCourriel(courriel)).thenReturn(Optional.empty());

        // Act
        UtilisateurDTO result = utilisateurService.getUtilisateurByCourriel(courriel);

        // Assert
        assertNull(result, "The returned UtilisateurDTO should be null when user does not exist");
        verify(utilisateurRepository, times(1)).findUtilisateurByCourriel(courriel);
        verify(modelMapper, never()).map(any(), eq(UtilisateurDTO.class));
    }

    @Test
    void testGetUtilisateurByTelephone_Success() {
        // Arrange
        String numero = "555-555-5555";
        when(utilisateurRepository.findUtilisateurByNumeroDeTelephone(numero)).thenReturn(Optional.of(enseignant));
        EnseignantDTO enseignantDTO = EnseignantDTO.toDTO(enseignant);

        // Act
        UtilisateurDTO result = utilisateurService.getUtilisateurByTelephone(numero);

        // Assert
        assertNotNull(result, "The returned UtilisateurDTO should not be null");
        assertEquals(enseignantDTO.getId(), result.getId(), "The returned UtilisateurDTO should match the expected DTO");
        verify(utilisateurRepository, times(1)).findUtilisateurByNumeroDeTelephone(numero);
    }

    @Test
    void testGetUtilisateurByTelephone_NotFound() {
        // Arrange
        String numero = "000-000-0000";
        when(utilisateurRepository.findUtilisateurByNumeroDeTelephone(numero)).thenReturn(Optional.empty());

        // Act
        UtilisateurDTO result = utilisateurService.getUtilisateurByTelephone(numero);

        // Assert
        assertNull(result, "The returned UtilisateurDTO should be null when user does not exist");
        verify(utilisateurRepository, times(1)).findUtilisateurByNumeroDeTelephone(numero);
        verify(modelMapper, never()).map(any(), eq(UtilisateurDTO.class));
    }

    @Test
    void testCredentialsDejaPris_OnlyEmailTaken() {
        // Arrange
        String courriel = "jane.smith@techcorp.com";
        String numero = "000-000-0000";
        when(utilisateurRepository.findUtilisateurByCourriel(courriel)).thenReturn(Optional.of(employeur));

        // Act
        boolean result = utilisateurService.credentialsDejaPris(courriel, numero);

        // Assert
        assertTrue(result, "credentialsDejaPris should return true when only email is taken");
        verify(utilisateurRepository, times(1)).findUtilisateurByCourriel(courriel);
    }

    @Test
    void testCredentialsDejaPris_OnlyPhoneTaken() {
        // Arrange
        String courriel = "new.user@domain.com";
        String numero = "555-555-5555";
        when(utilisateurRepository.findUtilisateurByCourriel(courriel)).thenReturn(Optional.empty());
        when(utilisateurRepository.findUtilisateurByNumeroDeTelephone(numero)).thenReturn(Optional.of(enseignant));

        // Act
        boolean result = utilisateurService.credentialsDejaPris(courriel, numero);

        // Assert
        assertTrue(result, "credentialsDejaPris should return true when only phone number is taken");
        verify(utilisateurRepository, times(1)).findUtilisateurByCourriel(courriel);
        verify(utilisateurRepository, times(1)).findUtilisateurByNumeroDeTelephone(numero);
    }

    @Test
    void testCredentialsDejaPris_NeitherTaken() {
        // Arrange
        String courriel = "unique.user@domain.com";
        String numero = "111-222-3333";
        when(utilisateurRepository.findUtilisateurByCourriel(courriel)).thenReturn(Optional.empty());
        when(utilisateurRepository.findUtilisateurByNumeroDeTelephone(numero)).thenReturn(Optional.empty());

        // Act
        boolean result = utilisateurService.credentialsDejaPris(courriel, numero);

        // Assert
        assertFalse(result, "credentialsDejaPris should return false when neither email nor phone number is taken");
        verify(utilisateurRepository, times(1)).findUtilisateurByCourriel(courriel);
        verify(utilisateurRepository, times(1)).findUtilisateurByNumeroDeTelephone(numero);
    }

    @Test
    void testGetUserIdByToken_Success() throws AccessDeniedException {
        // Arrange
        String token = "Bearer valid_token";
        String email = "john.doe@example.com";

        when(jwtTokenProvider.getEmailFromJWT("valid_token")).thenReturn(email);
        when(utilisateurRepository.findUtilisateurByCourriel(email)).thenReturn(Optional.of(etudiant));
        when(etudiantRepository.findById(etudiant.getId())).thenReturn(Optional.of(etudiant));

        // Act
        Long userId = utilisateurService.getUserIdByToken(token);

        // Assert
        assertNotNull(userId, "The returned user ID should not be null");
        assertEquals(etudiant.getId(), userId, "The returned user ID should match the Etudiant's ID");
        verify(jwtTokenProvider).getEmailFromJWT("valid_token");
        verify(utilisateurRepository).findUtilisateurByCourriel(email);
    }

    @Test
    void testGetUserIdByToken_UserNotFound() throws AccessDeniedException {
        // Arrange
        String token = "Bearer valid_token";
        String email = "nonexistent@domain.com";

        when(jwtTokenProvider.getEmailFromJWT("valid_token")).thenReturn(email);
        when(utilisateurRepository.findUtilisateurByCourriel(email)).thenReturn(Optional.empty());

        // Act
        Long userId = utilisateurService.getUserIdByToken(token);

        // Assert
        assertNull(userId, "The returned user ID should be null when user does not exist");
        verify(jwtTokenProvider).getEmailFromJWT("valid_token");
        verify(utilisateurRepository).findUtilisateurByCourriel(email);
        verify(modelMapper, never()).map(any(), any());
    }

    @Test
    void testGetUserIdByToken_InvalidToken() throws AccessDeniedException {
        // Arrange
        String token = "Bearer invalid_token";

        when(jwtTokenProvider.getEmailFromJWT("invalid_token")).thenThrow(new AccessDeniedException("Token Invalide"));

        // Act
        Long userId = utilisateurService.getUserIdByToken(token);

        // Assert
        assertNull(userId, "The returned user ID should be null when token is invalid");
        verify(jwtTokenProvider).getEmailFromJWT("invalid_token");
        verify(utilisateurRepository, never()).findUtilisateurByCourriel(anyString());
        verify(modelMapper, never()).map(any(), any());
    }

    @Test
    void testGetMe_SuccessEmployeur() throws AccessDeniedException {
        // Arrange
        String token = "Bearer employeur_token";
        String email = "jane.smith@techcorp.com";

        when(jwtTokenProvider.getEmailFromJWT("employeur_token")).thenReturn(email);
        when(utilisateurRepository.findUtilisateurByCourriel(email)).thenReturn(Optional.of(employeur));

        // Act
        UtilisateurDTO result = utilisateurService.getMe(token);

        // Assert
        assertNotNull(result, "The returned UtilisateurDTO should not be null");
        assertTrue(result instanceof EmployeurDTO, "The returned UtilisateurDTO should be an instance of EmployeurDTO");
        verify(jwtTokenProvider).getEmailFromJWT("employeur_token");
    }

    @Test
    void testGetMe_SuccessEnseignant() throws AccessDeniedException {
        // Arrange
        String token = "Bearer enseignant_token";
        String email = "emily.clark@university.edu";

        when(jwtTokenProvider.getEmailFromJWT("enseignant_token")).thenReturn(email);
        when(utilisateurRepository.findUtilisateurByCourriel(email)).thenReturn(Optional.of(enseignant));

        // Act
        UtilisateurDTO result = utilisateurService.getMe(token);

        // Assert
        assertNotNull(result, "The returned UtilisateurDTO should not be null");
        assertTrue(result instanceof EnseignantDTO, "The returned UtilisateurDTO should be an instance of EnseignantDTO");
        verify(jwtTokenProvider).getEmailFromJWT("enseignant_token");
    }

    @Test
    void testGetUserIdByToken_GetMeThrowsException() {
        // Arrange
        String token = "Bearer invalid_token";

        // Act
        Long userId = utilisateurService.getUserIdByToken(token);

        // Assert
        assertNull(userId, "The returned user ID should be null when getMe(token) throws an exception");
        assertThrows(UserNotFoundException.class , () -> {
            utilisateurService.getMe(token);
        });
    }
}
