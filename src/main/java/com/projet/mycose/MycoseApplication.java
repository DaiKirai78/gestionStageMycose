package com.projet.mycose;

import com.projet.mycose.modele.OffreStage;
import com.projet.mycose.modele.Programme;
import com.projet.mycose.service.*;
import com.projet.mycose.dto.FormulaireOffreStageDTO;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.LocalDateTime;


@SpringBootApplication
public class MycoseApplication implements CommandLineRunner {


	public MycoseApplication(EnseignantService enseignantService, EtudiantService etudiantService, EmployeurService employeurService, OffreStageService offreStageService, UtilisateurService utilisateurService, GestionnaireStageService gestionnaireStageService) {
        this.enseignantService = enseignantService;
        this.etudiantService = etudiantService;
		this.employeurService = employeurService;
		this.offreStageService = offreStageService;
		this.utilisateurService = utilisateurService;
		this.gestionnaireStageService = gestionnaireStageService;
	}

    public static void main(String[] args) {
		SpringApplication.run(MycoseApplication.class, args);
		System.out.println("Hello world!");
	}
	private final EnseignantService enseignantService;
	private final EtudiantService etudiantService;
	private final EmployeurService employeurService;
	private final OffreStageService offreStageService;
	private final UtilisateurService utilisateurService;
	private final GestionnaireStageService gestionnaireStageService;

	@Override
	public void run(String... args) {
		//enseignantService.creationDeCompte("Guillaume", "Courtemanche", "283-948-2738", "courtemanche@gmail.com", "courte123$");
		etudiantService.creationDeCompte("Roberto", "Berrios", "273-389-2937", "roby@gmail.com", "Roby123$", Programme.TECHNIQUE_INFORMATIQUE);
		//enseignantService.creationDeCompte("Guillaume", "Courtemanche", "283-948-2738", "courtebite@gmail.com", "courte123$");
		etudiantService.creationDeCompte("Guillaume", "Courtemanche", "283-948-2738", "gc@gmail.com", "Passw0rd", Programme.GENIE_LOGICIEL);
		gestionnaireStageService.creationDeCompte("Elie", "Boucher-Gendron", "438-333-4343", "eliescrummaster@gmail.com", "Passw0rd");
		employeurService.creationDeCompte("Willy", "Wonka", "555-333-4343", "wonka@gmail.com", "Passw0rd", "Wonka INC");
		//String token = utilisateurService.authentificationUtilisateur(new LoginDTO("wonka@gmail.com", "Chocolatayyy$"));
		etudiantService.creationDeCompte("Guillaume", "Courtemanche", "283-948-2738", "gc@gmail.com", "Passw0rd", Programme.GENIE_LOGICIEL);
		etudiantService.creationDeCompte("Sophie", "Leroux", "514-123-4567", "sleroux@gmail.com", "Password1", Programme.GENIE_LOGICIEL);
		etudiantService.creationDeCompte("Jean", "Martin", "438-987-6543", "jmartin@gmail.com", "Password2", Programme.GENIE_LOGICIEL);
		etudiantService.creationDeCompte("Marie", "Dubois", "450-234-5678", "mdubois@gmail.com", "Password3", Programme.GENIE_LOGICIEL);
		etudiantService.creationDeCompte("Lucas", "Bernard", "581-345-6789", "lbernard@gmail.com", "Password4", Programme.GENIE_LOGICIEL);
		etudiantService.creationDeCompte("Emma", "Roy", "819-456-7890", "eroy@gmail.com", "Password5", Programme.GENIE_LOGICIEL);
		etudiantService.creationDeCompte("Alexandre", "Tremblay", "418-567-8901", "atremblay@gmail.com", "Password6", Programme.GENIE_LOGICIEL);
		etudiantService.creationDeCompte("Olivia", "Gagnon", "450-678-9012", "ogagnon@gmail.com", "Password7", Programme.GENIE_LOGICIEL);
		etudiantService.creationDeCompte("Charles", "Lefebvre", "581-789-0123", "clefebvre@gmail.com", "Password8", Programme.GENIE_LOGICIEL);
		etudiantService.creationDeCompte("Alice", "Girard", "819-890-1234", "agirard@gmail.com", "Password9", Programme.GENIE_LOGICIEL);
		etudiantService.creationDeCompte("Mathieu", "Fortin", "514-901-2345", "mfortin@gmail.com", "Password10", Programme.GENIE_LOGICIEL);
		enseignantService.creationDeCompte("François", "Lacousière", "514-024-0174", "francoislacoursiere@gmail.com", "Password10");
		enseignantService.creationDeCompte("Didier", "Tremblay", "438-589-4792", "didiertremblay@gmail.com", "Password10");
		//FormulaireOffreStageDTO formulaireOffreStageDTO = new FormulaireOffreStageDTO(1L, "Chco Factory", "Willy Wonka", "wonka@mail.com", "wonka.com", "Master Chocolatier", "New York", "100 000", "Make Chocolate", 4L, OffreStage.Status.WAITING);
		FormulaireOffreStageDTO formulaireOffreStageDTO = new FormulaireOffreStageDTO(1L, "SUUPER", "Vicente", "vicen@mail.com", "www.vicen.ca", "Software Engineer Java Expert boy", "Montrèal", "95 000", "J'adore ingénieur!",LocalDateTime.now(), LocalDateTime.now(), 1L, OffreStage.Status.WAITING, Programme.GENIE_LOGICIEL, OffreStage.Visibility.PUBLIC, null, OffreStage.SessionEcole.AUTOMNE, 2022);

		try{
			//offreStageService.saveForm(formulaireOffreStageDTO, "Bearer " + token);
		} catch (Exception e) {
			System.out.println("Erreur dans main: " + e);
		}

//		offreStageService.assignerEmployeur(3L, 1L);
	}
}
