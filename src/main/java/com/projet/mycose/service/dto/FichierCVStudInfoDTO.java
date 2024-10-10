package com.projet.mycose.service.dto;

import com.projet.mycose.modele.FichierCV;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Base64;


@Getter
@Setter
@NoArgsConstructor
public class FichierCVStudInfoDTO extends FichierCVDTO {

    public FichierCVStudInfoDTO(
            Long id,
            String filename,
            String fileData,
            FichierCV.Status status,
            String statusDescription,
            LocalDateTime createdAt,
            Long etudiant_id,
            String studentLastName,
            String studentFirstName,
            String programme) {
        super(id, filename, fileData, String.valueOf(status), statusDescription, createdAt, etudiant_id);
        this.studentLastName = studentLastName;
        this.studentFirstName = studentFirstName;
        this.programme = programme;
    }

    private String studentLastName;
    private String studentFirstName;
    private String programme;

    public static FichierCVStudInfoDTO toDto(FichierCV fichierCV) {
        return new FichierCVStudInfoDTO(
                fichierCV.getId(),
                fichierCV.getFilename(),
                Base64.getEncoder().encodeToString(fichierCV.getData()),
                fichierCV.getStatus(),
                fichierCV.getStatusDescription(),
                fichierCV.getCreatedAt(),
                fichierCV.getEtudiant().getId(),
                fichierCV.getEtudiant().getNom(),
                fichierCV.getEtudiant().getPrenom(),
                fichierCV.getEtudiant().getProgramme().toString()
                );
    }
}
