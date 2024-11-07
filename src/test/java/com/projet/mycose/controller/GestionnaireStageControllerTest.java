package com.projet.mycose.controller;

import com.projet.mycose.dto.ContratDTO;
import com.projet.mycose.dto.EnseignantDTO;
import com.projet.mycose.dto.EtudiantDTO;
import com.projet.mycose.exceptions.*;
import com.projet.mycose.modele.Etudiant;
import com.projet.mycose.modele.Programme;
import com.projet.mycose.modele.auth.Role;
import com.projet.mycose.service.EtudiantService;
import com.projet.mycose.service.GestionnaireStageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.io.IOException;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class GestionnaireStageControllerTest {
    private MockMvc mockMvc;

    @Mock
    private GestionnaireStageService gestionnaireStageService;

    @Mock
    private EtudiantService etudiantService;

    @InjectMocks
    private GestionnaireController gestionnaireController;

    EtudiantDTO etudiantMock;
    EtudiantDTO etudiantMock2;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(gestionnaireController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        etudiantMock = new EtudiantDTO(
                1L,
                "unPrenom",
                "unNom",
                "unCourriel@mail.com",
                "555-666-4756",
                Role.ETUDIANT,
                Programme.TECHNIQUE_INFORMATIQUE,
                Etudiant.ContractStatus.NO_CONTRACT
        );
        etudiantMock2 = new EtudiantDTO(
                2L,
                "unPrenom",
                "unNom",
                "unCourriel@mail.com",
                "555-666-4756",
                Role.ETUDIANT,
                Programme.TECHNIQUE_INFORMATIQUE,
                Etudiant.ContractStatus.NO_CONTRACT
        );

    }

    @Test
    public void testGetEtudiantsSansEnseignants_Success() throws Exception {
        // Arrange

        List<EtudiantDTO> listeEtudiantsDTOMock = new ArrayList<>();
        listeEtudiantsDTOMock.add(etudiantMock);
        when(gestionnaireStageService.getEtudiantsSansEnseignants(0, Programme.TECHNIQUE_INFORMATIQUE)).thenReturn(listeEtudiantsDTOMock);

        // Act & Assert
        mockMvc.perform(post("/gestionnaire/getEtudiants")
                        .param("pageNumber", String.valueOf(0))
                        .param("programme", Programme.TECHNIQUE_INFORMATIQUE.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isAccepted())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].courriel").value("unCourriel@mail.com"))
                .andExpect(jsonPath("$[0].numeroDeTelephone").value("555-666-4756"));
    }

    @Test
    public void testRechercherEnseignants_Success() throws Exception {
        // Arrange
        EnseignantDTO enseignantDTOMock = new EnseignantDTO(
                1L,
                "Vicente",
                "Cabezas",
                "vicen@gmail.com",
                "514-556-5566",
                Role.ENSEIGNANT
        );

        List<EnseignantDTO> listeEnseignantsDTOMock = new ArrayList<>();
        listeEnseignantsDTOMock.add(enseignantDTOMock);

        when(gestionnaireStageService.getEnseignantsParRecherche("vicente")).thenReturn(listeEnseignantsDTOMock);

        // Act & Assert
        mockMvc.perform(post("/gestionnaire/rechercheEnseignants")
                        .param("search", "vicente")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isAccepted())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].courriel").value("vicen@gmail.com"))
                .andExpect(jsonPath("$[0].numeroDeTelephone").value("514-556-5566"));
    }

    @Test
    public void testGetAmountOfPages_Success() throws Exception {
        //Arrange
        when(gestionnaireStageService.getAmountOfPages(Programme.TECHNIQUE_INFORMATIQUE)).thenReturn(2);

        //Act & Assert
        mockMvc.perform(get("/gestionnaire/getEtudiantsPages")
                        .param("programme", Programme.TECHNIQUE_INFORMATIQUE.toString()))
                .andExpect(status().isAccepted())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("2"));
    }

    @Test
    public void testAssignerEnseignatEtudiant_Success() throws Exception {
        // Arrange
        Long idEtudiant = 1L;
        Long idEnseignant = 2L;

        // Act
        doNothing().when(gestionnaireStageService).assignerEnseigantEtudiant(idEtudiant, idEnseignant);

        // Assert & Assert
        mockMvc.perform(post("/gestionnaire/assignerEnseignantEtudiant")
                        .param("idEtudiant", String.valueOf(idEtudiant))
                        .param("idEnseignant", String.valueOf(idEnseignant)))
                .andExpect(status().isOk());

        verify(gestionnaireStageService, times(1)).assignerEnseigantEtudiant(idEtudiant, idEnseignant);
    }

    @Test
    void getEtudiantsByProgramme_Success() throws Exception {
        // Arrange
        Programme programme = Programme.TECHNIQUE_INFORMATIQUE;
        List<EtudiantDTO> etudiants = Arrays.asList(
                etudiantMock,
                etudiantMock2
        );

        when(etudiantService.findEtudiantsByProgramme(programme)).thenReturn(etudiants);

        // Act & Assert
        mockMvc.perform(get("/gestionnaire/getEtudiantsParProgramme")
                        .param("programme", "TECHNIQUE_INFORMATIQUE")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isAccepted())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(etudiants.size()))
                .andExpect(jsonPath("$[0].id").value(etudiants.get(0).getId()))
                .andExpect(jsonPath("$[0].nom").value(etudiants.get(0).getNom()))
                .andExpect(jsonPath("$[1].id").value(etudiants.get(1).getId()))
                .andExpect(jsonPath("$[1].nom").value(etudiants.get(1).getNom()));

        verify(etudiantService, times(1)).findEtudiantsByProgramme(programme);
    }

    @Test
    void getEtudiantsByProgramme_EmptyList() throws Exception {
        // Arrange
        Programme programme = Programme.TECHNIQUE_INFORMATIQUE;
        List<EtudiantDTO> etudiants = Collections.emptyList();

        when(etudiantService.findEtudiantsByProgramme(programme)).thenReturn(etudiants);

        // Act & Assert
        mockMvc.perform(get("/gestionnaire/getEtudiantsParProgramme")
                        .param("programme", "TECHNIQUE_INFORMATIQUE")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isAccepted())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(0));

        verify(etudiantService, times(1)).findEtudiantsByProgramme(programme);
    }

    @Test
    void testGetEtudiantsContratEnDemande_Success() throws Exception {
        // Arrange
        Etudiant etudiant = new Etudiant(1L, "Karim", "Mihoubi", "438-532-2729", "mihoubi@gmail.com", "Mimi123$", Programme.TECHNIQUE_INFORMATIQUE, Etudiant.ContractStatus.PENDING);
        List<EtudiantDTO> etudiantDTOList = new ArrayList<>();
        etudiantDTOList.add(EtudiantDTO.toDTO(etudiant));
        when(etudiantService.getEtudiantsContratEnDemande()).thenReturn(etudiantDTOList);

        // Act & Assert
        mockMvc.perform(get("/gestionnaire/getEtudiantsContratEnDemande")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].prenom").value("Karim"))
                .andExpect(jsonPath("$[0].nom").value("Mihoubi"))
                .andExpect(jsonPath("$[0].contractStatus").value("PENDING"));
        verify(etudiantService, times(1)).getEtudiantsContratEnDemande();
    }

    @Test
    void testGetEtudiantsContratEnDemande_Vide() throws Exception {
        // Arrange
        when(etudiantService.getEtudiantsContratEnDemande()).thenReturn(new ArrayList<>());

        // Act & Assert
        mockMvc.perform(get("/gestionnaire/getEtudiantsContratEnDemande")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(0));
        verify(etudiantService, times(1)).getEtudiantsContratEnDemande();
    }

    @Test
    void testGetEtudiantsContratEnDemande_Erreur() throws Exception {
        // Arrange
        when(etudiantService.getEtudiantsContratEnDemande()).thenThrow(new RuntimeException("Service failure"));

        // Act & Assert
        mockMvc.perform(get("/gestionnaire/getEtudiantsContratEnDemande")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
        verify(etudiantService, times(1)).getEtudiantsContratEnDemande();
    }

    @Test
    void testGetEtudiantsSansContratPages_Success() throws Exception {
        // Arrange
        when(etudiantService.getEtudiantsSansContratPages()).thenReturn(3);

        // Act & Assert
        mockMvc.perform(get("/gestionnaire/getEtudiantsSansContratPages")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("3"));
        verify(etudiantService, times(1)).getEtudiantsSansContratPages();
    }

    @Test
    void testGetEtudiantsSansContratPages_Erreur() throws Exception {
        // Arrange
        when(etudiantService.getEtudiantsSansContratPages()).thenThrow(new RuntimeException("Service failure"));

        // Act & Assert
        mockMvc.perform(get("/gestionnaire/getEtudiantsSansContratPages")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
        verify(etudiantService, times(1)).getEtudiantsSansContratPages();
    }
    @Test
    public void testGetAllContratsNonSignes_Success() throws Exception {
        List<ContratDTO> contrats = List.of(new ContratDTO());
        when(gestionnaireStageService.getAllContratsNonSignes(0)).thenReturn(contrats);

        mockMvc.perform(get("/gestionnaire/contrats/non-signes")
                        .param("page", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(contrats.size()));
    }

    @Test
    public void testGetAllContratsNonSignes_NotFound() throws Exception {
        when(gestionnaireStageService.getAllContratsNonSignes(0)).thenThrow(new ResourceNotFoundException("Contrats not found"));

        mockMvc.perform(get("/gestionnaire/contrats/non-signes")
                        .param("page", "0"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Contrats not found"))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.timestamp").isNumber());
    }

    @Test
    void testGetAmountOfPagesOfContratNonSignee_NoContracts() throws Exception {
        when(gestionnaireStageService.getAmountOfPagesOfContractNonSignees()).thenReturn(0);

        mockMvc.perform(get("/gestionnaire/contrats/non-signes/pages"))
                .andExpect(status().isAccepted())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("0"));
    }

    @Test
    void testGetAmountOfPagesOfContratNonSignee_SomeContracts() throws Exception {
        when(gestionnaireStageService.getAmountOfPagesOfContractNonSignees()).thenReturn(2);

        mockMvc.perform(get("/gestionnaire/contrats/non-signes/pages"))
                .andExpect(status().isAccepted())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("2"));
    }

    @Test
    void testGetAllContratsSignes_Success() throws Exception {
        int page = 0;
        int annee = 2024;

        ContratDTO contratDTO1 = new ContratDTO();
        ContratDTO contratDTO2 = new ContratDTO();

        when(gestionnaireStageService.getAllContratsSignes(page, annee))
                .thenReturn(List.of(contratDTO1, contratDTO2));

        mockMvc.perform(get("/gestionnaire/contrats/signes")
                        .param("page", String.valueOf(page))
                        .param("annee", String.valueOf(annee)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void testGetAllContratsSignes_NotFound() throws Exception {
        int page = 0;
        int annee = 2024;

        when(gestionnaireStageService.getAllContratsSignes(page, annee))
                .thenThrow(new ResourceNotFoundException("Contrats not found"));

        mockMvc.perform(get("/gestionnaire/contrats/signes")
                        .param("page", String.valueOf(page))
                        .param("annee", String.valueOf(annee)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Contrats not found"))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.timestamp").isNumber());
    }

    @Test
    void testGetAmountOfPagesOfContratSignee_Success() throws Exception {
        int annee = 2024;

        when(gestionnaireStageService.getAmountOfPagesOfContractSignees(annee)).thenReturn(3);

        mockMvc.perform(get("/gestionnaire/contrats/signes/pages")
                        .param("annee", String.valueOf(annee)))
                .andExpect(status().isAccepted())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("3"));
    }

    @Test
    void testGetAmountOfPagesOfContratSignee_NoContracts() throws Exception {
        int annee = 2024;

        when(gestionnaireStageService.getAmountOfPagesOfContractSignees(annee)).thenReturn(0);

        mockMvc.perform(get("/gestionnaire/contrats/signes/pages")
                        .param("annee", String.valueOf(annee)))
                .andExpect(status().isAccepted())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("0"));
    }

    @Test
    public void testEnregistrerSignature_Success() throws Exception {
        // Arrange
        MockMultipartFile signatureFile = new MockMultipartFile("signature", "signature.png", "image/png", "test signature content".getBytes());

        when(gestionnaireStageService.enregistrerSignature(any(MultipartFile.class), anyString(), any(Long.class)))
                .thenReturn("Signature sauvegardees");

        // Act & Assert
        mockMvc.perform(multipart("/gestionnaire/enregistrerSignature")
                        .file(signatureFile)
                        .param("contratId", "1")
                        .param("password", "motDePasse")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isAccepted())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("Signature sauvegardees"));
    }

    @Test
    public void testEnregistrerSignature_UserNotFound() throws Exception {
        // Arrange
        MockMultipartFile signatureFile = new MockMultipartFile("signature", "signature.png", "image/png", "test signature content".getBytes());

        when(gestionnaireStageService.enregistrerSignature(any(MultipartFile.class), anyString(), any(Long.class)))
                .thenThrow(new UserNotFoundException());

        // Act & Assert
        mockMvc.perform(multipart("/gestionnaire/enregistrerSignature")
                        .file(signatureFile)
                        .param("contratId", "1")
                        .param("password", "motDePasse")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Utilisateur not found"))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.timestamp").isNumber());
    }

    @Test
    public void testEnregistrerSignature_BadCredentials() throws Exception {
        // Arrange
        MockMultipartFile signatureFile = new MockMultipartFile("signature", "signature.png", "image/png", "test signature content".getBytes());

        when(gestionnaireStageService.enregistrerSignature(any(MultipartFile.class), anyString(), any(Long.class)))
                .thenThrow(new AuthenticationException(HttpStatus.UNAUTHORIZED, "Email ou mot de passe invalide."));

        // Act & Assert
        mockMvc.perform(multipart("/gestionnaire/enregistrerSignature")
                        .file(signatureFile)
                        .param("contratId", "1")
                        .param("password", "motDePasse")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Email ou mot de passe invalide."))
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.timestamp").isNumber());
    }

    @Test
    public void testEnregistrerSignature_ContratNotFound() throws Exception {
        // Arrange
        MockMultipartFile signatureFile = new MockMultipartFile("signature", "signature.png", "image/png", "test signature content".getBytes());

        when(gestionnaireStageService.enregistrerSignature(any(MultipartFile.class), anyString(), any(Long.class)))
                .thenThrow(new ResourceNotFoundException("Contrat not found"));

        // Act & Assert
        mockMvc.perform(multipart("/gestionnaire/enregistrerSignature")
                        .file(signatureFile)
                        .param("contratId", "1")
                        .param("password", "motDePasse")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Contrat not found"))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.timestamp").isNumber());
    }

    @Test
    public void testEnregistrerSignature_ErrorDuringSignature() throws Exception {
        // Arrange
        MockMultipartFile signatureFile = new MockMultipartFile("signature", "signature.png", "image/png", "test signature content".getBytes());

        when(gestionnaireStageService.enregistrerSignature(any(MultipartFile.class), anyString(), any(Long.class)))
                .thenThrow(new SignaturePersistenceException("Error while saving signature"));

        // Act & Assert
        mockMvc.perform(multipart("/gestionnaire/enregistrerSignature")
                        .file(signatureFile)
                        .param("contratId", "1")
                        .param("password", "motDePasse")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Error while saving signature"))
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.timestamp").isNumber());
    }
    @Test
    public void testGetYearFirstContratUploaded_NormalSuccess() throws Exception {
        // Arrange
        Set set = new HashSet();
        set.add(2019);
        set.add(2021);
        when(gestionnaireStageService.getYearFirstContratUploaded()).thenReturn(set);

        // Act & Assert
        mockMvc.perform(get("/gestionnaire/contrats/signes/anneeminimum")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isAccepted())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0]").value(2019))
                .andExpect(jsonPath("$[1]").value(2021));
    }
    @Test
    public void testGetYearFirstContratUploaded_Empty() throws Exception {
        // Arrange
        when(gestionnaireStageService.getYearFirstContratUploaded()).thenReturn(Set.of());

        // Act & Assert
        mockMvc.perform(get("/gestionnaire/contrats/signes/anneeminimum")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isAccepted())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void testImprimerContrat_Success() throws Exception {
        long contratId = 1L;
        String pdfBase64 = "JVBERi0xLjQKJcTl8uXrp/Og0MTGCjEgMCBvYmoKPDwvTGluZWFyaXpl";

        when(gestionnaireStageService.getContratSignee(contratId)).thenReturn(pdfBase64);

        mockMvc.perform(get("/gestionnaire/contrat/print")
                        .param("id", String.valueOf(contratId))
                        .accept(MediaType.TEXT_PLAIN))
                .andExpect(status().isOk())
                .andExpect(content().string(pdfBase64));
    }

    @Test
    void testImprimerContrat_Failure() throws Exception {
        long contratId = 1L;

        when(gestionnaireStageService.getContratSignee(contratId)).thenThrow(new RuntimeException("Une erreur est surevenue de notre coté"));

        mockMvc.perform(get("/gestionnaire/contrat/print")
                        .param("id", String.valueOf(contratId)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Une erreur est surevenue de notre coté"))
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.timestamp").isNumber());
    }

}
