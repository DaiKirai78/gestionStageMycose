package com.projet.mycose.modele;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
public class FicheEvaluationStagiaire {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "etudiant_id", nullable = false)
    private Etudiant etudiant;

    @ManyToOne
    @JoinColumn(name = "employeur_id", nullable = false)
    private Employeur employeur;

    @OneToOne
    @JoinColumn(name = "contrat_id", unique = true)
    private Contrat contrat;

    private String nomEtudiant;
    private Programme programmeEtude;
    private String nomEntreprise;
    private String nomSuperviseur;

    @Column(columnDefinition = "BYTEA")
    private byte[] signatureSuperviseur;

    private String fonctionSuperviseur;
    private String numeroTelephone;


    //Section 1 - Productivité

    //Question a) planifier et organiser son travail de façon efficace
    private ComportementReponses prodQA;

    //Question b) comprendre rapidement les directives relatives à son travail
    private ComportementReponses prodQB;

    //Question c) maintenir un rythme de travail soutenu
    private ComportementReponses prodQC;

    //Question d) établir ses priorités
    private ComportementReponses prodQD;

    //Question e) respecter ses échéanciers
    private ComportementReponses prodQE;

    private String prodCommentaires;


    //Section 2 - Qualité du Travail

    //Question a) respecter les mandats qui lui ont été confiés
    private ComportementReponses qualTravailQA;

    //Question b) porter attention aux détails dans la réalisation de ses tâches
    private ComportementReponses qualTravailQB;

    //Question c) vérifier son travail, s’assurer que rien n’a été oublié
    private ComportementReponses qualTravailQC;

    //Question d) rechercher des occasions de se perfectionner
    private ComportementReponses qualTravailQD;

    //Question e) faire une bonne analyse des problèmes rencontrés
    private ComportementReponses qualTravailQE;

    private String qualTravailCommentaires;


    //Section 3 - Qualités des Relations Interpersonnelles

    //Question a) établir facilement des contacts avec les gens
    private ComportementReponses qualRelQA;

    //Question b) contribuer activement au travail d’équipe
    private ComportementReponses qualRelQB;

    //Question c) s’adapter facilement à la culture de l’entreprise
    private ComportementReponses qualRelQC;

    //Question d) accepter les critiques constructives
    private ComportementReponses qualRelQD;

    //Question e) être respectueux envers les gens
    private ComportementReponses qualRelQE;

    //Question f) faire preuve d'écoute active en essayant de comprendre le point de vue de l'autre
    private ComportementReponses qualRelQF;

    private String qualRelCommentaires;


    //Section 4 - Habiletés Personnelles

    //Question a) démontrer de l’intérêt et de la motivation au travail
    private ComportementReponses habPersQA;

    //Question b) exprimer clairement ses idées
    private ComportementReponses habPersQB;

    //Question c) faire preuve d’initiative
    private ComportementReponses habPersQC;

    //Question d) travailler de façon sécuritaire
    private ComportementReponses habPersQD;

    //Question e) démontrer un bon sens des responsabilités ne requérant qu’un minimum de supervision
    private ComportementReponses habPersQE;

    //Question f) être ponctuel et assidu à son travail
    private ComportementReponses habPersQF;

    private String habPersCommentaires;


    //Appréciation Globale
    private AppreciationGlobaleReponses appreciationGlobale;
    private String precisionAppreciationReponse;
    private boolean discuteeStagiaireReponse;
    private String heuresAccordeStagiaireReponse;


    //Accueillir le stagiaire pour le prochain stage
    private AcceuillirProchainStageReponses aimeraitAccueillirProchainStage;
    private String formationSuffisanteReponse;

    public enum ComportementReponses {
        TOTALEMENT_EN_ACCORD,
        PLUTOT_EN_ACCORD,
        PLUTOT_EN_DESACCORD,
        TOTALEMENT_EN_DESACCORD,
        NA
    }

    public enum AppreciationGlobaleReponses {
        HABILITES_DEPASSENT_BEAUCOUP_ATTENTES,
        HABILITES_DEPASSENT_ATTENTES,
        HABILITES_REPONDENT_PLEINEMENT_ATTENTES,
        HABILITES_REPONDENT_PARTIELLEMENT_ATTENTES,
        HABILITES_REPONDENT_PAS_ATTENTES
    }

    public enum AcceuillirProchainStageReponses {
        OUI,
        NON,
        PEUT_ETRE
    }
}