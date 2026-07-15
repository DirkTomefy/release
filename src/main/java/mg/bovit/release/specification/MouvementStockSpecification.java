package mg.bovit.release.specification;

import org.springframework.data.jpa.domain.Specification;

import mg.bovit.release.dto.MultiCriteriaEtatStockMateriel;
import mg.bovit.release.model.MouvementStock;
import jakarta.persistence.criteria.*;
public class MouvementStockSpecification {
     public static Specification<MouvementStock> fromForm(MultiCriteriaEtatStockMateriel form) {
         return (root, query, cb) -> {
            Predicate predicate = cb.conjunction();

             if (form.getDateDebut() != null) {
                predicate = cb.and(predicate,
                        cb.greaterThan(root.get("dateMouvement"), form.getDateDebut()));
            }

            if (form.getDateFin() != null) {
                predicate = cb.and(predicate,
                        cb.lessThanOrEqualTo(root.get("dateMouvement"), form.getDateFin()));
            }

            if (form.getIdMateriel() != null) {
                predicate = cb.and(predicate,
                        cb.equal(root.get("materiel").get("id"), form.getIdMateriel()));
            }

            if (form.getIdTypeMateriel() != null) {
                predicate = cb.and(predicate,
                        cb.equal(root.get("materiel").get("type").get("id"), form.getIdTypeMateriel()));
            }
            
            return predicate;
        };
     }
}
