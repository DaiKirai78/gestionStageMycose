package com.projet.mycose.modele;

import com.projet.mycose.modele.auth.Credentials;
import com.projet.mycose.modele.auth.Role;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@DiscriminatorValue("Etudiant")
@Getter
@Setter
@Entity
public class Etudiant extends Utilisateur {

    @Enumerated(EnumType.STRING)
    private Programme programme;
//    private List<FichierOffreStage> stagesVisiblesFichiers;
//    private List<FormulaireOffreStage> stagesVisiblesFormulaires;

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
