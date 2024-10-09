package com.projet.mycose.modele;

import com.projet.mycose.modele.auth.Credentials;
import com.projet.mycose.modele.auth.Role;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@NoArgsConstructor
@DiscriminatorValue("Etudiant")
@Getter
@Setter
@Entity
public class Etudiant extends Utilisateur {

    @Enumerated(EnumType.STRING)
    private Programme programme;

    @OneToMany(mappedBy = "etudiant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FichierCV> fichiersCV;

    @ManyToMany(cascade = CascadeType.ALL)
    private Set<OffreStage> offres;

    @OneToMany(mappedBy = "etudiant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ApplicationStage> applications;

    @Builder
    public Etudiant(Long id, String prenom, String nom, String numeroDeTelephone, String courriel, String motDePasse, Programme programme) {
        super(id,
                prenom,
                nom,
                numeroDeTelephone,
                Credentials.builder().email(courriel).password(motDePasse).role(Role.ETUDIANT).build());
        this.programme = programme;
    }

    // Sans Id
    public Etudiant(String prenom, String nom, String numeroDeTelephone, String courriel, String motDePasse, Programme programme) {
        super(prenom,
                nom,
                numeroDeTelephone,
                Credentials.builder().email(courriel).password(motDePasse).role(Role.ETUDIANT).build());
        this.programme = programme;
    }
}
