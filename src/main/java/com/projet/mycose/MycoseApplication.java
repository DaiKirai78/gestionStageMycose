package com.projet.mycose;

import com.projet.mycose.modele.Programme;
import com.projet.mycose.service.EnseignantService;
import com.projet.mycose.service.EtudiantService;
import com.projet.mycose.service.FichierCVService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MycoseApplication implements CommandLineRunner {

    public MycoseApplication(EnseignantService enseignantService, EtudiantService etudiantService, FichierCVService fichierCVService) {
        this.enseignantService = enseignantService;
        this.etudiantService = etudiantService;
        this.fichierCVService = fichierCVService;
    }

    public static void main(String[] args) {
		SpringApplication.run(MycoseApplication.class, args);
		System.out.println("Hello world!");
	}
	private final EnseignantService enseignantService;
	private final EtudiantService etudiantService;
	private final FichierCVService fichierCVService;
	@Override
	public void run(String... args) {
		//enseignantService.creationDeCompte("Guillaume", "Courtemanche", "283-948-2738", "courtemanche@gmail.com", "courte123$");
		etudiantService.creationDeCompte("Roberto", "Berrios", "273-389-2937", "roby@gmail.com", "Roby123$", Programme.TECHNIQUE_INFORMATIQUE);
		//enseignantService.creationDeCompte("Guillaume", "Courtemanche", "283-948-2738", "courtebite@gmail.com", "courte123$");
		etudiantService.creationDeCompte("Guillaume", "Courtemanche", "283-948-2738", "gc@gmail.com", "courte123$", Programme.GENIE_LOGICIEL);

		System.out.println(fichierCVService.getCurrentCV_returnNullIfEmpty(1L));
		System.out.println(fichierCVService.getCurrentCV_returnNullIfEmpty(2L));
	}
}
