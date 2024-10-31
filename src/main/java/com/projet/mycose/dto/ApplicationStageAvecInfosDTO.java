package com.projet.mycose.dto;

import com.projet.mycose.modele.ApplicationStage;
import com.projet.mycose.modele.Convocation;
import com.projet.mycose.modele.FichierOffreStage;
import com.projet.mycose.modele.FormulaireOffreStage;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Base64;

@Getter
@NoArgsConstructor
@Setter
@ToString
public class ApplicationStageAvecInfosDTO {

    private Long id;
    private String title;
    private String entrepriseName;
    private String email;
    private String website;
    private String location;
    private String salary;
    private String description;
    private String filename;
    private String fileData;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long etudiant_id;
    private Long offreStage_id;
    private Long convocation_id;
    private LocalDateTime scheduledAt;
    private LocalDateTime summonedAt;
    private String messageConvocation;
    private String convocationMessageEtudiant;
    private String locationConvocation;
    private Convocation.ConvocationStatus convocationStatus;
    private ApplicationStage.ApplicationStatus status;
    private LocalDateTime appliedAt;

    public static ApplicationStageAvecInfosDTO toDTO(ApplicationStage applicationStage) {
        ApplicationStageAvecInfosDTO dto = new ApplicationStageAvecInfosDTO();
        dto.setId(applicationStage.getId());
        dto.setEtudiant_id(applicationStage.getEtudiant().getId());
        dto.setOffreStage_id(applicationStage.getOffreStage().getId());
        dto.setStatus(applicationStage.getStatus());
        dto.setAppliedAt(applicationStage.getAppliedAt());
        dto.setCreatedAt(applicationStage.getOffreStage().getCreatedAt());
        dto.setUpdatedAt(applicationStage.getOffreStage().getUpdatedAt());
        dto.setTitle(applicationStage.getOffreStage().getTitle());
        dto.setEntrepriseName(applicationStage.getOffreStage().getEntrepriseName());
        if (applicationStage.getConvocation() != null) {
            dto.setConvocation_id(applicationStage.getConvocation().getId());
            dto.setScheduledAt(applicationStage.getConvocation().getScheduledAt());
            dto.setSummonedAt(applicationStage.getConvocation().getSummonedAt());
            dto.setMessageConvocation(applicationStage.getConvocation().getMessageConvocation());
            dto.setConvocationMessageEtudiant(applicationStage.getConvocation().getMessageEtudiant());
            dto.setLocationConvocation(applicationStage.getConvocation().getLocation());
            dto.setConvocationStatus(applicationStage.getConvocation().getStatus());
        }
        if (applicationStage.getOffreStage() instanceof FichierOffreStage fichier) {
            System.out.println("FichierOffreStage");
            dto.setFilename(fichier.getFilename());
            dto.setFileData(Base64.getEncoder().encodeToString(fichier.getData()));
        } else if (applicationStage.getOffreStage() instanceof FormulaireOffreStage formulaire) {
            System.out.println("FormulaireOffreStage");
            dto.setEmail(formulaire.getEmail());
            dto.setWebsite(formulaire.getWebsite());
            dto.setLocation(formulaire.getLocation());
            dto.setSalary(formulaire.getSalary());
            dto.setDescription(formulaire.getDescription());
        } else {
            System.out.println("nothing");
        }
        return dto;
    }
}
