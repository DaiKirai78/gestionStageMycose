package com.projet.mycose.dto;

import com.projet.mycose.modele.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OffreStageDTO {
    private long id;


    private String title;

    private String entrepriseName;

    private Long createur_id;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private OffreStage.Status status;
    private OffreStage.Visibility visibility;
    private Programme programme;
    private SessionEcole session;
    private int annee;

    public OffreStageDTO(long id) {
        this.id = id;
    }

    public static OffreStageDTO toOffreStageInstaceDTO(OffreStage offreStage) {
        OffreStageDTO offreStageDTO = null;
        if(offreStage instanceof FormulaireOffreStage) {
            offreStageDTO = FormulaireOffreStageDTO.toDTO((FormulaireOffreStage) offreStage);
        }
        else if(offreStage instanceof FichierOffreStage) {
            offreStageDTO = FichierOffreStageDTO.toDTO((FichierOffreStage) offreStage);
        }
        return offreStageDTO;
    }

    public OffreStageDTO(Long id, String title, String entrepriseName, Long createurId, LocalDateTime createdAt, LocalDateTime updatedAt, OffreStage.Status status, OffreStage.Visibility visibility, Programme programme, SessionEcole session, int annee) {
        this.id = id;
        this.title = title;
        this.entrepriseName = entrepriseName;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.createur_id = createurId;
        this.status = status;
        this.visibility = visibility;
        this.programme = programme;
        this.session = session;
        this.annee = annee;
    }

    public static OffreStageDTO toOffreStageInstaceDTOAll(OffreStage offreStage) {
        OffreStageDTO offreStageDTO = null;
        if(offreStage instanceof FormulaireOffreStage) {
            offreStageDTO = FormulaireOffreStageDTO.toDTO((FormulaireOffreStage) offreStage);
        }
        else if(offreStage instanceof FichierOffreStage) {
            offreStageDTO = FichierOffreStageDTO.toDTOAll((FichierOffreStage) offreStage);
        }
        return offreStageDTO;
    }

}
