package mg.bovit.release.specification;

import mg.bovit.release.dto.MortaliteCriteria;
import mg.bovit.release.model.Mortalite;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;

import java.sql.Date;
import java.time.LocalDate;

public class MortaliteSpecification {

    public static Specification<Mortalite> fromCriteria(MortaliteCriteria criteria) throws Exception {
        boolean hasDateMin = criteria.getDateMin() != null && !criteria.getDateMin().isBlank();
        boolean hasDateMax = criteria.getDateMax() != null && !criteria.getDateMax().isBlank();

        // 1. Vérification de la cohérence des dates
        if (hasDateMin && hasDateMax) {
            LocalDate min = LocalDate.parse(criteria.getDateMin());
            LocalDate max = LocalDate.parse(criteria.getDateMax());
            
            if (max.isBefore(min)) {
                throw new Exception("La date max doit être après la date min");
            }
        }

        return (root, query, cb) -> {
            Predicate predicate = cb.conjunction();

            // Filtre par race
            if (criteria.getRaceId() != null) {
                predicate = cb.and(predicate,
                        cb.equal(root.get("race").get("id"), criteria.getRaceId()));
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
