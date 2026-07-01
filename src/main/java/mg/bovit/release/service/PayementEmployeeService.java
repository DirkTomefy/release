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

    /**
     * Retourne les mois pour lesquels un employé aurait dû être payé,
     * depuis son mois d'entrée jusqu'au mois précédent (mois en cours exclu).
     */
    public List<YearMonth> getUnpaidMonths(Employee employee) {
        YearMonth premierMoisDu = getExpectedMonth(employee);
        YearMonth dernierMoisDu = getExpectedEndMonth(); // borne exclusive

        List<YearMonth> moisAttendus = generateMonthsBetween(premierMoisDu, dernierMoisDu);
        Set<YearMonth> moisPayes = getPaidMonths(employee);

        return moisAttendus.stream()
                .filter(mois -> !moisPayes.contains(mois))
                .toList();
    }

    /** Premier mois où l'employé doit être payé : son mois d'entrée. */
    private YearMonth getExpectedMonth(Employee employee) {
        return YearMonth.from(employee.getDateEntree().toLocalDate());
    }

    /** Borne exclusive : on ne réclame jamais le mois en cours. */
    private YearMonth getExpectedEndMonth() {
        return YearMonth.now();
    }

    /** Liste tous les mois entre deux bornes (fin exclue). */
    private List<YearMonth> generateMonthsBetween(YearMonth debut, YearMonth finExclusive) {
        List<YearMonth> mois = new ArrayList<>();
        YearMonth courant = debut;
        while (courant.isBefore(finExclusive)) {
            mois.add(courant);
            courant = courant.plusMonths(1);
        }
        return mois;
    }

    /** Règle "payé" : au moins un paiement enregistré pour ce mois. */
    private Set<YearMonth> getPaidMonths(Employee employee) {
        return payementEmployeeRepository.findByEmployee(employee).stream()
                .map(p -> YearMonth.from(p.getDatePayement().toLocalDateTime()))
                .collect(Collectors.toSet());
    }
}
