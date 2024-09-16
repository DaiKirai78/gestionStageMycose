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
		Etudiant etudiant = new Etudiant("Roberto", "Berrios", "robyking@gmail.com", "RobyKing123", "438-502-8263");
		etudiantService.creationDeCompte(EtudiantDTO.toDTO(etudiant));
	}
}
