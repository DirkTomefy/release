package mg.bovit.release.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import mg.bovit.release.model.Contrat;
import mg.bovit.release.model.Employee;
import mg.bovit.release.repository.ContratRepository;
import mg.bovit.release.repository.EmployeeRepository;

@ExtendWith(MockitoExtension.class)
class PayementEmployeeServiceTest {

    @Mock
    private ContratRepository contratRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private ContratService contratService;

    @Test
    void shouldProrateSalaryForPartialMonth() {
        Contrat contrat = new Contrat();
        contrat.setDateDebut(Date.valueOf("2025-02-15"));
        contrat.setDateFin(Date.valueOf("2025-04-23"));
        contrat.setSalaire(new BigDecimal("300000"));

        PayementEmployeeService service = new PayementEmployeeService();

        BigDecimal montant = service.calculerSalairePourMois(contrat, YearMonth.of(2025, 2));

        assertEquals(new BigDecimal("150000.00"), montant);
    }

    @Test
    void shouldRejectOverlappingContractForSameEmployee() {
        Employee employee = new Employee();
        employee.setId(1L);
        employee.setDateEntree(Date.valueOf("2025-01-01"));

        Contrat existing = new Contrat();
        existing.setDateDebut(Date.valueOf("2025-02-01"));
        existing.setDateFin(Date.valueOf("2025-02-20"));
        existing.setEmployee(employee);

        Contrat nouveau = new Contrat();
        nouveau.setDateDebut(Date.valueOf("2025-02-10"));
        nouveau.setDateFin(Date.valueOf("2025-03-10"));

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(contratRepository.findByEmployee(employee)).thenReturn(List.of(existing));

        assertThrows(IllegalArgumentException.class, () -> contratService.saveContratWithEmployee(nouveau, 1L));
    }
}
