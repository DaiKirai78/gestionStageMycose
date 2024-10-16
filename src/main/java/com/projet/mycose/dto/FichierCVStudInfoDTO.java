package com.projet.mycose.dto;

import com.projet.mycose.modele.FichierCV;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
