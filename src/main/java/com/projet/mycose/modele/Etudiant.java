package com.projet.mycose.modele;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.projet.mycose.modele.auth.Credentials;
import com.projet.mycose.modele.auth.Role;
import jakarta.persistence.*;
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
    private ContractStatus contractStatus;

    public enum ContractStatus {
        ACTIVE,
        NO_CONTRACT,
        PENDING
    }

    @PrePersist
    public void PrePersist() {
        if (contractStatus == null)
            contractStatus = ContractStatus.NO_CONTRACT;
    }

    @Builder
    public Etudiant(Long id, String prenom, String nom, String numeroDeTelephone, String courriel, String motDePasse, Programme programme, ContractStatus contractStatus) {
        super(id,
                prenom,
                nom,
                numeroDeTelephone,
                Credentials.builder().email(courriel).password(motDePasse).role(Role.ETUDIANT).build());
        this.programme = programme;
        this.contractStatus = contractStatus;
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
