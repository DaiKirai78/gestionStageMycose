package com.projet.mycose.dto;

import com.projet.mycose.modele.Contrat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "pdf")
public class ContratDTO {
    private Long id;

    @NotBlank(message = "PDF is required.")
    @Pattern(regexp = "^[A-Za-z0-9+/=]+$",
            message = "Invalid file data. Should be Base64 encoded.")
    private String pdf;

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

    private Long etudiantId;

    private Long employeurId;

    private Contrat.Status status;
}
