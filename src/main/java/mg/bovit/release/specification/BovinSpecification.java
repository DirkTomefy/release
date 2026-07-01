package mg.bovit.release.specification;

import mg.bovit.release.model.Bovin;
import mg.bovit.release.dto.MultiCriteriaFormBovinList;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.*;

public class BovinSpecification {

    public static Specification<Bovin> fromForm(MultiCriteriaFormBovinList form) {
        return (root, query, cb) -> {
            Predicate predicate = cb.conjunction();

            if (form.getRaceId() != null) {
                predicate = cb.and(predicate,
                        cb.equal(root.get("race").get("id"), form.getRaceId()));
            }

            if (form.getDateAchatMin() != null) {
                predicate = cb.and(predicate,
                        cb.greaterThanOrEqualTo(root.get("date_achat"), form.getDateAchatMin()));
            }
            if (form.getDateAchatMax() != null) {
                predicate = cb.and(predicate,
                        cb.lessThanOrEqualTo(root.get("date_achat"), form.getDateAchatMax()));
            }

            if (form.getPrixAchatMin() != null) {
                predicate = cb.and(predicate,
                        cb.greaterThanOrEqualTo(root.get("prix_achat"), form.getPrixAchatMin()));
            }
            if (form.getPrixAchatMax() != null) {
                predicate = cb.and(predicate,
                        cb.lessThanOrEqualTo(root.get("prix_achat"), form.getPrixAchatMax()));
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

            // Recherche textuelle : on recherche dans l'ID (converti en string) et
            // éventuellement dans race.nom
            if (form.getSearch() != null && !form.getSearch().isEmpty()) {
                String searchPattern = "%" + form.getSearch().toLowerCase() + "%";
                Predicate searchPredicate = cb.or(
                        cb.like(cb.lower(root.get("id").as(String.class)), searchPattern),
                        cb.like(cb.lower(root.get("race").get("nom")), searchPattern));
                predicate = cb.and(predicate, searchPredicate);
            }

            return predicate;
        };
    }
}
