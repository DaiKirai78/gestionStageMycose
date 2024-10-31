package com.projet.mycose.dto;

import com.projet.mycose.modele.Convocation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AnswerSummonDTO {

    @NotBlank(message = "Le message de l'étudiant est obligatoire")
    private String messageEtudiant;

    @NotNull
    private Convocation.ConvocationStatus status;
}
