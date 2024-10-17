package com.projet.mycose.modele;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@DiscriminatorColumn(name = "FORMAT")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class OffreStage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String title;

    private String entrepriseName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OffreStage.Status status;

    private String statusDescription;

    //Doit contenir un programme si public et null si privé
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Programme programme;

    //Doit être à private si une liste d'étudiants est associée (EtudiantOffreStagePrivee)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OffreStage.Visibility visibility;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "createur_id")
    private Utilisateur createur;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "offreStage", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ApplicationStage> applicationStages;

    @PrePersist
    public void prePersist() {
        if (status == null) {
            status = OffreStage.Status.WAITING;
        }
        if (visibility == null) {
            visibility = OffreStage.Visibility.PUBLIC;
        }
        //Important car un employeur ne peut pas déterminer ce que le programme est, c'est le gestionnaire qui va le faire.
        if (programme == null) {
            programme = Programme.NOT_SPECIFIED;
        }
    }

    public enum Status {
        WAITING,
        ACCEPTED,
        REFUSED,
        DELETED
    }

    public enum Visibility {
        PUBLIC,
        PRIVATE,
        UNDEFINED
    }
}
