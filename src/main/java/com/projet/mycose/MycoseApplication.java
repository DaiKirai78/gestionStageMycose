package com.projet.mycose;

import com.projet.mycose.modele.Etudiant;
import com.projet.mycose.service.EtudiantService;
import com.projet.mycose.service.dto.EtudiantDTO;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MycoseApplication implements CommandLineRunner {
	private final EtudiantService etudiantService;

    public MycoseApplication(EtudiantService etudiantService) {
        this.etudiantService = etudiantService;
    }

    public static void main(String[] args) {
		SpringApplication.run(MycoseApplication.class, args);
	}

	@Override
	public void run(String... args) {
		//Etudiant etudiant2 = new Etudiant("Karim", "Mihoubi", "4385372039", "mihoubi@gmail.com", "Mimi123$");
		//etudiantService.creationDeCompte(etudiant2.getPrenom(), etudiant2.getNom(), etudiant2.getNumeroDeTelephone(), etudiant2.getCourriel(), etudiant2.getMotDePasse());
	}
}
