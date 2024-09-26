package com.projet.mycose;

import com.projet.mycose.modele.Etudiant;
import com.projet.mycose.service.EmployeurService;
import com.projet.mycose.service.EtudiantService;
import com.projet.mycose.service.dto.EtudiantDTO;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MycoseApplication implements CommandLineRunner {
	private final EtudiantService etudiantService;
	private final EmployeurService employeurService;

    public MycoseApplication(EtudiantService etudiantService, EmployeurService employeurService) {
        this.etudiantService = etudiantService;
        this.employeurService = employeurService;
    }

    public static void main(String[] args) {
		SpringApplication.run(MycoseApplication.class, args);
	}

	@Override
	public void run(String... args) {
		employeurService.creationDeCompte("Michel", "Généreux", "282-282-2828", "gege23@gmail.com", "GeGe123$", "Couche-Tard");
	}
}
