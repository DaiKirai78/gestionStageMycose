package com.projet.mycose.dto;

import com.projet.mycose.modele.FicheEvaluationStagiaire;
import com.projet.mycose.modele.Programme;
import lombok.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class FicheEvaluationStagiaireDTO {
    private Long id;

    @NotBlank(message = "Student name is required.")
    private String nomEtudiant;

    @NotBlank(message = "Program is required.")
    private Programme programmeEtude;

    @NotBlank(message = "Company name is required.")
    private String nomEntreprise;

    @NotBlank(message = "Supervisor name is required.")
    private String nomSuperviseur;

    @NotBlank(message = "Employer's signature is required.")
    @Pattern(regexp = "^[A-Za-z0-9+/=]+$",
            message = "Invalid file data. Should be Base64 encoded.")
    private String signatureSuperviseur;

    @NotBlank(message = "Supervisor position is required.")
    private String fonctionSuperviseur;

    @NotBlank(message = "Phone number is required.")
    @Pattern(regexp = "^[0-9]+$", message = "Phone number should be digits.")
    private String numeroTelephone;

    @NotBlank(message = "Response for prodQA is required.")
    private FicheEvaluationStagiaire.ComportementReponses prodQA;

    @NotBlank(message = "Response for prodQB is required.")
    private FicheEvaluationStagiaire.ComportementReponses prodQB;

    @NotBlank(message = "Response for prodQC is required.")
    private FicheEvaluationStagiaire.ComportementReponses prodQC;

    @NotBlank(message = "Response for prodQD is required.")
    private FicheEvaluationStagiaire.ComportementReponses prodQD;

    @NotBlank(message = "Response for prodQE is required.")
    private FicheEvaluationStagiaire.ComportementReponses prodQE;
    private String prodCommentaires;

    @NotBlank(message = "Response for qualTravailQA is required.")
    private FicheEvaluationStagiaire.ComportementReponses qualTravailQA;

    @NotBlank(message = "Response for qualTravailQB is required.")
    private FicheEvaluationStagiaire.ComportementReponses qualTravailQB;

    @NotBlank(message = "Response for qualTravailQC is required.")
    private FicheEvaluationStagiaire.ComportementReponses qualTravailQC;

    @NotBlank(message = "Response for qualTravailQD is required.")
    private FicheEvaluationStagiaire.ComportementReponses qualTravailQD;

    @NotBlank(message = "Response for qualTravailQE is required.")
    private FicheEvaluationStagiaire.ComportementReponses qualTravailQE;
    private String qualTravailCommentaires;

    @NotBlank(message = "Response for qualRelQA is required.")
    private FicheEvaluationStagiaire.ComportementReponses qualRelQA;

    @NotBlank(message = "Response for qualRelQB is required.")
    private FicheEvaluationStagiaire.ComportementReponses qualRelQB;

    @NotBlank(message = "Response for qualRelQC is required.")
    private FicheEvaluationStagiaire.ComportementReponses qualRelQC;

    @NotBlank(message = "Response for qualRelQD is required.")
    private FicheEvaluationStagiaire.ComportementReponses qualRelQD;

    @NotBlank(message = "Response for qualRelQE is required.")
    private FicheEvaluationStagiaire.ComportementReponses qualRelQE;
    @NotBlank(message = "Response for qualRelQF is required.")
    private FicheEvaluationStagiaire.ComportementReponses qualRelQF;
    private String qualRelCommentaires;

    @NotBlank(message = "Response for habPersQA is required.")
    private FicheEvaluationStagiaire.ComportementReponses habPersQA;

    @NotBlank(message = "Response for habPersQB is required.")
    private FicheEvaluationStagiaire.ComportementReponses habPersQB;

    @NotBlank(message = "Response for habPersQC is required.")
    private FicheEvaluationStagiaire.ComportementReponses habPersQC;

    @NotBlank(message = "Response for habPersQD is required.")
    private FicheEvaluationStagiaire.ComportementReponses habPersQD;

    @NotBlank(message = "Response for habPersQE is required.")
    private FicheEvaluationStagiaire.ComportementReponses habPersQE;

    @NotBlank(message = "Response for habPersQF is required.")
    private FicheEvaluationStagiaire.ComportementReponses habPersQF;
    private String habPersCommentaires;

    @NotBlank(message = "Global appreciation is required.")
    private FicheEvaluationStagiaire.AppreciationGlobaleReponses appreciationGlobale;

    @NotBlank(message = "Response for precision appreciation is required.")
    private String precisionAppreciationReponse;

    @NotBlank(message = "Response for discussed with trainee is required.")
    private Boolean discuteeStagiaireReponse;

    @NotBlank(message = "Hours granted to trainee is required.")
    private String heuresAccordeStagiaireReponse;

    @NotBlank(message = "Response for hire intern another time is required.")
    private FicheEvaluationStagiaire.AcceuillirProchainStageReponses aimeraitAccueillirProchainStage;

    @NotBlank(message = "Response for adequate training is required.")
    private String formationSuffisanteReponse;
}
