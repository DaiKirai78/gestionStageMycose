package com.projet.mycose.service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.modelmapper.internal.bytebuddy.implementation.bind.annotation.Empty;

import java.time.LocalDateTime;


@Getter
@Setter
public class FichierCVDTO {

    private Long id;

    @NotBlank(message = "Filename is required.")
    @Size(max = 255, message = "Filename cannot exceed 255 characters.")
    @Pattern(regexp = "^[\\p{L}0-9\\s,_.-]+\\.pdf$",
            message = "Invalid filename format. Only PDF files are allowed.")
    private String filename;

    @NotBlank(message = "File data is required.")
    @Pattern(regexp = "^[A-Za-z0-9+/=]+$",
            message = "Invalid file data. Should be Base64 encoded.")
    private String fileData;

    private String status;

    private String statusDescription;

    private LocalDateTime createdAt;

    private Long etudiant_id;

}
