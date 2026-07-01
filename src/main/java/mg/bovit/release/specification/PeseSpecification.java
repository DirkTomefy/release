package mg.bovit.release.specification;

import mg.bovit.release.model.PeseBovin;
import mg.bovit.release.dto.MulticriteriaListPeseBovin;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.*;

public class PeseSpecification {

    public static Specification<PeseBovin> fromForm(MulticriteriaListPeseBovin form) {
        return (root, query, cb) -> {
            Predicate predicate = cb.conjunction();

            // 1. Filtre par Race du Bovin
            if (form.getRaceId() != null) {
                predicate = cb.and(predicate,
                        cb.equal(root.get("bovin").get("race").get("id"), form.getRaceId()));
            }

            // 2. Filtre par Date de la pesée
            if (form.getDateRecherePese() != null) {
                predicate = cb.and(predicate,
                        cb.equal(root.get("date_pese"), form.getDateRecherePese()));
            }

            // 3. Filtre par Prix d'achat minimum du Bovin
            if (form.getPrixAchatMin() != null) {
                predicate = cb.and(predicate,
                        cb.greaterThanOrEqualTo(root.get("bovin").get("prix_achat"), form.getPrixAchatMin()));
            }

            // 4. Filtre par Prix d'achat maximum du Bovin
            if (form.getPrixAchatMax() != null) {
                predicate = cb.and(predicate,
                        cb.lessThanOrEqualTo(root.get("bovin").get("prix_achat"), form.getPrixAchatMax()));
            }

            // 5. Filtre par Statut de vente du Bovin
            String statut = form.getStatut();
            if (statut != null) {
                if ("vendu".equalsIgnoreCase(statut)) {
                    predicate = cb.and(predicate,
                            cb.isNotNull(root.get("bovin").get("date_vente")));
                } else if ("non_vendu".equalsIgnoreCase(statut)) {
                    predicate = cb.and(predicate,
                            cb.isNull(root.get("bovin").get("date_vente")));
                }
            }

            // 6. Recherche textuelle : ID du Bovin ou Nom de sa Race
            if (form.getSearch() != null && !form.getSearch().isEmpty()) {
                String searchPattern = "%" + form.getSearch().toLowerCase() + "%";
                Predicate searchPredicate = cb.or(
                        cb.like(cb.lower(root.get("bovin").get("id").as(String.class)), searchPattern),
                        cb.like(cb.lower(root.get("bovin").get("race").get("nom")), searchPattern)
                );
                predicate = cb.and(predicate, searchPredicate);
            }

            return predicate;
        };
    }
}