package com.projet.mycose.dto;

import com.projet.mycose.modele.FicheEvaluationMilieuStage.EvaluationMilieuStageReponses;
import com.projet.mycose.modele.FicheEvaluationMilieuStage.MilieuAPrivilegierReponses;
import com.projet.mycose.modele.FicheEvaluationMilieuStage.MilieuPretAAccueillirNombreStagiairesReponses;
import com.projet.mycose.modele.FicheEvaluationMilieuStage.OuiNonReponses;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FicheEvaluationMilieuStageDTO {
    private long id;

    // Identification de l'entreprise
    @NotBlank(message = "Le nom de l'entreprise ne peut pas être vide.")
    private String nomEntreprise;

    @NotBlank(message = "Le nom de la personne de contact ne peut pas être vide.")
    private String nomPersonneContact;

    @NotBlank(message = "L'adresse de l'entreprise ne peut pas être vide.")
    private String adresseEntreprise;

    @NotBlank(message = "La ville de l'entreprise ne peut pas être vide.")
    private String villeEntreprise;

    @NotBlank(message = "Le code postal de l'entreprise ne peut pas être vide.")
    private String codePostalEntreprise;

    @NotBlank(message = "Le téléphone de l'entreprise ne peut pas être vide.")
    @Pattern(regexp = "^[0-9]+$", message = "Le numéro de téléphone doit contenir uniquement des chiffres.")
    private String telephoneEntreprise;

    @NotBlank(message = "Le télécopieur de l'entreprise ne peut pas être vide.")
    @Pattern(regexp = "^[0-9]+$", message = "Le numéro de télécopieur doit contenir uniquement des chiffres.")
    private String telecopieurEntreprise;

    // Identification du stagiaire
    @NotBlank(message = "Le nom du stagiaire ne peut pas être vide.")
    private String nomStagiaire;

    @NotNull(message = "La date de début du stage ne peut pas être nulle.")
    private LocalDateTime dateDebutStage;

    // Stage 1 ou 2
    @NotNull(message = "Le numéro du stage ne peut pas être nul.")
    private Integer numeroStage; // Changed to Integer for @NotNull

    // Évaluation du milieu de stage

    // Question a) Les tâches confiées au stagiaire sont conformes aux tâches annoncées dans l’entente de stage.
    @NotNull(message = "L'évaluation QA ne peut pas être nulle.")
    private EvaluationMilieuStageReponses evalQA;

    // Question b) Des mesures d’accueil facilitent l’intégration du nouveau stagiaire.
    @NotNull(message = "L'évaluation QB ne peut pas être nulle.")
    private EvaluationMilieuStageReponses evalQB;

    // Question c) Le temps réel consacré à l’encadrement du stagiaire est suffisant.
    @NotNull(message = "L'évaluation QC ne peut pas être nulle.")
    private EvaluationMilieuStageReponses evalQC;

    // Nombre d'heures par semaine pour les 3 mois sous question C
    @NotNull(message = "Le nombre d'heures par semaine du premier mois ne peut pas être nul.")
    private Float nombreHeuresParSemainePremierMois;

    @NotNull(message = "Le nombre d'heures par semaine du deuxième mois ne peut pas être nul.")
    private Float nombreHeuresParSemaineDeuxiemeMois;

    @NotNull(message = "Le nombre d'heures par semaine du troisième mois ne peut pas être nul.")
    private Float nombreHeuresParSemaineTroisiemeMois;

    // Question d) L’environnement de travail respecte les normes d’hygiène et de sécurité au travail.
    @NotNull(message = "L'évaluation QD ne peut pas être nulle.")
    private EvaluationMilieuStageReponses evalQD;

    // Question e) Le climat de travail est agréable.
    @NotNull(message = "L'évaluation QE ne peut pas être nulle.")
    private EvaluationMilieuStageReponses evalQE;

    // Question f) Le milieu de stage est accessible par transport en commun.
    @NotNull(message = "L'évaluation QF ne peut pas être nulle.")
    private EvaluationMilieuStageReponses evalQF;

    // Question g) Le salaire offert est intéressant pour le stagiaire.
    @NotNull(message = "L'évaluation QG ne peut pas être nulle.")
    private EvaluationMilieuStageReponses evalQG;

    // Salaire horaire lié à la question G
    @NotNull(message = "Le salaire horaire ne peut pas être nul.")
    private Float salaireHoraire;

    // Question h) La communication avec le superviseur de stage facilite le déroulement du stage.
    @NotNull(message = "L'évaluation QH ne peut pas être nulle.")
    private EvaluationMilieuStageReponses evalQH;

    // Question i) L’équipement fourni est adéquat pour réaliser les tâches confiées.
    @NotNull(message = "L'évaluation QI ne peut pas être nulle.")
    private EvaluationMilieuStageReponses evalQI;

    // Question j) Le volume de travail est acceptable.
    @NotNull(message = "L'évaluation QJ ne peut pas être nulle.")
    private EvaluationMilieuStageReponses evalQJ;

    // Commentaires additionnels
    @NotBlank(message = "Les commentaires ne peuvent pas être vides.")
    private String commentaires;

    // Observations générales

    // Ce milieu est à privilégier pour le premier ou deuxième stage?
    @NotNull(message = "Le milieu à privilégier ne peut pas être nul.")
    private MilieuAPrivilegierReponses milieuAPrivilegier;

    // Ce milieu est ouvert à accueillir combien de stagiaires?
    @NotNull(message = "Le nombre de stagiaires à accueillir ne peut pas être nul.")
    private MilieuPretAAccueillirNombreStagiairesReponses milieuPretAAccueillirNombreStagiaires;

    // Ce milieu désire accueillir le même stagiaire pour un prochain stage?
    @NotNull(message = "La réponse pour accueillir le même stagiaire ne peut pas être nulle.")
    private OuiNonReponses milieuDesireAccueillirMemeStagiaire;

    // Ce milieu offre des quarts de travail variables?
    @NotNull(message = "La réponse pour les quarts de travail variables ne peut pas être nulle.")
    private OuiNonReponses millieuOffreQuartsTravailVariables;

    // Si oui, nommer les quarts de travail (jusqu'à 3)
    private LocalDateTime quartTravailDebut1;

    private LocalDateTime quartTravailFin1;

    private LocalDateTime quartTravailDebut2;

    private LocalDateTime quartTravailFin2;

    private LocalDateTime quartTravailDebut3;

    private LocalDateTime quartTravailFin3;
}