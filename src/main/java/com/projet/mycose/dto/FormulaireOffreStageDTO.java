package com.projet.mycose.dto;



import com.projet.mycose.modele.FormulaireOffreStage;
import com.projet.mycose.modele.OffreStage;
import com.projet.mycose.modele.Programme;
import com.projet.mycose.modele.SessionEcole;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.Collections;
import java.util.List;


@NoArgsConstructor
@Getter
@Setter
public class FormulaireOffreStageDTO extends OffreStageDTO {

    @NotBlank(message = "Employer name is required.")
    private String employerName;

    @NotBlank(message = "Email is required.")
    @Email(message = "Invalid email format.")
    private String email;

    @NotBlank(message = "Website is required.")
    @Pattern(regexp = "^(https?:\\/\\/)?(www\\.)?([a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}(\\/[^\\s]*)?$",
            message = "Invalid website URL.")
    private String website;

    @NotBlank(message = "Location is required.")
    private String location;

    @NotBlank(message = "Salary is required.")
    @Digits(integer = 10, fraction =2, message = "Salary must be a valid number with up to 10 digits and 2 decimal places.")
    private String salary;

    @NotBlank(message = "Description is required.")
    @Size(max = 500, message = "Description cannot exceed 500 characters.")
    private String description;

    private Programme programme;

    private OffreStage.Visibility visibility;

    private List<Long> etudiantsPrives;


    //Avec étudiants privées
    @Builder
    public FormulaireOffreStageDTO(Long id, String entrepriseName, String employerName, String email, String website, String title, String location, String salary, String description, LocalDateTime createdAt, LocalDateTime updatedAt, Long createur_id, OffreStage.Status status, Programme programme, OffreStage.Visibility visibility, List<Long> etudiantsPrives, SessionEcole session, int annee) {
        super(id);
        this.employerName = employerName;
        this.email = email;
        this.website = website;
        this.location = location;
        this.salary = salary;
        this.description = description;
        this.programme = programme;
        this.visibility = visibility;
        this.etudiantsPrives = etudiantsPrives;
        this.setCreatedAt(createdAt);
        this.setUpdatedAt(updatedAt);
        this.setCreateur_id(createur_id);
        this.setStatus(status);
        this.setEntrepriseName(entrepriseName);
        this.setTitle(title);
        this.setSession(session);
        this.setAnnee(annee);
    }

    @Builder
    public FormulaireOffreStageDTO(Long id, String entrepriseName, String employerName, String email, String website, String title, String location, String salary, String description, LocalDateTime createdAt, LocalDateTime updatedAt, Long createur_id, OffreStage.Status status, Programme programme, OffreStage.Visibility visibility, SessionEcole session, int annee) {
        super(id);
        this.employerName = employerName;
        this.email = email;
        this.website = website;
        this.location = location;
        this.salary = salary;
        this.description = description;
        this.programme = programme;
        this.visibility = visibility;
        this.setCreatedAt(createdAt);
        this.setUpdatedAt(updatedAt);
        this.setCreateur_id(createur_id);
        this.setStatus(status);
        this.setEntrepriseName(entrepriseName);
        this.setTitle(title);
        this.setSession(session);
        this.setAnnee(annee);
    }

    public static FormulaireOffreStageDTO toDTO(FormulaireOffreStage formulaireOffreStage) {
        return new FormulaireOffreStageDTO(
                formulaireOffreStage.getId(),
                formulaireOffreStage.getEntrepriseName(),
                formulaireOffreStage.getEmployerName(),
                formulaireOffreStage.getEmail(),
                formulaireOffreStage.getWebsite(),
                formulaireOffreStage.getTitle(),
                formulaireOffreStage.getLocation(),
                formulaireOffreStage.getSalary(),
                formulaireOffreStage.getDescription(),
                formulaireOffreStage.getCreatedAt(),
                formulaireOffreStage.getUpdatedAt(),
                formulaireOffreStage.getCreateur().getId(),
                formulaireOffreStage.getStatus(),
                formulaireOffreStage.getProgramme(),
                formulaireOffreStage.getVisibility(),
                Collections.emptyList(),
                formulaireOffreStage.getSession(),
                formulaireOffreStage.getAnnee().getValue()
        );
    }
}
