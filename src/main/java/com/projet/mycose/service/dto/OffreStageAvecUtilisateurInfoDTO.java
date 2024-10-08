package com.projet.mycose.service.dto;

import com.projet.mycose.modele.FichierOffreStage;
import com.projet.mycose.modele.FormulaireOffreStage;
import com.projet.mycose.modele.OffreStage;
import com.projet.mycose.modele.Utilisateur;
import com.projet.mycose.modele.auth.Role;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Base64;

@NoArgsConstructor
@Getter
@Setter
public class OffreStageAvecUtilisateurInfoDTO {

    private Long id;
    private String title;
    private String entrepriseName;
    private String employerName;
    private String email;
    private String website;
    private String location;
    private String salary;
    private String description;
    private String filename;
    private String fileData;
    private LocalDateTime createdAt;
    private Long createur_id;
    private String createur_prenom;
    private String createur_nom;
    private String createur_email;
    private Role createur_role;
    private String createur_telephone;

    public static OffreStageAvecUtilisateurInfoDTO toDto(OffreStage offreStage) {
        OffreStageAvecUtilisateurInfoDTO dto = new OffreStageAvecUtilisateurInfoDTO();

        // Set common fields
        setCommonFields(dto, offreStage);

        if (offreStage instanceof FormulaireOffreStage formulaire) {
            dto.setEmployerName(formulaire.getEmployerName());
            dto.setEmail(formulaire.getEmail());
            dto.setWebsite(formulaire.getWebsite());
            dto.setLocation(formulaire.getLocation());
            dto.setSalary(formulaire.getSalary());
            dto.setDescription(formulaire.getDescription());
        } else if (offreStage instanceof FichierOffreStage fichier) {
            dto.setFilename(fichier.getFilename());
            dto.setFileData(Base64.getEncoder().encodeToString(fichier.getData()));
        }
        return dto;
    }

    private static void setCommonFields(OffreStageAvecUtilisateurInfoDTO dto, OffreStage offreStage) {
        dto.setId(offreStage.getId());
        dto.setTitle(offreStage.getTitle());
        dto.setCreatedAt(offreStage.getCreatedAt());
        dto.setEntrepriseName(offreStage.getEntrepriseName());

        Utilisateur createur = offreStage.getCreateur();
        dto.setCreateur_id(createur.getId());
        dto.setCreateur_prenom(createur.getPrenom());
        dto.setCreateur_nom(createur.getNom());
        dto.setCreateur_email(createur.getCourriel());
        dto.setCreateur_role(createur.getRole());
        dto.setCreateur_telephone(createur.getNumeroDeTelephone());
    }
}
