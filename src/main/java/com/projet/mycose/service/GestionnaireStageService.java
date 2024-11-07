package com.projet.mycose.service;

import com.lowagie.text.Image;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import com.projet.mycose.dto.ContratDTO;
import com.projet.mycose.dto.EnseignantDTO;
import com.projet.mycose.dto.EtudiantDTO;
import com.projet.mycose.exceptions.ResourceNotFoundException;
import com.projet.mycose.modele.*;
import com.projet.mycose.repository.ContratRepository;
import com.projet.mycose.repository.GestionnaireStageRepository;
import com.projet.mycose.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GestionnaireStageService {

    private final UtilisateurService utilisateurService;
    private final PasswordEncoder passwordEncoder;
    private final GestionnaireStageRepository gestionnaireStageRepository;
    private final ContratRepository contratRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final AuthenticationManager authenticationManager;

    private final int LIMIT_PER_PAGE = 10;

    public void creationDeCompte(String prenom, String nom, String numeroTelephone, String courriel, String motDePasse) {
        if (!utilisateurService.credentialsDejaPris(courriel, numeroTelephone)) {
            gestionnaireStageRepository.save(new GestionnaireStage(prenom, nom, numeroTelephone, courriel, passwordEncoder.encode(motDePasse)));
        }
    }

    public List<EtudiantDTO> getEtudiantsSansEnseignants(int pageNumber, Programme programme) {
        PageRequest pageRequest = PageRequest.of(pageNumber, LIMIT_PER_PAGE);

        Page<Etudiant> pageEtudiantsRetournee = utilisateurRepository.findAllEtudiantsSansEnseignants(programme, pageRequest);

        if(pageEtudiantsRetournee.isEmpty()) {
            return null;
        }

        List<Etudiant> etudiants = pageEtudiantsRetournee.getContent();

        return etudiants.stream().map(EtudiantDTO::toDTO).toList();
    }

    public List<EnseignantDTO> getEnseignantsParRecherche(String recherche) {
        List<Enseignant>  listeRecu = utilisateurRepository.findAllEnseignantsBySearch(recherche);

        if(listeRecu.isEmpty())
            return null;

        return listeRecu.stream().map(EnseignantDTO::toDTO).toList();
    }

    public Integer getAmountOfPages(Programme programme) {
        long amountOfRows = utilisateurRepository.countAllEtudiantsSansEnseignants(programme);

        if (amountOfRows == 0)
            return 0;

        int nombrePages = (int) Math.floor((double) amountOfRows / LIMIT_PER_PAGE);

        if (amountOfRows % 10 > 0) {
            // Return ++ (équivalent -> nombrePage + 1) parce que
            // floor(13/10) = 1 mais il y a 2 page et pas 1
            nombrePages++;
        }

        return nombrePages;
    }

    @Transactional
    public void assignerEnseigantEtudiant(Long idEtudiant, Long idEnseignant) {
        Objects.requireNonNull(idEtudiant, "ID Étudiant ne peut pas être NULL");
        Objects.requireNonNull(idEnseignant, "ID Enseignant ne peut pas être NULL");

        Optional<Utilisateur> etudiantRecu = utilisateurRepository.findUtilisateurById(idEtudiant);
        Optional<Utilisateur> enseignantRecu = utilisateurRepository.findUtilisateurById(idEnseignant);

        if(etudiantRecu.isPresent() && enseignantRecu.isPresent()) {
            if(etudiantRecu.get() instanceof Etudiant etudiant &&
                    enseignantRecu.get() instanceof Enseignant enseignant) {
                etudiant.setEnseignantAssignee(enseignant);
                enseignant.getEtudiantsAssignees().add(etudiant);

                utilisateurRepository.save(etudiant);
                utilisateurRepository.save(enseignant);
            } else {
                throw new RuntimeException();
            }
        }
    }


    public List<ContratDTO> getAllContratsNonSignes(int page) {
        PageRequest pageRequest = PageRequest.of(page, LIMIT_PER_PAGE);

        Page<Contrat> contratsRetournessEnPages = contratRepository.findContratsBySignatureGestionnaireIsNull(pageRequest);
        if(contratsRetournessEnPages.isEmpty()) {
            throw new ResourceNotFoundException("Contrats not found");
        }

        return contratsRetournessEnPages.stream().map(ContratDTO::toDTO).toList();
    }

    public Integer getAmountOfPagesOfContractNonSignees() {
        long amountOfRows = contratRepository.countBySignatureGestionnaireIsNull();

        if (amountOfRows == 0)
            return 0;

        int nombrePages = (int) Math.floor((double) amountOfRows / LIMIT_PER_PAGE);

        if (amountOfRows % 10 > 0) {
            // Return ++ (équivalent -> nombrePage + 1) parce que
            // floor(13/10) = 1 mais il y a 2 page et pas 1
            nombrePages++;
        }

        return nombrePages;
    }

    public List<ContratDTO> getAllContratsSignes(int page, int annee) {
        PageRequest pageRequest = PageRequest.of(page, LIMIT_PER_PAGE);

        Page<Contrat> contratsRetournessEnPages = contratRepository.findContratSigneeParGestionnaire(annee, pageRequest);
        if(contratsRetournessEnPages.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Contrats not found");
        }

        return contratsRetournessEnPages.stream().map(ContratDTO::toDTO).toList();
    }

    public Integer getAmountOfPagesOfContractSignees(int annee) {
        long amountOfRows = contratRepository.countByContratSigneeParGestionnaire(annee);

        if (amountOfRows == 0)
            return 0;

        int nombrePages = (int) Math.floor((double) amountOfRows / LIMIT_PER_PAGE);

        if (amountOfRows % 10 > 0) {
            // Return ++ (équivalent -> nombrePage + 1) parce que
            // floor(13/10) = 1 mais il y a 2 page et pas 1
            nombrePages++;
        }

        return nombrePages;
    }

    @Transactional
    public String enregistrerSignature(MultipartFile signature, String password, Long contratId) {
        Long gestionnaireId = utilisateurService.getMyUserId();
        Optional<Utilisateur> utilisateurOpt = utilisateurRepository.findUtilisateurById(gestionnaireId);

        if (utilisateurOpt.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur not found");

        Utilisateur utilisateur = utilisateurOpt.get();

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(utilisateur.getCourriel(), password)
        );

        if(!authentication.isAuthenticated())
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email ou mot de passe invalide.");

        Optional<Contrat> contrat = contratRepository.findById(contratId);
        if(contrat.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Contrat not found");

        Contrat contratDispo = contrat.get();
        try {
            contratDispo.setSignatureEmployeur(signature.getBytes());
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error while saving signature");
        }
        contratRepository.save(contratDispo);
        return "Signature sauvegardée";
    }

    public byte[] getContratSignee(long id) {
        Optional<Contrat> contratOpt = contratRepository.findById(id);

        if (contratOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Contrat not found");
        }

        Contrat contrat = contratOpt.get();

        if (!isAllSignatureThere(contrat)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Aucune signature n'est présente sur le contrat");
        }

        return getPdfCompletContrat(contrat);
    }

    private boolean isAllSignatureThere(Contrat contrat) {
        return contrat.getSignatureGestionnaire() != null &&
                contrat.getSignatureEtudiant() != null &&
                contrat.getSignatureEmployeur() != null;
    }

    private byte[] getPdfCompletContrat(Contrat contrat) {
        try {
            ByteArrayInputStream pdfInputStream = new ByteArrayInputStream(contrat.getPdf());
            PdfReader pdfReader = new PdfReader(pdfInputStream);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PdfStamper stamper = new PdfStamper(pdfReader, outputStream);

            ajouterImagesSurPage(stamper,
                    pdfReader.getNumberOfPages(),
                    contrat.getSignatureGestionnaire(),
                    contrat.getSignatureEtudiant(),
                    contrat.getSignatureEmployeur());

            stamper.close();
            pdfReader.close();

            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors de la création du PDF complet du contrat" + e);
        }
    }

    private void ajouterImagesSurPage(PdfStamper stamper, int pageNumber, byte[]... images) {
        try {
            PdfContentByte contentByte = stamper.getOverContent(pageNumber);

            float yPosition = 100;

            for (byte[] imageBytes : images) {
                if (imageBytes != null) {
                    Image image = Image.getInstance(imageBytes);
                    image.scaleToFit(500, 700);

                    image.setAbsolutePosition(50, yPosition);
                    contentByte.addImage(image);

                    yPosition += image.getScaledHeight() + 10;
                }
            }

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors de l'ajout des images au PDF" + e);
        }
    }
}