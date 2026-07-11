package mg.bovit.release.specification;

import java.sql.Date;
import java.time.LocalDate;

import jakarta.persistence.criteria.Predicate;

import org.springframework.data.jpa.domain.Specification;

import mg.bovit.release.dto.MvtCaisseCriteria;
import mg.bovit.release.model.MvtCaisse;

public class MvtCaisseSpecification {

    public static Specification<MvtCaisse> fromCriteria(MvtCaisseCriteria criteria) {
        return (root, query, cb) -> {
            Predicate predicate = cb.conjunction();

            // Filtre par caisse
            if (criteria.getCaisseId() != null) {
                predicate = cb.and(predicate,
                        cb.equal(root.get("caisse").get("id"), criteria.getCaisseId()));
            }

            // Filtre par cause (raison du mouvement)
            if (criteria.getCauseCaisseId() != null) {
                predicate = cb.and(predicate,
                        cb.equal(root.get("causeCaisse").get("id"), criteria.getCauseCaisseId()));
            }

            // Filtre par date min
            if (criteria.getDateMin() != null && !criteria.getDateMin().isBlank()) {
                Date dateMin = Date.valueOf(LocalDate.parse(criteria.getDateMin()));
                predicate = cb.and(predicate,
                        cb.greaterThanOrEqualTo(root.get("date"), dateMin));
            }

            // Filtre par date max
            if (criteria.getDateMax() != null && !criteria.getDateMax().isBlank()) {
                Date dateMax = Date.valueOf(LocalDate.parse(criteria.getDateMax()));
                predicate = cb.and(predicate,
                        cb.lessThanOrEqualTo(root.get("date"), dateMax));
            }

            return predicate;
        };
    }
}
