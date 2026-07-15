package mg.bovit.release.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mg.bovit.release.dto.PayementDTO;
import mg.bovit.release.model.*;
import mg.bovit.release.repository.*;

@Service
public class PayementEmployeeService {

    private static final String LIBELLE_SALAIRE = "Salaire";
    private static final String LIBELLE_AVANCE = "Avance";
    private static final String LIBELLE_SANCTION = "Sanction";

    private static final String CAUSE_PAYEMENT = "PAYEMENT";

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

    @Autowired
    private CauseCaisseRepository causeCaisseRepository;

    @Autowired
    private ContratRepository contratRepository;

    @Transactional
    public String preciterPaiement(PayementDTO dto) {
        if (dto.getMois() == null || dto.getMois().isBlank()) {
            throw new RuntimeException("Le mois à payer est obligatoire.");
        }

        Employee emp = employeeRepository.findById(dto.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Employé introuvable"));
        TypePayementEmployee type = typePayementEmployeeRepository.findById(dto.getTypePayementId())
                .orElseThrow(() -> new RuntimeException("Type de paiement introuvable"));

        Date mois = firstDayOfMonth(dto.getMois());
        boolean estSalaire = isSalaire(type);
        boolean estAvance = isAvance(type);
        boolean estSanction = isSanction(type);
        List<PayementDTO.CaissePaymentDTO> lignesPaiement = dto.getPayments() != null ? dto.getPayments() : List.of();

        if (!estSanction && lignesPaiement.isEmpty()) {
            throw new RuntimeException("Aucune caisse sélectionnée pour ce paiement.");
        }

        if (estSalaire) {
            BigDecimal salaireMois = getSalaireActif(emp, mois);
            List<PayementEmployee> paiementsDuMois = payementEmployeeRepository.findByEmployeeAndMois(emp, mois);
            BigDecimal totalSalairePaye = sommeParType(paiementsDuMois, LIBELLE_SALAIRE);
            BigDecimal totalAvance = sommeParType(paiementsDuMois, LIBELLE_AVANCE);
            BigDecimal totalSanction = sommeParType(paiementsDuMois, LIBELLE_SANCTION);
            BigDecimal resteDuInitial = salaireMois.subtract(totalSalairePaye).subtract(totalAvance).subtract(totalSanction)
                    .max(BigDecimal.ZERO);
            
            if (resteDuInitial.compareTo(BigDecimal.ZERO) <= 0) {
                return "skipped"; 
            }
        }

        BigDecimal montantTotal = dto.getMontant() != null ? dto.getMontant() : BigDecimal.ZERO;
        BigDecimal sommeLignes = lignesPaiement.stream()
                .map(p -> p.getMontant() != null ? p.getMontant() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (estSanction) {
            if (montantTotal.compareTo(BigDecimal.ZERO) <= 0) {
                throw new RuntimeException("Le montant de la sanction doit être supérieur à 0.");
            }
        } else {
            if (sommeLignes.compareTo(BigDecimal.ZERO) <= 0) {
                throw new RuntimeException("Le montant réparti sur les caisses doit être supérieur à 0.");
            }
            if (montantTotal.compareTo(BigDecimal.ZERO) > 0
                    && sommeLignes.subtract(montantTotal).abs().compareTo(new BigDecimal("0.01")) > 0) {
                throw new RuntimeException("Le total réparti sur les caisses ne correspond pas au montant à verser.");
            }
            if (montantTotal.compareTo(BigDecimal.ZERO) <= 0) {
                montantTotal = sommeLignes;
            }
        }

        if (!estSanction && (estSalaire || estAvance)) {
            for (PayementDTO.CaissePaymentDTO ligne : lignesPaiement) {
                if (ligne.getCaisseId() == null || ligne.getMontant() == null
                        || ligne.getMontant().compareTo(BigDecimal.ZERO) <= 0) {
                    continue;
                }
                Caisse caisse = caisseRepository.findById(ligne.getCaisseId())
                        .orElseThrow(() -> new RuntimeException("Caisse introuvable"));

                double montantDouble = ligne.getMontant().doubleValue();
                if (caisse.getMontant_actuelle() < montantDouble) {
                    throw new RuntimeException("Solde insuffisant dans la caisse : " + caisse.getLibelle());
                }

                caisse.setMontant_actuelle(caisse.getMontant_actuelle() - montantDouble);
                caisseRepository.save(caisse);

                MvtCaisse mvt = new MvtCaisse();
                mvt.setCaisse(caisse);
                mvt.setDate(new Date(System.currentTimeMillis()));
                mvt.setMontant(-montantDouble);
                mvt.setCauseCaisse(causeCaisseRepository.findByLibelleIgnoreCase(CAUSE_PAYEMENT)
                        .orElseThrow(() -> new RuntimeException("Cause de caisse introuvable : " + CAUSE_PAYEMENT)));
                mvtCaisseRepository.save(mvt);
            }
        }

        BigDecimal salaireMois = getSalaireActif(emp, mois);
        List<PayementEmployee> paiementsDuMois = payementEmployeeRepository.findByEmployeeAndMois(emp, mois);
        BigDecimal totalSalairePaye = sommeParType(paiementsDuMois, LIBELLE_SALAIRE);
        BigDecimal totalAvance = sommeParType(paiementsDuMois, LIBELLE_AVANCE);
        BigDecimal totalSanction = sommeParType(paiementsDuMois, LIBELLE_SANCTION);
        BigDecimal resteDu = salaireMois.subtract(totalSalairePaye).subtract(totalAvance).subtract(totalSanction)
            .max(BigDecimal.ZERO);

        if (estSalaire) {
            if (montantTotal.subtract(resteDu).compareTo(new BigDecimal("0.01")) > 0) {
                throw new RuntimeException("Le montant saisi dépasse le reste dû du mois (" + resteDu + " Ar) pour cet employé.");
            }
        }

        BigDecimal restePaye = resteDu.subtract(montantTotal).max(BigDecimal.ZERO);

        PayementEmployee payement = new PayementEmployee();
        payement.setEmployee(emp);
        payement.setTypePayementEmployee(type);
        payement.setDatePayement(new Timestamp(System.currentTimeMillis()));
        payement.setMois(mois);
        payement.setMontant(montantTotal);
        payement.setRestePaye(restePaye);
        payementEmployeeRepository.save(payement);

        return "success";
    }

    public Map<String, Object> getStatutPaye(Long employeeId, String moisStr) {
        Employee emp = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employé introuvable"));
        Date mois = firstDayOfMonth(moisStr);

        BigDecimal salaire = getSalaireActif(emp, mois);
        List<PayementEmployee> paiementsDuMois = payementEmployeeRepository.findByEmployeeAndMois(emp, mois);

        BigDecimal totalSalairePaye = sommeParType(paiementsDuMois, LIBELLE_SALAIRE);
        BigDecimal totalAvance = sommeParType(paiementsDuMois, LIBELLE_AVANCE);
        BigDecimal totalSanction = sommeParType(paiementsDuMois, LIBELLE_SANCTION);

        boolean dejaPaye = totalSalairePaye.compareTo(BigDecimal.ZERO) > 0;
        BigDecimal resteDu = salaire.subtract(totalSalairePaye).subtract(totalAvance).subtract(totalSanction)
            .max(BigDecimal.ZERO);

        Map<String, Object> resultat = new HashMap<>();
        resultat.put("salaire", salaire);
        resultat.put("dejaPaye", dejaPaye);
        resultat.put("resteDu", resteDu);
        resultat.put("totalAvance", totalAvance);
        resultat.put("totalSanction", totalSanction);
        return resultat;
    }

    public List<YearMonth> getUnpaidMonths(Employee employee) {
        YearMonth premierMoisDu = YearMonth.from(employee.getDateEntree().toLocalDate());
        YearMonth moisCourant = YearMonth.now();

        List<YearMonth> moisManquants = new ArrayList<>();
        YearMonth examine = premierMoisDu;
        while (examine.isBefore(moisCourant)) {
            if (!moisSalairePaye(employee, examine)) {
                moisManquants.add(examine);
            }
            examine = examine.plusMonths(1);
        }
        return moisManquants;
    }

    public List<Map<String, Object>> getAlertesNonPayes() {
        List<Employee> allEmployees = employeeRepository.findAll();
        List<Map<String, Object>> alertes = new ArrayList<>();
        YearMonth moisCourant = YearMonth.now();

        for (Employee emp : allEmployees) {
            YearMonth moisEmbauche = YearMonth.from(emp.getDateEntree().toLocalDate());
            YearMonth examine = moisEmbauche;

            while (examine.isBefore(moisCourant)) {
                if (!moisSalairePaye(emp, examine)) {
                    Map<String, Object> alerte = new HashMap<>();
                    alerte.put("employee", emp);
                    alerte.put("moisManquant", examine.toString());
                    alertes.add(alerte);
                }
                examine = examine.plusMonths(1);
            }
        }
        return alertes;
    }

    private boolean moisSalairePaye(Employee employee, YearMonth mois) {
        Date premierJour = Date.valueOf(mois.atDay(1));
        return payementEmployeeRepository.findByEmployeeAndMois(employee, premierJour).stream()
                .anyMatch(p -> isSalaire(p.getTypePayementEmployee()));
    }

    private boolean isSalaire(TypePayementEmployee type) {
        return type.getLibelle() != null && type.getLibelle().equalsIgnoreCase(LIBELLE_SALAIRE);
    }

    private boolean isAvance(TypePayementEmployee type) {
        return type.getLibelle() != null && type.getLibelle().equalsIgnoreCase(LIBELLE_AVANCE);
    }

    private boolean isSanction(TypePayementEmployee type) {
        return type.getLibelle() != null && type.getLibelle().equalsIgnoreCase(LIBELLE_SANCTION);
    }

    private BigDecimal sommeParType(List<PayementEmployee> paiements, String libelle) {
        return paiements.stream()
                .filter(p -> p.getTypePayementEmployee().getLibelle() != null
                        && p.getTypePayementEmployee().getLibelle().equalsIgnoreCase(libelle))
                .map(PayementEmployee::getMontant)
                .filter(m -> m != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal calculerSalairePourMois(Contrat contrat, YearMonth mois) {
        if (contrat == null || contrat.getDateDebut() == null || contrat.getSalaire() == null) {
            return BigDecimal.ZERO;
        }

        LocalDate debutContrat = contrat.getDateDebut().toLocalDate();
        LocalDate finContrat = contrat.getDateFin() != null ? contrat.getDateFin().toLocalDate() : null;
        LocalDate debutMois = mois.atDay(1);
        LocalDate finMois = mois.atEndOfMonth();

        LocalDate debutEffectif = debutContrat.isAfter(debutMois) ? debutContrat : debutMois;
        LocalDate finEffectif = finContrat != null && finContrat.isBefore(finMois) ? finContrat : finMois;
        if (debutEffectif.isAfter(finEffectif)) {
            return BigDecimal.ZERO;
        }

        long joursTravail = ChronoUnit.DAYS.between(debutEffectif, finEffectif) + 1;
        long joursMois = ChronoUnit.DAYS.between(debutMois, finMois) + 1;
        BigDecimal salaireMensuel = contrat.getSalaire();
        return salaireMensuel.multiply(BigDecimal.valueOf(joursTravail))
                .divide(BigDecimal.valueOf(joursMois), 2, RoundingMode.HALF_UP);
    }

    private BigDecimal getSalaireActif(Employee employee, Date mois) {
        List<Contrat> contrats = contratRepository.findByEmployeeOrderByDateDebutDesc(employee);
        if (contrats.isEmpty()) {
            return BigDecimal.ZERO;
        }

        YearMonth moisCible = YearMonth.from(mois.toLocalDate());
        return contrats.stream()
                .filter(c -> c.getDateDebut() != null)
                .filter(c -> !c.getDateDebut().toLocalDate().isAfter(moisCible.atEndOfMonth()))
                .filter(c -> c.getDateFin() == null || !c.getDateFin().toLocalDate().isBefore(moisCible.atDay(1)))
                .findFirst()
                .map(c -> calculerSalairePourMois(c, moisCible))
                .orElse(BigDecimal.ZERO);
    }

    private Date firstDayOfMonth(String moisStr) {
        try {
            YearMonth ym = YearMonth.parse(moisStr);
            return Date.valueOf(ym.atDay(1));
        } catch (Exception e) {
            throw new RuntimeException("Format de mois invalide, attendu YYYY-MM : " + moisStr);
        }
    }
}