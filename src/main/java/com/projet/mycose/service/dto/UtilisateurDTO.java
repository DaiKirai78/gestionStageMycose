package com.projet.mycose.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public abstract class UtilisateurDTO {
    private Long id;
    private String prenom;
    private String nom;
    private String courriel;
    private String motDePasse;
    private String numeroDeTelephone;

    public UtilisateurDTO(Long id, String prenom, String nom, String courriel, String motDePasse, String numeroDeTelephone) {
        this.id = id;
        this.prenom = prenom;
        this.nom = nom;
        this.courriel = courriel;
        this.motDePasse = motDePasse;
        this.numeroDeTelephone = numeroDeTelephone;
    }
}
