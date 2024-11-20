package com.projet.mycose.dto;

import com.projet.mycose.modele.FicheEvaluationMilieuStage.EvaluationMilieuStageReponses;
import com.projet.mycose.modele.FicheEvaluationMilieuStage.MilieuAPrivilegierReponses;
import com.projet.mycose.modele.FicheEvaluationMilieuStage.MilieuPretAAccueillirNombreStagiairesReponses;
import com.projet.mycose.modele.FicheEvaluationMilieuStage.OuiNonReponses;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

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
    @Pattern(regexp = "^[0-9]+$", message = "Phone number should be digits.")
    private String telephoneEntreprise;

    @NotBlank(message = "Le télécopieur de l'entreprise ne peut pas être vide.")
    @Pattern(regexp = "^[0-9]+$", message = "Fax number should be digits.")
    private String telecopieurEntreprise;

    // Identification du stagiaire
    @NotBlank(message = "Le nom du stagiaire ne peut pas être vide.")
    private String nomStagiaire;

    @NotNull(message = "La date de début du stage ne peut pas être nulle.")
    private LocalDateTime dateDebutStage;

    @NotNull(message = "Le numéro du stage ne peut pas être nul.")
    private Integer numeroStage; // Changed to Integer for @NotNull

    // Évaluation du milieu de stage
    @NotNull(message = "L'évaluation QA ne peut pas être nulle.")
    private EvaluationMilieuStageReponses evaluationQA;

    @NotNull(message = "L'évaluation QB ne peut pas être nulle.")
    private EvaluationMilieuStageReponses evaluationQB;

    @NotNull(message = "L'évaluation QC ne peut pas être nulle.")
    private EvaluationMilieuStageReponses evaluationQC;

    // Nombre d'heures par semaine pour les 3 mois sous question C
    @NotNull(message = "Le nombre d'heures par semaine du premier mois ne peut pas être nul.")
    private Float nombreHeuresParSemainePremierMois;

    @NotNull(message = "Le nombre d'heures par semaine du deuxième mois ne peut pas être nul.")
    private Float nombreHeuresParSemaineDeuxiemeMois;

    @NotNull(message = "Le nombre d'heures par semaine du troisième mois ne peut pas être nul.")
    private Float nombreHeuresParSemaineTroisiemeMois;

    @NotNull(message = "L'évaluation QD ne peut pas être nulle.")
    private EvaluationMilieuStageReponses evaluationQD;

    @NotNull(message = "L'évaluation QE ne peut pas être nulle.")
    private EvaluationMilieuStageReponses evaluationQE;

    @NotNull(message = "L'évaluation QF ne peut pas être nulle.")
    private EvaluationMilieuStageReponses evaluationQF;

    @NotNull(message = "L'évaluation QG ne peut pas être nulle.")
    private EvaluationMilieuStageReponses evaluationQG;

    @NotNull(message = "Le salaire horaire ne peut pas être nul.")
    private Float salaireHoraire;

    @NotNull(message = "L'évaluation QH ne peut pas être nulle.")
    private EvaluationMilieuStageReponses evaluationQH;

    @NotNull(message = "L'évaluation QI ne peut pas être nulle.")
    private EvaluationMilieuStageReponses evaluationQI;

    @NotNull(message = "L'évaluation QJ ne peut pas être nulle.")
    private EvaluationMilieuStageReponses evaluationQJ;

    @NotBlank(message = "Les commentaires ne peuvent pas être vides.")
    private String commentaires;

    // Observations générales
    @NotNull(message = "Le milieu à privilégier ne peut pas être nul.")
    private MilieuAPrivilegierReponses milieuAPrivilegier;

    @NotNull(message = "Le nombre de stagiaires à accueillir ne peut pas être nul.")
    private MilieuPretAAccueillirNombreStagiairesReponses milieuPretAAccueillirNombreStagiaires;

    @NotNull(message = "La réponse pour accueillir le même stagiaire ne peut pas être nulle.")
    private OuiNonReponses milieuDesireAccueillirMemeStagiaire;

    @NotNull(message = "La réponse pour les quarts de travail variables ne peut pas être nulle.")
    private OuiNonReponses millieuOffreQuartsTravailVariables;

    // Quarts de travail (jusqu'à 3)
    private LocalDateTime quartTravailDebut1;

    private LocalDateTime quartTravailFin1;

    private LocalDateTime quartTravailDebut2;

    private LocalDateTime quartTravailFin2;

    private LocalDateTime quartTravailDebut3;

    private LocalDateTime quartTravailFin3;
}
