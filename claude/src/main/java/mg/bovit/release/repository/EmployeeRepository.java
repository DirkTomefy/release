package mg.bovit.release.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mg.bovit.release.model.*;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
     Optional<Employee> findByNomAndPrenom(String nom, String prenom);
}