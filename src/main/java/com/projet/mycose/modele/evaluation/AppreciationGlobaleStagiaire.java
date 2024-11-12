package com.projet.mycose.modele.evaluation;

public class AppreciationGlobaleStagiaire {

    private ReponseHabilitesAttentes reponse;
    private boolean evaluationDiscutee;

    public enum ReponseHabilitesAttentes {
        HABILITES_DEPASSENT_BEAUCOUP_ATTENTES,
        HABILITES_DEPASSENT_ATTENTES,
        HABILITES_REPONDENT_PLEINEMENT_ATTENTES,
        HABILITES_REPONDENT_PARTIELLEMENT_ATTENTES,
        HABILITES_REPONDENT_PAS_ATTENTES
    }
}
