package com.projet.mycose.service;

import com.projet.mycose.dto.*;
import com.projet.mycose.exceptions.ResourceConflictException;
import com.projet.mycose.exceptions.ResourceNotFoundException;
import com.projet.mycose.exceptions.UserNotFoundException;
import com.projet.mycose.modele.*;
import com.projet.mycose.modele.auth.Credentials;
import com.projet.mycose.modele.auth.Role;
import com.projet.mycose.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ContratServiceTest {

    private Contrat contrat;
    private Employeur employeur;
    private Etudiant etudiant;
    private GestionnaireStage gestionnaireStage;
    private OffreStageAvecUtilisateurInfoDTO offreStage;
    private OffreStage realOffreStage;

    @Mock
    private ContratRepository contratRepository;

    @Mock
    private EtudiantRepository etudiantRepository;

    @Mock
    private GestionnaireStageRepository gestionnaireStageRepository;

    @Mock
    private EmployeurRepository employeurRepository;

    @Mock
    private OffreStageRepository offreStageRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private UtilisateurService utilisateurService;
    @Mock
    private OffreStageService offreStageService;

    @InjectMocks
    private ContratService contratService;

    @BeforeEach
    void setUp() {

        etudiant = new Etudiant();
        etudiant.setId(1L);
        etudiant.setPrenom("Roberto");
        etudiant.setNom("Berrios");
        Credentials credentials = new Credentials("roby@gmail.com", "passw0rd", Role.ETUDIANT);
        etudiant.setCredentials(credentials);
        etudiant.setProgramme(Programme.GENIE_LOGICIEL);
        etudiant.setContractStatus(Etudiant.ContractStatus.PENDING);

        employeur = new Employeur();
        employeur.setId(2L);
        employeur.setPrenom("Jean");
        employeur.setNom("Tremblay");
        employeur.setEntrepriseName("McDonald");
        Credentials credentials2 = new Credentials("jean@gmail.com", "passw0rd", Role.EMPLOYEUR);
        employeur.setCredentials(credentials2);
        employeur.setNumeroDeTelephone("450-374-3783");

        gestionnaireStage = new GestionnaireStage();
        gestionnaireStage.setId(3L);
        gestionnaireStage.setPrenom("Patrice");
        gestionnaireStage.setNom("Brodeur");
        Credentials credentials3 = new Credentials("patrice@gmail.com", "passw0rd", Role.GESTIONNAIRE_STAGE);
        gestionnaireStage.setCredentials(credentials3);
        gestionnaireStage.setNumeroDeTelephone("438-646-3245");

        offreStage = new OffreStageAvecUtilisateurInfoDTO();
        offreStage.setId(1L);

        realOffreStage = new FormulaireOffreStage();
        realOffreStage.setId(offreStage.getId());

        contrat = new Contrat();
        contrat.setId(1L);
        contrat.setEtudiant(etudiant);
        contrat.setEmployeur(employeur);
        contrat.setGestionnaireStage(gestionnaireStage);
        contrat.setOffreStageid(offreStage.getId());
    }

    @Test
    void saveContrat_Success() {
        // Arrange
        when(contratRepository.save(contrat)).thenReturn(contrat);
        when(etudiantRepository.findById(anyLong())).thenReturn(Optional.ofNullable(etudiant));
        when(employeurRepository.findById(anyLong())).thenReturn(Optional.ofNullable(employeur));
        when(gestionnaireStageRepository.findById(anyLong())).thenReturn(Optional.ofNullable(gestionnaireStage));
        when(etudiantRepository.findEtudiantById(anyLong())).thenReturn(etudiant);
        when(modelMapper.map(any(Contrat.class), eq(ContratDTO.class))).thenReturn(ContratDTO.toDTO(contrat));
        when(modelMapper.map(any(ContratDTO.class), eq(Contrat.class))).thenReturn(contrat);
        when(utilisateurService.getEtudiantDTO(etudiant.getId())).thenReturn(EtudiantDTO.toDTO(etudiant));
        when(utilisateurService.getEmployeurDTO(employeur.getId())).thenReturn(EmployeurDTO.toDTO(employeur));
        when(utilisateurService.getGestionnaireDTO(gestionnaireStage.getId())).thenReturn(GestionnaireStageDTO.toDTO(gestionnaireStage));
        when(offreStageService.getOffreStageWithUtilisateurInfo(offreStage.getId())).thenReturn(offreStage);
        when(offreStageRepository.findById(anyLong())).thenReturn(Optional.ofNullable(realOffreStage));

        // Act
        ContratDTO contratSaved = contratService.save(etudiant.getId(), employeur.getId(), gestionnaireStage.getId(), offreStage.getId());

        // Assert
        assertEquals(1L, contratSaved.getEtudiantId());
        assertEquals(2L, contratSaved.getEmployeurId());
        assertEquals(3L, contratSaved.getGestionnaireStageId());
        assertEquals(1L, contratSaved.getOffreStageId());
    }

    @Test
    void saveContrat_EtudiantNotFound() {
        // Arrange
        Long invalidEtudiantId = 99L;
        when(utilisateurService.getEtudiantDTO(invalidEtudiantId)).thenReturn(null);

        // Act & Assert
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () ->
                contratService.save(invalidEtudiantId, employeur.getId(), gestionnaireStage.getId(), offreStage.getId())
        );

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Utilisateur not found", exception.getMessage());
    }

    @Test
    void saveContrat_ShouldThrowException_WhenContractStatusConflict() {
        // Arrange
        etudiant.setContractStatus(Etudiant.ContractStatus.ACTIVE);
        when(utilisateurService.getEtudiantDTO(etudiant.getId())).thenReturn(EtudiantDTO.toDTO(etudiant));
        when(utilisateurService.getEmployeurDTO(employeur.getId())).thenReturn(EmployeurDTO.toDTO(employeur));
        when(utilisateurService.getGestionnaireDTO(gestionnaireStage.getId())).thenReturn(GestionnaireStageDTO.toDTO(gestionnaireStage));
        when(etudiantRepository.findEtudiantById(etudiant.getId())).thenReturn(etudiant);
        when(offreStageService.getOffreStageWithUtilisateurInfo(offreStage.getId())).thenReturn(offreStage);

        // Act & Assert
        ResourceConflictException exception = assertThrows(ResourceConflictException.class, () ->
                contratService.save(etudiant.getId(), employeur.getId(), gestionnaireStage.getId(), offreStage.getId())
        );

        // Assert
        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
        assertEquals("L'étudiant a déjà un stage actif ou n'a pas fait de demande de stage", exception.getMessage());
    }

    @Test
    void getContractByIdTest() {
        // Arrange
        when(contratRepository.findById(1L)).thenReturn(Optional.ofNullable(contrat));

        // Act
        ContratDTO contratDTO = contratService.getContractById(1L);

        // Assert
        assertEquals(1L, contratDTO.getEtudiantId());
        assertEquals(2L, contratDTO.getEmployeurId());
        assertEquals(3L, contratDTO.getGestionnaireStageId());
        assertEquals(1L, contratDTO.getOffreStageId());
    }

    @Test
    void getContractByIdTest_NotFound() {
        //Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                contratService.getContractById(99L)
        );

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("Contract with id 99 not found", exception.getMessage());
    }
}
