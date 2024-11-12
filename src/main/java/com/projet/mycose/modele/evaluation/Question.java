package com.projet.mycose.modele.evaluation;

public class Question {
    private String questionKey;
    private String question;
    private Resultat resultat;

    public enum Resultat {
        TOTALEMENT_EN_ACCORD,
        PLUTOT_EN_ACCORD,
        PLUTOT_EN_DESACCORD,
        TOTALEMENT_EN_DESACCORD,
        NA
    }
}
