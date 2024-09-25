package com.projet.mycose.service.dto;

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
    @NotBlank
    @Email
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")
    private String courriel;

    @NotBlank
    @Pattern(regexp = "^\\$2[abxy]?\\$\\d{2}\\$[./A-Za-z0-9]{53}$")
    private String motDePasse;
}
