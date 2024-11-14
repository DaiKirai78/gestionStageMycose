package com.projet.mycose.modele.evaluation;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AcceuillirProchainStage {

    @Enumerated(EnumType.STRING)
    private ReponseAcceuillirProchainStage reponse;

    private String ReponseFormationSuffisante;
    private String nomEmployeur;

    @Column(columnDefinition = "BYTEA")
    private byte[] signatureSuperviseur;

    private LocalDateTime date;
    public enum ReponseAcceuillirProchainStage {
        OUI,
        NON,
        PEUT_ETRE
    }
}
