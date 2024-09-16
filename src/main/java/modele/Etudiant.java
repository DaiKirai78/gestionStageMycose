package modele;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Entity
@DiscriminatorValue("Etudiant")
public class Etudiant extends Utilisateur {
    public Etudiant(String nom, String nomDeFamille, String courriel, String motDePasse, String numeroDeTelephone) {
        super(nom, nomDeFamille, courriel, motDePasse, numeroDeTelephone);
    }
}
