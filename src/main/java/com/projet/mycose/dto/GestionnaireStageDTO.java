package com.projet.mycose.dto;

import com.projet.mycose.modele.GestionnaireStage;
import com.projet.mycose.modele.auth.Role;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class GestionnaireStageDTO extends UtilisateurDTO {

    public GestionnaireStageDTO(Long id, String prenom, String nom, String numeroDeTelephone, String courriel, Role role) {
        super(id, prenom, nom, courriel, numeroDeTelephone, role);
    }

    public static GestionnaireStageDTO empty() {
        return new GestionnaireStageDTO();
    }

    public static GestionnaireStageDTO toDTO(GestionnaireStage gestionnaireStage) {
        return new GestionnaireStageDTO(gestionnaireStage.getId(), gestionnaireStage.getPrenom(), gestionnaireStage.getNom(), gestionnaireStage.getNumeroDeTelephone(), gestionnaireStage.getCourriel(), gestionnaireStage.getRole());
    }
}
