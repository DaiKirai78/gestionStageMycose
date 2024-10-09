package com.projet.mycose;

import com.projet.mycose.modele.Etudiant;
import com.projet.mycose.modele.FichierOffreStage;
import com.projet.mycose.modele.FormulaireOffreStage;
import com.projet.mycose.modele.Programme;
import com.projet.mycose.service.EnseignantService;
import com.projet.mycose.service.EmployeurService;
import com.projet.mycose.service.EtudiantService;
import com.projet.mycose.service.OffreStageService;
import com.projet.mycose.service.dto.EtudiantDTO;
import com.projet.mycose.service.dto.FormulaireOffreStageDTO;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@SpringBootApplication
public class MycoseApplication implements CommandLineRunner {

	public MycoseApplication(EnseignantService enseignantService, EtudiantService etudiantService, OffreStageService offreStageService, EmployeurService employeurService) {
		this.enseignantService = enseignantService;
		this.etudiantService = etudiantService;
		this.offreStageService = offreStageService;
		this.employeurService = employeurService;
	}

	public static void main(String[] args) {
		SpringApplication.run(MycoseApplication.class, args);
		System.out.println("Hello world!");
	}
	private final EnseignantService enseignantService;
	private final EtudiantService etudiantService;
	private final OffreStageService offreStageService;
	private final EmployeurService employeurService;

	@Override
	public void run(String... args) {
		//enseignantService.creationDeCompte("Guillaume", "Courtemanche", "283-948-2738", "courtemanche@gmail.com", "courte123$");
		//etudiantService.creationDeCompte("Roberto", "Berrios", "273-389-2937", "roby@gmail.com", "Roby123$", "Technique de l'informatique");

		employeurService.creationDeCompte("Willy", "Wonka", "555-6565-9876", "wonka@mail.com", "Chocolatayy1", "Wonka INC");

		FormulaireOffreStageDTO formulaireOffreStageDTO = new FormulaireOffreStageDTO(1L, "SUUPER", "Vicente", "vicen@mail.com", "www.vicen.ca", "Software Engineer", "Montrèal", "95 000", "J'adore ingénieur!", 1L, LocalDateTime.now(), LocalDateTime.now());
		FormulaireOffreStageDTO formulaireOffreStageDTO2 = new FormulaireOffreStageDTO(2L, "MMM", "Vicente2", "vicen@mail.com", "www.vicen2.ca", "Software Engineer 2", "Montrèal 2", "95 000", "J'adore ingénieur! 2", 1L, LocalDateTime.now(), LocalDateTime.now());
		FormulaireOffreStageDTO formulaireOffreStageDTO3 = new FormulaireOffreStageDTO(3L, "MMMSUUPER", "Vicente3", "vicen@mail.com", "www.vicen3.ca", "Software Engineer 3", "Montrèal 3", "16", "J'adore ingénieur! 3", 1L, LocalDateTime.now(), LocalDateTime.now());
		FormulaireOffreStageDTO formulaireOffreStageDTO4 = new FormulaireOffreStageDTO(4L, "SUUPER", "Vicente444", "vicenere@mail.com", "www.vicenrere.ca", "Software Engineerrr", "Montrèalll", "95 001", "J'adore ingénieur!!!", 1L, LocalDateTime.now(), LocalDateTime.now());
		FormulaireOffreStageDTO formulaireOffreStageDTO5 = new FormulaireOffreStageDTO(5L, "MMM", "Vicente2", "vicen@mail.com", "www.vicen2.ca", "Software Engineer 2", "Montrèal 2", "95 000", "J'adore ingénieur! 2", 1L, LocalDateTime.now(), LocalDateTime.now());
		FormulaireOffreStageDTO formulaireOffreStageDTO6 = new FormulaireOffreStageDTO(6L, "MMMSUUPER", "Vicente3", "vicen@mail.com", "www.vicen3.ca", "Software Engineer 3", "Montrèal 3", "16", "J'adore ingénieur! 3", 1L, LocalDateTime.now(), LocalDateTime.now());
		FormulaireOffreStageDTO formulaireOffreStageDTO7 = new FormulaireOffreStageDTO(7L, "SUUPER", "Vicente", "vicen@mail.com", "www.vicen.ca", "Software Engineer", "Montrèal", "95 000", "J'adore ingénieur!", 1L, LocalDateTime.now(), LocalDateTime.now());
		FormulaireOffreStageDTO formulaireOffreStageDTO8 = new FormulaireOffreStageDTO(8L, "MMM", "Vicente2", "vicen@mail.com", "www.vicen2.ca", "Software Engineer 2", "Montrèal 2", "95 000", "J'adore ingénieur! 2", 1L, LocalDateTime.now(), LocalDateTime.now());
		FormulaireOffreStageDTO formulaireOffreStageDTO9 = new FormulaireOffreStageDTO(9L, "MMMSUUPER", "Vicente3", "vicen@mail.com", "www.vicen3.ca", "Software Engineer 3", "Montrèal 3", "16", "J'adore ingénieur! 3", 1L, LocalDateTime.now(), LocalDateTime.now());
		FormulaireOffreStageDTO formulaireOffreStageDTO10 = new FormulaireOffreStageDTO(10L, "SUUPER", "Vicente", "vicen@mail.com", "www.vicen.ca", "Software Engineer", "Montrèal", "95 000", "J'adore ingénieur!", 1L, LocalDateTime.now(), LocalDateTime.now());
		FormulaireOffreStageDTO formulaireOffreStageDTO11 = new FormulaireOffreStageDTO(11L, "MMM", "Vicente2", "vicen@mail.com", "www.vicen2.ca", "Software Engineer 2", "Montrèal 2", "95 000", "J'adore ingénieur! 2", 1L, LocalDateTime.now(), LocalDateTime.now());
		FormulaireOffreStageDTO formulaireOffreStageDTO12 = new FormulaireOffreStageDTO(12L, "MMMSUUPER", "Vicente3", "vicen@mail.com", "www.vicen3.ca", "Software Engineer 3", "Montrèal 3", "16", "J'adore ingénieur! 3", 1L, LocalDateTime.now(), LocalDateTime.now());
		FormulaireOffreStageDTO formulaireOffreStageDTO13 = new FormulaireOffreStageDTO(13L, "SUUPER", "Vicente", "vicen@mail.com", "www.vicen.ca", "Software Engineer", "Montrèal", "95 000", "J'adore ingénieur!", 1L, LocalDateTime.now(), LocalDateTime.now());
		FormulaireOffreStageDTO formulaireOffreStageDTO14 = new FormulaireOffreStageDTO(14L, "MMM", "Vicente2", "vicen@mail.com", "www.vicen2.ca", "Software Engineer 2", "Montrèal 2", "95 000", "J'adore ingénieur! 2", 1L, LocalDateTime.now(), LocalDateTime.now());
		FormulaireOffreStageDTO formulaireOffreStageDTO15 = new FormulaireOffreStageDTO(15L, "MMMSUUPER", "Vicente3", "vicen@mail.com", "www.vicen3.ca", "Software Engineer 3", "Montrèal 3", "16", "J'adore ingénieur! 3", 1L, LocalDateTime.now(), LocalDateTime.now());
		FormulaireOffreStageDTO formulaireOffreStageDTO16 = new FormulaireOffreStageDTO(16L, "SUUPER", "Vicente", "vicen@mail.com", "www.vicen.ca", "Software Engineer", "Montrèal", "95 000", "J'adore ingénieur!", 1L, LocalDateTime.now(), LocalDateTime.now());
		FormulaireOffreStageDTO formulaireOffreStageDTO17 = new FormulaireOffreStageDTO(17L, "MMM", "Vicente2", "vicen@mail.com", "www.vicen2.ca", "Software Engineer 2", "Montrèal 2", "95 000", "J'adore ingénieur! 2", 1L, LocalDateTime.now(), LocalDateTime.now());
		FormulaireOffreStageDTO formulaireOffreStageDTO18 = new FormulaireOffreStageDTO(18L, "SUUPER", "Vicente", "vicen@mail.com", "www.vicen.ca", "Software Engineer", "Montrèal", "95 000", "J'adore ingénieur!", 1L, LocalDateTime.now(), LocalDateTime.now());
		FormulaireOffreStageDTO formulaireOffreStageDTO19 = new FormulaireOffreStageDTO(19L, "MMM", "Vicente2", "vicen@mail.com", "www.vicen2.ca", "Software Engineer 2", "Montrèal 2", "95 000", "J'adore ingénieur! 2", 1L, LocalDateTime.now(), LocalDateTime.now());
		FormulaireOffreStageDTO formulaireOffreStageDTO20 = new FormulaireOffreStageDTO(20L, "MMMSUUPER", "Vicente3", "vicen@mail.com", "www.vicen3.ca", "Software Engineer 3", "Montrèal 3", "16", "J'adore ingénieur! 3", 1L, LocalDateTime.now(), LocalDateTime.now());
		FormulaireOffreStageDTO formulaireOffreStageDTO21 = new FormulaireOffreStageDTO(21L, "SUUPER", "Vicente", "vicen@mail.com", "www.vicen.ca", "Software Engineer", "Montrèal", "95 000", "J'adore ingénieur!", 1L, LocalDateTime.now(), LocalDateTime.now());
		FormulaireOffreStageDTO formulaireOffreStageDTO22 = new FormulaireOffreStageDTO(22L, "MMM", "Vicente2", "vicen@mail.com", "www.vicen2.ca", "Software Engineer 2", "Montrèal 2", "95 000", "J'adore ingénieur! 2", 1L, LocalDateTime.now(), LocalDateTime.now());

		offreStageService.saveForm(formulaireOffreStageDTO);
		offreStageService.saveForm(formulaireOffreStageDTO2);
		offreStageService.saveForm(formulaireOffreStageDTO3);
		offreStageService.saveForm(formulaireOffreStageDTO4);
		offreStageService.saveForm(formulaireOffreStageDTO5);
		offreStageService.saveForm(formulaireOffreStageDTO6);
		offreStageService.saveForm(formulaireOffreStageDTO7);
		offreStageService.saveForm(formulaireOffreStageDTO8);
		offreStageService.saveForm(formulaireOffreStageDTO9);
		offreStageService.saveForm(formulaireOffreStageDTO10);
		offreStageService.saveForm(formulaireOffreStageDTO11);
		offreStageService.saveForm(formulaireOffreStageDTO12);
		offreStageService.saveForm(formulaireOffreStageDTO13);
		offreStageService.saveForm(formulaireOffreStageDTO14);
		offreStageService.saveForm(formulaireOffreStageDTO15);
		offreStageService.saveForm(formulaireOffreStageDTO16);
		offreStageService.saveForm(formulaireOffreStageDTO17);
		offreStageService.saveForm(formulaireOffreStageDTO18);
		offreStageService.saveForm(formulaireOffreStageDTO19);
		offreStageService.saveForm(formulaireOffreStageDTO20);
		offreStageService.saveForm(formulaireOffreStageDTO21);
		offreStageService.saveForm(formulaireOffreStageDTO22);

//		MultipartFile fichierOffreStage = ("test.pdf", "test.pdf", "application/pdf", "contenu".getBytes());
//		offreStageService.saveFile(fichierOffreStage);
		etudiantService.creationDeCompte("Roberto", "Berrios", "273-389-2937", "roby@gmail.com", "Roby123$", Programme.TECHNIQUE_INFORMATIQUE);
		//enseignantService.creationDeCompte("Guillaume", "Courtemanche", "283-948-2738", "courtebite@gmail.com", "courte123$");
		etudiantService.creationDeCompte("Guillaume", "Courtemanche", "283-948-2738", "gc@gmail.com", "courte123$", Programme.GENIE_LOGICIEL);

		offreStageService.assignerOffre(2L, 1L);
		offreStageService.assignerOffre(3L, 3L);
		offreStageService.assignerOffre(2L, 4L);
	}
}