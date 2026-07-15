package mg.bovit.release.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mg.bovit.release.dto.FinancialStatsDTO;
import mg.bovit.release.repository.MvtCaisseRepository;
import mg.bovit.release.model.MvtCaisse;

import java.sql.Date;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FinancialService {

    @Autowired
    private MvtCaisseRepository mvtCaisseRepository;

    public FinancialStatsDTO getFinancialStats(LocalDate dateDebut, LocalDate dateFin) {
        if (dateDebut == null) dateDebut = LocalDate.of(2000, 1, 1);
        if (dateFin == null) dateFin = LocalDate.now();

        Date start = Date.valueOf(dateDebut);
        Date end = Date.valueOf(dateFin);

        List<MvtCaisse> mouvements = mvtCaisseRepository.findByDateBetweenOrderByDateAsc(start, end);

        // Regroupement par mois (année-mois)
        Map<YearMonth, double[]> aggregation = new TreeMap<>();

        for (MvtCaisse mvt : mouvements) {
            LocalDate date = mvt.getDate().toLocalDate();
            YearMonth ym = YearMonth.from(date);
            double montant = mvt.getMontant() != null ? mvt.getMontant() : 0.0;
            aggregation.computeIfAbsent(ym, k -> new double[2]); // [entrees, sorties]
            if (montant >= 0) {
                aggregation.get(ym)[0] += montant;
            } else {
                aggregation.get(ym)[1] += Math.abs(montant);
            }
        }

        List<String> labels = new ArrayList<>();
        List<Double> entrees = new ArrayList<>();
        List<Double> sorties = new ArrayList<>();
        List<Double> benefices = new ArrayList<>();

        double totalEntrees = 0;
        double totalSorties = 0;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM yyyy", Locale.FRENCH);
        for (Map.Entry<YearMonth, double[]> entry : aggregation.entrySet()) {
            YearMonth ym = entry.getKey();
            labels.add(ym.format(formatter));
            double entree = entry.getValue()[0];
            double sortie = entry.getValue()[1];
            entrees.add(entree);
            sorties.add(sortie);
            benefices.add(entree - sortie);
            totalEntrees += entree;
            totalSorties += sortie;
        }

        FinancialStatsDTO dto = new FinancialStatsDTO();
        dto.setLabels(labels);
        dto.setEntrees(entrees);
        dto.setSorties(sorties);
        dto.setBenefices(benefices);
        dto.setTotalEntrees(totalEntrees);
        dto.setTotalSorties(totalSorties);
        dto.setTotalBenefice(totalEntrees - totalSorties);

        return dto;
    }
}