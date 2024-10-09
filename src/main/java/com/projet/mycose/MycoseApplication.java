package com.projet.mycose;

import com.projet.mycose.modele.Etudiant;
import com.projet.mycose.modele.FichierOffreStage;
import com.projet.mycose.modele.Programme;
import com.projet.mycose.repository.UtilisateurRepository;
import com.projet.mycose.service.*;
import com.projet.mycose.service.dto.EtudiantDTO;
import com.projet.mycose.service.dto.FormulaireOffreStageDTO;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.multipart.MultipartFile;

@SpringBootApplication
public class MycoseApplication implements CommandLineRunner {

	private final EtudiantService etudiantService;
	private final GestionnaireStageService gestionnaireStageService;
	private final EmployeurService employeurService;
	private final OffreStageService offreStageService;

	public MycoseApplication(EtudiantService etudiantService, GestionnaireStageService gestionnaireStageService, EmployeurService employeurService, OffreStageService offreStageService) {
        this.etudiantService = etudiantService;
		this.gestionnaireStageService = gestionnaireStageService;
		this.employeurService = employeurService;
		this.offreStageService = offreStageService;
	}

	public static void main(String[] args) {
		SpringApplication.run(MycoseApplication.class, args);
		System.out.println("Hello world!");
	}

	@Override
	public void run(String... args) {
		employeurService.creationDeCompte("Willy", "Wonka", "555-6565-9876", "wonka@mail.com", "Chocolatayy1", "Wonka INC");
		gestionnaireStageService.creationDeCompte("Elie", "Boucher-Gendron", "450-948-2738", "eliescrummaster@gmail.com", "Passw0rd");
		etudiantService.creationDeCompte("Elie", "Boucher-Gendron", "450-948-2728", "elie@gmail.com", "Passw0rd", Programme.TECHNIQUE_INFORMATIQUE);
	}
}
