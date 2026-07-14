package mg.bovit.release.specification;

import mg.bovit.release.model.Race;
import mg.bovit.release.dto.RaceCriteria;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;

public class RaceSpecification {

    public static Specification<Race> fromCriteria(RaceCriteria criteria) {
        return (root, query, cb) -> {
            Predicate predicate = cb.conjunction();

            if (criteria.getNom() != null && !criteria.getNom().isEmpty()) {
                predicate = cb.and(predicate,
                        cb.like(cb.lower(root.get("nom")), "%" + criteria.getNom().toLowerCase() + "%"));
            }

            if (criteria.getDescriptions() != null && !criteria.getDescriptions().isEmpty()) {
                predicate = cb.and(predicate,
                        cb.like(cb.lower(root.get("descriptions")), "%" + criteria.getDescriptions().toLowerCase() + "%"));
            }

            // Pas de recherche globale
            return predicate;
        };
    }
}