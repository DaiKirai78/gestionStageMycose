package com.projet.mycose.modele;

import lombok.Getter;

@Getter
public enum Programme {
    TECHNIQUE_INFORMATIQUE("Technique de l'informatique"),
    GENIE_LOGICIEL("Génie logiciel"),
    RESEAU("Réseau"),
    ;

    private final String programme;

    Programme(String programme) {
        this.programme = programme;
    }

    @Override
    public String toString() {
        return programme;
    }
}
