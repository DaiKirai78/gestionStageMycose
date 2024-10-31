package com.projet.mycose.modele;

import com.projet.mycose.dto.SummonEtudiantDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Convocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    private LocalDateTime summonedAt;

    private LocalDateTime scheduledAt;

    @OneToOne
    @JoinColumn(name = "application_stage_id", nullable = false)
    private ApplicationStage applicationStage;

    @Enumerated(EnumType.STRING)
    private ConvocationStatus status;

    private String location;


    //Message envoyé par la personne qui a créé la convocation
    private String messageConvocation;

    //Message envoyé par l'étudiant en réponse à la convocation
    private String messageEtudiant;

    public Convocation(ApplicationStage applicationStage, SummonEtudiantDTO summonEtudiantDTO) {
        this.applicationStage = applicationStage;
        this.scheduledAt = summonEtudiantDTO.getScheduledAt();
        this.location = summonEtudiantDTO.getLocation();
        this.messageConvocation = summonEtudiantDTO.getMessageConvocation();
    }

    public enum ConvocationStatus {
        PENDING,
        ACCEPTED,
        REJECTED
    }

    @PrePersist
    protected void onCreate() {
        if (status == null) {
            status = ConvocationStatus.PENDING;
        }
    }
}
