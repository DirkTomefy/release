package mg.bovit.release.service;

import mg.bovit.release.dto.EmployeeContratDTO;
import mg.bovit.release.model.Contrat;
import mg.bovit.release.model.Employee;
import mg.bovit.release.repository.ContratRepository;
import mg.bovit.release.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ContratRepository contratRepository;

    @Transactional
    public void saveEmployeeWithContrat(EmployeeContratDTO dto) {
        // 1. Instanciation et sauvegarde de l'employé
        Employee employee = new Employee();
        employee.setNom(dto.getNom());
        employee.setPrenom(dto.getPrenom());
        employee.setDateNaissance(dto.getDateNaissance());
        employee.setDateEntree(dto.getDateEntree());
        
        Employee savedEmployee = employeeRepository.save(employee);

        // 2. Instanciation et sauvegarde du premier contrat lié
        Contrat contrat = new Contrat();
        contrat.setDateDebut(dto.getDateDebut());
        contrat.setDateFin(dto.getDateFin());
        contrat.setSalaire(dto.getSalaire());
        contrat.setDateCreation(new Timestamp(System.currentTimeMillis()));
        contrat.setEmployee(savedEmployee); // Liaison avec l'employé venant d'être généré

        contratRepository.save(contrat);
    }
    public List<Employee> findAllEmployees() {
    return employeeRepository.findAll();
    }
}