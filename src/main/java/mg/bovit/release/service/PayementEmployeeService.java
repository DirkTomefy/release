package mg.bovit.release.service;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import mg.bovit.release.model.Employee;
import mg.bovit.release.repository.PayementEmployeeRepository;

public class PayementEmployeeService {
    @Autowired
    private PayementEmployeeRepository payementEmployeeRepository;
    
    public List<YearMonth> getMoisImpayes(Employee employee) {
    YearMonth debut = YearMonth.from(employee.getDateEntree().toLocalDate());
    YearMonth moisActuel = YearMonth.now();

    // Rien à vérifier si l'employé est entré ce mois-ci ou après
    if (!debut.isBefore(moisActuel)) {
        return List.of();
    }

    Set<YearMonth> moisPayes = payementEmployeeRepository.findByEmployee(employee)
            .stream()
            .map(p -> YearMonth.from(p.getDatePayement().toLocalDateTime()))
            .collect(Collectors.toSet());

    List<YearMonth> moisImpayes = new ArrayList<>();
    for (YearMonth mois = debut; mois.isBefore(moisActuel); mois = mois.plusMonths(1)) {
        if (!moisPayes.contains(mois)) {
            moisImpayes.add(mois);
        }
    }
    return moisImpayes;
}
}
