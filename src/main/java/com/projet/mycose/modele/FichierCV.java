package com.projet.mycose.modele;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "fichiers_CV")
@Getter
@Setter
public class FichierCV {

    enum Status {
        WAITING,
        ACCEPTED,
        REFUSED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String filename;

    @Column(name = "data", columnDefinition = "BYTEA")
    private byte[] data;

    private Status status = Status.WAITING;

    private String statusDescription;

    @CreationTimestamp
    private LocalDateTime createdAt;
}