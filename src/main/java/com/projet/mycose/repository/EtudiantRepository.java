package com.projet.mycose.repository;

import com.projet.mycose.modele.Etudiant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EtudiantRepository extends JpaRepository<Etudiant, Long> {

    @Query("""
        select e from Etudiant e where trim(lower(e.credentials.email)) = :courriel
    """)
    Optional<Etudiant> findEtudiantByCourriel(@Param("courriel") String courriel);
    @Query("""
        select e from Etudiant e where trim(e.numeroDeTelephone) = :numero
    """)
    Optional<Etudiant> findEtudiantByNumeroDeTelephone(@Param("numero") String numero);


    Etudiant findEtudiantById(Long id);
}
