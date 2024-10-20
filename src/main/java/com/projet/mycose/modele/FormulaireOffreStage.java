package com.projet.mycose.modele;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@DiscriminatorValue("Formulaire")
public class FormulaireOffreStage extends OffreStage{


    private String employerName;
    private String email;
    private String website;
    private String location;
    private String salary;
    private String description;

    public FormulaireOffreStage(String title, String entrepriseName, String employerName, String email, String website, String location, String salary, String description, Utilisateur createur, OffreStage.Visibility visibility, Programme programme, OffreStage.Status status) {
        super();
        this.setTitle(title);
        this.setEntrepriseName(entrepriseName);
        this.setCreateur(createur);
        this.employerName = employerName;
        this.email = email;
        this.website = website;
        this.location = location;
        this.salary = salary;
        this.description = description;
        this.setVisibility(visibility);
        this.setProgramme(programme);
        this.setStatus(status);
    }
}
