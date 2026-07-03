package mg.bovit.release.specification;

import mg.bovit.release.model.Client;
import mg.bovit.release.dto.MultiCriteriaFormClientList;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.*;

public class ClientSpecification {

    public static Specification<Client> fromForm(MultiCriteriaFormClientList form) {
        return (root, query, cb) -> {
            Predicate predicate = cb.conjunction();

            // Recherche globale : nom OU prenom OU contact contient le texte recherché
            if (form.getSearch() != null && !form.getSearch().isBlank()) {
                String like = "%" + form.getSearch().toLowerCase() + "%";
                Predicate global = cb.or(
                        cb.like(cb.lower(root.get("nom")), like),
                        cb.like(cb.lower(root.get("prenom")), like),
                        cb.like(cb.lower(root.get("contact")), like)
                );
                predicate = cb.and(predicate, global);
            }

            // Filtres spécifiques additionnels
            if (form.getNom() != null && !form.getNom().isBlank()) {
                predicate = cb.and(predicate,
                        cb.like(cb.lower(root.get("nom")), "%" + form.getNom().toLowerCase() + "%"));
            }

            if (form.getContact() != null && !form.getContact().isBlank()) {
                predicate = cb.and(predicate,
                        cb.like(cb.lower(root.get("contact")), "%" + form.getContact().toLowerCase() + "%"));
            }

            return predicate;
        };
    }
}
