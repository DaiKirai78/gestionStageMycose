package com.projet.mycose.repository;

import com.projet.mycose.modele.Etudiant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EtudiantRepository extends JpaRepository<Etudiant, Long> {
    Optional<Etudiant> findEtudiantByCourriel(String courriel);
    Optional<Etudiant> findEtudiantByNumeroDeTelephone(String numero);
}
