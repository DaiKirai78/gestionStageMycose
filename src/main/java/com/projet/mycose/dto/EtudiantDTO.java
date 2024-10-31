package com.projet.mycose.dto;

import com.projet.mycose.modele.*;
import com.projet.mycose.modele.auth.Role;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class EtudiantDTO extends UtilisateurDTO {
    private Programme programme;
    private Etudiant.ContractStatus contractStatus;
    @Builder
    public EtudiantDTO(Long id, String prenom, String nom, String courriel, String numeroDeTelephone, Role role, Programme programme, Etudiant.ContractStatus contractStatus) {
        super(id, prenom, nom, courriel, numeroDeTelephone, role);
        this.programme = programme;
        this.contractStatus = contractStatus;
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
                .contractStatus(etudiant.getContractStatus())
                .build();

    }

    public static Etudiant toEntity(EtudiantDTO etudiantDTO) {
        return Etudiant.builder()
                .id(etudiantDTO.getId())
                .prenom(etudiantDTO.getPrenom())
                .nom(etudiantDTO.getNom())
                .courriel(etudiantDTO.getCourriel())
                .numeroDeTelephone(etudiantDTO.getNumeroDeTelephone())
                .programme(etudiantDTO.getProgramme())
                .contractStatus(etudiantDTO.getContractStatus())
                .build();

    }

    public static EtudiantDTO empty() {
        return new EtudiantDTO();
    }
}
