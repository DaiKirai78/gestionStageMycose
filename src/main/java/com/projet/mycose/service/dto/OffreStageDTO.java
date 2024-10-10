package com.projet.mycose.service.dto;

import com.projet.mycose.modele.FichierOffreStage;
import com.projet.mycose.modele.FormulaireOffreStage;
import com.projet.mycose.modele.OffreStage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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


//    @NotBlank(message = "Title is required.")
//    @Size(max = 100, message = "Title cannot exceed 100 characters.")
    private String title;


//    @NotBlank(message = "Enterprise name is required.")
    private String entrepriseName;

    private Long createur_id;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private OffreStage.Status status;
    public OffreStageDTO(long id) {
        this.id = id;
    }

    public OffreStageDTO(Long id, String title, String entrepriseName, Long createurId, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.title = title;
        this.entrepriseName = entrepriseName;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.createur_id = createurId;
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

    public static OffreStageDTO toOffreStageInstaceDTOAll(OffreStage offreStage) {
        OffreStageDTO offreStageDTO = null;
        if(offreStage instanceof FormulaireOffreStage) {
            offreStageDTO = FormulaireOffreStageDTO.toDTOAll((FormulaireOffreStage) offreStage);
        }
        else if(offreStage instanceof FichierOffreStage) {
            offreStageDTO = FichierOffreStageDTO.toDTOAll((FichierOffreStage) offreStage);
        }
        return offreStageDTO;
    }

}
