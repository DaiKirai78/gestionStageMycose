package com.projet.mycose.dto;

import com.projet.mycose.modele.FichierOffreStage;
import com.projet.mycose.modele.OffreStage;
import com.projet.mycose.modele.Programme;
import com.projet.mycose.modele.SessionEcole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.Year;
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
    public FichierOffreStageDTO(long id, String filename, String fileData, String title, String entrepriseName, LocalDateTime createdAt, LocalDateTime updateAt, Long createur_id, OffreStage.Status status, OffreStage.Visibility visibility, Programme programme, SessionEcole session, int annee) {
        super(id);
        this.filename = filename;
        this.fileData = fileData;
        this.setTitle(title);
        this.setEntrepriseName(entrepriseName);
        this.setCreatedAt(createdAt);
        this.setUpdatedAt(updateAt);
        this.setCreateur_id(createur_id);
        this.setStatus(status);
        this.setVisibility(visibility);
        this.setProgramme(programme);
        this.setSession(session);
        this.setAnnee(annee);
    }


    public static FichierOffreStageDTO toDTO(FichierOffreStage fichierOffreStage) {
        return FichierOffreStageDTO.builder()
                .id(fichierOffreStage.getId())
                .filename(fichierOffreStage.getFilename())
                .fileData(Base64.getEncoder().encodeToString(fichierOffreStage.getData()))
                .title(fichierOffreStage.getTitle())
                .entrepriseName(fichierOffreStage.getEntrepriseName())
                .createur_id(fichierOffreStage.getCreateur().getId())
                .createdAt(fichierOffreStage.getCreatedAt())
                .session(fichierOffreStage.getSession())
                .annee(fichierOffreStage.getAnnee().getValue())
                .build();
    }

    public static FichierOffreStageDTO toDTOAll(FichierOffreStage fichierOffreStage) {
        return new FichierOffreStageDTO(
                fichierOffreStage.getId(),
                fichierOffreStage.getFilename(),
                Base64.getEncoder().encodeToString(fichierOffreStage.getData()),
                fichierOffreStage.getTitle(),
                fichierOffreStage.getEntrepriseName(),
                fichierOffreStage.getCreatedAt(),
                fichierOffreStage.getUpdatedAt(),
                fichierOffreStage.getCreateur().getId(),
                fichierOffreStage.getStatus(),
                fichierOffreStage.getVisibility(),
                fichierOffreStage.getProgramme(),
                fichierOffreStage.getSession(),
                fichierOffreStage.getAnnee().getValue()
        );
    }

    public FichierOffreStageDTO(UploadFicherOffreStageDTO uploadFicherOffreStageDTO, Long createur_id) throws IOException {
        this.filename = uploadFicherOffreStageDTO.getFile().getOriginalFilename();
        this.fileData = Base64.getEncoder().encodeToString(uploadFicherOffreStageDTO.getFile().getBytes());
        this.setTitle(uploadFicherOffreStageDTO.getTitle());
        this.setEntrepriseName(uploadFicherOffreStageDTO.getEntrepriseName());
        this.setCreateur_id(createur_id);
        this.setSession(uploadFicherOffreStageDTO.getSession());
        this.setAnnee(uploadFicherOffreStageDTO.getAnnee());
    }

    @Override
    public String toString() {
        return "FichierOffreStageDTO{" +
                "filename='" + filename + '\'' +
                ", fileData='" + fileData + '\'' +
                ", title='" + getTitle() + '\'' +
                ", entrepriseName='" + getEntrepriseName() + '\'' +
                ", createdAt=" + getCreatedAt() +
                ", updatedAt=" + getUpdatedAt() +
                ", createur_id=" + getCreateur_id() +
                ", status=" + getStatus() +
                ", visibility=" + getVisibility() +
                ", programme=" + getProgramme() +
                ", session='" + getSession() + '\'' +
                ", annee=" + getAnnee() +
                '}';
    }
}
