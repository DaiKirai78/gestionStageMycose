package com.projet.mycose.service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class FichierOffreStageDTO {

    private Long id;

    @NotBlank(message = "Filename is required.")
    @Size(max = 255, message = "Filename cannot exceed 255 characters.")
    @Pattern(regexp = "^[\\w,\\s-]+\\.pdf$",
            message = "Invalid filename format. Only PDF files are allowed.")
    private String filename;

    @NotBlank(message = "File data is required.")
    @Pattern(regexp = "^[A-Za-z0-9+/=]+$",
            message = "Invalid file data. Should be Base64 encoded.")
    private String fileData;

}
