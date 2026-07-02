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
                Expression<String> bovinIdStr = cb.toString(root.get("bovin").get("id"));
                Predicate searchPredicate = cb.or(
                        cb.like(cb.lower(bovinIdStr), searchPattern),
                        cb.like(cb.lower(root.get("bovin").get("race").get("nom")), searchPattern));
                predicate = cb.and(predicate, searchPredicate);
            }

            return predicate;
        };
    }
}
