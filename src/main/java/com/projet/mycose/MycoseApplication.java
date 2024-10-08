package com.projet.mycose;

import com.projet.mycose.modele.Etudiant;
import com.projet.mycose.modele.Programme;
import com.projet.mycose.repository.UtilisateurRepository;
import com.projet.mycose.service.EnseignantService;
import com.projet.mycose.service.EmployeurService;
import com.projet.mycose.service.EtudiantService;
import com.projet.mycose.service.GestionnaireStageService;
import com.projet.mycose.service.dto.EtudiantDTO;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MycoseApplication implements CommandLineRunner {

	private final EtudiantService etudiantService;
	private final GestionnaireStageService gestionnaireStageService;
	private final UtilisateurRepository utilisateurRepository;

	public MycoseApplication(EtudiantService etudiantService, GestionnaireStageService gestionnaireStageService, UtilisateurRepository utilisateurRepository) {
        this.etudiantService = etudiantService;
		this.gestionnaireStageService = gestionnaireStageService;
		this.utilisateurRepository = utilisateurRepository;
	}

    public static void main(String[] args) {
		SpringApplication.run(MycoseApplication.class, args);
		System.out.println("Hello world!");
	}

	@Override
	public void run(String... args) {
		//enseignantService.creationDeCompte("Guillaume", "Courtemanche", "283-948-2738", "courtemanche@gmail.com", "courte123$");
		etudiantService.creationDeCompte("Roberto", "Berrios", "273-389-2937", "roby@gmail.com", "Roby123$", Programme.TECHNIQUE_INFORMATIQUE);
		//enseignantService.creationDeCompte("Guillaume", "Courtemanche", "283-948-2738", "courtebite@gmail.com", "courte123$");
		etudiantService.creationDeCompte("Guillaume", "Courtemanche", "283-948-2738", "gc@gmail.com", "courte123$", Programme.GENIE_LOGICIEL);
		//System.out.println(utilisateurRepository.findUtilisateurByCourriel("eliescrummaster@gmail.com"));
		gestionnaireStageService.creationDeCompte("Elie", "Boucher-Gendron", "450-948-2738", "eliescrummaster@gmail.com", "Passw0rd");
	}
}
