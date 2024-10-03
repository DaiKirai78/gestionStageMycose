package com.projet.mycose.modele;

public enum Programme {
    TECHNIQUE_INFORMATIQUE("TECHNIQUE_INFORMATIQUE"),
    GENIE_LOGICIEL("GÉNIE_LOGICIEL"),
    RESEAU("RÉSEAU"),
    ;

    private final String programme;

    Programme(String programme) {
        this.programme = programme;
    }

    public String getProgramme() {
        return programme;
    }

    @Override
    public String toString() {
        return programme;
    }
}
