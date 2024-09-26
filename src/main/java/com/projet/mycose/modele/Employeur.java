package com.projet.mycose.modele;

import com.projet.mycose.modele.auth.Credentials;
import com.projet.mycose.modele.auth.Role;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Getter
@Setter
@DiscriminatorValue("Employeur")
public class Employeur extends Utilisateur {
    private String nomOrganisation;
    @Builder
    public Employeur(Long id, String prenom, String nom, String numeroDeTelephone, String courriel, String motDePasse, String nomOrganisation) {
        super(id, prenom, nom, numeroDeTelephone, Credentials.builder().email(courriel).password(motDePasse).role(Role.EMPLOYEUR).build());
        this.nomOrganisation = nomOrganisation;
    }

    // Sans Id
    public Employeur(String prenom, String nom, String numeroDeTelephone, String courriel, String motDePasse) {
        super(prenom, nom, numeroDeTelephone, Credentials.builder().email(courriel).password(motDePasse).role(Role.EMPLOYEUR).build());
        this.nomOrganisation = nomOrganisation;
    }
}
