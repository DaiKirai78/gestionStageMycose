package com.projet.mycose.dto;

import com.projet.mycose.modele.Programme;
import jakarta.persistence.PreUpdate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
public class UploadFicherOffreStageDTO {

    @NotNull(message = "File is required")
    private MultipartFile file;

    @NotBlank(message = "Title is required")
    private String title;

    private String entrepriseName;

    private Programme programme;

    private List<Long> etudiantsPrives;

    @PreUpdate
    public void preUpdate() {
        if (this.programme == null) {
            this.setProgramme(Programme.NOT_SPECIFIED);
        }
    }
}