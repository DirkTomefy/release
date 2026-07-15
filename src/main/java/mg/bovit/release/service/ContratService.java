package mg.bovit.release.service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mg.bovit.release.model.Contrat;
import mg.bovit.release.model.Employee;
import mg.bovit.release.repository.ContratRepository;
import mg.bovit.release.repository.EmployeeRepository;

@Service
public class ContratService {

    @Autowired
    private ContratRepository contratRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    public List<Contrat> findAllContrats() {
        return contratRepository.findAll();
    }

    @Transactional
    public void saveContratWithEmployee(Contrat contrat, Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("Employé introuvable avec l'ID : " + employeeId));

        if (contrat.getDateDebut() == null) {
            throw new IllegalArgumentException("La date de début du contrat est obligatoire.");
        }
        LocalDate dateDebut = contrat.getDateDebut().toLocalDate();
        if (employee.getDateEntree() == null) {
            throw new IllegalArgumentException("La date d'entrée de l'employé est obligatoire.");
        }
        LocalDate dateEntree = employee.getDateEntree().toLocalDate();
        if (dateDebut.isBefore(dateEntree)) {
            throw new IllegalArgumentException("Le contrat ne peut pas commencer avant la date d'entrée de l'employé.");
        }

        List<Contrat> contratsExistants = contratRepository.findByEmployee(employee);
        if (contratsExistants.stream().anyMatch(existing -> chevauche(contrat, existing))) {
            throw new IllegalArgumentException("Ce contrat chevauche un autre contrat déjà enregistré pour cet employé.");
        }

        contrat.setEmployee(employee);
        contrat.setDateCreation(new Timestamp(System.currentTimeMillis()));
        contratRepository.save(contrat);
    }

    private boolean chevauche(Contrat nouveau, Contrat existant) {
        LocalDate debutNouveau = nouveau.getDateDebut().toLocalDate();
        LocalDate finNouveau = nouveau.getDateFin() != null ? nouveau.getDateFin().toLocalDate() : null;
        LocalDate debutExistant = existant.getDateDebut().toLocalDate();
        LocalDate finExistant = existant.getDateFin() != null ? existant.getDateFin().toLocalDate() : null;

        LocalDate finNouveauEffective = finNouveau != null ? finNouveau : LocalDate.MAX;
        LocalDate finExistantEffective = finExistant != null ? finExistant : LocalDate.MAX;

        return !debutNouveau.isAfter(finExistantEffective) && !debutExistant.isAfter(finNouveauEffective);
    }

    
}