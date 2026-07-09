package mg.bovit.release.specification;

import mg.bovit.release.model.PeseBovin;
import mg.bovit.release.dto.MulticriteriaListPeseBovin;

import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.*;

public class PeseSpecification {
    public static Specification<PeseBovin> fromForm(MulticriteriaListPeseBovin form) {
        return (root, query, cb) -> {
            Predicate predicate = cb.conjunction();

            if (form.getDateRecherePese() != null) {
                predicate = cb.and(predicate,
                        cb.greaterThanOrEqualTo(root.get("date_pese"), form.getDateRecherePese()));
            }

            if (form.getPrixAchatMin() != null) {
                predicate = cb.and(predicate,
                        cb.greaterThanOrEqualTo(root.get("prix_achat"), form.getPrixAchatMin()));
            }
            if (form.getPrixAchatMax() != null) {
                predicate = cb.and(predicate,
                        cb.lessThanOrEqualTo(root.get("prix_achat"), form.getPrixAchatMax()));
            }

            return predicate;
        };
    }
}
