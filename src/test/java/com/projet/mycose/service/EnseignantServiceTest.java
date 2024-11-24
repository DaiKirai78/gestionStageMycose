package com.projet.mycose.service;

import com.projet.mycose.exceptions.ResourceConflictException;
import com.projet.mycose.modele.Enseignant;
import com.projet.mycose.dto.EtudiantDTO;
import com.projet.mycose.exceptions.AuthenticationException;
import com.projet.mycose.exceptions.ResourceNotFoundException;
import com.projet.mycose.modele.*;
import com.projet.mycose.modele.auth.Role;
import com.projet.mycose.repository.EnseignantRepository;
import com.projet.mycose.dto.EnseignantDTO;
import com.projet.mycose.repository.FicheEvaluationMilieuStageRepository;
import com.projet.mycose.repository.UtilisateurRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class EnseignantServiceTest {



    @Mock
    private EnseignantRepository enseignantRepositoryMock;

    @Mock
    private UtilisateurService utilisateurService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ModelMapper modelMapperMock;

    @Mock
    private FicheEvaluationMilieuStageRepository ficheEvaluationMilieuStageRepository;

    @InjectMocks
    private EnseignantService enseignantService;

    @Test
    public void creationDeCompteAvecSucces() {
        //Arrange

        Enseignant enseignant = new Enseignant(1L, "Karim", "Mihoubi", "438-532-2729", "mihoubi@gmail.com", "Mimi123$");
        when(enseignantRepositoryMock.save(any(Enseignant.class))).thenReturn(enseignant);
        when(passwordEncoder.encode(eq("Mimi123$"))).thenReturn("mot de passe encodé");

        //Act
        EnseignantDTO enseignantDTO = enseignantService.creationDeCompte("Karim", "Mihoubi", "438-532-2729", "mihoubi@gmail.com", "Mimi123$");

        //Assert
        Assertions.assertEquals(enseignantDTO.getId(), 1);
        Assertions.assertEquals(enseignantDTO.getPrenom(), "Karim");
        Assertions.assertEquals(enseignantDTO.getNom(), "Mihoubi");
        Assertions.assertEquals(enseignantDTO.getCourriel(), "mihoubi@gmail.com");
        Assertions.assertEquals(enseignantDTO.getNumeroDeTelephone(), "438-532-2729");
        Assertions.assertEquals(enseignantDTO.getRole(), Role.ENSEIGNANT);
    }

    @Test
    public void creationDeCompteAvecEchec_CourrielDejaUtilise() {
        //Arrange

        doThrow(new DataIntegrityViolationException("Courriel déjà utilisé"))
                .when(enseignantRepositoryMock).save(any(Enseignant.class));

        //Act & Assert
        DataIntegrityViolationException exception = Assertions.assertThrows(
                DataIntegrityViolationException.class,
                () -> enseignantService.creationDeCompte("Karim", "Mihoubi", "438-532-2729", "mihoubi@gmail.com", "Mimi123$")
        );

        Assertions.assertEquals("Courriel déjà utilisé", exception.getMessage());
    }

    @Test
    public void creationDeCompteAvecEchec_NumeroDeTelephoneInvalide() {
        //Arrange

        String numeroDeTelephoneInvalide = "12345abc";

        //Act & Assert
        Assertions.assertThrows(
                NullPointerException.class,
                () -> enseignantService.creationDeCompte("Karim", "Mihoubi", numeroDeTelephoneInvalide, "mihoubi@gmail.com", "Mimi123$")
        );
    }

    @Test
    public void creationDeCompteAvecEchec_CourrielInvalide() {
        //Arrange

        String courrielInvalide = "jeSuisUnEmailQuiNeRespectePasLesConventionsDÉcriture";

        //Act & Assert
        Assertions.assertThrows(
                NullPointerException.class,
                () -> enseignantService.creationDeCompte("Karim", "Mihoubi", "450-389-2628", courrielInvalide, "Mimi123$")
        );
    }

    @Test
    public void creationDeCompteAvecEchec_MotDePasseInvalide_PasEncode() {
        //Arrange

        String motDePasseInvalide = "motDePassePasEncodé";

        //Act & Assert
        Assertions.assertThrows(
                NullPointerException.class,
                () -> enseignantService.creationDeCompte("Karim", "Mihoubi", "450-389-2628", "mihoubi@gmail.com", motDePasseInvalide)
        );
    }

    @Test
    public void creationDeCompteAvecEchec_MotDePasseInvalide_Null() {
        //Arrange

        String motDePasseInvalide = null;

        //Act & Assert
        Assertions.assertThrows(
                NullPointerException.class,
                () -> enseignantService.creationDeCompte("Karim", "Mihoubi", "450-389-2628", "mihoubi@gmail.com", motDePasseInvalide)
        );
    }

    @Test
    public void creationDeCompteAvecEchec_PrenomInvalide() {
        //Arrange

        String prenomInvalide = "3426unPrenomInvalide28382";

        //Act & Assert
        Assertions.assertThrows(
                NullPointerException.class,
                () -> enseignantService.creationDeCompte(prenomInvalide, "Mihoubi", "450-389-2628", "mihoubi@gmail.com", "Mimi123$")
        );
    }

    @Test
    public void creationDeCompteAvecEchec_NomInvalide() {
        //Arrange

        String nomInvalide = "3426unNomInvalide28382";

        //Act & Assert
        Assertions.assertThrows(
                NullPointerException.class,
                () -> enseignantService.creationDeCompte("Karim", nomInvalide, "450-389-2628", "mihoubi@gmail.com", "Mimi123$")
        );
    }



    @Test
    public void testCredentialsDejaPris_CourrielPris() {
        // Arrange
        when(utilisateurService.credentialsDejaPris("mihoubi@gmail.com", "450-389-2628")).thenReturn(true);

        // Act
        ResourceConflictException exception = Assertions.assertThrows(
                ResourceConflictException.class,
                () -> enseignantService.creationDeCompte("Karim", "Mihoubi", "450-389-2628", "mihoubi@gmail.com", "Mimi123$")
        );

        // Assert
        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
        assertEquals("Un enseignant avec ces credentials existe déjà", exception.getMessage());
    }


    @Test
    public void testGetAllEtudiantsAEvaluerParProf_Success() throws AccessDeniedException {
        // Arrange
        Long enseignantId = 1L;
        Employeur employeur = new Employeur();
        employeur.setId(enseignantId);

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
        when(ficheEvaluationMilieuStageRepository.findAllEtudiantsNonEvaluesByProf(enseignantId, pageRequest)).thenReturn(pageFind);

        when(modelMapperMock.map(etudiant1, EtudiantDTO.class))
                .thenReturn(dto1);
        when(modelMapperMock.map(etudiant2, EtudiantDTO.class))
                .thenReturn(dto2);

        // Act
        Page<EtudiantDTO> listeRetourne = enseignantService.getAllEtudiantsAEvaluerParProf(enseignantId, 1);

        //Assert
        verify(ficheEvaluationMilieuStageRepository, times(1)).findAllEtudiantsNonEvaluesByProf(enseignantId, pageRequest);
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
                enseignantService.getAllEtudiantsAEvaluerParProf(employeurId, 1)
        );

        // Assert
        assertEquals("Problème d'authentification", exception.getMessage());
        verify(ficheEvaluationMilieuStageRepository, never()).save(any(FicheEvaluationMilieuStage.class));
    }

    @Test
    public void testGetAllEtudiantsNonEvalues_ListeNotFound() throws AccessDeniedException {
        // Arrange
        Long employeurId = 1L;
        Employeur employeur = new Employeur();
        employeur.setId(employeurId);

        when(utilisateurService.getMeUtilisateur()).thenReturn(employeur);
        when(ficheEvaluationMilieuStageRepository.findAllEtudiantsNonEvaluesByProf(eq(employeurId), any(PageRequest.class))).thenReturn(Page.empty());

        // Act
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                enseignantService.getAllEtudiantsAEvaluerParProf(employeurId, 1)
        );

        // Assert
        assertEquals("Aucun Étudiant Trouvé", exception.getMessage());
        verify(ficheEvaluationMilieuStageRepository, never()).save(any(FicheEvaluationMilieuStage.class));
    }

    @Test
    void getValidatedEnseignant_WithEnseignant_ReturnsEnseignant() {
        // Arrange
        Enseignant enseignant = new Enseignant();
        enseignant.setId(1L);
        // Act
        Enseignant result = enseignantService.getValidatedEnseignant(enseignant);

        // Assert
        assertNotNull(result, "The returned Enseignant should not be null.");
        assertEquals(enseignant, result, "The returned Enseignant should be the same as the input.");
    }

    @Test
    void getValidatedEnseignant_WithNonEnseignant_ThrowsAuthenticationException() {
        // Arrange
        Utilisateur utilisateur = new Etudiant();
        utilisateur.setId(2L);

        // Act & Assert
        AuthenticationException exception = assertThrows(AuthenticationException.class, () ->
                        enseignantService.getValidatedEnseignant(utilisateur),
                "Expected getValidatedEnseignant to throw, but it didn't.");

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus(), "Exception should have status FORBIDDEN.");
        assertEquals("User is not an Enseignant.", exception.getMessage(), "Exception message should match.");
    }

    @Test
    void getValidatedEnseignant_WithNull_ThrowsAuthenticationException() {
        // Act & Assert
        AuthenticationException exception = assertThrows(AuthenticationException.class, () ->
                        enseignantService.getValidatedEnseignant(null),
                "Expected getValidatedEnseignant to throw, but it didn't.");

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatus(), "Exception should have status FORBIDDEN.");
        assertEquals("User is not an Enseignant.", exception.getMessage(), "Exception message should match.");
    }

}
