package com.projet.mycose.repository;

import com.projet.mycose.dto.EnseignantDTO;
import com.projet.mycose.dto.EtudiantDTO;
import com.projet.mycose.modele.Enseignant;
import com.projet.mycose.modele.Etudiant;
import com.projet.mycose.modele.Programme;
import com.projet.mycose.modele.Utilisateur;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
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
            "AND e.credentials.role = 'ETUDIANT' " +
            "AND e.programme = :programme")
    Page<Etudiant> findAllEtudiantsSansEnseignants(Programme programme, Pageable pageable);


    @Query("SELECT count(e) FROM Etudiant e " +
            "WHERE e.enseignantAssignee IS NULL " +
            "AND e.credentials.role = 'ETUDIANT' " +
            "AND e.programme = :programme")
    int countAllEtudiantsSansEnseignants(Programme programme);

    @Query("""
        SELECT e FROM Enseignant e 
        WHERE LOWER(e.nom) LIKE CONCAT('%', LOWER(:searchValue), '%') 
        OR LOWER(e.prenom) LIKE CONCAT('%', LOWER(:searchValue), '%') 
        OR LOWER(e.credentials.email) LIKE CONCAT('%', LOWER(:searchValue), '%')
    """)
    List<Enseignant> findAllEnseignantsBySearch(String searchValue);
}
