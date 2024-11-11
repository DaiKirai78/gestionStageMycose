package com.projet.mycose.service;

import com.projet.mycose.dto.ContratDTO;
import com.projet.mycose.dto.EmployeurDTO;
import com.projet.mycose.dto.EtudiantDTO;
import com.projet.mycose.dto.GestionnaireStageDTO;
import com.projet.mycose.exceptions.ResourceConflictException;
import com.projet.mycose.exceptions.UserNotFoundException;
import com.projet.mycose.modele.*;
import com.projet.mycose.modele.auth.Credentials;
import com.projet.mycose.modele.auth.Role;
import com.projet.mycose.repository.ContratRepository;
import com.projet.mycose.repository.EmployeurRepository;
import com.projet.mycose.repository.EtudiantRepository;
import com.projet.mycose.repository.GestionnaireStageRepository;
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

    @Mock
    private ContratRepository contratRepository;

    @Mock
    private EtudiantRepository etudiantRepository;

    @Mock
    private GestionnaireStageRepository gestionnaireStageRepository;

    @Mock
    private EmployeurRepository employeurRepository;
    @Mock
    private ModelMapper modelMapper;

    @Mock
    private UtilisateurService utilisateurService;

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

        contrat = new Contrat();
        contrat.setEtudiant(etudiant);
        contrat.setEmployeur(employeur);
        contrat.setStatus(Contrat.Status.INACTIVE);
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

        // Act
        ContratDTO contratSaved = contratService.save(etudiant.getId(), employeur.getId(), gestionnaireStage.getId());

        // Assert
        assertEquals(1L, contratSaved.getEtudiantId());
        assertEquals(2L, contratSaved.getEmployeurId());
        assertEquals(3L, contratSaved.getGestionnaireStageId());
    }

    @Test
    void saveContrat_EtudiantNotFound() {
        // Arrange
        Long invalidEtudiantId = 99L;
        when(utilisateurService.getEtudiantDTO(invalidEtudiantId)).thenReturn(null);

        // Act & Assert
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () ->
                contratService.save(invalidEtudiantId, employeur.getId(), gestionnaireStage.getId())
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

        // Act & Assert
        ResourceConflictException exception = assertThrows(ResourceConflictException.class, () ->
                contratService.save(etudiant.getId(), employeur.getId(), gestionnaireStage.getId())
        );

        // Assert
        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
        assertEquals("L'étudiant a déjà un stage actif ou n'a pas fait de demande de stage", exception.getMessage());
    }
}
