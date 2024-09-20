package com.projet.mycose.service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FichierOffreStageDTO {

    private Long id;
    private String filename;
    private String fileData; // Base64-encoded version of the byte array for easier transfer in JSON

}
