package mg.bovit.release.service;

import mg.bovit.release.model.Contrat;
import mg.bovit.release.model.Employee;
import mg.bovit.release.repository.ContratRepository;
import mg.bovit.release.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;

@Service
public class ContratService {

    @Autowired
    private ContratRepository contratRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Transactional
    public void saveContratWithEmployee(Contrat contrat, Long employeeId) {
        // Trouver l'employé lié
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("Employé introuvable avec l'ID : " + employeeId));
        
        // Mettre à jour les métadonnées du contrat
        contrat.setEmployee(employee);
        contrat.setDateCreation(new Timestamp(System.currentTimeMillis()));
        
        // Sauvegarde
        contratRepository.save(contrat);
    }
}