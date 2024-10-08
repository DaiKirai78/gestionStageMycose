package com.projet.mycose.service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class UploadFicherOffreStageDTO {

    @NotNull(message = "File is required")
    private MultipartFile file;

    @NotBlank(message = "Title is required")
    private String title;

    private String entrepriseName;
}