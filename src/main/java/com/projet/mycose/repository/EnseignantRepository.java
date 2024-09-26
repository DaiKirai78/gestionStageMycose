package com.projet.mycose.repository;

import com.projet.mycose.modele.Enseignant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EnseignantRepository extends JpaRepository<Enseignant, Long> {
    @Query("""
        select e from Enseignant e where trim(lower(e.credentials.email)) = :courriel
    """)
    Optional<Enseignant> findEnseignantByCourriel(@Param("courriel") String courriel);
    @Query("""
        select e from Enseignant e where trim(e.numeroDeTelephone) = :numero
    """)
    Optional<Enseignant> findEnseignantByNumeroDeTelephone(@Param("numero") String numero);
}
