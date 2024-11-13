package com.projet.mycose.dto;

import com.projet.mycose.modele.OffreStage;

import java.time.Year;

public record SessionInfoDTO(OffreStage.SessionEcole session, Year annee) {
}
