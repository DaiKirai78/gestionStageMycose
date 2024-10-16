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

    //TODO : À ENLEVER AU PLUS SACRANT
    private String statusDescription;

    @OneToMany(mappedBy = "offreStage", cascade = CascadeType.ALL)
    private List<EtudiantOffreStagePrivee> offresStagePrivees;

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
    }

    public enum Status {
        WAITING,
        ACCEPTED,
        REFUSED,
        DELETED
    }
}
