package com.projet.mycose.service.dto;



import com.projet.mycose.modele.FormulaireOffreStage;
import com.projet.mycose.modele.OffreStage;
import jakarta.persistence.Column;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

import java.time.LocalDateTime;

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

    @Builder
    public FormulaireOffreStageDTO(Long id, String entrepriseName,  String employerName, String email, String website, String title,  String location, String salary, String description, LocalDateTime createdAt, LocalDateTime updatedAt, Long createur_id) {
        super(id, title, entrepriseName, createur_id, createdAt, updatedAt);
    public FormulaireOffreStageDTO(Long id, String entrepriseName, String employerName, String email, String website, String title, String location, String salary, String description, LocalDateTime createdAt, LocalDateTime updatedAt, Long createur_id) {
        super(id, title, entrepriseName, createur_id, createdAt, updatedAt);
        this.employerName = employerName;
        this.email = email;
        this.website = website;
        this.location = location;
        this.salary = salary;
        this.description = description;
    }

    @Builder
    public FormulaireOffreStageDTO(Long id, String entrepriseName, String employerName, String email, String website, String title, String location, String salary, String description, LocalDateTime createdAt, LocalDateTime updatedAt, Long createur_id, OffreStage.Status status) {
        super(id, title, entrepriseName, createur_id, createdAt, updatedAt, status);
        this.employerName = employerName;
        this.email = email;
        this.website = website;
        this.location = location;
        this.salary = salary;
        this.description = description;
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
                formulaireOffreStage.getCreateur().getId());
    }

    public static FormulaireOffreStageDTO toDTOAll(FormulaireOffreStage formulaireOffreStage) {
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
                formulaireOffreStage.getStatus()
        );
        //System.out.println(formulaireOffreStage.getId());
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
                formulaireOffreStage.getCreateur().getId());
    }
}
