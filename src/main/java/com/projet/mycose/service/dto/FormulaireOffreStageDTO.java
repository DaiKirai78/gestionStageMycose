package com.projet.mycose.service.dto;



import com.projet.mycose.modele.FormulaireOffreStage;
import com.projet.mycose.modele.OffreStage;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class FormulaireOffreStageDTO extends OffreStageDTO {

    //private Long id;

    @NotBlank(message = "Enterprise name is required.")
    private String entrepriseName;

    @NotBlank(message = "Employer name is required.")
    private String employerName;

    @NotBlank(message = "Email is required.")
    @Email(message = "Invalid email format.")
    private String email;

    @NotBlank(message = "Website is required.")
    @Pattern(regexp = "^(https?:\\/\\/)?(www\\.)?([a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}(\\/[^\\s]*)?$",
            message = "Invalid website URL.")
    private String website;

    @NotBlank(message = "Title is required.")
    @Size(max = 100, message = "Title cannot exceed 100 characters.")
    private String title;

    @NotBlank(message = "Location is required.")
    private String location;

    @NotBlank(message = "Salary is required.")
    @Digits(integer = 10, fraction =2, message = "Salary must be a valid number with up to 10 digits and 2 decimal places.")
    private String salary;

    @NotBlank(message = "Description is required.")
    @Size(max = 500, message = "Description cannot exceed 500 characters.")
    private String description;

    @Builder
    public FormulaireOffreStageDTO(Long id, String entrepriseName, String employerName, String email, String website, String title, String location, String salary, String description) {
        super(id);
        this.entrepriseName = entrepriseName;
        this.employerName = employerName;
        this.email = email;
        this.website = website;
        this.title = title;
        this.location = location;
        this.salary = salary;
        this.description = description;
    }

    public static FormulaireOffreStageDTO toDTO(FormulaireOffreStage formulaireOffreStage) {
        return FormulaireOffreStageDTO.builder()
                .id(formulaireOffreStage.getId())
                .entrepriseName(formulaireOffreStage.getEntrepriseName())
                .employerName(formulaireOffreStage.getEmployerName())
                .email(formulaireOffreStage.getEmail())
                .website(formulaireOffreStage.getWebsite())
                .title(formulaireOffreStage.getTitle())
                .location(formulaireOffreStage.getLocation())
                .salary(formulaireOffreStage.getSalary())
                .location(formulaireOffreStage.getLocation())
                .build();
    }
}