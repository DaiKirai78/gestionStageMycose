package com.projet.mycose.modele;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@DiscriminatorValue("Fichier")
public class FichierOffreStage extends OffreStage{

    private String filename;

    @Column(name = "data", columnDefinition = "BYTEA")
    private byte[] data;

    @OneToMany(mappedBy = "offreStage", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ApplicationStage> applications;

    public FichierOffreStage(String title, String entrepriseName, String filename, byte[] data, Utilisateur createur, OffreStage.Visibility visibility, Programme programme, OffreStage.Status status) {
        super();
        this.setTitle(title);
        this.setEntrepriseName(entrepriseName);
        this.setCreateur(createur);
        this.filename = filename;
        this.data = data;
        this.setVisibility(visibility);
        this.setProgramme(programme);
        this.setStatus(status);
    }

}
