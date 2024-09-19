package com.projet.mycose.modele;
import jakarta.persistence.*;

@Entity
@Table(name = "fichiersOffresStage")
public class FichierOffreStage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String filename;

    @Lob
    @Column(name = "data", columnDefinition = "BYTEA")
    private byte[] data;
}