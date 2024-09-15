package modele;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type")
public abstract class Utilisateur {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String nom;
    private String nomDeFamille;
    private String courriel;
    private String motDePasse;
    private String numeroDeTelephone;

    public Utilisateur(String nom, String nomDeFamille, String courriel, String motDePasse, String numeroDeTelephone) {
        this.nom = nom;
        this.nomDeFamille = nomDeFamille;
        this.courriel = courriel;
        this.motDePasse = motDePasse;
        this.numeroDeTelephone = numeroDeTelephone;
    }
}
