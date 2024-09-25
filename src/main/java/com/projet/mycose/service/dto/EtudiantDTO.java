package com.projet.mycose.service.dto;

import com.projet.mycose.modele.Etudiant;
import com.projet.mycose.modele.auth.Credentials;
import com.projet.mycose.modele.auth.Role;
import lombok.*;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class EtudiantDTO extends UtilisateurDTO {
    @Builder
    public EtudiantDTO(Long id, String prenom, String nom, String courriel, String numeroDeTelephone, Role role) {
        super(id, prenom, nom, courriel, numeroDeTelephone, role);
    }

    public static EtudiantDTO toDTO(Etudiant etudiant) {
        return EtudiantDTO.builder()
                .id(etudiant.getId())
                .prenom(etudiant.getPrenom())
                .nom(etudiant.getNom())
                .courriel(etudiant.getCourriel())
                .numeroDeTelephone(etudiant.getNumeroDeTelephone())
                .role(etudiant.getRole())
                .build();

    }

    public static Etudiant toEntity(EtudiantDTO etudiantDto) {
        return Etudiant.builder()
                .id(etudiantDto.getId())
                .prenom(etudiantDto.getPrenom())
                .nom(etudiantDto.getNom())
                .courriel(etudiantDto.getCourriel())
                .numeroDeTelephone(etudiantDto.getNumeroDeTelephone())
                .build();
    }

    public static EtudiantDTO empty() {
        return new EtudiantDTO();
    }
}
