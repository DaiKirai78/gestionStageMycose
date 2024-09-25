package com.projet.mycose.modele;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "fichiers_CV")
@Getter
@Setter
public class FichierCV {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String filename;

    @Column(name = "data", columnDefinition = "BYTEA")
    private byte[] data;

}