package com.projet.mycose.modele.evaluation;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String questionKey;
    private String question;

    @Enumerated(EnumType.STRING)
    private Resultat resultat;

    public enum Resultat {
        TOTALEMENT_EN_ACCORD,
        PLUTOT_EN_ACCORD,
        PLUTOT_EN_DESACCORD,
        TOTALEMENT_EN_DESACCORD,
        NA
    }
}
