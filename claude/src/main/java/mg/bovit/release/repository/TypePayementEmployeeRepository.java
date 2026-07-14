package mg.bovit.release.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mg.bovit.release.model.*;

@Repository
public interface TypePayementEmployeeRepository extends JpaRepository<TypePayementEmployee, Long> {
    Optional<TypePayementEmployee> findByLibelle(String libelle);
}