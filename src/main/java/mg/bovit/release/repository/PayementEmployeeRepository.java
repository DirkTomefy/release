package mg.bovit.release.repository;

import java.sql.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mg.bovit.release.model.Employee;
import mg.bovit.release.model.PayementEmployee;
import mg.bovit.release.model.TypePayementEmployee;

@Repository
public interface PayementEmployeeRepository extends JpaRepository<PayementEmployee, Long> {

    List<PayementEmployee> findByEmployee(Employee employee);

    // Tous les paiements (quel que soit le type) d'un employé pour un mois donné
    List<PayementEmployee> findByEmployeeAndMois(Employee employee, Date mois);

    // Utilisé pour la règle "un salaire ne peut être payé qu'une seule fois par mois"
    boolean existsByEmployeeAndTypePayementEmployeeAndMois(
            Employee employee,
            TypePayementEmployee typePayementEmployee,
            Date mois
    );
}
