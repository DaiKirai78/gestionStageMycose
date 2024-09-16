package com.projet.mycose.service.dto;

import com.projet.mycose.modele.Etudiant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class EtudiantDTO extends UtilisateurDTO {
    public EtudiantDTO(Long id, String prenom, String nom, String courriel, String motDePasse, String numeroDeTelephone) {
        super(id, prenom, nom, courriel, motDePasse, numeroDeTelephone);
    }

    public static EtudiantDTO toDTO(Etudiant etudiant) {
        EtudiantDTO etudiantDto = new EtudiantDTO();
        etudiantDto.setId(etudiant.getId());
        etudiantDto.setPrenom(etudiant.getPrenom());
        etudiantDto.setNom(etudiant.getNom());
        etudiantDto.setCourriel(etudiant.getCourriel());
        etudiantDto.setMotDePasse(etudiant.getMotDePasse());
        etudiantDto.setNumeroDeTelephone(etudiant.getNumeroDeTelephone());
        return etudiantDto;
    }

    public static Etudiant toEntity(EtudiantDTO etudiantDto) {
        Etudiant etudiant = new Etudiant();
        etudiant.setId(etudiantDto.getId());
        etudiant.setPrenom(etudiantDto.getPrenom());
        etudiant.setNom(etudiantDto.getNom());
        etudiant.setCourriel(etudiantDto.getCourriel());
        etudiant.setMotDePasse(etudiantDto.getMotDePasse());
        etudiant.setNumeroDeTelephone(etudiantDto.getNumeroDeTelephone());
        return etudiant;
    }

    @Override
    public String toString() {
        return "EtudiantDTO(" +
                getId() + ", " +
                getPrenom() + ", " +
                getNom() + ", " +
                getCourriel() + ", " +
                getMotDePasse() + ", " +
                getNumeroDeTelephone() +
                ")";
    }
}
