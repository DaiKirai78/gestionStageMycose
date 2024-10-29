package com.projet.mycose.modele;

import com.fasterxml.jackson.annotation.JsonManagedReference;
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
    @JsonManagedReference
    private List<FichierCV> fichiersCV;

    @OneToMany(mappedBy = "etudiant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ApplicationStage> applications;

    @ManyToOne
    private Enseignant enseignantAssignee;

    @OneToMany(mappedBy = "etudiant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Contrat> contrat;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContractStatus contractStatus;

    public enum ContractStatus {
        ACTIVE,
        NO_CONTRACT,
        PENDING
    }

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
