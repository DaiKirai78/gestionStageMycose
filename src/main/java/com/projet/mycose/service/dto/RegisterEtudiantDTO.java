package com.projet.mycose.service.dto;

import lombok.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class RegisterEtudiantDTO {
    @NotBlank
    private String prenom;

    @NotBlank
    private String nom;

    @NotBlank
    @Pattern(regexp = "\\d{10}")
    private String numeroDeTelephone;

    @NotBlank
    @Email
    private String courriel;

    @NotBlank
    private String motDePasse;
}
