package com.projet.mycose.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.projet.mycose.modele.Programme;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class AcceptOffreDeStageDTO {

    @NotNull(message = "ID cannot be null.")
    @JsonProperty("id")
    private Long id;

    @Size(max = 255, message = "Status description cannot exceed 255 characters.")
    private String statusDescription;

    private Programme programme;

    private List<Long> etudiantsPrives;
}
