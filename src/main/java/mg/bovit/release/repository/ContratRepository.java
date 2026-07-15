package mg.bovit.release.repository;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mg.bovit.release.model.*;

@Repository
public interface ContratRepository extends JpaRepository<Contrat, Long> {

    List<Contrat> findByEmployee(Employee employee);

    // Tous les contrats d'un employé, du plus récent (date_debut) au plus ancien
    List<Contrat> findByEmployeeOrderByDateDebutDesc(Employee employee);

    // Tous les contrats du plus ancien au plus récent (pour traitement chronologique)
    List<Contrat> findByEmployeeOrderByDateDebutAsc(Employee employee);

    Optional<Contrat> findByEmployeeAndDateDebut(Employee employee, Date dateDebut);
}