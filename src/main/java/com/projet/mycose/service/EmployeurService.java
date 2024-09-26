package com.projet.mycose.service;

import com.projet.mycose.modele.Employeur;
import com.projet.mycose.repository.EmployeurRepository;
import com.projet.mycose.service.dto.EmployeurDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmployeurService {
    private final EmployeurRepository employeurRepository;
    private final PasswordEncoder passwordEncoder;

    public EmployeurDTO creationDeCompte(String prenom, String nom, String numeroTelephone, String courriel, String motDePasse, String nomOrganisation) {
        if (!credentialsDejaPris(courriel, numeroTelephone))
            return EmployeurDTO.toDTO(employeurRepository.save(new Employeur(prenom, nom, numeroTelephone, courriel, passwordEncoder.encode(motDePasse), nomOrganisation)));
        else
            return null;
    }

    public EmployeurDTO getEmployeurByCourriel(String courriel) {
        Optional<Employeur> optionalEmployeur = employeurRepository.findEmployeurByCourriel(courriel);
        return optionalEmployeur.map(EmployeurDTO::toDTO).orElse(null);
    }

    public EmployeurDTO getEmployeurByTelephone(String numero) {
        Optional<Employeur> optionalEmployeur = employeurRepository.findEmployeurByNumeroDeTelephone(numero);
        return optionalEmployeur.map(EmployeurDTO::toDTO).orElse(null);
    }

    public boolean credentialsDejaPris(String courriel, String numero) {
        return getEmployeurByCourriel(courriel) != null || getEmployeurByTelephone(numero) != null;
    }
}
