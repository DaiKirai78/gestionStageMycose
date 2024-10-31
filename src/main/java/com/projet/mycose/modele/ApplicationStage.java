package com.projet.mycose.modele;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(
        name = "application_stage",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"etudiant_id", "offre_stage_id"})}
)
public class ApplicationStage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "etudiant_id", nullable = false)
    private Etudiant etudiant;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "offre_stage_id", nullable = false)
    private OffreStage offreStage;

    @Enumerated(EnumType.STRING)
    private ApplicationStatus status;

    @CreationTimestamp
    private LocalDateTime appliedAt;

    @OneToOne(fetch = FetchType.EAGER, mappedBy = "applicationStage", cascade = CascadeType.ALL, orphanRemoval = true)
    private Convocation convocation;

    @Builder
    public ApplicationStage(Etudiant etudiant, OffreStage offreStage) {
        this.etudiant = etudiant;
        this.offreStage = offreStage;
    }

    public enum ApplicationStatus {
        PENDING,
        ACCEPTED,
        REJECTED,
        SUMMONED
    }

    @PrePersist
    protected void onCreate() {
        if (status == null) {
            status = ApplicationStatus.PENDING;
        }
    }
}
