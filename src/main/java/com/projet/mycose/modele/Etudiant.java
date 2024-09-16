package com.projet.mycose.modele;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@DiscriminatorValue("Etudiant")
@Entity
public class Etudiant extends Utilisateur {
    public Etudiant(String prenom, String nom, String courriel, String motDePasse, String numeroDeTelephone) {
        super(prenom, nom, courriel, motDePasse, numeroDeTelephone);
    }
}
