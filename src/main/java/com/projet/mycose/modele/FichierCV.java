package com.projet.mycose.modele;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "fichiers_CV")
@Getter
@Setter
public class FichierCV {

    public enum Status {
        WAITING,
        ACCEPTED,
        REFUSED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    private String filename;

    @Column(name = "data", columnDefinition = "BYTEA")
    private byte[] data;

    private Status status = Status.WAITING;

    private String statusDescription;

    @CreationTimestamp
    private LocalDateTime createdAt;
}