package com.projet.mycose.service.dto;

import lombok.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class RegisterDTO {
    @NotBlank(message = "Le prénom ne doit pas être vide.")
    @Pattern(regexp = "[a-zA-ZéÉàÀ\\-']+", message = "Le prénom ne doit pas contenir de chiffre ou de symbole")
    private String prenom;

    @NotBlank(message = "Le nom ne doit pas être vide.")
    @Pattern(regexp = "[a-zA-ZéÉàÀ\\-']+", message = "Le nom ne doit pas contenir de chiffre ou de symbole")
    private String nom;

    @NotBlank(message = "Le numéro de téléphone ne doit pas être vide.")
    @Pattern(regexp = "[0-9]{3}-? ?[0-9]{3}-? ?[0-9]{4}", message = "Le numéro de téléphone doit être dans le format suivant : \"000-000-0000\".")
    private String numeroDeTelephone;

    @NotBlank(message = "Le courriel ne doit pas être vide.")
    @Email
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "Le courriel peut contenir des lettres, chiffres et certains symboles et doit être dans le format suivant : \"example@gmail.com\"")
    private String courriel;

    @NotBlank(message = "Le mot de passe ne doit pas être vide.")
    @Pattern(regexp = "[a-zA-Z0-9$&+,:;=?@#|'<>.^*()%!-]{8,}", message = "Le mot de passe doit être une combinaison de 8 lettres, chiffres et symboles")
    private String motDePasse;
}
