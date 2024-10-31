package com.projet.mycose.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
public class SummonEtudiantDTO {

    @NotBlank(message = "L'heure de la convocation est obligatoire")
    private LocalDateTime scheduledAt;

    @NotBlank(message = "Le lieu de la convocation est obligatoire")
    private String location;

    @NotBlank(message = "Le message de la convocation est obligatoire")
    private String messageConvocation;

}
