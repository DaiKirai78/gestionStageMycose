package com.projet.mycose.modele;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Arrays;

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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "varchar(20) default 'WAITING'")
    private Status status;

    private String statusDescription;

    @PrePersist
    public void prePersist() {
        if (status == null) {
            status = Status.WAITING;
        }
    }

    @Override
    public String toString() {
        return "FichierCV{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", filename='" + filename + '\'' +
                ", status=" + status +
                ", statusDescription='" + statusDescription + '\'' +
                '}';
    }
}