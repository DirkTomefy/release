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
    private ContratRepository contratRepository;

    /**
     * Effectue le paiement d'un employé : répartit le montant sur une ou
     * plusieurs caisses (choisies par l'utilisateur, comme dans bovin/achat),
     * retire le montant de chaque caisse utilisée, enregistre un mvt_caisse
     * par caisse, et journalise le paiement de l'employé.
     */
    @Transactional
    public String preciterPaiement(PayementDTO dto) {
        if (dto.getPayments() == null || dto.getPayments().isEmpty()) {
            throw new RuntimeException("Aucune caisse sélectionnée pour ce paiement.");
        }
        if (dto.getMois() == null || dto.getMois().isBlank()) {
            throw new RuntimeException("Le mois à payer est obligatoire.");
        }

        Employee emp = employeeRepository.findById(dto.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Employé introuvable"));
        TypePayementEmployee type = typePayementEmployeeRepository.findById(dto.getTypePayementId())
                .orElseThrow(() -> new RuntimeException("Type de paiement introuvable"));

        Date mois = firstDayOfMonth(dto.getMois());
        boolean estSalaire = isSalaire(type);

        // 1. Vérifier si le mois est déjà payé — UNIQUEMENT pour le type "Salaire".
        //    Les avances et sanctions ne bloquent jamais et peuvent être multiples
        //    sur un même mois, quel que soit leur montant par rapport au salaire
        //    (ex : une avance de 10 000 Ar même si le salaire est de 3 000 000 Ar).
        if (estSalaire) {
            boolean dejaPaye = payementEmployeeRepository
                    .existsByEmployeeAndTypePayementEmployeeAndMois(emp, type, mois);
            if (dejaPaye) {
                return "skipped";
            }
        }

        // 2. Vérifier que la somme des lignes de caisse correspond au montant annoncé
        BigDecimal montantTotal = dto.getMontant() != null ? dto.getMontant() : BigDecimal.ZERO;
        BigDecimal sommeLignes = dto.getPayments().stream()
                .map(p -> p.getMontant() != null ? p.getMontant() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

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

        // 3. Pour chaque caisse utilisée : vérifier le solde, le débiter, tracer le mouvement
        for (PayementDTO.CaissePaymentDTO ligne : dto.getPayments()) {
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
            mvt.setMontant(-montantDouble); // sortie de fonds
            mvtCaisseRepository.save(mvt);
        }

        // 4. Calculer le reste dû (uniquement significatif pour le type "Salaire")
        BigDecimal restePaye = BigDecimal.ZERO;
        if (estSalaire) {
            BigDecimal salaireContrat = getSalaireActif(emp);
            restePaye = salaireContrat.subtract(montantTotal).max(BigDecimal.ZERO);
        }

        // 5. Enregistrer le paiement de l'employé
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

    /**
     * Statut de paiement d'un employé pour un mois donné, utilisé par le
     * formulaire de paiement (GET /employees/{id}/statut-paye?mois=YYYY-MM).
     */
    public Map<String, Object> getStatutPaye(Long employeeId, String moisStr) {
        Employee emp = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employé introuvable"));
        Date mois = firstDayOfMonth(moisStr);

        BigDecimal salaire = getSalaireActif(emp);
        List<PayementEmployee> paiementsDuMois = payementEmployeeRepository.findByEmployeeAndMois(emp, mois);

        boolean dejaPaye = paiementsDuMois.stream()
                .anyMatch(p -> isSalaire(p.getTypePayementEmployee()));

        BigDecimal totalAvance = sommeParType(paiementsDuMois, LIBELLE_AVANCE);
        BigDecimal totalSanction = sommeParType(paiementsDuMois, LIBELLE_SANCTION);

        BigDecimal resteDu = dejaPaye ? BigDecimal.ZERO : salaire;

        Map<String, Object> resultat = new HashMap<>();
        resultat.put("salaire", salaire);
        resultat.put("dejaPaye", dejaPaye);
        resultat.put("resteDu", resteDu);
        resultat.put("totalAvance", totalAvance);
        resultat.put("totalSanction", totalSanction);
        return resultat;
    }

    /**
     * Retourne les mois pour lesquels un employé aurait dû être payé (salaire),
     * depuis son mois d'entrée jusqu'au mois précédent (mois en cours exclu).
     */
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

    /**
     * Alertes pour les employés dont le salaire n'a pas été payé sur un mois
     * précédent ou plus ancien.
     *
     * Piège géré : un employé embauché CE mois-ci n'a par définition aucun
     * mois précédent à son actif → aucune alerte pour lui (la boucle démarre
     * à son mois d'embauche et s'arrête avant le mois courant, donc si
     * moisEmbauche == moisCourant la boucle ne s'exécute jamais).
     */
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

    // ================= Helpers =================

    private boolean moisSalairePaye(Employee employee, YearMonth mois) {
        Date premierJour = Date.valueOf(mois.atDay(1));
        return payementEmployeeRepository.findByEmployeeAndMois(employee, premierJour).stream()
                .anyMatch(p -> isSalaire(p.getTypePayementEmployee()));
    }

    private boolean isSalaire(TypePayementEmployee type) {
        return type.getLibelle() != null && type.getLibelle().equalsIgnoreCase(LIBELLE_SALAIRE);
    }

    private BigDecimal sommeParType(List<PayementEmployee> paiements, String libelle) {
        return paiements.stream()
                .filter(p -> p.getTypePayementEmployee().getLibelle() != null
                        && p.getTypePayementEmployee().getLibelle().equalsIgnoreCase(libelle))
                .map(PayementEmployee::getMontant)
                .filter(m -> m != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Salaire du contrat actif de l'employé (contrat en cours à la date du
     * jour). À défaut de contrat strictement "actif", on retient le contrat
     * le plus récent (par date_debut) pour ne pas bloquer l'affichage.
     */
    private BigDecimal getSalaireActif(Employee employee) {
        List<Contrat> contrats = contratRepository.findByEmployeeOrderByDateDebutDesc(employee);
        if (contrats.isEmpty()) {
            return BigDecimal.ZERO;
        }

        LocalDate aujourdHui = LocalDate.now();
        return contrats.stream()
                .filter(c -> !c.getDateDebut().toLocalDate().isAfter(aujourdHui))
                .filter(c -> c.getDateFin() == null || !c.getDateFin().toLocalDate().isBefore(aujourdHui))
                .findFirst()
                .map(Contrat::getSalaire)
                .orElse(contrats.get(0).getSalaire());
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
