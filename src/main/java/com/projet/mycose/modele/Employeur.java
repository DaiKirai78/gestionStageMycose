package com.projet.mycose.modele;

import com.projet.mycose.modele.auth.Credentials;
import com.projet.mycose.modele.auth.Role;
import jakarta.persistence.CascadeType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Set;

@Entity
@NoArgsConstructor
@Getter
@Setter
@DiscriminatorValue("Employeur")
public class Employeur extends Utilisateur {
    private String nomOrganisation;

    @OneToMany(mappedBy = "employeur", cascade = CascadeType.ALL)
    @ToString.Exclude
    private Set<OffreStage> offres;

    @Builder
    public Employeur(Long id, String prenom, String nom, String numeroDeTelephone, String courriel, String motDePasse, String nomOrganisation) {
        super(id, prenom, nom, numeroDeTelephone, Credentials.builder().email(courriel).password(motDePasse).role(Role.EMPLOYEUR).build());
        this.nomOrganisation = nomOrganisation;
    }

    // Sans Id
    public Employeur(String prenom, String nom, String numeroDeTelephone, String courriel, String motDePasse, String nomOrganisation) {
        super(prenom, nom, numeroDeTelephone, Credentials.builder().email(courriel).password(motDePasse).role(Role.EMPLOYEUR).build());
        this.nomOrganisation = nomOrganisation;
    }
}
