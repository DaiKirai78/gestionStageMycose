package com.projet.mycose.modele;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    private String filename;

    @Column(name = "data", columnDefinition = "BYTEA")
    private byte[] data;

}