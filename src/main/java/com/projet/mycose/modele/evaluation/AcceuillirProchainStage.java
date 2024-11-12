package com.projet.mycose.modele.evaluation;

import java.time.LocalDateTime;

public class AcceuillirProchainStage {

    private ReponseAcceuillirProchainStage reponse;
    private String ReponseFormationSuffisante;

    private String nomEmployeur;

    private byte[] signatureSuperviseur;
    private LocalDateTime date;
    public enum ReponseAcceuillirProchainStage {
        OUI,
        NON,
        PEUT_ETRE
    }
}
