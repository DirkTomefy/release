package mg.bovit.release.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Date;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import mg.bovit.release.dto.CaisseStatDTO;
import mg.bovit.release.repository.*;
import mg.bovit.release.model.*;

@Service
public class CaisseService {
    @Autowired
    private CaisseRepository caisseRepository;

    @Autowired
    private MvtCaisseRepository mvtCaisseRepository;

    // Granularité de regroupement des barres de l'histogramme,
    // choisie automatiquement selon l'étendue de la période filtrée.
    private enum Granularite { JOUR, SEMAINE, MOIS }

    // function to save caisse 
    public Caisse save(Caisse caisse) throws Exception {
        return caisseRepository.save(caisse);
    }

    // function to find caisse by id
    public Caisse findById(Long id_caisse) throws Exception {
        return caisseRepository.findById(id_caisse).orElseThrow();
    }

    public List<Caisse> findAll() {
        return caisseRepository.findAll();
    }

    /**
     * Construit l'histogramme des entrées/sorties de caisse entre deux dates,
     * pour toutes les caisses (caisseId == null) ou une caisse précise.
     * Un mouvement (mvt_caisse) est déjà signé à la source : montant positif
     * = entrée (ex. vente de bovin), montant négatif = sortie (ex. paiement
     * employé) — on assemble donc les deux ici à partir de ce même signe.
     */
    public CaisseStatDTO getStatistiques(Date dateDebut, Date dateFin, Long caisseId) throws Exception {
        if (dateDebut == null || dateFin == null) {
            throw new Exception("La date de début et la date de fin sont obligatoires.");
        }
        if (dateDebut.after(dateFin)) {
            throw new Exception("La date de début doit être antérieure à la date de fin.");
        }

        List<MvtCaisse> mouvements = (caisseId == null)
                ? mvtCaisseRepository.findByDateBetweenOrderByDateAsc(dateDebut, dateFin)
                : mvtCaisseRepository.findByDateBetweenAndCaisse_IdOrderByDateAsc(dateDebut, dateFin, caisseId);

        LocalDate debutLocal = dateDebut.toLocalDate();
        LocalDate finLocal = dateFin.toLocalDate();
        long nbJours = ChronoUnit.DAYS.between(debutLocal, finLocal) + 1;

        Granularite granularite = nbJours <= 31 ? Granularite.JOUR
                : nbJours <= 180 ? Granularite.SEMAINE
                : Granularite.MOIS;

        // TreeMap<LocalDate,...> : la clé est le début de l'intervalle (jour, lundi
        // de la semaine, ou 1er du mois), ce qui garantit un ordre chronologique
        // correct des barres même quand on regroupe par semaine/mois.
        Map<LocalDate, double[]> aggregation = new TreeMap<>();

        for (MvtCaisse mvt : mouvements) {
            LocalDate jour = mvt.getDate().toLocalDate();
            LocalDate cle = cleIntervalle(jour, granularite);

            double[] valeurs = aggregation.computeIfAbsent(cle, k -> new double[2]);
            double montant = mvt.getMontant() != null ? mvt.getMontant() : 0.0;
            if (montant >= 0) {
                valeurs[0] += montant;      // entrée
            } else {
                valeurs[1] += -montant;     // sortie (valeur absolue)
            }
        }

        CaisseStatDTO stats = new CaisseStatDTO();
        List<String> labels = new java.util.ArrayList<>();
        List<Double> entrees = new java.util.ArrayList<>();
        List<Double> sorties = new java.util.ArrayList<>();

        double totalEntree = 0.0;
        double totalSortie = 0.0;

        for (Map.Entry<LocalDate, double[]> entry : aggregation.entrySet()) {
            labels.add(formaterLabel(entry.getKey(), granularite));
            double entree = entry.getValue()[0];
            double sortie = entry.getValue()[1];
            entrees.add(entree);
            sorties.add(sortie);
            totalEntree += entree;
            totalSortie += sortie;
        }

        stats.setLabels(labels);
        stats.setEntrees(entrees);
        stats.setSorties(sorties);
        stats.setTotalEntree(totalEntree);
        stats.setTotalSortie(totalSortie);
        stats.setSolde(totalEntree - totalSortie);

        return stats;
    }

    private LocalDate cleIntervalle(LocalDate jour, Granularite granularite) {
        switch (granularite) {
            case SEMAINE:
                return jour.with(DayOfWeek.MONDAY);
            case MOIS:
                return jour.withDayOfMonth(1);
            case JOUR:
            default:
                return jour;
        }
    }

    private String formaterLabel(LocalDate debutIntervalle, Granularite granularite) {
        switch (granularite) {
            case SEMAINE:
                return "Sem. " + debutIntervalle.format(DateTimeFormatter.ofPattern("dd/MM", Locale.FRENCH));
            case MOIS:
                return debutIntervalle.format(DateTimeFormatter.ofPattern("MMM yyyy", Locale.FRENCH));
            case JOUR:
            default:
                return debutIntervalle.format(DateTimeFormatter.ofPattern("dd/MM", Locale.FRENCH));
        }
    }
}