package com.projet.mycose.modele.auth;

import java.util.HashSet;
import java.util.Set;

public enum Role {
    GESTIONNAIRE_STAGE("ROLE_GESTIONNAIRE_STAGE"),
    ETUDIANT("ROLE_ETUDIANT"),
    ;

    //TODO: AJOUTER LES AUTRES ROLES

    private final String string;
    private final Set<Role> managedRoles = new HashSet<>();

    static{
        GESTIONNAIRE_STAGE.managedRoles.add(ETUDIANT);
    }

    Role(String string){
        this.string = string;
    }

    @Override
    public String toString(){
        return string;
    }

}
