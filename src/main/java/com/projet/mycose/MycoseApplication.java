package com.projet.mycose;

import com.projet.mycose.modele.Employeur;
import com.projet.mycose.modele.Etudiant;
import com.projet.mycose.modele.OffreStage;
import com.projet.mycose.modele.Programme;
import com.projet.mycose.repository.OffreStageRepository;
import com.projet.mycose.service.*;
import com.projet.mycose.service.dto.EtudiantDTO;
import com.projet.mycose.service.dto.FormulaireOffreStageDTO;
import com.projet.mycose.service.dto.LoginDTO;
import com.projet.mycose.service.dto.FormulaireOffreStageDTO;
import com.projet.mycose.service.dto.LoginDTO;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MycoseApplication implements CommandLineRunner {


	public MycoseApplication(EnseignantService enseignantService, EtudiantService etudiantService, EmployeurService employeurService, OffreStageService offreStageService, UtilisateurService utilisateurService) {
        this.etudiantService = etudiantService;
		this.employeurService = employeurService;
		this.offreStageService = offreStageService;
		this.utilisateurService = utilisateurService;
    }

    public static void main(String[] args) {
		SpringApplication.run(MycoseApplication.class, args);
		System.out.println("Hello world!");
	}
	private final EtudiantService etudiantService;
	private final EmployeurService employeurService;
	private final OffreStageService offreStageService;
	private final UtilisateurService utilisateurService;
	@Override
	public void run(String... args) {

        employeurService.creationDeCompte("Willy", "Wonka", "555-333-4343", "wonka@gmail.com", "Chocolatayyy$", "Wonka INC");
        String token = utilisateurService.authentificationUtilisateur(new LoginDTO("wonka@gmail.com", "Chocolatayyy$"));
        FormulaireOffreStageDTO formulaireOffreStageDTO = new FormulaireOffreStageDTO(1L, "Willy Wona", "wonka@gmail.com", "wonka.com", "New York", "100 000", "OHH CHOCOLATE", "Data Analyst", "Wonka INC", 4L);
        //FormulaireOffreStageDTO formulaireOffreStageDTO2 = new FormulaireOffreStageDTO(2L, "MMM", "Vicente2", "vicen@mail.com", "www.vicen2.ca", "Software Engineer 2", "Montrèal 2", "95 000", "J'adore ingénieur! 2", 1L);
        offreStageService.saveForm(formulaireOffreStageDTO, "Bearer " + token);
//		offreStageService.assignerEmployeur(3L, 1L);
	}
}
