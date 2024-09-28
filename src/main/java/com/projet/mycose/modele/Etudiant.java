package com.projet.mycose.modele;

import com.projet.mycose.modele.auth.Credentials;
import com.projet.mycose.modele.auth.Role;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@DiscriminatorValue("Etudiant")
@Getter
@Setter
@Entity
public class Etudiant extends Utilisateur {
    private String programme;
    @Builder
    public Etudiant(Long id, String prenom, String nom, String numeroDeTelephone, String courriel, String motDePasse, String programme) {
        super(id,
                prenom,
                nom,
                numeroDeTelephone,
                Credentials.builder().email(courriel).password(motDePasse).role(Role.ETUDIANT).build());
        this.programme = programme;
    }

    // Sans Id
    public Etudiant(String prenom, String nom, String numeroDeTelephone, String courriel, String motDePasse, String programme) {
        super(prenom,
                nom,
                numeroDeTelephone,
                Credentials.builder().email(courriel).password(motDePasse).role(Role.ETUDIANT).build());
        this.programme = programme;
    }
}
