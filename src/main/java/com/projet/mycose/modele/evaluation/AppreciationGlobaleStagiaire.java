package com.projet.mycose.modele.evaluation;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AppreciationGlobaleStagiaire {

    @Enumerated(EnumType.STRING)
    private ReponseHabilitesAttentes reponseAppreciationGlobale;

    private boolean evaluationDiscutee;
    private String nombreHeuresParSemaineAccordeStagiaire;

    public enum ReponseHabilitesAttentes {
        HABILITES_DEPASSENT_BEAUCOUP_ATTENTES,
        HABILITES_DEPASSENT_ATTENTES,
        HABILITES_REPONDENT_PLEINEMENT_ATTENTES,
        HABILITES_REPONDENT_PARTIELLEMENT_ATTENTES,
        HABILITES_REPONDENT_PAS_ATTENTES
    }
}
