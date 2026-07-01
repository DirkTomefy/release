package mg.bovit.release.service;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mg.bovit.release.dto.PayementDTO;
import mg.bovit.release.model.*;
import mg.bovit.release.repository.*;

@Service
public class PayementEmployeeService {

    @Autowired
    private PayementEmployeeRepository payementEmployeeRepository;
    
    @Autowired
    private EmployeeRepository employeeRepository;
    
    @Autowired
    private TypePayementEmployeeRepository typePayementEmployeeRepository;
    
    @Autowired
    private CaisseRepository caisseRepository;
    
    @Autowired
    private MvtCaisseRepository mvtCaisseRepository;

    /**
     * Effectue le paiement d'un employé, gère la caisse et génère les mouvements de caisse.
     */
    @Transactional
    public String preciterPaiement(PayementDTO dto) {
        Employee emp = employeeRepository.findById(dto.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Employé introuvable"));
        Caisse caisse = caisseRepository.findById(dto.getCaisseId())
                .orElseThrow(() -> new RuntimeException("Caisse introuvable"));
        TypePayementEmployee type = typePayementEmployeeRepository.findById(dto.getTypePayementId())
                .orElseThrow(() -> new RuntimeException("Type de paiement introuvable"));

        // 1. Vérifier si le mois est déjà payé (uniquement pour le type Salaire, pour ne pas bloquer les avances multiples)
        if (type.getLibelle().equalsIgnoreCase("Salaire") || type.getId() == 1) {
            boolean dejaPaye = payementEmployeeRepository.findByEmployee(emp).stream()
                    .anyMatch(p -> p.getTypePayementEmployee().getId() == type.getId() 
                            && p.getDatePayement().toLocalDateTime().toString().contains(dto.getMois())); 
            if (dejaPaye) {
                return "skipped";
            }
        }

        // 2. Vérifier qu'il n'y ait pas de valeur négative dans la caisse
        double montantDouble = dto.getMontant().doubleValue();
        if (caisse.getMontant_actuelle() < montantDouble) {
            throw new RuntimeException("Solde insuffisant dans la caisse : " + caisse.getLibelle());
        }

        // 3. Soustraire la valeur actuelle de la caisse
        caisse.setMontant_actuelle(caisse.getMontant_actuelle() - montantDouble);
        caisseRepository.save(caisse);

        // 4. Insérer un nouveau mvt_caisse (sortie de fonds)
        MvtCaisse mvt = new MvtCaisse();
        mvt.setCaisse(caisse);
        mvt.setDate(new Date(System.currentTimeMillis()));
        mvt.setMontant(-montantDouble); 
        mvtCaisseRepository.save(mvt);

        // 5. Enregistrer le paiement de l'employé
        PayementEmployee payement = new PayementEmployee();
        payement.setEmployee(emp);
        payement.setTypePayementEmployee(type);
        payement.setDatePayement(new Timestamp(System.currentTimeMillis()));
        payement.setRestePaye(BigDecimal.ZERO); 
        payementEmployeeRepository.save(payement);

        return "success";
    }

    /**
     * Retourne les mois pour lesquels un employé aurait dû être payé,
     * depuis son mois d'entrée jusqu'au mois précédent (mois en cours exclu).
     */
    public List<YearMonth> getUnpaidMonths(Employee employee) {
        YearMonth premierMoisDu = getExpectedMonth(employee);
        YearMonth dernierMoisDu = getExpectedEndMonth();

        List<YearMonth> moisAttendus = generateMonthsBetween(premierMoisDu, dernierMoisDu);
        Set<YearMonth> moisPayes = getPaidMonths(employee);

        return moisAttendus.stream()
                .filter(mois -> !moisPayes.contains(mois))
                .toList();
    }

    /**
     * Retourne la liste des alertes d'employés non payés pour les mois précédents.
     */
    public List<Map<String, Object>> getAlertesNonPayes() {
        List<Employee> allEmployees = employeeRepository.findAll();
        List<Map<String, Object>> alertes = new ArrayList<>();
        YearMonth moisCourant = YearMonth.now();
        
        for (Employee emp : allEmployees) {
            YearMonth moisEmbauche = YearMonth.from(emp.getDateEntree().toLocalDate());
            YearMonth examine = moisEmbauche;
            
            while (examine.isBefore(moisCourant)) {
                final YearMonth moisAVerifier = examine;
                
                boolean aUnPaiement = payementEmployeeRepository.findByEmployee(emp).stream()
                        .anyMatch(p -> {
                            LocalDate dateP = p.getDatePayement().toLocalDateTime().toLocalDate();
                            return YearMonth.from(dateP).equals(moisAVerifier);
                        });
                
                if (!aUnPaiement) {
                    Map<String, Object> alerte = new HashMap<>();
                    alerte.put("employee", emp);
                    alerte.put("moisManquant", moisAVerifier.toString());
                    alertes.add(alerte);
                }
                
                examine = examine.plusMonths(1);
            }
        }
        return alertes;
    }

    private YearMonth getExpectedMonth(Employee employee) {
        return YearMonth.from(employee.getDateEntree().toLocalDate());
    }

    private YearMonth getExpectedEndMonth() {
        return YearMonth.now();
    }

    private List<YearMonth> generateMonthsBetween(YearMonth debut, YearMonth finExclusive) {
        List<YearMonth> mois = new ArrayList<>();
        YearMonth courant = debut;
        while (courant.isBefore(finExclusive)) {
            mois.add(courant);
            courant = courant.plusMonths(1);
        }
        return mois;
    }

    private Set<YearMonth> getPaidMonths(Employee employee) {
        return payementEmployeeRepository.findByEmployee(employee).stream()
                .map(p -> YearMonth.from(p.getDatePayement().toLocalDateTime().toLocalDate()))
                .collect(Collectors.toSet());
    }
}