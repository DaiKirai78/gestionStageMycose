package com.projet.mycose.service.dto;

import lombok.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class RegisterDTO {
    @NotBlank
    @Pattern(regexp = "[a-zA-ZéÉàÀ\\-']+")
    private String prenom;

    @NotBlank
    @Pattern(regexp = "[a-zA-ZéÉàÀ\\-']+")
    private String nom;

    @NotBlank
    @Pattern(regexp = "[0-9]{3}-? ?[0-9]{3}-? ?[0-9]{4}")
    private String numeroDeTelephone;

    @NotBlank
    @Email
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")
    private String courriel;

    @NotBlank
    @Pattern(regexp = "^\\$2[abxy]?\\$\\d{2}\\$[./A-Za-z0-9]{53}$")
    private String motDePasse;
}
