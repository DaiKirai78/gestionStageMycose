package com.projet.mycose.modele;
import com.projet.mycose.service.UtilisateurService;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@DiscriminatorValue("Fichier")
public class FichierOffreStage extends OffreStage{

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    private String filename;

    @Column(name = "data", columnDefinition = "BYTEA")
    private byte[] data;

    @OneToMany(mappedBy = "offreStage", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ApplicationStage> applications;

    public FichierOffreStage(String title, String entrepriseName, String filename, byte[] data, Utilisateur createur) {
        super();
        this.setTitle(title);
        this.setEntrepriseName(entrepriseName);
        this.setCreateur(createur);
        this.filename = filename;
        this.data = data;
    }

}
