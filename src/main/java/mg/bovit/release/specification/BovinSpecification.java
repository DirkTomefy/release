package mg.bovit.release.specification;

import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import mg.bovit.release.dto.MultiCriteriaFormBovinList;
import mg.bovit.release.model.VenteDetail;
import mg.bovit.release.model.sqlview.BovinWithPoids;

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

            if ("non_vendu".equalsIgnoreCase(statut)) {
                Subquery<VenteDetail> venteSubquery = query.subquery(VenteDetail.class);
                Root<VenteDetail> venteRoot = venteSubquery.from(VenteDetail.class);
                venteSubquery.select(venteRoot);
                venteSubquery.where(cb.equal(venteRoot.get("bovin").get("id"), root.get("id")));

                predicate = cb.and(predicate,
                        cb.not(cb.exists(venteSubquery)));
            }

            return predicate;
        };
    }
}