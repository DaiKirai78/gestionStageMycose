package com.projet.mycose.service.dto;

import com.projet.mycose.modele.ApplicationStage;
import com.projet.mycose.modele.FichierOffreStage;
import com.projet.mycose.modele.FormulaireOffreStage;
import com.projet.mycose.modele.auth.Role;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import java.time.LocalDateTime;
import java.util.Base64;

@Getter
@NoArgsConstructor
@Setter
@ToString
public class ApplicationStageAvecInfosDTO {

    private Long id;
    private String title;
    private String entrepriseName;
    private String email;
    private String website;
    private String location;
    private String salary;
    private String description;
    private String filename;
    private String fileData;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long etudiant_id;
    private Long offreStage_id;
    private ApplicationStage.ApplicationStatus status;
    private LocalDateTime appliedAt;

    public static ApplicationStageAvecInfosDTO toDTO(ApplicationStage applicationStage) {
        ApplicationStageAvecInfosDTO dto = new ApplicationStageAvecInfosDTO();
        dto.setId(applicationStage.getId());
        dto.setEtudiant_id(applicationStage.getEtudiant().getId());
        dto.setOffreStage_id(applicationStage.getOffreStage().getId());
        dto.setStatus(applicationStage.getStatus());
        dto.setAppliedAt(applicationStage.getAppliedAt());
        dto.setCreatedAt(applicationStage.getOffreStage().getCreatedAt());
        dto.setUpdatedAt(applicationStage.getOffreStage().getUpdatedAt());
        dto.setTitle(applicationStage.getOffreStage().getTitle());
        dto.setEntrepriseName(applicationStage.getOffreStage().getEntrepriseName());
        //Ensure that the offreStage object is fully initialized before the instanceof (hibernate lazy loading)
        System.out.println("Actual class: " + applicationStage.getOffreStage().getClass().getName());
        Hibernate.initialize(applicationStage.getOffreStage());
        if (applicationStage.getOffreStage() instanceof FichierOffreStage fichier) {
            System.out.println("FichierOffreStage");
            dto.setFilename(fichier.getFilename());
            dto.setFileData(Base64.getEncoder().encodeToString(fichier.getData()));
        } else if (applicationStage.getOffreStage() instanceof FormulaireOffreStage formulaire) {
            System.out.println("FormulaireOffreStage");
            dto.setEmail(formulaire.getEmail());
            dto.setWebsite(formulaire.getWebsite());
            dto.setLocation(formulaire.getLocation());
            dto.setSalary(formulaire.getSalary());
            dto.setDescription(formulaire.getDescription());
        } else {
            System.out.println("nothing");
        }
        return dto;
    }
}
