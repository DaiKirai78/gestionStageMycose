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

	public static void main(String[] args) {
		SpringApplication.run(MycoseApplication.class, args);
		System.out.println("Hello world!");
	}

	@Override
	public void run(String... args) {
	}
}
