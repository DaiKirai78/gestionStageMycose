package com.projet.mycose.dto;

import java.util.List;

public class OffresStagesDTO {
    private List<FichierOffreStageDTO> listeFichiersOffresStages;
    private List<FormulaireOffreStageDTO> listeFormulairesOffresStages;

    public OffresStagesDTO(List<FichierOffreStageDTO> listeFichiersOffresStages, List<FormulaireOffreStageDTO> listeFormulairesOffresStages) {
        this.listeFichiersOffresStages = listeFichiersOffresStages;
        this.listeFormulairesOffresStages = listeFormulairesOffresStages;
    }

}
