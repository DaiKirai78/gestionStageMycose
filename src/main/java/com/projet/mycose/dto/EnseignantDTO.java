package com.projet.mycose.dto;

import com.projet.mycose.modele.Enseignant;
import com.projet.mycose.modele.auth.Role;
import lombok.*;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class EnseignantDTO extends UtilisateurDTO{
    @Builder
    public EnseignantDTO(Long id, String prenom, String nom, String courriel, String numeroDeTelephone, Role role) {
        super(id, prenom, nom, courriel, numeroDeTelephone, role);
    }

    public static EnseignantDTO toDTO(Enseignant enseignant) {
        return EnseignantDTO.builder()
                .id(enseignant.getId())
                .prenom(enseignant.getPrenom())
                .nom(enseignant.getNom())
                .courriel(enseignant.getCourriel())
                .numeroDeTelephone(enseignant.getNumeroDeTelephone())
                .role(enseignant.getRole())
                .build();

    }

    public static EnseignantDTO empty() {
        return new EnseignantDTO();
    }
}
