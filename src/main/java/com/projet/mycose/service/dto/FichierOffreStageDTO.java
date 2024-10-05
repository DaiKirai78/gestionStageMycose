package com.projet.mycose.service.dto;

import com.projet.mycose.modele.FichierOffreStage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@NoArgsConstructor
@Getter
@Setter
public class FichierOffreStageDTO extends OffreStageDTO{

    //private Long id;

    @NotBlank(message = "Filename is required.")
    @Size(max = 255, message = "Filename cannot exceed 255 characters.")
    @Pattern(regexp = "^[\\p{L}0-9\\s,_.-]+\\.pdf$",
            message = "Invalid filename format. Only PDF files are allowed.")
    private String filename;

    @NotBlank(message = "File data is required.")
    @Pattern(regexp = "^[A-Za-z0-9+/=]+$",
            message = "Invalid file data. Should be Base64 encoded.")
    private String fileData;

    @Builder
    public FichierOffreStageDTO(long id, String filename, String fileData) {
        super(id);
        this.filename = filename;
        this.fileData = fileData;
    }

    public static FichierOffreStageDTO toDTO(FichierOffreStage fichierOffreStage) {
        return FichierOffreStageDTO.builder()
                .id(fichierOffreStage.getId())
                .filename(fichierOffreStage.getFilename())
                .fileData(fichierOffreStage.getFilename())
                .build();
    }

}
