package mg.bovit.release.specification;
import mg.bovit.release.model.sqlview.PeseBovinWithDateVente;
import mg.bovit.release.dto.MulticriteriaListPeseBovin;

import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.*;

public class PeseSpecification {
    public static Specification<PeseBovinWithDateVente> fromForm(MulticriteriaListPeseBovin form) throws Exception {
        // verify poids
        if (form.getPoidsApresMax() != null && form.getPoidsApresMax() < 0) {
            throw new Exception("Le poids ne devrais pas ê négatif");
        }

        if (form.getPoidsApresMin() != null && form.getPoidsApresMin() < 0) {
            throw new Exception("Le poids ne devrais pas ê négatif");
        }

        if (form.getPoidsApresMax() != null && form.getPoidsApresMin() != null && form.getPoidsApresMax() < form.getPoidsApresMin()) {
            throw new Exception("Le poids max doit être supérieur ou égal au poids min");
        }

        // verif date
        if (form.getDatePeseMax() != null && form.getDatePeseMin() != null && form.getDatePeseMax().isBefore(form.getDatePeseMin())) {
            throw new Exception("La date max doit être après la date min");
        }

        return (root, query, cb) -> {
            Predicate predicate = cb.conjunction();

            if (form.getDatePeseMin() != null) {
                predicate = cb.and(predicate,
                        cb.greaterThanOrEqualTo(root.get("date_pese"), form.getDatePeseMin()));
            }

            if (form.getDatePeseMax() != null) {
                predicate = cb.and(predicate,
                        cb.lessThanOrEqualTo(root.get("date_pese"), form.getDatePeseMax()));
            }

            if (form.getPoidsApresMin() != null) {
                predicate = cb.and(predicate,
                        cb.greaterThanOrEqualTo(root.get("poids_apres"), form.getPoidsApresMin()));
            }
            if (form.getPoidsApresMax() != null) {
                predicate = cb.and(predicate,
                        cb.lessThanOrEqualTo(root.get("poids_apres"), form.getPoidsApresMax()));
            }

            // Filtre sur le statut (vendu / non vendu)
            String statut = form.getStatut();
            if (statut != null) {
                if ("vendu".equalsIgnoreCase(statut)) {
                    predicate = cb.and(predicate,
                            cb.isNotNull(root.get("date_vente")));
                } else if ("non_vendu".equalsIgnoreCase(statut)) {
                    predicate = cb.and(predicate,
                            cb.isNull(root.get("date_vente")));
                }
                // "tous" => pas de filtre
            }

            return predicate;
        };
    }
}
