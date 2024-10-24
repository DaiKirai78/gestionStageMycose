package com.projet.mycose.modele;

import com.projet.mycose.modele.auth.Credentials;
import com.projet.mycose.modele.auth.Role;
import jakarta.persistence.CascadeType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@DiscriminatorValue("Enseignant")
@Entity
@Getter
@Setter
public class Enseignant extends Utilisateur {

    @OneToMany(mappedBy = "enseignantAssignee", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Etudiant> etudiantsAssignees = new ArrayList<>();

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
