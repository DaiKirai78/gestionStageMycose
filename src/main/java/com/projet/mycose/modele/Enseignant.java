package com.projet.mycose.modele;

import com.projet.mycose.modele.auth.Credentials;
import com.projet.mycose.modele.auth.Role;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@DiscriminatorValue("Enseignant")
@Entity
public class Enseignant extends Utilisateur {
    @Builder
    public Enseignant(Long id, String prenom, String nom, String numeroDeTelephone, String courriel, String motDePasse) {
        super(id,
                prenom,
                nom,
                numeroDeTelephone,
                Credentials.builder().email(courriel).password(motDePasse).role(Role.ENSEIGNANT).build());
    }

    // Sans Id
    public Enseignant(String prenom, String nom, String numeroDeTelephone, String courriel, String motDePasse) {
        super(prenom,
                nom,
                numeroDeTelephone,
                Credentials.builder().email(courriel).password(motDePasse).role(Role.ENSEIGNANT).build());
    }
}
