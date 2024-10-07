package com.projet.mycose.service.dto;

import com.projet.mycose.modele.FichierOffreStage;
import com.projet.mycose.modele.FormulaireOffreStage;
import com.projet.mycose.modele.OffreStage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OffreStageDTO {
    private long id;


//    @NotBlank(message = "Title is required.")
//    @Size(max = 100, message = "Title cannot exceed 100 characters.")
    private String title;


//    @NotBlank(message = "Enterprise name is required.")
    private String entrepriseName;

    private Long createur_id;
    public OffreStageDTO(long id) {
        this.id = id;
    }
}
