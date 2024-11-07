package com.projet.mycose.service;

import com.projet.mycose.dto.ContratDTO;
import com.projet.mycose.dto.EtudiantDTO;
import com.projet.mycose.exceptions.ResourceConflictException;
import com.projet.mycose.exceptions.ResourceNotFoundException;
import com.projet.mycose.modele.Contrat;
import com.projet.mycose.modele.Employeur;
import com.projet.mycose.modele.Etudiant;
import com.projet.mycose.modele.Programme;
import com.projet.mycose.modele.auth.Credentials;
import com.projet.mycose.modele.auth.Role;
import com.projet.mycose.repository.ContratRepository;
import com.projet.mycose.repository.EmployeurRepository;
import com.projet.mycose.repository.EtudiantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Base64;
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
    private MultipartFile multipartFile;

    @Mock
    private ContratRepository contratRepository;

    @Mock
    private EtudiantRepository etudiantRepository;

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

        String fileName = "testfile.txt";
        String contentType = "text/plain";
        String content = "Ceci est un contenu de test";

        contrat = new Contrat();
        contrat.setEtudiant(etudiant);
        contrat.setEmployeur(employeur);
        contrat.setStatus(Contrat.Status.INACTIVE);
        contrat.setPdf(content.getBytes());

        multipartFile = new MockMultipartFile(
                "file",
                fileName,
                contentType,
                content.getBytes()
        );
    }

    @Test
    void saveContrat_Success() throws IOException {
        // Arrange
        when(contratRepository.save(contrat)).thenReturn(contrat);
        when(etudiantRepository.findById(anyLong())).thenReturn(Optional.ofNullable(etudiant));
        when(employeurRepository.findById(anyLong())).thenReturn(Optional.ofNullable(employeur));
        when(etudiantRepository.findEtudiantById(anyLong())).thenReturn(etudiant);
        when(modelMapper.map(any(Contrat.class), eq(ContratDTO.class))).thenReturn(ContratDTO.toDTO(contrat));
        when(modelMapper.map(any(ContratDTO.class), eq(Contrat.class))).thenReturn(contrat);
        when(utilisateurService.getEtudiantDTO(etudiant.getId())).thenReturn(EtudiantDTO.toDTO(etudiant));

        // Act
        ContratDTO contratSaved = contratService.save(multipartFile, etudiant.getId(), employeur.getId());

        // Assert
        String expectedPdfBase64 = Base64.getEncoder().encodeToString(contrat.getPdf());
        assertEquals(expectedPdfBase64, contratSaved.getPdf());
        assertEquals("roby@gmail.com", contrat.getEtudiant().getCourriel());
        assertEquals("jean@gmail.com", contrat.getEmployeur().getCourriel());
    }

    @Test
    void saveContrat_EtudiantNotFound() {
        // Arrange
        Long invalidEtudiantId = 99L;
        when(etudiantRepository.findById(anyLong())).thenReturn(Optional.ofNullable(etudiant));
        when(employeurRepository.findById(anyLong())).thenReturn(Optional.ofNullable(employeur));
        when(modelMapper.map(any(ContratDTO.class), eq(Contrat.class))).thenReturn(contrat);
        when(utilisateurService.getEtudiantDTO(invalidEtudiantId)).thenReturn(null);

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                contratService.save(multipartFile, invalidEtudiantId, employeur.getId())
        );

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("L'étudiant avec l'ID " + invalidEtudiantId + " est innexistant", exception.getMessage());
    }

    @Test
    void saveContrat_ShouldThrowException_WhenContractStatusConflict() {
        // Arrange
        etudiant.setContractStatus(Etudiant.ContractStatus.ACTIVE);
        when(utilisateurService.getEtudiantDTO(etudiant.getId())).thenReturn(EtudiantDTO.toDTO(etudiant));
        when(employeurRepository.findById(anyLong())).thenReturn(Optional.ofNullable(employeur));
        when(etudiantRepository.findById(anyLong())).thenReturn(Optional.ofNullable(etudiant));
        when(modelMapper.map(any(ContratDTO.class), eq(Contrat.class))).thenReturn(contrat);
        when(etudiantRepository.findEtudiantById(etudiant.getId())).thenReturn(etudiant);

        // Act & Assert
        ResourceConflictException exception = assertThrows(ResourceConflictException.class, () ->
                contratService.save(multipartFile, etudiant.getId(), employeur.getId())
        );

        // Assert
        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
        assertEquals("L'étudiant a déjà un stage actif ou n'a pas fait de demande de stage", exception.getMessage());
    }
}
