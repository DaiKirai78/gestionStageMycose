package com.projet.mycose.service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FormulaireOffreStageDTO {

    private Long id;
    private String entrepriseName;
    private String employerName;
    private String email;
    private String website;
    private String title;
    private String location;
    private String salary;
    private String description;
}