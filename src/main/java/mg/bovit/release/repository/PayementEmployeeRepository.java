package mg.bovit.release.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mg.bovit.release.model.Employee;
import mg.bovit.release.model.PayementEmployee;

@Repository
public interface PayementEmployeeRepository extends JpaRepository<PayementEmployee, Long> {
    List<PayementEmployee> findByEmployee(Employee employee);
}