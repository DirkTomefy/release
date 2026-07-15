package mg.bovit.release.service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mg.bovit.release.dto.MortaliteCriteria;
import mg.bovit.release.dto.MortaliteStatsDTO;
import mg.bovit.release.model.Bovin;
import mg.bovit.release.model.Mortalite;
import mg.bovit.release.model.PeseBovin;
import mg.bovit.release.repository.BovinRepository;
import mg.bovit.release.repository.MortaliteRepository;
import mg.bovit.release.repository.PeseBovinRepository;
import mg.bovit.release.repository.VenteDetailRepository;
import mg.bovit.release.specification.MortaliteSpecification;

@Service
public class MortaliteService {

    @Autowired
    private MortaliteRepository mortaliteRepository;

    @Autowired
    private BovinRepository bovinRepository;

    @Autowired
    private PeseBovinService peseBovinService;

    @Autowired
    private PeseBovinRepository peseBovinRepository;

    @Autowired
    private VenteDetailRepository venteDetailRepository;

    /**
     * Déclare la mortalité d'un bovin (identifié par son id) : on conserve
     * un instantané de ses infos (race, prix d'achat, poids au moment du
     * décès) dans la table mortalite, puis on supprime le bovin de la
     * table bovin (ainsi que ses pesées liées, pour respecter la
     * contrainte de clé étrangère fk_bovin_poids).
     */
    @Transactional(rollbackFor = Exception.class)
    public void declareMortalite(Long bovinId, LocalDate date) throws Exception {
        if (bovinId == null) {
            throw new Exception("L'identifiant du bovin est obligatoire.");
        }
        if (date == null) {
            throw new Exception("La date est obligatoire.");
        }

        Bovin bovin = bovinRepository.findById(bovinId)
                .orElseThrow(() -> new Exception("Aucun bovin trouvé avec l'id " + bovinId));

        if (venteDetailRepository.existsByBovin_Id(bovinId)) {
            throw new Exception("Impossible de déclarer la mortalité du bovin #" + bovinId
                    + " car il est encore associé à une vente.");
        }

        // Poids au moment du décès = poids actuel connu, en créant une pesée initiale si nécessaire
        PeseBovin dernierePese = peseBovinService.getOrCreateLatestPeseByBovin(bovinId);
        Double poidsMort = (dernierePese != null) ? dernierePese.getPoids_apres() : bovin.getPoids_achat();

        Mortalite mortalite = new Mortalite();
        mortalite.setRace(bovin.getRace());
        mortalite.setPrix_achat(bovin.getPrix_achat());
        mortalite.setPoids_mort(poidsMort);
        mortalite.setDate(Date.valueOf(date));
        mortaliteRepository.save(mortalite);

        // Suppression des pesées liées, sinon la suppression du bovin
        // échouerait à cause de la contrainte fk_bovin_poids.
        peseBovinRepository.deleteByBovin_Id(bovinId);

        try {
            bovinRepository.delete(bovin);
        } catch (Exception e) {
            throw new Exception(
                "Impossible de supprimer le bovin #" + bovinId
                + " (il est probablement lié à une vente existante) : " + e.getMessage());
        }
    }

    /**
     * Déclare la mortalité de plusieurs bovins (par leurs id) pour une
     * même date (page d'insertion multiple).
     */
    @Transactional(rollbackFor = Exception.class)
    public void declareMortaliteMultiple(List<Long> bovinIds, LocalDate date) throws Exception {
        if (bovinIds == null || bovinIds.isEmpty()) {
            throw new Exception("Veuillez saisir au moins un identifiant de bovin.");
        }
        if (date == null) {
            throw new Exception("La date est obligatoire.");
        }

        for (Long bovinId : bovinIds) {
            declareMortalite(bovinId, date);
        }
    }

    public Page<Mortalite> findPaginated(MortaliteCriteria criteria) throws Exception {
        Specification<Mortalite> spec = MortaliteSpecification.fromCriteria(criteria);
        Pageable pageable = PageRequest.of(
                criteria.getPage(),
                criteria.getSize(),
                Sort.by(Sort.Direction.DESC, "date"));
        return mortaliteRepository.findAll(spec, pageable);
    }

    public MortaliteStatsDTO getStats(LocalDate dateDebut, LocalDate dateFin, Long raceId) {
        Long total = mortaliteRepository.countMortalitesWithFilters(dateDebut, dateFin, raceId);
        Double totalPrix = mortaliteRepository.sumPrixMortalitesWithFilters(dateDebut, dateFin, raceId);
        List<Object[]> grouped = mortaliteRepository.findMortaliteStatsGroupedByMonth(dateDebut, dateFin, raceId);

        List<String> labels = new ArrayList<>();
        List<Long> counts = new ArrayList<>();
        List<Double> prixTotals = new ArrayList<>();

        for (Object[] row : grouped) {
            String mois = (String) row[0];
            Long count = ((Number) row[1]).longValue();
            Double prixTotal = ((Number) row[2]).doubleValue();
            labels.add(mois);
            counts.add(count);
            prixTotals.add(prixTotal);
        }

        MortaliteStatsDTO dto = new MortaliteStatsDTO();
        dto.setTotalMortalites(total != null ? total : 0L);
        dto.setTotalPrixMortalites(totalPrix != null ? totalPrix : 0D);
        dto.setLabels(labels);
        dto.setCounts(counts);
        dto.setPrixTotals(prixTotals);
        return dto;
    }
}
