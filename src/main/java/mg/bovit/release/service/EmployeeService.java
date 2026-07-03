package mg.bovit.release.service;

import mg.bovit.release.dto.EmployeeContratDTO;
import mg.bovit.release.model.Contrat;
import mg.bovit.release.model.Employee;
import mg.bovit.release.repository.ContratRepository;
import mg.bovit.release.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import java.time.LocalDate;
import java.time.Period;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ContratRepository contratRepository;

    @Transactional
    public void saveEmployeeWithContrat(EmployeeContratDTO dto) {
        // 1. Instanciation et sauvegarde de l'employé
        if (dto.getDateNaissance() == null) {
                throw new RuntimeException("La date de naissance est obligatoire.");
            }
            
            // Vérification de la majorité (18 ans) à la date d'entrée ou à la date actuelle
            LocalDate naissance = dto.getDateNaissance().toLocalDate();
            LocalDate entree = (dto.getDateEntree() != null) ? dto.getDateEntree().toLocalDate() : LocalDate.now();
            
            if (Period.between(naissance, entree).getYears() < 18) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "L'employé n'aura pas 18 ans à sa date d'entrée officielle (" + entree + ").");
            }
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