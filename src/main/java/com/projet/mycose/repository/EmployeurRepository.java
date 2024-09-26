package com.projet.mycose.repository;

import com.projet.mycose.modele.Employeur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeurRepository extends JpaRepository<Employeur, Long> {
    @Query("""
        select e from Employeur e where trim(lower(e.credentials.email)) = :courriel
    """)
    Optional<Employeur> findEmployeurByCourriel(@Param("courriel") String courriel);
    @Query("""
        select e from Employeur e where trim(e.numeroDeTelephone) = :numero
    """)
    Optional<Employeur> findEmployeurByNumeroDeTelephone(@Param("numero") String numero);
}
