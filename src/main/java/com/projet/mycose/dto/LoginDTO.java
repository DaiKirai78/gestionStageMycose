package com.projet.mycose.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Data
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LoginDTO {
    @NotBlank(message = "Le courriel ne doit pas être vide.")
    @Email
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "Le courriel peut contenir des lettres, chiffres et certains symboles et doit être dans le format suivant : \"example@gmail.com\"")
    private String courriel;

    @NotBlank(message = "Le mot de passe ne doit pas être vide.")
    @Pattern(regexp = "[a-zA-Z0-9$&+,:;=?@#|'<>.^*()%!-]{8,}", message = "Le mot de passe doit être une combinaison de 8 lettres, chiffres et symboles")
    private String motDePasse;
}
