package mg.bovit.release.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mg.bovit.release.model.*;

@Repository
public interface ContratRepository extends JpaRepository<Contrat, Long> {

    List<Contrat> findByEmployee(Employee employee);

    // Tous les contrats d'un employé, du plus récent (date_debut) au plus ancien
    List<Contrat> findByEmployeeOrderByDateDebutDesc(Employee employee);
}
