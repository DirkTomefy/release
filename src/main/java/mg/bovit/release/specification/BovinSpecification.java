package mg.bovit.release.specification;

import mg.bovit.release.model.sqlview.BovinWithPoids;
import mg.bovit.release.dto.MultiCriteriaFormBovinList;
import org.springframework.data.jpa.domain.Specification;
<<<<<<< HEAD
import jakarta.persistence.criteria.*;
=======
import jakarta.persistence.criteria.Predicate;
>>>>>>> e73ef66ceba853f4b2bc24cc3c41dd5b4dbc833e

public class BovinSpecification {

    public static Specification<BovinWithPoids> fromForm(MultiCriteriaFormBovinList form) {
        return (root, query, cb) -> {
            Predicate predicate = cb.conjunction();

            // Filtre par race
            if (form.getRaceId() != null) {
                predicate = cb.and(predicate,
                        cb.equal(root.get("idRace"), form.getRaceId()));
            }

            // Filtre par date d'achat (min)
            if (form.getDateAchatMin() != null) {
                predicate = cb.and(predicate,
                        cb.greaterThanOrEqualTo(root.get("dateAchat"), form.getDateAchatMin()));
            }
            
            // Filtre par date d'achat (max)
            if (form.getDateAchatMax() != null) {
                predicate = cb.and(predicate,
                        cb.lessThanOrEqualTo(root.get("dateAchat"), form.getDateAchatMax()));
            }

            // Filtre par prix d'achat (min)
            if (form.getPrixAchatMin() != null) {
                predicate = cb.and(predicate,
                        cb.greaterThanOrEqualTo(root.get("prixAchat"), form.getPrixAchatMin()));
            }
            
            // Filtre par prix d'achat (max)
            if (form.getPrixAchatMax() != null) {
                predicate = cb.and(predicate,
                        cb.lessThanOrEqualTo(root.get("prixAchat"), form.getPrixAchatMax()));
            }

            // Filtre par poids actuel (min)
            if (form.getPoidsMin() != null) {
                predicate = cb.and(predicate,
                        cb.greaterThanOrEqualTo(root.get("poidsActuel"), form.getPoidsMin()));
            }
            
            // Filtre par poids actuel (max)
            if (form.getPoidsMax() != null) {
                predicate = cb.and(predicate,
                        cb.lessThanOrEqualTo(root.get("poidsActuel"), form.getPoidsMax()));
            }

            // Filtre sur le statut (vendu / non vendu)
            String statut = form.getStatut();
            if (statut != null) {
                if ("vendu".equalsIgnoreCase(statut)) {
                    predicate = cb.and(predicate,
                            cb.isNotNull(root.get("dateVente")));
                } else if ("non_vendu".equalsIgnoreCase(statut)) {
                    predicate = cb.and(predicate,
                            cb.isNull(root.get("dateVente")));
                }
                // "tous" => pas de filtre
            }

<<<<<<< HEAD
            // Recherche textuelle : on recherche dans l'ID (converti en string) et
            // éventuellement dans race.nom
            if (form.getSearch() != null && !form.getSearch().isEmpty()) {
                String searchPattern = "%" + form.getSearch().toLowerCase() + "%";
                Predicate searchPredicate = cb.or(
                        cb.like(cb.lower(root.get("id").as(String.class)), searchPattern),
                        cb.like(cb.lower(root.get("race").get("nom")), searchPattern));
                predicate = cb.and(predicate, searchPredicate);
            }

=======
>>>>>>> e73ef66ceba853f4b2bc24cc3c41dd5b4dbc833e
            return predicate;
        };
    }
}