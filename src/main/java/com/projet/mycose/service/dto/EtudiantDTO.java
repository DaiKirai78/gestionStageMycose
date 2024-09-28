package com.projet.mycose.service.dto;

import com.projet.mycose.modele.Etudiant;
import com.projet.mycose.modele.auth.Role;
import lombok.*;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class EtudiantDTO extends UtilisateurDTO {
    private String programme;
    @Builder
    public EtudiantDTO(Long id, String prenom, String nom, String courriel, String numeroDeTelephone, Role role, String programme) {
        super(id, prenom, nom, courriel, numeroDeTelephone, role);
        this.programme = programme;
    }

    public static EtudiantDTO toDTO(Etudiant etudiant) {
        return EtudiantDTO.builder()
                .id(etudiant.getId())
                .prenom(etudiant.getPrenom())
                .nom(etudiant.getNom())
                .courriel(etudiant.getCourriel())
                .numeroDeTelephone(etudiant.getNumeroDeTelephone())
                .role(etudiant.getRole())
                .programme(etudiant.getProgramme())
                .build();

    }

    public static EtudiantDTO empty() {
        return new EtudiantDTO();
    }
}
