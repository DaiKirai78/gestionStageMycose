package com.projet.mycose.modele;
import com.projet.mycose.service.UtilisateurService;
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
@DiscriminatorValue("FILE")
public class FichierOffreStage extends OffreStage{

    private String filename;

    @Column(name = "data", columnDefinition = "BYTEA")
    private byte[] data;

    public FichierOffreStage(String title, String entrepriseName, String filename, byte[] data, Utilisateur createur) {
        super();
        this.setTitle(title);
        this.setEntrepriseName(entrepriseName);
        this.setCreateur(createur);
        this.filename = filename;
        this.data = data;
    }

}