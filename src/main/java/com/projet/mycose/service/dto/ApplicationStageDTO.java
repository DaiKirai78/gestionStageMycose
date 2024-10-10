package com.projet.mycose.service.dto;


import com.projet.mycose.modele.ApplicationStage;
import jdk.jshell.Snippet;
import lombok.*;

@Getter
@NoArgsConstructor
@Setter
@ToString
public class ApplicationStageDTO {
    private Long id;
    private Long etudiant_id;
    private Long offreStage_id;

    @Builder
    public ApplicationStageDTO(Long id, Long etudiant_id, Long offreStage_id) {
        this.id = id;
        this.etudiant_id = etudiant_id;
        this.offreStage_id = offreStage_id;
    }

    public static ApplicationStageDTO empty() {
        return new ApplicationStageDTO();
    }

    public static ApplicationStageDTO toDTO(ApplicationStage applicationStage) {
        return ApplicationStageDTO.builder()
                .id(applicationStage.getId())
                .etudiant_id(applicationStage.getEtudiant().getId())
                .offreStage_id(applicationStage.getOffreStage().getId())
                .build();
    }
}
