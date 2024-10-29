package com.projet.mycose.modele;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Arrays;

@Entity
@Getter
@Setter
public class Contrat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(columnDefinition = "BYTEA")
    private byte[] pdf;

    @Lob
    @Column(columnDefinition = "BYTEA")
    private byte[] signatureGestionnaire;

    @Lob
    @Column(columnDefinition = "BYTEA")
    private byte[] signatureEtudiant;

    @Lob
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

    @Override
    public String toString() {
        return "Contrat{" +
                "id=" + id +
                ", pdf=" + Arrays.toString(pdf) +
                ", signatureGestionnaire=" + Arrays.toString(signatureGestionnaire) +
                ", signatureEtudiant=" + Arrays.toString(signatureEtudiant) +
                ", signatureEmployeur=" + Arrays.toString(signatureEmployeur) +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", etudiant=" + etudiant +
                '}';
    }
}
