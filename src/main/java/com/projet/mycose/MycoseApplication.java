package com.projet.mycose;

import com.projet.mycose.modele.Etudiant;
import com.projet.mycose.modele.FichierOffreStage;
import com.projet.mycose.modele.FormulaireOffreStage;
import com.projet.mycose.modele.Programme;
import com.projet.mycose.repository.UtilisateurRepository;
import com.projet.mycose.service.*;
import com.projet.mycose.service.dto.EmployeurDTO;
import com.projet.mycose.service.dto.EtudiantDTO;
import com.projet.mycose.service.dto.FormulaireOffreStageDTO;
import com.projet.mycose.service.dto.LoginDTO;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@SpringBootApplication
public class MycoseApplication implements CommandLineRunner {

	private final EtudiantService etudiantService;
	private final GestionnaireStageService gestionnaireStageService;
	private final EmployeurService employeurService;
	private final OffreStageService offreStageService;
	private final UtilisateurService utilisateurService;

	public MycoseApplication(EtudiantService etudiantService, GestionnaireStageService gestionnaireStageService, EmployeurService employeurService, OffreStageService offreStageService, UtilisateurService utilisateurService) {
        this.etudiantService = etudiantService;
		this.gestionnaireStageService = gestionnaireStageService;
		this.employeurService = employeurService;
		this.offreStageService = offreStageService;
		this.utilisateurService = utilisateurService;
	}

	public static void main(String[] args) {
		SpringApplication.run(MycoseApplication.class, args);
		System.out.println("Hello world!");
	}

	@Override
	public void run(String... args) {
		employeurService.creationDeCompte("Willy", "Wonka", "555-6565-9876", "wonka@mail.com", "Chocolatayy1", "Wonka INC");
		etudiantService.creationDeCompte("Roberto", "Berrios", "273-389-2937", "roby@gmail.com", "Roby123$", Programme.TECHNIQUE_INFORMATIQUE);
		gestionnaireStageService.creationDeCompte("Elie", "Boucher-Gendron", "450-948-2738", "eliescrummaster@gmail.com", "Passw0rd");

		// Hydrater BD, pour tester front end
		String token = utilisateurService.authentificationUtilisateur(new LoginDTO("wonka@mail.com", "Chocolatayy1"));

		FormulaireOffreStageDTO formulaireOffreStageDTO = new FormulaireOffreStageDTO(1L, "SUUPER", "Vicente", "vicen@mail.com", "www.vicen.ca", "Software Engineer Java Expert boy", "Montrèal", "95 000", "J'adore ingénieur!", 1L);
		FormulaireOffreStageDTO formulaireOffreStageDTO2 = new FormulaireOffreStageDTO(2L, "MMM", "Vicente2", "vicen@mail.com", "www.vicen2.ca", "Software Engineer 2", "Montrèal 2", "95 000", "J'adore ingénieur! 2", 1L);
		FormulaireOffreStageDTO formulaireOffreStageDTO3 = new FormulaireOffreStageDTO(3L, "MMMSUUPER", "Vicente3", "vicen@mail.com", "www.vicen3.ca", "Software Engineer 3", "Montrèal 3", "16", "J'adore ingénieur! 3", 1L);
		FormulaireOffreStageDTO formulaireOffreStageDTO4 = new FormulaireOffreStageDTO(4L, "SUUPER", "Vicente444", "vicenere@mail.com", "www.vicenrere.ca", "Software Engineerrr", "Montrèalll", "95 001", "J'adore ingénieur!!!", 1L);
		FormulaireOffreStageDTO formulaireOffreStageDTO5 = new FormulaireOffreStageDTO(5L, "MMM", "Vicente2", "vicen@mail.com", "www.vicen2.ca", "Java Developper", "Montrèal 2", "95 000", "J'adore ingénieur! 2", 1L);
		FormulaireOffreStageDTO formulaireOffreStageDTO6 = new FormulaireOffreStageDTO(6L, "MMMSUUPER", "Vicente3", "vicen@mail.com", "www.vicen3.ca", "Android Java", "Montrèal 3", "16", "J'adore ingénieur! 3", 1L);
		FormulaireOffreStageDTO formulaireOffreStageDTO7 = new FormulaireOffreStageDTO(7L, "SUUPER", "Vicente", "vicen@mail.com", "www.vicen.ca", "Mec Jadore Kotlin", "Montrèal", "95 000", "J'adore ingénieur!", 1L);
		FormulaireOffreStageDTO formulaireOffreStageDTO8 = new FormulaireOffreStageDTO(8L, "MMM", "Vicente2", "vicen@mail.com", "www.vicen2.ca", "OHH ASTROOO", "Montrèal 2", "95 000", "J'adore ingénieur! 2", 1L);
		FormulaireOffreStageDTO formulaireOffreStageDTO9 = new FormulaireOffreStageDTO(9L, "MMMSUUPER", "Vicente3", "vicen@mail.com", "www.vicen3.ca", "Software Engineer 3", "Montrèal 3", "16", "J'adore ingénieur! 3", 1L);
		FormulaireOffreStageDTO formulaireOffreStageDTO10 = new FormulaireOffreStageDTO(10L, "SUUPER", "Vicente", "vicen@mail.com", "www.vicen.ca", "Software Engineer", "Montrèal", "95 000", "J'adore ingénieur!", 1L);
		FormulaireOffreStageDTO formulaireOffreStageDTO11 = new FormulaireOffreStageDTO(11L, "MMM", "Vicente2", "vicen@mail.com", "www.vicen2.ca", "Software Engineer 2", "Montrèal 2", "95 000", "J'adore ingénieur! 2", 1L);
		FormulaireOffreStageDTO formulaireOffreStageDTO12 = new FormulaireOffreStageDTO(12L, "MMMSUUPER", "Vicente3", "vicen@mail.com", "www.vicen3.ca", "Software Engineer 3", "Montrèal 3", "16", "J'adore ingénieur! 3", 1L);
		FormulaireOffreStageDTO formulaireOffreStageDTO13 = new FormulaireOffreStageDTO(13L, "SUUPER", "Vicente", "vicen@mail.com", "www.vicen.ca", "Software Engineer", "Montrèal", "95 000", "J'adore ingénieur!", 1L);
		FormulaireOffreStageDTO formulaireOffreStageDTO14 = new FormulaireOffreStageDTO(14L, "MMM", "Vicente2", "vicen@mail.com", "www.vicen2.ca", "Software Engineer 2", "Montrèal 2", "95 000", "J'adore ingénieur! 2", 1L);
		FormulaireOffreStageDTO formulaireOffreStageDTO15 = new FormulaireOffreStageDTO(15L, "MMMSUUPER", "Vicente3", "vicen@mail.com", "www.vicen3.ca", "Software Engineer 3", "Montrèal 3", "16", "J'adore ingénieur! 3", 1L);
		FormulaireOffreStageDTO formulaireOffreStageDTO16 = new FormulaireOffreStageDTO(16L, "SUUPER", "Vicente", "vicen@mail.com", "www.vicen.ca", "Software Engineer", "Montrèal", "95 000", "J'adore ingénieur!", 1L);
		FormulaireOffreStageDTO formulaireOffreStageDTO17 = new FormulaireOffreStageDTO(17L, "MMM", "Vicente2", "vicen@mail.com", "www.vicen2.ca", "Software Engineer 2", "Montrèal 2", "95 000", "J'adore ingénieur! 2", 1L);
		FormulaireOffreStageDTO formulaireOffreStageDTO18 = new FormulaireOffreStageDTO(18L, "SUUPER", "Vicente", "vicen@mail.com", "www.vicen.ca", "Software Engineer", "Montrèal", "95 000", "J'adore ingénieur!", 1L);
		FormulaireOffreStageDTO formulaireOffreStageDTO19 = new FormulaireOffreStageDTO(19L, "MMM", "Vicente2", "vicen@mail.com", "www.vicen2.ca", "Software Engineer 2", "Montrèal 2", "95 000", "J'adore ingénieur! 2", 1L);
		FormulaireOffreStageDTO formulaireOffreStageDTO20 = new FormulaireOffreStageDTO(20L, "MMMSUUPER", "Vicente3", "vicen@mail.com", "www.vicen3.ca", "Software Engineer 3", "Montrèal 3", "16", "J'adore ingénieur! 3", 1L);
		FormulaireOffreStageDTO formulaireOffreStageDTO21 = new FormulaireOffreStageDTO(21L, "SUUPER", "Vicente", "vicen@mail.com", "www.vicen.ca", "Software Engineer", "Montrèal", "95 000", "J'adore ingénieur!", 1L);
		FormulaireOffreStageDTO formulaireOffreStageDTO22 = new FormulaireOffreStageDTO(22L, "MMM", "Vicente2", "vicen@mail.com", "www.vicen2.ca", "Software Engineer 2", "Montrèal 2", "95 000", "J'adore ingénieur! 2", 1L);

		try {
			offreStageService.saveForm(formulaireOffreStageDTO, "Bearer " + token);
			offreStageService.saveForm(formulaireOffreStageDTO2, "Bearer " + token);
			offreStageService.saveForm(formulaireOffreStageDTO3, "Bearer " + token);
			offreStageService.saveForm(formulaireOffreStageDTO4, "Bearer " + token);
			offreStageService.saveForm(formulaireOffreStageDTO5, "Bearer " + token);
			offreStageService.saveForm(formulaireOffreStageDTO6, "Bearer " + token);
			offreStageService.saveForm(formulaireOffreStageDTO7, "Bearer " + token);
			offreStageService.saveForm(formulaireOffreStageDTO8, "Bearer " + token);
			offreStageService.saveForm(formulaireOffreStageDTO9, "Bearer " + token);
			offreStageService.saveForm(formulaireOffreStageDTO10, "Bearer " + token);
			offreStageService.saveForm(formulaireOffreStageDTO11, "Bearer " + token);
			offreStageService.saveForm(formulaireOffreStageDTO12, "Bearer " + token);
			offreStageService.saveForm(formulaireOffreStageDTO13, "Bearer " + token);
			offreStageService.saveForm(formulaireOffreStageDTO14, "Bearer " + token);
			offreStageService.saveForm(formulaireOffreStageDTO15, "Bearer " + token);
			offreStageService.saveForm(formulaireOffreStageDTO16, "Bearer " + token);
			offreStageService.saveForm(formulaireOffreStageDTO17, "Bearer " + token);
			offreStageService.saveForm(formulaireOffreStageDTO18, "Bearer " + token);
			offreStageService.saveForm(formulaireOffreStageDTO19, "Bearer " + token);
			offreStageService.saveForm(formulaireOffreStageDTO20, "Bearer " + token);
			offreStageService.saveForm(formulaireOffreStageDTO21, "Bearer " + token);
			offreStageService.saveForm(formulaireOffreStageDTO22, "Bearer " + token);
		} catch(Exception e) {
			System.out.println("Erreur Main 75: " + e);
			System.out.println(token);
		}
	}
}
