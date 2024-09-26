package com.projet.mycose.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class CourrielTelephoneDTO {
    @NotBlank(message = "Le courriel ne doit pas être vide.")
    @Email
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "Le courriel peut contenir des lettres, chiffres et certains symboles et doit être dans le format suivant : \"example@gmail.com\"")
    private String courriel;

    @NotBlank(message = "Le numéro de téléphone ne doit pas être vide.")
    @Pattern(regexp = "[0-9]{3}-? ?[0-9]{3}-? ?[0-9]{4}", message = "Le numéro de téléphone doit être dans le format suivant : \"000-000-0000\".")
    private String telephone;
}
