package com.projet.mycose.service.dto;

import com.projet.mycose.modele.auth.Role;
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
    private String numeroDeTelephone;
    private Role role;

    public UtilisateurDTO(Long id, String prenom, String nom, String courriel, String numeroDeTelephone, Role role) {
        this.id = id;
        this.prenom = prenom;
        this.nom = nom;
        this.courriel = courriel;
        this.numeroDeTelephone = numeroDeTelephone;
        this.role = role;
    }
}
