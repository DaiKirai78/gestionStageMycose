package com.projet.mycose.service;

import com.projet.mycose.modele.Etudiant;
import com.projet.mycose.repository.EtudiantRepository;
import com.projet.mycose.service.dto.EtudiantDTO;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.projet.mycose.service.dto.EtudiantDTO.toDTO;

@Service
public class EtudiantService {

    private final EtudiantRepository etudiantRepository;
    private final PasswordEncoder passwordEncoder;

    public EtudiantService(EtudiantRepository etudiantRepository, PasswordEncoder passwordEncoder) {
        this.etudiantRepository = etudiantRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public EtudiantDTO creationDeCompte(String prenom, String nom, String numeroTelephone, String courriel, String motDePasse) {
        if (prenom == null || prenom.isEmpty() || !prenom.matches("[a-zA-Z -'éÉàÀ]"))
            throw new IllegalArgumentException("Le prénom de l'utilisateur est invalide");
        if (nom == null || nom.isEmpty() || !nom.matches("[a-zA-Z -'éÉàÀ]"))
            throw new IllegalArgumentException("Le nom de l'utilisateur est invalide");
        if (numeroTelephone == null || numeroTelephone.isEmpty() || !numeroTelephone.matches("[0-9]{3}-? ?[0-9]{3}-? ?[0-9]{4}"))
            throw new IllegalArgumentException("Le numéro de téléphone de l'utilisateur est invalide");
        if (courriel == null || courriel.isEmpty() || !courriel.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"))
            throw new IllegalArgumentException("Le courriel de l'utilisateur est invalide");
        if (motDePasse == null || motDePasse.isEmpty() || !motDePasse.matches("[a-zA-Z0-9$&+,:;=?@#|'<>.^*()%!-]{8,}"))
            throw new IllegalArgumentException("Le mot de passe de l'utilisateur est invalide");

        return toDTO(etudiantRepository.save(new Etudiant(prenom, nom, numeroTelephone, courriel,  passwordEncoder.encode(motDePasse))));
    }

    public EtudiantDTO getEtudiantByCourriel(String courriel) {
        Optional<Etudiant> optionalEtudiant = etudiantRepository.findEtudiantByCourriel(courriel);
        return optionalEtudiant.map(EtudiantDTO::toDTO).orElse(null);
    }

    public EtudiantDTO getEtudiantByTelephone(String numero) {
        Optional<Etudiant> optionalEtudiant = etudiantRepository.findEtudiantByNumeroDeTelephone(numero);
        return optionalEtudiant.map(EtudiantDTO::toDTO).orElse(null);
    }

    public boolean credentialsDejaPris(String courriel, String numero) {
        return getEtudiantByCourriel(courriel) != null || getEtudiantByTelephone(numero) != null;
    }
}