package com.projet.mycose.service.dto;

import com.projet.mycose.modele.FichierOffreStage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;


@NoArgsConstructor
@Getter
@Setter
public class FichierOffreStageDTO extends OffreStageDTO{

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
    public FichierOffreStageDTO(long id, String filename, String fileData, String title, String entrepriseName, Long createur_id) {
        super(id);
        this.filename = filename;
        this.fileData = fileData;
        this.setTitle(title);
        this.setEntrepriseName(entrepriseName);
        this.setCreateur_id(createur_id);
    }

    public static FichierOffreStageDTO toDTO(FichierOffreStage fichierOffreStage) {
        return FichierOffreStageDTO.builder()
                .id(fichierOffreStage.getId())
                .filename(fichierOffreStage.getFilename())
                .fileData(Base64.getEncoder().encodeToString(fichierOffreStage.getData()))
                .title(fichierOffreStage.getTitle())
                .entrepriseName(fichierOffreStage.getEntrepriseName())
                .createur_id(fichierOffreStage.getCreateur().getId())
                .build();
    }

    public FichierOffreStageDTO(UploadFicherOffreStageDTO uploadFicherOffreStageDTO, Long createur_id) throws IOException {
        this.filename = uploadFicherOffreStageDTO.getFile().getOriginalFilename();
        this.fileData = Base64.getEncoder().encodeToString(uploadFicherOffreStageDTO.getFile().getBytes());
        this.setTitle(uploadFicherOffreStageDTO.getTitle());
        this.setEntrepriseName(uploadFicherOffreStageDTO.getEntrepriseName());
        this.setCreateur_id(createur_id);
    }


    //TODO: Ajouter les champs title et entrepriseName au front-end pour qu'on puisse les envoyer au DTO avec validation

}
