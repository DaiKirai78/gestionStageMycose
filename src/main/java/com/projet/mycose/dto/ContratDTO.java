package com.projet.mycose.dto;

import com.projet.mycose.modele.Contrat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.util.Base64;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ContratDTO {
    private Long id;

    @NotBlank(message = "Manager's signature is required.")
    @Pattern(regexp = "^[A-Za-z0-9+/=]+$",
            message = "Invalid file data. Should be Base64 encoded.")
    private String signatureGestionnaire;

    @NotBlank(message = "Student's signature is required.")
    @Pattern(regexp = "^[A-Za-z0-9+/=]+$",
            message = "Invalid file data. Should be Base64 encoded.")
    private String signatureEtudiant;

    @NotBlank(message = "Employer's signature is required.")
    @Pattern(regexp = "^[A-Za-z0-9+/=]+$",
            message = "Invalid file data. Should be Base64 encoded.")
    private String signatureEmployeur;

    @NotBlank(message = "Student is required.")
    @Pattern(regexp = "^[0-9]*$",
            message = "Must be a number.")
    private Long etudiantId;

    @NotBlank(message = "Employer is required.")
    @Pattern(regexp = "^[0-9]*$",
            message = "Must be a number.")
    private Long employeurId;

    @NotBlank(message = "Manager is required.")
    @Pattern(regexp = "^[0-9]*$",
            message = "Must be a number.")
    private Long gestionnaireStageId;

    public static ContratDTO toDTO(Contrat contrat) {
        return new ContratDTO(
                contrat.getId(),
                contrat.getSignatureGestionnaire() != null ? Base64.getEncoder().encodeToString(contrat.getSignatureGestionnaire()) : null,
                contrat.getSignatureEtudiant() != null ? Base64.getEncoder().encodeToString(contrat.getSignatureEtudiant()) : null,
                contrat.getSignatureEmployeur() != null ? Base64.getEncoder().encodeToString(contrat.getSignatureEmployeur()) : null,
                contrat.getEtudiant() != null ? contrat.getEtudiant().getId() : null,
                contrat.getEmployeur() != null ? contrat.getEmployeur().getId() : null,
                contrat.getGestionnaireStage() != null ? contrat.getGestionnaireStage().getId() : null
        );
    }
}
