package com.projet.mycose.modele;

import com.projet.mycose.modele.auth.Credentials;
import com.projet.mycose.modele.auth.Role;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@DiscriminatorValue("Etudiant")
@Entity
public class Etudiant extends Utilisateur {
    @Builder
    public Etudiant(Long id, String prenom, String nom, String numeroDeTelephone, String courriel, String motDePasse) {
        super(id,
                prenom,
                nom,
                numeroDeTelephone,
                Credentials.builder().email(courriel).password(motDePasse).role(Role.ETUDIANT).build());
    }

    //Constructeur classique
    public Etudiant(String prenom, String nom, String numeroDeTelephone, String courriel, String motDePasse) {
        super(prenom,
                nom,
                numeroDeTelephone,
                Credentials.builder().email(courriel).password(motDePasse).role(Role.ETUDIANT).build());
    }
}
