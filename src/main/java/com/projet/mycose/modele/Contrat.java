package com.projet.mycose.modele;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Contrat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "BYTEA")
    private byte[] signatureGestionnaire;

    @Column(columnDefinition = "BYTEA")
    private byte[] signatureEtudiant;

    @Column(columnDefinition = "BYTEA")
    private byte[] signatureEmployeur;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "etudiant_id")
    private Etudiant etudiant;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "employeur_id")
    private Employeur employeur;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "gestionnaire_id")
    private GestionnaireStage gestionnaireStage;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    public enum Status {
        ACTIVE,
        INACTIVE
    }

    @PrePersist
    public void prePersist() {
        if (status == null) {
            status = Status.ACTIVE;
        }
    }

    @Override
    public String toString() {
        return "Contrat{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", etudiant=" + etudiant +
                ", employeur=" + employeur +
                ", gestionnaireStage=" + gestionnaireStage +
                ", status=" + status +
                '}';
    }
}
