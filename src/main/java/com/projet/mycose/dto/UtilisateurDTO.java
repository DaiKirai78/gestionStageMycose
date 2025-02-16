package com.projet.mycose.dto;

import com.projet.mycose.modele.*;
import com.projet.mycose.modele.auth.Role;
import jakarta.validation.constraints.Email;
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
    @Email
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

    public static UtilisateurDTO toDTO(Utilisateur utilisateur) {
        return switch (utilisateur.getCredentials().getRole()) {
            case ETUDIANT -> EtudiantDTO.toDTO((Etudiant) utilisateur);
            case ENSEIGNANT -> EnseignantDTO.toDTO((Enseignant) utilisateur);
            case EMPLOYEUR -> EmployeurDTO.toDTO((Employeur) utilisateur);
            case GESTIONNAIRE_STAGE -> GestionnaireStageDTO.toDTO((GestionnaireStage) utilisateur);
        };
    }
}
