package com.projet.mycose.dto;

import com.projet.mycose.modele.Employeur;
import com.projet.mycose.modele.Etudiant;
import com.projet.mycose.modele.auth.Role;
import lombok.*;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class EmployeurDTO extends UtilisateurDTO {
    private String entrepriseName;
    @Builder
    public EmployeurDTO(Long id, String prenom, String nom, String courriel, String numeroDeTelephone, String entrepriseName, Role role) {
        super(id, prenom, nom, courriel, numeroDeTelephone, role);
        this.entrepriseName = entrepriseName;
    }

    public static EmployeurDTO toDTO(Employeur employeur) {
        return EmployeurDTO.builder()
                .id(employeur.getId())
                .prenom(employeur.getPrenom())
                .nom(employeur.getNom())
                .courriel(employeur.getCourriel())
                .numeroDeTelephone(employeur.getNumeroDeTelephone())
                .entrepriseName(employeur.getEntrepriseName())
                .role(employeur.getRole())
                .build();

    }

    public static EmployeurDTO empty() {
        return new EmployeurDTO();
    }
}
