package com.projet.mycose.modele;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
public class FicheEvaluationMilieuStage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "etudiant_id", nullable = false)
    private Etudiant etudiant;

    @ManyToOne
    @JoinColumn(name = "enseigant_id", nullable = false)
    private Enseignant enseignant;

    @OneToOne
    @JoinColumn(name = "contrat_id", unique = true)
    private Contrat contrat;
    //Pourrait être nul dans le futur si on veut permettre des stages créés par les gestionnaires de stage d'être évalués
    @ManyToOne
    @JoinColumn(name = "employeur_id", nullable = false)
    private Employeur employeur;

    @OneToOne
    @JoinColumn(name = "contrat_id")
    private Contrat contrat;

    //Section sur l'identification de l'entreprise
    private String nomEntreprise;
    private String nomPersonneContact;
    private String adresseEntreprise;
    private String villeEntreprise;
    private String codePostalEntreprise;
    private String telephoneEntreprise;
    private String telecopieurEntreprise;

    //Section sur l'identification du stagiaire
    private String nomStagiaire;
    private LocalDateTime dateDebutStage;
    //Stage 1 ou 2
    private int numeroStage;

    //Section sur l'évaluation du milieu de stage
    //Question a) Les tâches confiées au stagiaire sont conformes aux tâches annoncées dans l’entente de stage.
    private EvaluationMilieuStageReponses evaluationQA;

    //Question b) Des mesures d’accueil facilitent l’intégration du nouveau stagiaire.
    private EvaluationMilieuStageReponses evaluationQB;

    //Question c) Le temps réel consacré à l’encadrement du stagiaire est suffisant.
    private EvaluationMilieuStageReponses evaluationQC;

    //Nombre d'heures par semaine pour les 3 mois se situant sous la question C
    private float nombreHeuresParSemainePremierMois;
    private float nombreHeuresParSemaineDeuxiemeMois;
    private float nombreHeuresParSemaineTroisiemeMois;

    //Question d) L’environnement de travail respecte les normes d’hygiène et de sécurité au travail.
    private EvaluationMilieuStageReponses evaluationQD;

    //Question e) Le climat de travail est agréable.
    private EvaluationMilieuStageReponses evaluationQE;

    //Question f) Le milieu de stage est accessible par transport en commun.
    private EvaluationMilieuStageReponses evaluationQF;

    //Question g) Le salaire offert est intéressant pour le stagiaire.
    private EvaluationMilieuStageReponses evaluationQG;

    //Salaire horaire lié à la question G
    private float salaireHoraire;

    //Question h) La communication avec le superviseur de stage facilite le déroulement du stage.
    private EvaluationMilieuStageReponses evaluationQH;

    //Question i) L’équipement fourni est adéquat pour réaliser les tâches confiées.
    private EvaluationMilieuStageReponses evaluationQI;

    //Question j) Le volume de travail est acceptable.
    private EvaluationMilieuStageReponses evaluationQJ;

    private String commentaires;

    //Section sur les observations générales
    //Ce milieu est à privilégier pour le premier ou deuxième stage?
    private MilieuAPrivilegierReponses milieuAPrivilegier;

    //Ce milieu est ouvert à accueillir combien de stagiaires?
    private MilieuPretAAccueillirNombreStagiairesReponses milieuPretAAccueillirNombreStagiaires;

    //Ce milieu désire accueillir le même stagiaire pour un prochain stage?
    private OuiNonReponses milieuDesireAccueillirMemeStagiaire;

    //Ce milieu offre des quarts de travail variables?
    private OuiNonReponses millieuOffreQuartsTravailVariables;

    //Si oui, nommer les quarts de travail (jusqu'à 3)
    private LocalDateTime quartTravailDebut1;
    private LocalDateTime quartTravailFin1;
    private LocalDateTime quartTravailDebut2;
    private LocalDateTime quartTravailFin2;
    private LocalDateTime quartTravailDebut3;
    private LocalDateTime quartTravailFin3;

    @CreationTimestamp
    private LocalDate dateEvaluation;

    //TODO: AJOUTER LA SIGNATURE DE L'ENSEIGNANT

    public enum EvaluationMilieuStageReponses {
        TOTALEMENT_EN_ACCORD,
        PLUTOT_EN_ACCORD,
        PLUTOT_EN_DESACCORD,
        TOTALEMENT_EN_DESACCORD,
        IMPOSSIBLE_DE_SE_PRONONCER
    }

    public enum MilieuAPrivilegierReponses {
        PREMIER_STAGE,
        DEUXIEME_STAGE
    }

    public enum MilieuPretAAccueillirNombreStagiairesReponses {
        UN,
        DEUX,
        TROIS,
        PLUS_DE_TROIS
    }

    public enum OuiNonReponses {
        OUI,
        NON
    }
}
