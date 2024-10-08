package com.projet.mycose;

import com.projet.mycose.modele.Etudiant;
import com.projet.mycose.modele.FichierOffreStage;
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

		FormulaireOffreStageDTO formulaireOffreStageDTO = new FormulaireOffreStageDTO(1L, "SUUPER", "Vicente", "vicen@mail.com", "www.vicen.ca", "Software Engineer", "Montrèal", "95 000", "J'adore ingénieur!", 1L);
		FormulaireOffreStageDTO formulaireOffreStageDTO2 = new FormulaireOffreStageDTO(2L, "MMM", "Vicente2", "vicen@mail.com", "www.vicen2.ca", "Software Engineer 2", "Montrèal 2", "95 000", "J'adore ingénieur! 2", 1L);
		FormulaireOffreStageDTO formulaireOffreStageDTO3 = new FormulaireOffreStageDTO(3L, "MMMSUUPER", "Vicente3", "vicen@mail.com", "www.vicen3.ca", "Software Engineer 3", "Montrèal 3", "16", "J'adore ingénieur! 3", 1L);
		offreStageService.saveForm(formulaireOffreStageDTO);
		offreStageService.saveForm(formulaireOffreStageDTO2);
		offreStageService.saveForm(formulaireOffreStageDTO3);


//		MultipartFile fichierOffreStage = ("test.pdf", "test.pdf", "application/pdf", "contenu".getBytes());
//		offreStageService.saveFile(fichierOffreStage);
		etudiantService.creationDeCompte("Roberto", "Berrios", "273-389-2937", "roby@gmail.com", "Roby123$", Programme.TECHNIQUE_INFORMATIQUE);
		//enseignantService.creationDeCompte("Guillaume", "Courtemanche", "283-948-2738", "courtebite@gmail.com", "courte123$");
		etudiantService.creationDeCompte("Guillaume", "Courtemanche", "283-948-2738", "gc@gmail.com", "courte123$", Programme.GENIE_LOGICIEL);

		offreStageService.assignerOffre(2L, 1L);
		offreStageService.assignerOffre(3L, 3L);
	}
}
