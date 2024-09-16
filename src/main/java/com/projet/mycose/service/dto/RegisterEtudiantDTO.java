package com.projet.mycose.service.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class RegisterEtudiantDTO {
    private String prenom;
    private String nom;
    private String numeroTelephone;
    private String courriel;
    private String motDePasse;
}
