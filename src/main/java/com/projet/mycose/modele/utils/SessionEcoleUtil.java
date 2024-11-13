package com.projet.mycose.modele.utils;

import com.projet.mycose.dto.SessionInfoDTO;
import com.projet.mycose.modele.OffreStage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.Year;

public class SessionEcoleUtil {

    //25 janvier - Début Session Hiver
    //30 mai - Fin Session Hiver
    //31 mai - Début Été
    //20 août - Fin Été
    //21 août - Début Session Automne
    //27 décembre - Fin Session Automne
    //On veut toujours retourner la date selon la prochaine session qui débute
    public static SessionInfoDTO getSessionInfo(LocalDateTime dateTime) {
        LocalDate date = dateTime.toLocalDate();
        int year = date.getYear();

        // Define session start dates
        LocalDate hiverStart = LocalDate.of(year, Month.JANUARY, 25);
        LocalDate eteStart = LocalDate.of(year, Month.MAY, 31);
        LocalDate automneStart = LocalDate.of(year, Month.AUGUST, 21);

        if (!date.isBefore(hiverStart) && date.isBefore(eteStart)) {
            //N'est pas avant le début de la session d'hiver et est avant le début de la session d'été
            //Donc on va l'associer avec la prochaine session qui débute, soit la session d'été
            return new SessionInfoDTO(OffreStage.SessionEcole.ETE, Year.of(year));
        } else if (!date.isBefore(eteStart) && date.isBefore(automneStart)) {
            //N'est pas avant le début de la session d'été et est avant le début de la session d'automne
            //Donc on va l'associer avec la prochaine session qui débute, soit la session d'automne
            return new SessionInfoDTO(OffreStage.SessionEcole.AUTOMNE, Year.of(year));
        } else {
            //Est avant le début de la session d'hiver et après le début de la session d'automne
            //Donc on va l'associer avec la prochaine session qui débute, soit la session d'hiver de l'année prochaine
            return new SessionInfoDTO(OffreStage.SessionEcole.HIVER, Year.of(year + 1));
        }
    }
}
