package com.projet.mycose.modele;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
//@Table(name = "fichiersOffresStage")
@Getter
@Setter
@NoArgsConstructor
@DiscriminatorValue("FILE")
public class FichierOffreStage extends OffreStage{

    //@Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    //private Long id;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    private String filename;

    @Column(name = "data", columnDefinition = "BYTEA")
    private byte[] data;

}