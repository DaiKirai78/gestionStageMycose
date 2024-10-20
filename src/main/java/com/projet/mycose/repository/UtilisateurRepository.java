package com.projet.mycose.repository;

import com.projet.mycose.dto.EtudiantDTO;
import com.projet.mycose.modele.Etudiant;
import com.projet.mycose.modele.Utilisateur;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {
    @Query("""
        select u from Utilisateur u where trim(lower(u.credentials.email)) = :courriel
    """)
    Optional<Utilisateur> findUtilisateurByCourriel(@Param("courriel") String courriel);

    Optional<Utilisateur> findUtilisateurByNumeroDeTelephone(String numero);

    Optional<Utilisateur> findUtilisateurById(Long id);

    @Query("SELECT e FROM Etudiant e " +
            "WHERE e.enseignantAssignee IS NULL " +
            "AND e.credentials.role = 'ETUDIANT'")
    Page<Etudiant> findAllEtudiantsSansEnseignants(Pageable pageable);
}
