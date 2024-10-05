package com.projet.mycose.modele;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
//@Table(name = "formulairesOffresStage")
@Getter
@Setter
@NoArgsConstructor
@DiscriminatorValue("FORM")
public class FormulaireOffreStage extends OffreStage{

    //@Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    //private Long id;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    private String entrepriseName;
    private String employerName;
    private String email;
    private String website;
    private String title;
    private String location;
    private String salary;
    private String description;
}
