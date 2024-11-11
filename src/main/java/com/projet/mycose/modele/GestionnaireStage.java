package com.projet.mycose.modele;

import com.projet.mycose.modele.auth.Credentials;
import com.projet.mycose.modele.auth.Role;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@DiscriminatorValue("GestionnaireStage")
@Getter
@Setter
@Entity
public class GestionnaireStage extends Utilisateur {

    @OneToMany(mappedBy = "gestionnaireStage", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Contrat> contrat;

    @Builder
    public GestionnaireStage(Long id, String prenom, String nom, String numeroDeTelephone, String courriel, String motDePasse) {
        super(id,
                prenom,
                nom,
                numeroDeTelephone,
                Credentials.builder().email(courriel).password(motDePasse).role(Role.GESTIONNAIRE_STAGE).build());
    }
    @Builder
    public GestionnaireStage(String prenom, String nom, String numeroDeTelephone, String courriel, String motDePasse) {
        super(prenom,
                nom,
                numeroDeTelephone,
                Credentials.builder().email(courriel).password(motDePasse).role(Role.GESTIONNAIRE_STAGE).build());
    }
}