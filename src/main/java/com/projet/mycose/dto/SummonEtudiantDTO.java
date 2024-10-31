package com.projet.mycose.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
public class SummonEtudiantDTO {

    @NotNull(message = "L'heure de la convocation est obligatoire")
    @Future(message = "Event date must be in the future")
    private LocalDateTime scheduledAt;

    @NotBlank(message = "Le lieu de la convocation est obligatoire")
    private String location;

    @NotBlank(message = "Le message de la convocation est obligatoire")
    private String messageConvocation;

}
