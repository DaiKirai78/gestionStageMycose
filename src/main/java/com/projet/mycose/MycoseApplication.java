package com.projet.mycose;

import com.projet.mycose.modele.Employeur;
import com.projet.mycose.modele.Etudiant;
import com.projet.mycose.modele.OffreStage;
import com.projet.mycose.modele.Programme;
import com.projet.mycose.repository.OffreStageRepository;
import com.projet.mycose.service.EnseignantService;
import com.projet.mycose.service.EmployeurService;
import com.projet.mycose.service.EtudiantService;
import com.projet.mycose.service.OffreStageService;
import com.projet.mycose.service.dto.EtudiantDTO;
import com.projet.mycose.service.dto.FormulaireOffreStageDTO;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MycoseApplication implements CommandLineRunner {

    public MycoseApplication(EnseignantService enseignantService, EtudiantService etudiantService, EmployeurService employeurService, OffreStageService offreStageService) {
        this.enseignantService = enseignantService;
        this.etudiantService = etudiantService;
		this.employeurService = employeurService;
		this.offreStageService = offreStageService;
    }

    public static void main(String[] args) {
		SpringApplication.run(MycoseApplication.class, args);
		System.out.println("Hello world!");
	}
	private final EnseignantService enseignantService;
	private final EtudiantService etudiantService;
	private final EmployeurService employeurService;
	private final OffreStageService offreStageService;
	@Override
	public void run(String... args) {
		//enseignantService.creationDeCompte("Guillaume", "Courtemanche", "283-948-2738", "courtemanche@gmail.com", "courte123$");
		etudiantService.creationDeCompte("Roberto", "Berrios", "273-389-2937", "roby@gmail.com", "Roby123$", Programme.TECHNIQUE_INFORMATIQUE);
		//enseignantService.creationDeCompte("Guillaume", "Courtemanche", "283-948-2738", "courtebite@gmail.com", "courte123$");
		etudiantService.creationDeCompte("Guillaume", "Courtemanche", "283-948-2738", "gc@gmail.com", "courte123$", Programme.GENIE_LOGICIEL);

		employeurService.creationDeCompte("Willy", "Wonka", "555-333-4343", "wonka@gmail.com", "Chocolatayyy$", "Wonka INC");
//		FormulaireOffreStageDTO formulaireOffreStageDTO = new FormulaireOffreStageDTO(1L, "Chco Factory", "Willy Wonka", "wonka@mail.com", "wonka.com", "Master Chocolatier", "New York", "100 000", "Make Chocolate");
//		offreStageService.saveForm(formulaireOffreStageDTO);
//		offreStageService.assignerEmployeur(3L, 1L);
	}
}
