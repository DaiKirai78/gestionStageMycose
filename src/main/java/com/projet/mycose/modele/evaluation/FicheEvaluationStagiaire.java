package com.projet.mycose.modele.evaluation;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.projet.mycose.modele.Enseignant;
import com.projet.mycose.modele.Etudiant;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Entity
public class FicheEvaluationStagiaire {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "etudiant_id", nullable = false)
    private Etudiant etudiant;

    @ManyToOne
    @JoinColumn(name = "enseignant_id")
    private Enseignant enseignant;
    private String NomCegep;
    private String adresse;
    private String numeroTelecopieur;

    @OneToMany(mappedBy = "ficheEvaluationStagiaire")
    private List<FicheEvaluationQuestionnaire> questionnaires = new ArrayList<>();

    @Embedded
    private AppreciationGlobaleStagiaire appreciationGlobaleStagiaire;

    @Embedded
    private AcceuillirProchainStage acceuillirProchainStage;


}
