package com.projet.mycose.repository;

import com.projet.mycose.modele.ApplicationStage;
import com.projet.mycose.modele.Etudiant;
import com.projet.mycose.modele.FichierCV;
import com.projet.mycose.modele.Programme;
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


    List<Etudiant> findAllByProgramme(@Param("programme") Programme programme);

    List<Etudiant> findEtudiantsByContractStatusEquals(Etudiant.ContractStatus contractStatus);

    int countByContractStatusEquals(Etudiant.ContractStatus contractStatus);

    List<Etudiant> findAllByFichiersCVIsEmpty();

    @Query("SELECT DISTINCT e FROM Etudiant e JOIN e.fichiersCV f WHERE f.status = :status")
    List<Etudiant> findEtudiantsWithFichierCVStatus(@Param("status") FichierCV.Status status);

    //J'assume que la query doit retourner toutes les personnes qui ont pas le status spécifié, même si elles n'ont aucune application
    @Query("SELECT DISTINCT e FROM Etudiant e LEFT OUTER JOIN e.applications a WHERE (a.status != :status OR a.id IS NULL)")
    List<Etudiant> findEtudiantsWithApplicationStatusNotEquals(@Param("status") ApplicationStage.ApplicationStatus status);

    @Query("SELECT DISTINCT e FROM Etudiant e JOIN e.applications a WHERE a.status = :status")
    List<Etudiant> findEtudiantsWithApplicationStatusEquals(@Param("status") ApplicationStage.ApplicationStatus status);
}
